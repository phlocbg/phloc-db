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
package com.phloc.db.jpa.mysql;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;
import com.phloc.db.jpa.AbstractJPASingleton;

/**
 * JPA Singleton specific for MySQL database.
 * 
 * @author philip
 */
public abstract class AbstractJPASingletonMySQL extends AbstractJPASingleton
{
  public static final String JDBC_URL_PREFIX_MYSQL = "jdbc:mysql:";
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJPASingleton.class);
  private static final Map <EMySQLConnectionProperty, String> s_aDefaultConnectionProperties = new HashMap <EMySQLConnectionProperty, String> ();

  /**
   * Build the final connection string from the base JDBC URL and an optional
   * set of connection properties.
   * 
   * @param sJdbcURL
   *        The base JDBC URL. May neither be <code>null</code> nor empty and
   *        must started with {@link #JDBC_URL_PREFIX_MYSQL}
   * @param aConnectionProperties
   *        An optional map with connection properties. May be <code>null</code>
   *        .
   * @return The final JDBC connection string to be used. Never
   *         <code>null</code> or empty
   */
  @Nonnull
  @Nonempty
  private static String _buildJDBCString (@Nonnull @Nonempty final String sJdbcURL,
                                          @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties)
  {
    if (StringHelper.hasNoText (sJdbcURL))
      throw new IllegalArgumentException ("JDBC URL may not be empty!");
    if (!sJdbcURL.startsWith (JDBC_URL_PREFIX_MYSQL))
      s_aLogger.error ("The JDBC URL '" + sJdbcURL + "' does not seem to be a MySQL connection string!");

    // FIMXE does this fit for MySQL DBs?

    // Build connection properties from default values and the optional ones
    final Map <EMySQLConnectionProperty, String> aProps = ContainerHelper.newMap (s_aDefaultConnectionProperties);
    if (aConnectionProperties != null)
      aProps.putAll (aConnectionProperties);

    // Add the connection properties to the JDBC string
    final StringBuilder aSB = new StringBuilder ();
    for (final Map.Entry <EMySQLConnectionProperty, String> aEntry : aProps.entrySet ())
    {
      if (aSB.length () == 0)
        aSB.append ('?');
      else
        aSB.append ('&');
      aSB.append (aEntry.getKey ().getName ()).append ('=').append (aEntry.getValue ());
    }
    return aSB.insert (0, sJdbcURL).toString ();
  }

  /**
   * Constructor. Never initialize manually!
   */
  protected AbstractJPASingletonMySQL (@Nonnull @Nonempty final String sJdbcURL,
                                       final String sUser,
                                       final String sPassword,
                                       final String sPersistenceUnitName)
  {
    this (sJdbcURL, null, sUser, sPassword, sPersistenceUnitName, null);
  }

  /**
   * Constructor. Never initialize manually!
   */
  protected AbstractJPASingletonMySQL (@Nonnull @Nonempty final String sJdbcURL,
                                       @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties,
                                       final String sUser,
                                       final String sPassword,
                                       final String sPersistenceUnitName)
  {
    this (sJdbcURL, aConnectionProperties, sUser, sPassword, sPersistenceUnitName, null);
  }

  /**
   * Constructor. Never initialize manually!
   */
  protected AbstractJPASingletonMySQL (@Nonnull @Nonempty final String sJdbcURL,
                                       @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties,
                                       final String sUser,
                                       final String sPassword,
                                       final String sPersistenceUnitName,
                                       @Nullable final Map <String, Object> aAdditionalFactoryProps)
  {
    super (com.mysql.jdbc.Driver.class.getName (),
           _buildJDBCString (sJdbcURL, aConnectionProperties),
           sUser,
           sPassword,
           MySQLPlatform.class,
           sPersistenceUnitName,
           aAdditionalFactoryProps);
  }
}
