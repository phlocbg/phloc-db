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

import java.io.Closeable;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Abstract implementation of {@link IDataSourceProvider} based on
 * {@link BasicDataSource} implementation.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractConnector implements IDataSourceProvider, Closeable
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractConnector.class);

  private final Lock m_aLock = new ReentrantLock ();
  protected BasicDataSource m_aDataSource;

  public AbstractConnector ()
  {}

  @Nonnull
  protected final Lock getLock ()
  {
    return m_aLock;
  }

  @Nonnull
  @Nonempty
  protected abstract String getJDBCDriverClassName ();

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

  /**
   * @return The final connection URL to be used for connecting. May not be
   *         <code>null</code>.
   */
  @Nonnull
  public abstract String getConnectionUrl ();

  @OverrideOnDemand
  protected boolean isUseDefaultAutoCommit ()
  {
    return false;
  }

  @OverrideOnDemand
  protected boolean isPoolPreparedStatements ()
  {
    return true;
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
        m_aDataSource.setDriverClassName (getJDBCDriverClassName ());
        if (getUserName () != null)
          m_aDataSource.setUsername (getUserName ());
        if (getPassword () != null)
          m_aDataSource.setPassword (getPassword ());
        m_aDataSource.setUrl (getConnectionUrl ());

        // settings
        m_aDataSource.setDefaultAutoCommit (isUseDefaultAutoCommit ());
        m_aDataSource.setPoolPreparedStatements (isPoolPreparedStatements ());
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
          throw new IllegalStateException ("Failed to close DataSource", ex);
        }

        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("Closed database connection to '" + getDatabase () + "'");
      }
    }
    finally
    {
      m_aLock.unlock ();
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("dataSource", m_aDataSource).toString ();
  }
}
