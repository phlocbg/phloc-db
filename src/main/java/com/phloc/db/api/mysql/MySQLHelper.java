/**
 * Copyright (C) 2006-2013 phloc systems
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
package com.phloc.db.api.mysql;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.db.api.CJDBC_MySQL;

@Immutable
public final class MySQLHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MySQLHelper.class);

  private MySQLHelper ()
  {}

  /**
   * Build the final connection string from the base JDBC URL and an optional
   * set of connection properties.
   * 
   * @param sJdbcURL
   *        The base JDBC URL. May neither be <code>null</code> nor empty and
   *        must started with {@link CJDBC_MySQL#CONNECTION_PREFIX}
   * @param aConnectionProperties
   *        An map with all connection properties. May be <code>null</code> .
   * @return The final JDBC connection string to be used. Never
   *         <code>null</code> or empty
   */
  @Nonnull
  @Nonempty
  public static String buildJDBCString (@Nonnull @Nonempty final String sJdbcURL,
                                        @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties)
  {
    if (StringHelper.hasNoText (sJdbcURL))
      throw new IllegalArgumentException ("JDBC URL may not be empty!");
    if (!sJdbcURL.startsWith (CJDBC_MySQL.CONNECTION_PREFIX))
      s_aLogger.error ("The JDBC URL '" + sJdbcURL + "' does not seem to be a MySQL connection string!");

    // Add the connection properties to the JDBC string
    final StringBuilder aSB = new StringBuilder ();
    if (aConnectionProperties != null)
      for (final Map.Entry <EMySQLConnectionProperty, String> aEntry : aConnectionProperties.entrySet ())
      {
        if (aSB.length () == 0)
          aSB.append ('?');
        else
          aSB.append ('&');
        aSB.append (aEntry.getKey ().getName ()).append ('=').append (aEntry.getValue ());
      }
    return aSB.insert (0, sJdbcURL).toString ();
  }

}
