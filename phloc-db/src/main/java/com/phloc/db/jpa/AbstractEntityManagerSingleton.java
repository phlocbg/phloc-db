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

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.singleton.RequestSingleton;

/**
 * Abstract request singleton to handle a single {@link EntityManager}.
 * 
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractEntityManagerSingleton extends RequestSingleton implements IEntityManagerProvider
{
  protected final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  private volatile EntityManager m_aEntityManager;

  @Deprecated
  @UsedViaReflection
  public AbstractEntityManagerSingleton ()
  {}

  @Nonnull
  protected abstract EntityManager createEntityManager ();

  @Nonnull
  public EntityManager getEntityManager ()
  {
    EntityManager ret;
    m_aRWLock.readLock ().lock ();
    try
    {
      ret = m_aEntityManager;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }

    if (ret == null)
    {
      m_aRWLock.writeLock ().lock ();
      try
      {
        // Try again in write lock
        ret = m_aEntityManager;
        if (ret == null)
        {
          ret = createEntityManager ();
          m_aEntityManager = ret;
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
        aEM.clear ();
        m_aEntityManager = null;
      }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }
}
