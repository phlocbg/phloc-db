/**
 * Copyright (C) 2006-2012 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.db.h2;

import java.io.File;
import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.io.file.FileOperations;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.string.StringHelper;
import com.phloc.datetime.PDTFactory;

/**
 * This class handles some of the specialities of the H2 database.
 * 
 * @author philip
 */
public class H2FileConnector extends H2MemConnector
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (H2FileConnector.class);
  private final String m_sDirectory;

  public H2FileConnector (@Nonnull @Nonempty final String sDirectory,
                          @Nonnull @Nonempty final String sDBName,
                          @Nullable final String sUser,
                          @Nullable final String sPassword)
  {
    super (sDBName, sUser, sPassword);
    if (StringHelper.hasNoText (sDirectory))
      throw new IllegalArgumentException ("directory is empty");
    m_sDirectory = sDirectory;
  }

  @Override
  @Nonnull
  @Nonempty
  public String getDatabase ()
  {
    return m_sDirectory + "/" + m_sDBName;
  }

  /**
   * Compact the database by exporting everything, resetting the DB and
   * importing it again. Before compacting process is started, <b>the data
   * source is closed</b>!!!
   * 
   * @param bDeleteTemporarySQLFile
   *        if <code>true</code> the temporary file (h2temp.sql) is deleted
   *        after successful compacting.
   * @return {@link ESuccess#SUCCESS} if everything went well,
   *         <code>false</code> if an error occurred. In case of an error, see
   *         the log file.
   */
  @Nonnull
  public ESuccess compactDatabase (final boolean bDeleteTemporarySQLFile)
  {
    getLock ().lock ();
    try
    {
      // Close DB
      close ();

      // Set temporary file name
      final String sScriptFileName = "h2dump$$.sql";
      final File aScriptFileName = new File (sScriptFileName);
      try
      {
        if (dumpDatabase (aScriptFileName).isSuccess ())
        {
          // Delete all DB files
          DeleteDbFiles.execute (m_sDirectory, m_sDBName, true);

          // And re-execute the stuff from the file
          RunScript.execute (getConnectionUrl (), m_sUser, m_sPassword, sScriptFileName, null, false);

          // And delete temporary file
          if (bDeleteTemporarySQLFile && FileOperations.deleteFile (aScriptFileName).isFailure ())
            s_aLogger.error ("Failed to delete temporary export file '" + sScriptFileName + "'");
          return ESuccess.SUCCESS;
        }
      }
      catch (final SQLException ex)
      {
        // Oops, we lost our data -> save file to avoid overwriting with another
        // compact call
        String sUniqueFileName = "h2dump-" + PDTFactory.getCurrentMillis () + ".sql";
        if (FileOperations.renameFile (aScriptFileName, new File (sUniqueFileName)).isFailure ())
          sUniqueFileName = sScriptFileName;
        s_aLogger.error ("Failed to delete and refill database. Data is contained in file '" + sUniqueFileName + "'!",
                         ex);
      }
      return ESuccess.FAILURE;
    }
    finally
    {
      getLock ().unlock ();
    }
  }
}
