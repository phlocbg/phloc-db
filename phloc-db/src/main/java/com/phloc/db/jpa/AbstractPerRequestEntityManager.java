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

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.IsLocked;
import com.phloc.commons.annotations.IsLocked.ELockType;
import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.singleton.RequestSingleton;

/**
 * Abstract request singleton to handle a single {@link EntityManager}.<br>
 * Note: this class does NOT implement {@link IEntityManagerProvider} by
 * purpose, as this class should not be used as a direct callback parameter,
 * because than only the object of this particular request is used.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractPerRequestEntityManager extends RequestSingleton
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractPerRequestEntityManager.class);

  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private volatile EntityManager m_aEntityManager;
  private boolean m_bDestroyed = false;

  @Deprecated
  @UsedViaReflection
  public AbstractPerRequestEntityManager ()
  {}

  /**
   * Create a new {@link EntityManager} when required.
   * 
   * @return The created {@link EntityManager}. Never <code>null</code>.
   */
  @Nonnull
  @IsLocked (ELockType.WRITE)
  protected abstract EntityManager createEntityManager ();

  /**
   * @return The {@link EntityManager} to be used in this request. If it is the
   *         first request to an {@link EntityManager} in this request is
   *         created via {@link #createEntityManager()}. Never <code>null</code>
   *         .
   */
  @Nonnull
  public EntityManager getEntityManager ()
  {
    EntityManager ret;
    m_aRWLock.readLock ().lock ();
    try
    {
      if (m_bDestroyed)
        throw new IllegalStateException ("This object was already destroyed and should not be re-used!");
      ret = m_aEntityManager;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    if (ret == null)
    {
      // No EntityManager present for this request
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Try again in write lock
        ret = m_aEntityManager;
        if (ret == null)
        {
          ret = createEntityManager ();
          if (ret == null)
            throw new IllegalStateException ("Failed to create EntityManager!");
          m_aEntityManager = ret;

          if (s_aLogger.isDebugEnabled ())
            s_aLogger.debug ("EntityManager created");
        }
      }
      finally
      {
        m_aRWLock.writeLock ().unlock ();
      }
    }
    return ret;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onDestroy ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      // Close EntityManager, if present
      final EntityManager aEM = m_aEntityManager;
      if (aEM != null)
      {
        aEM.close ();
        m_aEntityManager = null;

        if (s_aLogger.isDebugEnabled ())
          s_aLogger.debug ("EntityManager destroyed");
      }
      m_bDestroyed = true;
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}
