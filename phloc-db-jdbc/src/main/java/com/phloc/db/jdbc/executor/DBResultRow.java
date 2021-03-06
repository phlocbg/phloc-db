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
package com.phloc.db.jdbc.executor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.ICloneable;
import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents a single DB query result row.
 * 
 * @author Philip Helger
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

  void add (@Nonnull final DBResultField aResultField)
  {
    if (aResultField == null)
      throw new NullPointerException ("ResultField");

    m_aCols[m_nIndex++] = aResultField;
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

  public int getColumnType (@Nonnegative final int nIndex)
  {
    return get (nIndex).getColumnType ();
  }

  @Nullable
  public String getColumnTypeName (@Nonnegative final int nIndex)
  {
    return get (nIndex).getColumnTypeName ();
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

  @Nullable
  public String getAsString (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsString ();
  }

  @Nullable
  public BigDecimal getAsBigDecimal (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsBigDecimal ();
  }

  @Nullable
  public BigInteger getAsBigInteger (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsBigInteger ();
  }

  public boolean getAsBoolean (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsBoolean ();
  }

  public byte getAsByte (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsByte ();
  }

  public byte getAsByte (@Nonnegative final int nIndex, final byte nDefault)
  {
    return get (nIndex).getAsByte (nDefault);
  }

  public char getAsChar (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsChar ();
  }

  public char getAsChar (@Nonnegative final int nIndex, final char cDefault)
  {
    return get (nIndex).getAsChar (cDefault);
  }

  public double getAsDouble (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsDouble ();
  }

  public double getAsDouble (@Nonnegative final int nIndex, final double dDefault)
  {
    return get (nIndex).getAsDouble (dDefault);
  }

  public float getAsFloat (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsFloat ();
  }

  public float getAsFloat (@Nonnegative final int nIndex, final float fDefault)
  {
    return get (nIndex).getAsFloat (fDefault);
  }

  public int getAsInt (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsInt ();
  }

  public int getAsInt (@Nonnegative final int nIndex, final int nDefault)
  {
    return get (nIndex).getAsInt (nDefault);
  }

  public long getAsLong (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsLong ();
  }

  public long getAsLong (@Nonnegative final int nIndex, final long nDefault)
  {
    return get (nIndex).getAsLong (nDefault);
  }

  public short getAsShort (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsShort ();
  }

  public short getAsShort (@Nonnegative final int nIndex, final short nDefault)
  {
    return get (nIndex).getAsShort (nDefault);
  }

  @Nullable
  public Boolean getAsBooleanObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsBooleanObj ();
  }

  @Nullable
  public Byte getAsByteObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsByteObj ();
  }

  @Nullable
  public Character getAsCharObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsCharObj ();
  }

  @Nullable
  public Double getAsDoubleObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsDoubleObj ();
  }

  @Nullable
  public Float getAsFloatObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsFloatObj ();
  }

  @Nullable
  public Integer getAsIntObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsIntObj ();
  }

  @Nullable
  public Long getAsLongObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsLongObj ();
  }

  @Nullable
  public Short getAsShortObj (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsShortObj ();
  }

  @Nullable
  public Blob getAsBlob (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsBlob ();
  }

  @Nullable
  public Clob getAsClob (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsClob ();
  }

  @Nullable
  public Date getAsDate (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsDate ();
  }

  @Nullable
  public NClob getAsNClob (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsNClob ();
  }

  @Nullable
  public RowId getAsRowId (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsRowId ();
  }

  @Nullable
  public Time getAsTime (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsTime ();
  }

  @Nullable
  public Timestamp getAsTimestamp (@Nonnegative final int nIndex)
  {
    return get (nIndex).getAsTimestamp ();
  }

  /**
   * @return A map that contains the mapping from column name to the respective
   *         index
   */
  @Nonnull
  @ReturnsMutableCopy
  public Map <String, Integer> getColumnNameToIndexMap ()
  {
    final Map <String, Integer> ret = new HashMap <String, Integer> ();
    for (int i = 0; i < m_aCols.length; ++i)
      ret.put (m_aCols[i].getColumnName (), Integer.valueOf (i));
    return ret;
  }

  @Nonnull
  public DBResultRow getClone ()
  {
    return new DBResultRow (this);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("cols", m_aCols).append ("index", m_nIndex).toString ();
  }
}
