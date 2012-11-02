/**
 * Copyright (C) 2006-2012 phloc systems
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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.ICloneable;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single DB query result row.
 * 
 * @author philip
 */
@Nonnull
public final class DBResultRow implements ICloneable <DBResultRow>
{
  private final DBResultField [] m_aCols;
  private int m_nIndex;

  private DBResultRow (@Nonnull final DBResultRow rhs)
  {
    m_aCols = ArrayHelper.getCopy (rhs.m_aCols);
    m_nIndex = rhs.m_nIndex;
  }

  public DBResultRow (final int nCols)
  {
    m_aCols = new DBResultField [nCols];
    m_nIndex = 0;
  }

  void clear ()
  {
    for (int i = 0; i < m_aCols.length; ++i)
      m_aCols[i] = null;
    m_nIndex = 0;
  }

  void add (@Nonnull final DBResultField aCol)
  {
    m_aCols[m_nIndex++] = aCol;
  }

  @Nonnegative
  public int getUsedColumnIndex ()
  {
    return m_nIndex;
  }

  @Nonnegative
  public int getColumnCount ()
  {
    return m_aCols.length;
  }

  @Nullable
  public DBResultField get (@Nonnegative final int nIndex)
  {
    return m_aCols[nIndex];
  }

  @Nonnull
  @Nonempty
  public String getColumnName (@Nonnegative final int nIndex)
  {
    return get (nIndex).getColumnName ();
  }

  @Nullable
  public Object getValue (@Nonnegative final int nIndex)
  {
    return get (nIndex).getValue ();
  }

  @Nonnull
  public DBResultRow getClone ()
  {
    return new DBResultRow (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("cols", m_aCols).toString ();
  }
}