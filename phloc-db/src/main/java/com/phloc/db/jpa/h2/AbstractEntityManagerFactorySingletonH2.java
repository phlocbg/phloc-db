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
package com.phloc.db.jpa.h2;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.persistence.platform.database.H2Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;
import com.phloc.db.api.CJDBC_H2;
import com.phloc.db.jpa.AbstractEntityManagerFactorySingleton;

/**
 * JPA Singleton specific for H2 database.
 * 
 * @author Philip Helger
 */
public abstract class AbstractEntityManagerFactorySingletonH2 extends AbstractEntityManagerFactorySingleton
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractEntityManagerFactorySingleton.class);
  private static final Map <String, String> s_aDefaultConnectionProperties = new HashMap <String, String> ();

  /**
   * Build the final connection string from the base JDBC URL and an optional
   * set of connection properties.
   * 
   * @param sJdbcURL
   *        The base JDBC URL. May neither be <code>null</code> nor empty and
   *        must started with {@link #JDBC_URL_PREFIX_H2}
   * @param aConnectionProperties
   *        An optional map with connection properties. May be <code>null</code>
   *        .
   * @return The final JDBC connection string to be used. Never
   *         <code>null</code> or empty
   */
  @Nonnull
  @Nonempty
  private static String _buildJDBCString (@Nonnull @Nonempty final String sJdbcURL,
                                          @Nullable final Map <String, String> aConnectionProperties)
  {
    if (StringHelper.hasNoText (sJdbcURL))
      throw new IllegalArgumentException ("JDBC URL may not be empty!");
    if (!sJdbcURL.startsWith (CJDBC_H2.CONNECTION_PREFIX))
      s_aLogger.error ("The JDBC URL '" + sJdbcURL + "' does not seem to be a H2 connection string!");

    // Build connection properties from default values and the optional ones
    final Map <String, String> aProps = ContainerHelper.newMap (s_aDefaultConnectionProperties);
    if (aConnectionProperties != null)
      aProps.putAll (aConnectionProperties);

    // Add the connection properties to the JDBC string
    final StringBuilder aSB = new StringBuilder (sJdbcURL);
    for (final Map.Entry <String, String> aEntry : aProps.entrySet ())
      aSB.append (';').append (aEntry.getKey ()).append ('=').append (aEntry.getValue ());
    return aSB.toString ();
  }

  /*
   * Constructor. Never initialize manually!
   */
  protected AbstractEntityManagerFactorySingletonH2 (@Nonnull @Nonempty final String sJdbcURL,
                                                     @Nullable final String sUser,
                                                     @Nullable final String sPassword,
                                                     @Nonnull @Nonempty final String sPersistenceUnitName)
  {
    this (sJdbcURL, null, sUser, sPassword, sPersistenceUnitName, null);
  }

  /*
   * Constructor. Never initialize manually!
   */
  protected AbstractEntityManagerFactorySingletonH2 (@Nonnull @Nonempty final String sJdbcURL,
                                                     @Nullable final Map <String, String> aConnectionProperties,
                                                     @Nullable final String sUser,
                                                     @Nullable final String sPassword,
                                                     @Nonnull @Nonempty final String sPersistenceUnitName)
  {
    this (sJdbcURL, aConnectionProperties, sUser, sPassword, sPersistenceUnitName, null);
  }

  /**
   * Constructor. Never initialize manually!
   */
  protected AbstractEntityManagerFactorySingletonH2 (@Nonnull @Nonempty final String sJdbcURL,
                                                     @Nullable final Map <String, String> aConnectionProperties,
                                                     @Nullable final String sUser,
                                                     @Nullable final String sPassword,
                                                     @Nonnull @Nonempty final String sPersistenceUnitName,
                                                     @Nullable final Map <String, Object> aAdditionalFactoryProps)
  {
    super (H2DriverSingleton.getInstance ().getDriverClass (),
           _buildJDBCString (sJdbcURL, aConnectionProperties),
           sUser,
           sPassword,
           H2Platform.class,
           sPersistenceUnitName,
           aAdditionalFactoryProps);
  }

  public static final void setJMXEnabledByDefault (final boolean bEnabled)
  {
    if (bEnabled)
      s_aDefaultConnectionProperties.put ("JMX", "TRUE");
    else
      s_aDefaultConnectionProperties.remove ("JMX");
  }
}
