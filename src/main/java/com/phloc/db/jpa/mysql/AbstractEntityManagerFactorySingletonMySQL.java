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

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.persistence.platform.database.MySQLPlatform;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.db.api.mysql.EMySQLConnectionProperty;
import com.phloc.db.api.mysql.MySQLHelper;
import com.phloc.db.jpa.AbstractEntityManagerFactorySingleton;

/**
 * JPA Singleton specific for MySQL database.
 * 
 * @author Philip Helger
 */
public abstract class AbstractEntityManagerFactorySingletonMySQL extends AbstractEntityManagerFactorySingleton
{
  private static final Map <EMySQLConnectionProperty, String> s_aDefaultConnectionProperties = new EnumMap <EMySQLConnectionProperty, String> (EMySQLConnectionProperty.class);

  @Nonnull
  @Nonempty
  private static String _buildJDBCString (@Nonnull @Nonempty final String sJdbcURL,
                                          @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties)
  {
    // Build connection properties from default values and the optional ones
    final Map <EMySQLConnectionProperty, String> aProps = ContainerHelper.newMap (s_aDefaultConnectionProperties);
    if (aConnectionProperties != null)
      aProps.putAll (aConnectionProperties);

    return MySQLHelper.buildJDBCString (sJdbcURL, aProps);
  }

  /*
   * Constructor. Never initialize manually!
   */
  protected AbstractEntityManagerFactorySingletonMySQL (@Nonnull @Nonempty final String sJdbcURL,
                                                        @Nullable final String sUserName,
                                                        @Nullable final String sPassword,
                                                        @Nonnull @Nonempty final String sPersistenceUnitName)
  {
    this (sJdbcURL, null, sUserName, sPassword, sPersistenceUnitName, null);
  }

  /*
   * Constructor. Never initialize manually!
   */
  protected AbstractEntityManagerFactorySingletonMySQL (@Nonnull @Nonempty final String sJdbcURL,
                                                        @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties,
                                                        @Nullable final String sUserName,
                                                        @Nullable final String sPassword,
                                                        @Nonnull @Nonempty final String sPersistenceUnitName)
  {
    this (sJdbcURL, aConnectionProperties, sUserName, sPassword, sPersistenceUnitName, null);
  }

  /*
   * Constructor. Never initialize manually!
   */
  protected AbstractEntityManagerFactorySingletonMySQL (@Nonnull @Nonempty final String sJdbcURL,
                                                        @Nullable final Map <EMySQLConnectionProperty, String> aConnectionProperties,
                                                        @Nullable final String sUserName,
                                                        @Nullable final String sPassword,
                                                        @Nonnull @Nonempty final String sPersistenceUnitName,
                                                        @Nullable final Map <String, Object> aAdditionalFactoryProperties)
  {
    super (com.mysql.jdbc.Driver.class,
           _buildJDBCString (sJdbcURL, aConnectionProperties),
           sUserName,
           sPassword,
           MySQLPlatform.class,
           sPersistenceUnitName,
           aAdditionalFactoryProperties);
  }
}
