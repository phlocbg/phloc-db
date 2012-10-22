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

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.h2.api.DatabaseEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.charset.CCharset;
import com.phloc.commons.io.file.FileUtils;
import com.phloc.commons.io.streams.StreamUtils;
import com.phloc.commons.state.ESuccess;
import com.phloc.db.jdbc.IDataSourceProvider;
import com.phloc.db.jdbc.executor.DBExecutor;
import com.phloc.db.jdbc.executor.DBResultRow;
import com.phloc.db.jdbc.executor.IResultSetRowCallback;

public abstract class AbstractH2Connector implements IDataSourceProvider, Closeable
{
  public static final int DEFAULT_TRACE_LEVEL_FILE = 1;
  public static final int DEFAULT_TRACE_LEVEL_SYSOUT = 0;
  public static final boolean DEFAULT_CLOSE_ON_EXIT = true;
  protected static final String CONNECTION_PREFIX = "jdbc:h2:";
  protected static final String JDBC_DRIVER_CLASS = "org.h2.Driver";
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractH2Connector.class);

  private final Lock m_aLock = new ReentrantLock ();
  protected BasicDataSource m_aDataSource;
  private int m_nTraceLevelFile = DEFAULT_TRACE_LEVEL_FILE;
  private int m_nTraceLevelSysOut = DEFAULT_TRACE_LEVEL_SYSOUT;
  private Class <? extends DatabaseEventListener> m_aEventListenerClass = H2EventListener.class;
  private boolean m_bCloseOnExit = DEFAULT_CLOSE_ON_EXIT;

  public AbstractH2Connector ()
  {}

  @Nonnull
  protected final Lock getLock ()
  {
    return m_aLock;
  }

  public final int getTraceLevelFile ()
  {
    m_aLock.lock ();
    try
    {
      return m_nTraceLevelFile;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final void setTraceLevelFile (final int nTraceLevelFile)
  {
    m_aLock.lock ();
    try
    {
      m_nTraceLevelFile = nTraceLevelFile;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final int getTraceLevelSysOut ()
  {
    m_aLock.lock ();
    try
    {
      return m_nTraceLevelSysOut;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final void setTraceLevelSysOut (final int nTraceLevelSysOut)
  {
    m_aLock.lock ();
    try
    {
      m_nTraceLevelSysOut = nTraceLevelSysOut;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  @Nullable
  public final Class <? extends DatabaseEventListener> getEventListenerClass ()
  {
    m_aLock.lock ();
    try
    {
      return m_aEventListenerClass;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final void setEventListenerClass (@Nullable final Class <? extends DatabaseEventListener> aEventListenerClass)
  {
    m_aLock.lock ();
    try
    {
      m_aEventListenerClass = aEventListenerClass;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final boolean isCloseOnExit ()
  {
    m_aLock.lock ();
    try
    {
      return m_bCloseOnExit;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final void setCloseOnExit (final boolean bCloseOnExit)
  {
    m_aLock.lock ();
    try
    {
      m_bCloseOnExit = bCloseOnExit;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  /**
   * @return Connection user name
   */
  @Nullable
  protected abstract String getUserName ();

  /**
   * @return Connection password
   */
  @Nullable
  protected abstract String getPassword ();

  /**
   * @return Name of the database to connect to
   */
  @Nonnull
  protected abstract String getDatabase ();

  @Nonnull
  public final String getConnectionUrl ()
  {
    final StringBuilder ret = new StringBuilder (CONNECTION_PREFIX);
    ret.append (getDatabase ());
    if (m_nTraceLevelFile != DEFAULT_TRACE_LEVEL_FILE)
      ret.append (";TRACE_LEVEL_FILE=").append (m_nTraceLevelFile);
    if (m_nTraceLevelSysOut != DEFAULT_TRACE_LEVEL_SYSOUT)
      ret.append (";TRACE_LEVEL_SYSTEM_OUT=").append (m_nTraceLevelSysOut);
    if (m_aEventListenerClass != null)
      ret.append (";DATABASE_EVENT_LISTENER='").append (m_aEventListenerClass.getName ()).append ("'");
    if (m_bCloseOnExit != DEFAULT_CLOSE_ON_EXIT)
      ret.append (";DB_CLOSE_ON_EXIT=").append (Boolean.toString (m_bCloseOnExit).toUpperCase (Locale.US));
    return ret.toString ();
  }

  @Nonnull
  public final DataSource getDataSource ()
  {
    m_aLock.lock ();
    try
    {
      if (m_aDataSource == null)
      {
        // build data source
        m_aDataSource = new BasicDataSource ();
        m_aDataSource.setDriverClassName (JDBC_DRIVER_CLASS);
        if (getUserName () != null)
          m_aDataSource.setUsername (getUserName ());
        if (getPassword () != null)
          m_aDataSource.setPassword (getPassword ());
        m_aDataSource.setUrl (getConnectionUrl ());
        m_aDataSource.setDefaultAutoCommit (false);
        m_aDataSource.setPoolPreparedStatements (true);
      }
      return m_aDataSource;
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  public final void close ()
  {
    m_aLock.lock ();
    try
    {
      if (m_aDataSource != null)
      {
        try
        {
          m_aDataSource.close ();
          m_aDataSource = null;
        }
        catch (final SQLException ex)
        {
          throw new IllegalStateException ("Failed to close H2 DataSource", ex);
        }

        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Closed H2 database connection to '" + getDatabase () + "'");
      }
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  @Nonnull
  public final ESuccess dumpDatabase (@Nonnull final File aFile)
  {
    return dumpDatabase (FileUtils.getOutputStream (aFile));
  }

  /**
   * Dump the database to the passed output stream and closed the passed output
   * stream.
   * 
   * @param aOS
   *        The output stream to dump the DB content to. May not be
   *        <code>null</code>. Automatically closed when done.
   * @return <code>true</code> upon success, <code>false</code> if an error
   *         occurred.
   */
  @Nonnull
  public final ESuccess dumpDatabase (@Nonnull @WillClose final OutputStream aOS)
  {
    if (aOS == null)
      throw new NullPointerException ("outputStream");

    // Save the DB data to an SQL file
    try
    {
      s_aLogger.info ("Dumping database '" + getDatabase () + "' to OutputStream");
      final PrintWriter aPrintWriter = new PrintWriter (new BufferedWriter (StreamUtils.createWriter (aOS,
                                                                                                      CCharset.CHARSET_UTF_8_OBJ)));
      final DBExecutor aExecutor = new DBExecutor (this);
      final ESuccess ret = aExecutor.queryAll ("SCRIPT SIMPLE", new IResultSetRowCallback ()
      {
        public void run (@Nonnull final DBResultRow aCurrentObject)
        {
          // The value of the first column is the script line
          aPrintWriter.println (aCurrentObject.get (0).getValue ());
        }
      });
      aPrintWriter.flush ();
      return ret;
    }
    finally
    {
      StreamUtils.close (aOS);
    }
  }

  /**
   * Create a backup file. The file is a ZIP file.
   * 
   * @param fDestFile
   *        Destination filename. May not be <code>null</code>.
   * @return {@link ESuccess}
   */
  @Nonnull
  public final ESuccess createBackup (@Nonnull final File fDestFile)
  {
    if (fDestFile == null)
      throw new NullPointerException ("destFile");

    s_aLogger.info ("Backing up database '" + getDatabase () + "' to " + fDestFile);
    final DBExecutor aExecutor = new DBExecutor (this);
    return aExecutor.executeStatement ("BACKUP TO '" + fDestFile.getAbsolutePath () + "'");
  }
}
