/**
 * Copyright (C) 2006-2015 phloc systems
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
package com.phloc.db.jdbc.mssql;

import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.db.api.CJDBC_SQLServer;
import com.phloc.db.jdbc.AbstractConnector;

/**
 * Abstract DB connector for Microsoft SQL Server
 * 
 * @author Philip Helger
 */
public abstract class AbstractMSSQLConnector extends AbstractConnector
{
  public AbstractMSSQLConnector ()
  {}

  @Override
  @Nonnull
  @Nonempty
  protected String getJDBCDriverClassName ()
  {
    return CJDBC_SQLServer.DEFAULT_JDBC_DRIVER_CLASS_NAME;
  }

  @Override
  @Nonnull
  public final String getConnectionUrl ()
  {
    final StringBuilder ret = new StringBuilder (CJDBC_SQLServer.CONNECTION_PREFIX);
    ret.append (getDatabase ());
    return ret.toString ();
  }
}
