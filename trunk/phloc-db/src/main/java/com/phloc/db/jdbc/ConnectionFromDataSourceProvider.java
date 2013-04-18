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
package com.phloc.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFromDataSourceProvider implements IConnectionProvider
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ConnectionFromDataSourceProvider.class);
  private final DataSource m_aDS;

  public ConnectionFromDataSourceProvider (@Nonnull final IDataSourceProvider aDSP)
  {
    if (aDSP == null)
      throw new NullPointerException ("dataSourceProvider");
    m_aDS = aDSP.getDataSource ();
    if (m_aDS == null)
      throw new IllegalArgumentException ("Failed to create dataSource");
  }

  public Connection getConnection ()
  {
    try
    {
      final Connection ret = m_aDS.getConnection ();
      if (ret == null)
        s_aLogger.warn ("Failed to get connection!");
      return ret;
    }
    catch (final SQLException ex)
    {
      s_aLogger.error ("no connection retrieved", ex);
      return null;
    }
  }

  public boolean shouldCloseConnection ()
  {
    return true;
  }
}
