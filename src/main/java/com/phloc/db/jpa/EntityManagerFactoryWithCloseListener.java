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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.EntityManagerFactory;

public class EntityManagerFactoryWithCloseListener extends EntityManagerFactoryProxy implements IEntityManagerCloseListener
{
  private static final ThreadLocal <EntityManagerWithCloseListener> s_aTL = new ThreadLocal <EntityManagerWithCloseListener> ();

  protected EntityManagerFactoryWithCloseListener (@Nonnull final EntityManagerFactory aEntityMgrFactory)
  {
    super (aEntityMgrFactory);
  }

  @Override
  public EntityManagerWithCloseListener createEntityManager ()
  {
    return createEntityManager (null);
  }

  @Override
  public EntityManagerWithCloseListener createEntityManager (@SuppressWarnings ("rawtypes") @Nullable final Map aProperties)
  {
    EntityManagerWithCloseListener aEntityMgr = s_aTL.get ();
    if (aEntityMgr == null)
    {
      aEntityMgr = new EntityManagerWithCloseListener (super.createEntityManager (aProperties));
      s_aTL.set (aEntityMgr);
      // Set special listener, so that the ThreadLocal is cleared after close
      aEntityMgr.setCloseListener (this);
    }
    return aEntityMgr;
  }

  @OverridingMethodsMustInvokeSuper
  public void onAfterEntityManagerClosed ()
  {
    s_aTL.remove ();
  }
}
