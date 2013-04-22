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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.db.jpa.AbstractJPAEnabledManager;

/**
 * Special H2 version of {@link AbstractJPAEnabledManager}
 * 
 * @author Philip Helger
 */
@ThreadSafe
public abstract class AbstractJPAEnabledManagerH2 extends AbstractJPAEnabledManager
{
  public static enum ELockMode
  {
    READ_COMMITTED (3),
    SERIALIZABLE (1),
    READ_UNCOMMITED (0);

    /** Default lock mode: read committed */
    public static final ELockMode DEFAULT = READ_COMMITTED;

    private int m_nValue;

    private ELockMode (final int i)
    {
      m_nValue = i;
    }

    public int getValue ()
    {
      return m_nValue;
    }
  }

  public static enum ELog
  {
    DISABLE (0),
    LOG (1),
    LOG_AND_SYNC (2);

    /** Default log mode: log and sync */
    public static final ELog DEFAULT = LOG_AND_SYNC;

    private int m_nValue;

    private ELog (final int i)
    {
      m_nValue = i;
    }

    public int getValue ()
    {
      return m_nValue;
    }
  }

  static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJPAEnabledManager.class);

  protected AbstractJPAEnabledManagerH2 ()
  {}

  protected final boolean isTableExisting (@Nonnull final String sTableName)
  {
    return getSelectCountResultObj (getEntityManager ().createQuery ("SELECT count(ID) FROM TABLES t WHERE t.TABLE_TYPE = 'TABLE' AND TABLE_NAME = :tablename",
                                                                     Integer.class)
                                                       .setParameter ("tablename", sTableName)).intValue () > 0;
  }

  private void _executeH2Native (@Nonnull @Nonempty final String sNativeSQL)
  {
    doInTransaction (new Runnable ()
    {
      public void run ()
      {
        s_aLogger.info ("Running H2 native command: " + sNativeSQL);
        getEntityManager ().createNativeQuery (sNativeSQL).executeUpdate ();
      }
    });
  }

  public final void doH2Analyze ()
  {
    _executeH2Native ("ANALYZE");
  }

  public final void setH2LockMode (@Nonnull final ELockMode eLockMode)
  {
    _executeH2Native ("SET LOCK_MODE=" + eLockMode.getValue ());
  }

  public final void setH2Log (@Nonnull final ELog eLog)
  {
    _executeH2Native ("SET LOG=" + eLog.getValue ());
  }

  public final void setH2UndoLog (final boolean bEnabled)
  {
    _executeH2Native ("SET UNDO_LOG=" + (bEnabled ? "1" : "0"));
  }
}
