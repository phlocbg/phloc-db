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
package com.phloc.db.jpa;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.callback.IExceptionHandler;
import com.phloc.commons.callback.IThrowingCallableWithParameter;
import com.phloc.commons.callback.IThrowingRunnableWithParameter;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.state.impl.SuccessWithValue;
import com.phloc.commons.stats.IStatisticsHandlerCounter;
import com.phloc.commons.stats.IStatisticsHandlerTimer;
import com.phloc.commons.stats.StatisticsManager;
import com.phloc.commons.timing.StopWatch;

/**
 * Abstract base class for entity managers. Provides the
 * {@link #doInTransaction(IThrowingRunnableWithParameter)} method and other
 * sanity methods. Implementing classes must override the
 * <code>protected abstract EntityManager getEntityManager ()</code> method that
 * is usually retrieved from a subclass of
 * {@link com.phloc.db.jpa.AbstractJPASingleton}.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractJPAEnabledManager
{
  /** By default the entity manager is locked */
  public static final boolean DEFAULT_LOCK_ENTITY_MGR = true;
  /** By default nested transactions are not allowed */
  public static final boolean DEFAULT_ALLOW_NESTED_TRANSACTIONS = false;
  /** By default no transaction is used for select statements */
  public static final boolean DEFAULT_USE_TRANSACTIONS_FOR_SELECT = false;

  private static final int DEFAULT_EXECUTION_WARN_TIME_MS = 1000;
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJPAEnabledManager.class);
  private static final IStatisticsHandlerCounter s_aStatsCounterTransactions = StatisticsManager.getCounterHandler (AbstractJPAEnabledManager.class.getName () +
                                                                                                                    "$transactions");
  private static final IStatisticsHandlerCounter s_aStatsCounterRollback = StatisticsManager.getCounterHandler (AbstractJPAEnabledManager.class.getName () +
                                                                                                                "$rollback");
  private static final IStatisticsHandlerCounter s_aStatsCounterSuccess = StatisticsManager.getCounterHandler (AbstractJPAEnabledManager.class.getName () +
                                                                                                               "$success");
  private static final IStatisticsHandlerCounter s_aStatsCounterError = StatisticsManager.getCounterHandler (AbstractJPAEnabledManager.class.getName () +
                                                                                                             "$error");
  private static final IStatisticsHandlerTimer s_aStatsTimerExecutionSuccess = StatisticsManager.getTimerHandler (AbstractJPAEnabledManager.class.getName () +
                                                                                                                  "$execSuccess");
  private static final IStatisticsHandlerTimer s_aStatsTimerExecutionError = StatisticsManager.getTimerHandler (AbstractJPAEnabledManager.class.getName () +
                                                                                                                "$execError");

  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();
  private static IExceptionHandler <Throwable> s_aExceptionHandler;
  private static final AtomicInteger s_aExecutionWarnTime = new AtomicInteger (DEFAULT_EXECUTION_WARN_TIME_MS);
  private static IJPAExecutionTimeExceededHandler s_aExecutionTimeExceededHandler = new LoggingJPAExecutionTimeExceededHandler (true);

  private final AtomicBoolean m_aSyncEntityMgr = new AtomicBoolean (DEFAULT_LOCK_ENTITY_MGR);
  private final AtomicBoolean m_aAllowNestedTransactions = new AtomicBoolean (DEFAULT_ALLOW_NESTED_TRANSACTIONS);
  private final AtomicBoolean m_aUseTransactionsForSelect = new AtomicBoolean (DEFAULT_USE_TRANSACTIONS_FOR_SELECT);

  protected AbstractJPAEnabledManager ()
  {}

  public final boolean isSyncEntityMgr ()
  {
    return m_aSyncEntityMgr.get ();
  }

  /**
   * Set whether the entity manager should be synchronized upon each access
   * 
   * @param bSyncEntityMgr
   *        <code>true</code> to enable sync, <code>false</code> to disable sync
   */
  public final void setSyncEntityMgr (final boolean bSyncEntityMgr)
  {
    m_aSyncEntityMgr.set (bSyncEntityMgr);
  }

  public final boolean isAllowNestedTransactions ()
  {
    return m_aAllowNestedTransactions.get ();
  }

  /**
   * Allow nested transaction
   * 
   * @param bAllowNestedTransactions
   *        <code>true</code> to enable nested transaction
   */
  public final void setAllowNestedTransactions (final boolean bAllowNestedTransactions)
  {
    m_aAllowNestedTransactions.set (bAllowNestedTransactions);
  }

  /**
   * @return <code>true</code> if transactions should be used for selecting,
   *         <code>false</code> if this can be done without transactions
   */
  public final boolean isUseTransactionsForSelect ()
  {
    return m_aUseTransactionsForSelect.get ();
  }

  /**
   * Use transactions for select statements?
   * 
   * @param bUseTransactionsForSelect
   *        <code>true</code> to enable the usage of transactions for select
   *        statements.
   */
  public final void setUseTransactionsForSelect (final boolean bUseTransactionsForSelect)
  {
    m_aAllowNestedTransactions.set (bUseTransactionsForSelect);
  }

  /**
   * @return An entity manager to be used.
   */
  @Nonnull
  protected abstract EntityManager createEntityManager ();

  /**
   * Set a custom exception handler that is called in case performing some
   * operation fails.
   * 
   * @param aExceptionHandler
   *        The exception handler to be set. May be <code>null</code> to
   *        indicate no custom exception handler.
   */
  public static final void setCustomExceptionHandler (@Nullable final IExceptionHandler <Throwable> aExceptionHandler)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aExceptionHandler = aExceptionHandler;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Get the custom exception handler.
   * 
   * @return <code>null</code> if non is set
   */
  @Nullable
  public static final IExceptionHandler <Throwable> getCustomExceptionHandler ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aExceptionHandler;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * @return The milliseconds after which a warning is emitted, if an SQL
   *         statement takes longer to execute.
   */
  @Nonnegative
  public static final int getDefaultExecutionWarnTime ()
  {
    return s_aExecutionWarnTime.get ();
  }

  /**
   * Set the milli seconds duration on which a warning should be emitted, if a
   * single SQL execution too at least that long.
   * 
   * @param nMillis
   *        The number of milli seconds. Must be &ge; 0.
   */
  public static final void setDefaultExecutionWarnTime (final int nMillis)
  {
    if (nMillis < 0)
      throw new IllegalArgumentException ("Milliseconds may not be negative: " + nMillis);
    s_aExecutionWarnTime.set (nMillis);
  }

  public static final void setExecutionTimeExceededHandler (@Nullable final IJPAExecutionTimeExceededHandler aExecutionTimeExceededHandler)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aExecutionTimeExceededHandler = aExecutionTimeExceededHandler;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  /**
   * Get the custom exception handler.
   * 
   * @return <code>null</code> if non is set
   */
  @Nullable
  public static final IJPAExecutionTimeExceededHandler getExecutionTimeExceededHandler ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aExecutionTimeExceededHandler;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static final void onExecutionTimeExceeded (@Nonnull final String sMsg, @Nonnegative final long nExecutionMillis)
  {
    final IJPAExecutionTimeExceededHandler aHdl = getExecutionTimeExceededHandler ();
    if (aHdl != null)
      try
      {
        // Invoke the handler
        aHdl.onExecutionTimeExceeded (sMsg, nExecutionMillis);
      }
      catch (final Throwable t)
      {
        s_aLogger.error ("Failed to invoke exceution time exceeded handler " + aHdl, t);
      }
  }

  @Nonnull
  public static final ESuccess doInTransaction (@Nonnull @WillNotClose final EntityManager aEntityMgr,
                                                @Nonnull final IThrowingRunnableWithParameter <EntityManager> aRunnable)
  {
    return doInTransaction (aEntityMgr, DEFAULT_ALLOW_NESTED_TRANSACTIONS, aRunnable);
  }

  @Nonnull
  public static final ESuccess doInTransaction (@Nonnull @WillNotClose final EntityManager aEntityMgr,
                                                final boolean bAllowNestedTransactions,
                                                @Nonnull final IThrowingRunnableWithParameter <EntityManager> aRunnable)
  {
    final StopWatch aSWOverall = new StopWatch (true);
    final EntityTransaction aTransaction = aEntityMgr.getTransaction ();
    final boolean bTransactionRequired = !bAllowNestedTransactions || !aTransaction.isActive ();
    if (bTransactionRequired)
    {
      s_aStatsCounterTransactions.increment ();
      aTransaction.begin ();
    }
    final StopWatch aSWCallback = new StopWatch (true);
    try
    {
      // Execute whatever you want to do
      aRunnable.run (aEntityMgr);
      // And if no exception was thrown, commit it
      if (bTransactionRequired)
        aTransaction.commit ();
      s_aStatsCounterSuccess.increment ();
      s_aStatsTimerExecutionSuccess.addTime (aSWCallback.stopAndGetMillis ());
      return ESuccess.SUCCESS;
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to perform something in a transaction for callback " + aRunnable, t);
      s_aStatsCounterError.increment ();
      s_aStatsTimerExecutionError.addTime (aSWCallback.stopAndGetMillis ());
      final IExceptionHandler <Throwable> aExceptionHandler = getCustomExceptionHandler ();
      if (aExceptionHandler != null)
        try
        {
          aExceptionHandler.onException (t);
        }
        catch (final Throwable t2)
        {
          s_aLogger.error ("Error in custom exception handler " + aExceptionHandler, t2);
        }
    }
    finally
    {
      if (bTransactionRequired)
        if (aTransaction.isActive ())
        {
          // We got an exception -> rollback
          aTransaction.rollback ();
          s_aLogger.warn ("Rolled back transaction for callback " + aRunnable);
          s_aStatsCounterRollback.increment ();
        }

      aSWOverall.stop ();
      if (aSWCallback.getMillis () > getDefaultExecutionWarnTime ())
        onExecutionTimeExceeded ("Callback: " +
                                 aSWCallback.getMillis () +
                                 "; overall: " +
                                 aSWOverall.getMillis () +
                                 "; transaction: " +
                                 bTransactionRequired +
                                 "; Execution of callback in transaction took too long: " +
                                 aRunnable.toString (), aSWCallback.getMillis ());
    }
    return ESuccess.FAILURE;
  }

  @Nonnull
  public final ESuccess doInTransaction (@Nonnull final Runnable aRunnable)
  {
    return doInTransaction (new AdapterRunnableToRunnableWithParam <EntityManager> (aRunnable));
  }

  @Nonnull
  public final ESuccess doInTransaction (@Nonnull final IThrowingRunnableWithParameter <EntityManager> aRunnable)
  {
    // Create entity manager
    final EntityManager aEntityMgr = createEntityManager ();
    try
    {
      if (!isSyncEntityMgr ())
      {
        // No synchronization required
        return doInTransaction (aEntityMgr, isAllowNestedTransactions (), aRunnable);
      }

      // Sync on the whole entity manager, to have a cross-manager
      // synchronization!
      synchronized (aEntityMgr)
      {
        return doInTransaction (aEntityMgr, isAllowNestedTransactions (), aRunnable);
      }
    }
    finally
    {
      aEntityMgr.close ();
    }
  }

  @Nonnull
  public static final <T> SuccessWithValue <T> doInTransaction (@Nonnull @WillNotClose final EntityManager aEntityMgr,
                                                                @Nonnull final IThrowingCallableWithParameter <T, EntityManager> aCallable)
  {
    return doInTransaction (aEntityMgr, DEFAULT_ALLOW_NESTED_TRANSACTIONS, aCallable);
  }

  @Nonnull
  public static final <T> SuccessWithValue <T> doInTransaction (@Nonnull @WillNotClose final EntityManager aEntityMgr,
                                                                final boolean bAllowNestedTransactions,
                                                                @Nonnull final IThrowingCallableWithParameter <T, EntityManager> aCallable)
  {
    final EntityTransaction aTransaction = aEntityMgr.getTransaction ();
    final boolean bTransactionRequired = !bAllowNestedTransactions || !aTransaction.isActive ();
    if (bTransactionRequired)
    {
      s_aStatsCounterTransactions.increment ();
      aTransaction.begin ();
    }
    final StopWatch aSW = new StopWatch (true);
    try
    {
      // Execute whatever you want to do
      final T ret = aCallable.call (aEntityMgr);
      // And if no exception was thrown, commit it
      if (bTransactionRequired)
        aTransaction.commit ();
      s_aStatsCounterSuccess.increment ();
      s_aStatsTimerExecutionSuccess.addTime (aSW.stopAndGetMillis ());
      return SuccessWithValue.createSuccess (ret);
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to perform something in a transaction!", t);
      s_aStatsCounterError.increment ();
      s_aStatsTimerExecutionError.addTime (aSW.stopAndGetMillis ());
      final IExceptionHandler <Throwable> aExceptionHandler = getCustomExceptionHandler ();
      if (aExceptionHandler != null)
        try
        {
          aExceptionHandler.onException (t);
        }
        catch (final Throwable t2)
        {
          s_aLogger.error ("Error in custom exception handler", t2);
        }
      return SuccessWithValue.<T> createFailure (null);
    }
    finally
    {
      if (bTransactionRequired)
        if (aTransaction.isActive ())
        {
          aTransaction.rollback ();
          s_aLogger.warn ("Rolled back transaction!");
          s_aStatsCounterRollback.increment ();
        }

      if (aSW.getMillis () > getDefaultExecutionWarnTime ())
        onExecutionTimeExceeded ("Execution of something in transaction took too long: " + aCallable.toString (),
                                 aSW.getMillis ());
    }
  }

  @Nonnull
  public final <T> SuccessWithValue <T> doInTransaction (@Nonnull final Callable <T> aCallable)
  {
    return doInTransaction (new AdapterCallableToCallableWithParam <T, EntityManager> (aCallable));
  }

  @Nonnull
  public final <T> SuccessWithValue <T> doInTransaction (@Nonnull final IThrowingCallableWithParameter <T, EntityManager> aCallable)
  {
    // Create entity manager
    final EntityManager aEntityMgr = createEntityManager ();
    try
    {
      if (!isSyncEntityMgr ())
      {
        // No synchronization required
        return doInTransaction (aEntityMgr, isAllowNestedTransactions (), aCallable);
      }

      // Sync on the whole entity manager, to have a cross-manager
      // synchronization!
      synchronized (aEntityMgr)
      {
        return doInTransaction (aEntityMgr, isAllowNestedTransactions (), aCallable);
      }
    }
    finally
    {
      // and close it
      aEntityMgr.close ();
    }
  }

  /**
   * Perform a select, without a transaction
   * 
   * @param aCallable
   *        The callable
   * @return The return of the callable or <code>null</code> upon success
   */
  @Nonnull
  public static final <T> SuccessWithValue <T> doSelectStatic (@Nonnull final Callable <T> aCallable)
  {
    if (aCallable == null)
      throw new NullPointerException ("callable");

    final StopWatch aSW = new StopWatch (true);
    try
    {
      final T ret = aCallable.call ();
      s_aStatsCounterSuccess.increment ();
      s_aStatsTimerExecutionSuccess.addTime (aSW.stopAndGetMillis ());
      return SuccessWithValue.createSuccess (ret);
    }
    catch (final Throwable t)
    {
      s_aLogger.error ("Failed to select something in a transaction!", t);
      s_aStatsCounterError.increment ();
      s_aStatsTimerExecutionError.addTime (aSW.stopAndGetMillis ());
      final IExceptionHandler <Throwable> aExceptionHandler = getCustomExceptionHandler ();
      if (aExceptionHandler != null)
        try
        {
          aExceptionHandler.onException (t);
        }
        catch (final Throwable t2)
        {
          s_aLogger.error ("Error in custom exception handler", t2);
        }
      return SuccessWithValue.<T> createFailure (null);
    }
    finally
    {
      if (aSW.getMillis () > getDefaultExecutionWarnTime ())
        onExecutionTimeExceeded ("Execution of select took too long: " + aCallable.toString (), aSW.getMillis ());
    }
  }

  /**
   * Run a read-only query. By default no transaction is used, and the entity
   * manager is synchronized.
   * 
   * @param aCallable
   *        The callable to execute.
   * @return A non-<code>null</code> result of the select.
   */
  @Nonnull
  public final <T> SuccessWithValue <T> doSelect (@Nonnull final Callable <T> aCallable)
  {
    if (isUseTransactionsForSelect ())
    {
      // Use transactions for select statement!
      return doInTransaction (new AdapterCallableToCallableWithParam <T, EntityManager> (aCallable));
    }

    // Ensure that only one transaction is active for all users!
    final EntityManager aEntityMgr = createEntityManager ();
    try
    {
      if (!isSyncEntityMgr ())
      {
        // No synchronization required
        return doSelectStatic (aCallable);
      }

      // Sync on the whole entity manager, to have a cross-manager
      // synchronization!
      synchronized (aEntityMgr)
      {
        return doSelectStatic (aCallable);
      }
    }
    finally
    {
      aEntityMgr.close ();
    }
  }

  /**
   * Helper method to handle the execution of "SELECT COUNT(...) ..." SQL
   * statements
   * 
   * @param aQuery
   *        The SELECT COUNT query
   * @return a non-negative row count
   */
  @Nonnull
  public static final Number getSelectCountResultObj (@Nonnull final Query aQuery)
  {
    final Number ret = (Number) aQuery.getSingleResult ();
    return ret != null ? ret : Integer.valueOf (0);
  }

  /**
   * Helper method to handle the execution of "SELECT COUNT(...) ..." SQL
   * statements
   * 
   * @param aQuery
   *        The SELECT COUNT query
   * @return a non-negative row count
   */
  @Nonnegative
  public static final long getSelectCountResult (@Nonnull final Query aQuery)
  {
    return getSelectCountResultObj (aQuery).longValue ();
  }
}
