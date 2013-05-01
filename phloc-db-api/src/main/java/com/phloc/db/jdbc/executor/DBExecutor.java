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
package com.phloc.db.jdbc.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.CheckForSigned;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.CGlobal;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.annotations.CodingStyleguideUnaware;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.callback.IExceptionHandler;
import com.phloc.commons.callback.LoggingExceptionHandler;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.collections.pair.IReadonlyPair;
import com.phloc.commons.collections.pair.ReadonlyPair;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.db.jdbc.ConnectionFromDataSourceProvider;
import com.phloc.db.jdbc.IConnectionProvider;
import com.phloc.db.jdbc.IDataSourceProvider;
import com.phloc.db.jdbc.JDBCUtils;
import com.phloc.db.jdbc.callback.GetSingleGeneratedKeyCallback;
import com.phloc.db.jdbc.callback.IGeneratedKeysCallback;
import com.phloc.db.jdbc.callback.IPreparedStatementDataProvider;
import com.phloc.db.jdbc.callback.IResultSetRowCallback;
import com.phloc.db.jdbc.callback.IUpdatedRowCountCallback;
import com.phloc.db.jdbc.callback.UpdatedRowCountCallback;

/**
 * Simple wrapper around common JDBC functionality.
 * 
 * @author Philip Helger
 */
@NotThreadSafe
public class DBExecutor
{
  protected interface IWithConnectionCallback
  {
    void run (@Nonnull Connection aConnection) throws SQLException;
  }

  protected interface IWithStatementCallback
  {
    void run (@Nonnull Statement aStatement) throws SQLException;
  }

  protected interface IWithPreparedStatementCallback
  {
    void run (@Nonnull PreparedStatement aPreparedStatement) throws SQLException;
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (DBExecutor.class);

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private final IConnectionProvider m_aConnectionProvider;
  private IExceptionHandler <? super SQLException> m_aExceptionHdl = new LoggingExceptionHandler ();

  public DBExecutor (@Nonnull final IDataSourceProvider aDataSourceProvider)
  {
    this (new ConnectionFromDataSourceProvider (aDataSourceProvider));
  }

  public DBExecutor (@Nonnull final IConnectionProvider aConnectionProvider)
  {
    if (aConnectionProvider == null)
      throw new NullPointerException ("connectionProvider");
    m_aConnectionProvider = aConnectionProvider;
  }

  public void setSQLExceptionHandler (@Nonnull final IExceptionHandler <? super SQLException> aExceptionHdl)
  {
    if (aExceptionHdl == null)
      throw new NullPointerException ("exceptionHandler");

    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aExceptionHdl = aExceptionHdl;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public IExceptionHandler <? super SQLException> getSQLExceptionHandler ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aExceptionHdl;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  // ESCA-JAVA0143:
  @CodingStyleguideUnaware ("Needs to be synchronized!")
  @Nonnull
  private synchronized ESuccess _withConnectionDo (@Nonnull final IWithConnectionCallback aCB)
  {
    Connection aConnection = null;
    ESuccess eCommited = ESuccess.FAILURE;
    try
    {
      aConnection = m_aConnectionProvider.getConnection ();
      if (aConnection == null)
        throw new IllegalStateException ("Failed to get a connection");

      aCB.run (aConnection);
      eCommited = JDBCUtils.commit (aConnection);
    }
    catch (final SQLException ex)
    {
      try
      {
        getSQLExceptionHandler ().onException (ex);
      }
      catch (final Throwable t2)
      {
        s_aLogger.error ("Failed to handle exception in custom exception handler", t2);
      }
      return ESuccess.FAILURE;
    }
    finally
    {
      if (eCommited.isFailure ())
        JDBCUtils.rollback (aConnection);

      if (m_aConnectionProvider.shouldCloseConnection ())
        JDBCUtils.close (aConnection);
    }
    return ESuccess.SUCCESS;
  }

  protected static void handleGeneratedKeys (@Nonnull final ResultSet aGeneratedKeysRS,
                                             @Nonnull final IGeneratedKeysCallback aGeneratedKeysCB) throws SQLException
  {
    final int nCols = aGeneratedKeysRS.getMetaData ().getColumnCount ();
    final List <List <Object>> aValues = new ArrayList <List <Object>> ();
    while (aGeneratedKeysRS.next ())
    {
      final List <Object> aRow = new ArrayList <Object> (nCols);
      for (int i = 1; i <= nCols; ++i)
        aRow.add (aGeneratedKeysRS.getObject (i));
      aValues.add (aRow);
    }
    aGeneratedKeysCB.onGeneratedKeys (aValues);
  }

  @Nonnull
  protected final ESuccess withStatementDo (@Nonnull final IWithStatementCallback aCB,
                                            @Nullable final IGeneratedKeysCallback aGeneratedKeysCB)
  {
    return _withConnectionDo (new IWithConnectionCallback ()
    {
      public void run (@Nonnull final Connection aConnection) throws SQLException
      {
        Statement aStatement = null;
        try
        {
          aStatement = aConnection.createStatement ();
          aCB.run (aStatement);

          if (aGeneratedKeysCB != null)
            handleGeneratedKeys (aStatement.getGeneratedKeys (), aGeneratedKeysCB);
        }
        finally
        {
          JDBCUtils.close (aStatement);
        }
      }
    });
  }

  @Nonnull
  protected final ESuccess withPreparedStatementDo (@Nonnull final String sSQL,
                                                    @Nonnull final IPreparedStatementDataProvider aPSDP,
                                                    @Nonnull final IWithPreparedStatementCallback aPSCallback,
                                                    @Nullable final IUpdatedRowCountCallback aUpdatedRowCountCB,
                                                    @Nullable final IGeneratedKeysCallback aGeneratedKeysCB)
  {
    return _withConnectionDo (new IWithConnectionCallback ()
    {
      public void run (@Nonnull final Connection aConnection) throws SQLException
      {
        final PreparedStatement aPS = aConnection.prepareStatement (sSQL, Statement.RETURN_GENERATED_KEYS);
        try
        {
          if (aPS.getParameterMetaData ().getParameterCount () != aPSDP.getValueCount ())
            throw new IllegalArgumentException ("parameter count (" +
                                                aPS.getParameterMetaData ().getParameterCount () +
                                                ") does not match passed column name count (" +
                                                aPSDP.getValueCount () +
                                                ")");

          // assign values
          int nIndex = 1;
          for (final Object aArg : aPSDP.getObjectValues ())
            aPS.setObject (nIndex++, aArg);

          if (GlobalDebug.isDebugMode ())
            s_aLogger.info ("Executing prepared statement: " + sSQL);

          // call callback
          aPSCallback.run (aPS);

          // Updated row count callback present?
          if (aUpdatedRowCountCB != null)
            aUpdatedRowCountCB.setUpdatedRowCount (aPS.getUpdateCount ());

          // retrieve generated keys?
          if (aGeneratedKeysCB != null)
            handleGeneratedKeys (aPS.getGeneratedKeys (), aGeneratedKeysCB);
        }
        finally
        {
          aPS.close ();
        }
      }
    });
  }

  @Nonnull
  public ESuccess executeStatement (@Nonnull final String sSQL)
  {
    return executeStatement (sSQL, null);
  }

  @Nonnull
  public ESuccess executeStatement (@Nonnull final String sSQL, @Nullable final IGeneratedKeysCallback aGeneratedKeysCB)
  {
    return withStatementDo (new IWithStatementCallback ()
    {
      public void run (@Nonnull final Statement aStatement) throws SQLException
      {
        if (GlobalDebug.isDebugMode ())
          s_aLogger.info ("Executing statement: " + sSQL);
        aStatement.execute (sSQL);
      }
    }, aGeneratedKeysCB);
  }

  @Nonnull
  public ESuccess executePreparedStatement (@Nonnull final String sSQL,
                                            @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    return executePreparedStatement (sSQL, aPSDP, null, null);
  }

  @Nonnull
  public ESuccess executePreparedStatement (@Nonnull final String sSQL,
                                            @Nonnull final IPreparedStatementDataProvider aPSDP,
                                            @Nullable final IUpdatedRowCountCallback aURWCC,
                                            @Nullable final IGeneratedKeysCallback aGeneratedKeysCB)
  {
    return withPreparedStatementDo (sSQL, aPSDP, new IWithPreparedStatementCallback ()
    {
      public void run (@Nonnull final PreparedStatement aPS) throws SQLException
      {
        aPS.execute ();
      }
    }, aURWCC, aGeneratedKeysCB);
  }

  @Nullable
  public Object executePreparedStatementAndGetGeneratedKey (@Nonnull final String sSQL,
                                                            @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    final GetSingleGeneratedKeyCallback aCB = new GetSingleGeneratedKeyCallback ();
    return executePreparedStatement (sSQL, aPSDP, null, aCB).isSuccess () ? aCB.getGeneratedKey () : null;
  }

  /**
   * Perform an INSERT or UPDATE statement.
   * 
   * @param sSQL
   *        SQL to execute.
   * @param aPSDP
   *        The prepared statement provider.
   * @return The number of modified/inserted rows.
   */
  public int insertOrUpdateOrDelete (@Nonnull final String sSQL, @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    return insertOrUpdateOrDelete (sSQL, aPSDP, null);
  }

  /**
   * Perform an INSERT or UPDATE statement.
   * 
   * @param sSQL
   *        SQL to execute.
   * @param aPSDP
   *        The prepared statement provider.
   * @param aGeneratedKeysCB
   *        An optional callback to retrieve eventually generated values. May be
   *        <code>null</code>.
   * @return The number of modified/inserted rows.
   */
  public int insertOrUpdateOrDelete (@Nonnull final String sSQL,
                                     @Nonnull final IPreparedStatementDataProvider aPSDP,
                                     @Nullable final IGeneratedKeysCallback aGeneratedKeysCB)
  {
    // We need this wrapper because the anonymous inner class cannot change
    // variables in outer scope.
    final IUpdatedRowCountCallback aURCCB = new UpdatedRowCountCallback ();
    withPreparedStatementDo (sSQL, aPSDP, new IWithPreparedStatementCallback ()
    {
      public void run (@Nonnull final PreparedStatement aPS) throws SQLException
      {
        aPS.execute ();
      }
    }, aURCCB, aGeneratedKeysCB);
    return aURCCB.getUpdatedRowCount ();
  }

  @Nonnull
  public IReadonlyPair <Integer, Object> insertOrUpdateAndGetGeneratedKey (@Nonnull final String sSQL,
                                                                           @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    final GetSingleGeneratedKeyCallback aCB = new GetSingleGeneratedKeyCallback ();
    final int nUpdateCount = insertOrUpdateOrDelete (sSQL, aPSDP, aCB);
    return ReadonlyPair.create (Integer.valueOf (nUpdateCount),
                                nUpdateCount != IUpdatedRowCountCallback.NOT_INITIALIZED ? aCB.getGeneratedKey ()
                                                                                        : null);
  }

  /**
   * Iterate the passed result set, collect all values of a single result row,
   * and call the callback for each row of result objects.
   * 
   * @param aRS
   *        The result set to iterate.
   * @param aCallback
   *        The callback to be invoked for each row.
   * @throws SQLException
   *         on error
   */
  protected static final void iterateResultSet (@WillClose final ResultSet aRS,
                                                @Nonnull final IResultSetRowCallback aCallback) throws SQLException
  {
    try
    {
      // Get column names
      final ResultSetMetaData aRSMD = aRS.getMetaData ();
      final int nCols = aRSMD.getColumnCount ();
      final String [] aColumnNames = new String [nCols];
      final int [] aColumnTypes = new int [nCols];
      for (int i = 1; i <= nCols; ++i)
      {
        aColumnNames[i - 1] = aRSMD.getColumnName (i).intern ();
        aColumnTypes[i - 1] = aRSMD.getColumnType (i);
      }

      // create object once for all rows
      final DBResultRow aRow = new DBResultRow (nCols);

      // for all result set elements
      while (aRS.next ())
      {
        // fill map
        aRow.clear ();
        for (int i = 1; i <= nCols; ++i)
        {
          final Object aColumnValue = aRS.getObject (i);
          aRow.add (new DBResultField (aColumnNames[i - 1], aColumnTypes[i - 1], aColumnValue));
        }

        // add result object
        aCallback.run (aRow);
      }
    }
    finally
    {
      aRS.close ();
    }
  }

  @Nonnull
  public ESuccess queryAll (@Nonnull @Nonempty final String sSQL,
                            @Nonnull final IResultSetRowCallback aResultItemCallback)
  {
    return withStatementDo (new IWithStatementCallback ()
    {
      public void run (@Nonnull final Statement aStatement) throws SQLException
      {
        final ResultSet aResultSet = aStatement.executeQuery (sSQL);
        iterateResultSet (aResultSet, aResultItemCallback);
      }
    }, null);
  }

  @Nonnull
  public ESuccess queryAll (@Nonnull final String sSQL,
                            @Nonnull final IPreparedStatementDataProvider aPSDP,
                            @Nonnull final IResultSetRowCallback aResultItemCallback)
  {
    return withPreparedStatementDo (sSQL, aPSDP, new IWithPreparedStatementCallback ()
    {
      public void run (@Nonnull final PreparedStatement aPreparedStatement) throws SQLException
      {
        final ResultSet aResultSet = aPreparedStatement.executeQuery ();
        iterateResultSet (aResultSet, aResultItemCallback);
      }
    }, null, null);
  }

  @Nullable
  public List <DBResultRow> queryAll (@Nonnull @Nonempty final String sSQL)
  {
    final List <DBResultRow> aAllResultRows = new ArrayList <DBResultRow> ();
    return queryAll (sSQL, new IResultSetRowCallback ()
    {
      public void run (@Nullable final DBResultRow aCurrentObject)
      {
        if (aCurrentObject != null)
        {
          // We need to clone the object!
          aAllResultRows.add (aCurrentObject.getClone ());
        }
      }
    }).isFailure () ? null : aAllResultRows;
  }

  @Nullable
  public List <DBResultRow> queryAll (@Nonnull @Nonempty final String sSQL,
                                      @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    final List <DBResultRow> aAllResultRows = new ArrayList <DBResultRow> ();
    return queryAll (sSQL, aPSDP, new IResultSetRowCallback ()
    {
      public void run (@Nullable final DBResultRow aCurrentObject)
      {
        if (aCurrentObject != null)
        {
          // We need to clone the object!
          aAllResultRows.add (aCurrentObject.getClone ());
        }
      }
    }).isFailure () ? null : aAllResultRows;
  }

  @Nullable
  public DBResultRow querySingle (@Nonnull @Nonempty final String sSQL)
  {
    final List <DBResultRow> aAllResultRows = queryAll (sSQL);
    if (aAllResultRows == null)
      return null;
    if (aAllResultRows.size () > 1)
      throw new IllegalStateException ("Found more than 1 result row!");
    return ContainerHelper.getFirstElement (aAllResultRows);
  }

  @Nullable
  public DBResultRow querySingle (@Nonnull @Nonempty final String sSQL,
                                  @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    final List <DBResultRow> aAllResultRows = queryAll (sSQL, aPSDP);
    if (aAllResultRows == null)
      return null;
    if (aAllResultRows.size () > 1)
      throw new IllegalStateException ("Found more than 1 result row!");
    return ContainerHelper.getFirstElement (aAllResultRows);
  }

  @CheckForSigned
  public int queryCount (@Nonnull final String sSQL)
  {
    final DBResultRow aResult = querySingle (sSQL);
    return aResult == null ? CGlobal.ILLEGAL_UINT : ((Number) aResult.getValue (0)).intValue ();
  }

  @CheckForSigned
  public int queryCount (@Nonnull final String sSQL, @Nonnull final IPreparedStatementDataProvider aPSDP)
  {
    final DBResultRow aResult = querySingle (sSQL, aPSDP);
    return aResult == null ? CGlobal.ILLEGAL_UINT : ((Number) aResult.getValue (0)).intValue ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("connectionProvider", m_aConnectionProvider)
                                       .append ("exceptionHandler", m_aExceptionHdl)
                                       .toString ();
  }
}
