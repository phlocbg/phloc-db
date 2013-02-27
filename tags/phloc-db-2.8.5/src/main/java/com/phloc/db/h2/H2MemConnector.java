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
package com.phloc.db.h2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;

public class H2MemConnector extends AbstractH2Connector
{
  protected final String m_sDBName;
  protected final String m_sUser;
  protected final String m_sPassword;

  public H2MemConnector (@Nullable final String sUser, @Nullable final String sPassword)
  {
    this ("h2memdb", sUser, sPassword);
  }

  public H2MemConnector (@Nonnull @Nonempty final String sDBName,
                         @Nullable final String sUser,
                         @Nullable final String sPassword)
  {
    if (StringHelper.hasNoText (sDBName))
      throw new IllegalArgumentException ("DB name is empty");
    if (false)
    {
      if (sUser == null)
        throw new NullPointerException ("user");
      if (sPassword == null)
        throw new NullPointerException ("password");
    }
    m_sDBName = sDBName;
    m_sUser = sUser;
    m_sPassword = sPassword;
  }

  @Override
  @Nonnull
  protected String getUserName ()
  {
    return m_sUser;
  }

  @Override
  @Nonnull
  protected String getPassword ()
  {
    return m_sPassword;
  }

  @Override
  @Nonnull
  protected String getDatabase ()
  {
    return "mem:" + m_sDBName;
  }
}
