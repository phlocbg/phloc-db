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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.Time;
import java.sql.Timestamp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.typeconvert.TypeConverter;
import com.phloc.db.jdbc.JDBCUtils;

/**
 * Represents a single DB query result value within a result row.
 * 
 * @author Philip Helger
 */
@Nonnull
public final class DBResultField
{
  private final String m_sColumnName;
  private final int m_nColumnType;
  private final Object m_aValue;

  public DBResultField (@Nonnull @Nonempty final String sColumnName,
                        final int nColumnType,
                        @Nullable final Object aValue)
  {
    if (StringHelper.hasNoText (sColumnName))
      throw new IllegalArgumentException ("columnName");
    m_sColumnName = sColumnName;
    m_nColumnType = nColumnType;
    m_aValue = aValue;
  }

  @Nonnull
  @Nonempty
  public String getColumnName ()
  {
    return m_sColumnName;
  }

  /**
   * @return The column type as defined in {@link java.sql.Types}.
   */
  public int getColumnType ()
  {
    return m_nColumnType;
  }

  @Nullable
  public String getColumnTypeName ()
  {
    return JDBCUtils.getJDBCTypeName (m_nColumnType);
  }

  /**
   * @return The generic value as retrieved from the DB
   */
  @Nullable
  public Object getValue ()
  {
    return m_aValue;
  }

  @Nullable
  public String getAsString ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, String.class);
  }

  @Nullable
  public BigDecimal getAsBigDecimal ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, BigDecimal.class);
  }

  @Nullable
  public BigInteger getAsBigInteger ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, BigInteger.class);
  }

  public boolean getAsBoolean ()
  {
    return TypeConverter.convertToBoolean (m_aValue);
  }

  public boolean getAsBoolean (final boolean bDefault)
  {
    return m_aValue == null ? bDefault : getAsBoolean ();
  }

  public byte getAsByte ()
  {
    return TypeConverter.convertToByte (m_aValue);
  }

  public byte getAsByte (final byte nDefault)
  {
    return m_aValue == null ? nDefault : getAsByte ();
  }

  public char getAsChar ()
  {
    return TypeConverter.convertToChar (m_aValue);
  }

  public char getAsChar (final char cDefault)
  {
    return m_aValue == null ? cDefault : getAsChar ();
  }

  public double getAsDouble ()
  {
    return TypeConverter.convertToDouble (m_aValue);
  }

  public double getAsDouble (final double dDefault)
  {
    return m_aValue == null ? dDefault : getAsDouble ();
  }

  public float getAsFloat ()
  {
    return TypeConverter.convertToFloat (m_aValue);
  }

  public float getAsFloat (final float fDefault)
  {
    return m_aValue == null ? fDefault : getAsFloat ();
  }

  public int getAsInt ()
  {
    return TypeConverter.convertToInt (m_aValue);
  }

  public int getAsInt (final int nDefault)
  {
    return m_aValue == null ? nDefault : getAsInt ();
  }

  public long getAsLong ()
  {
    return TypeConverter.convertToLong (m_aValue);
  }

  public long getAsLong (final long nDefault)
  {
    return m_aValue == null ? nDefault : getAsLong ();
  }

  public short getAsShort ()
  {
    return TypeConverter.convertToShort (m_aValue);
  }

  public short getAsShort (final short nDefault)
  {
    return m_aValue == null ? nDefault : getAsShort ();
  }

  @Nullable
  public Boolean getAsBooleanObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Boolean.class);
  }

  @Nullable
  public Byte getAsByteObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Byte.class);
  }

  @Nullable
  public Character getAsCharObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Character.class);
  }

  @Nullable
  public Double getAsDoubleObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Double.class);
  }

  @Nullable
  public Float getAsFloatObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Float.class);
  }

  @Nullable
  public Integer getAsIntObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Integer.class);
  }

  @Nullable
  public Long getAsLongObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Long.class);
  }

  @Nullable
  public Short getAsShortObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Short.class);
  }

  @Nullable
  public Blob getAsBlob ()
  {
    return (Blob) m_aValue;
  }

  @Nullable
  public Clob getAsClob ()
  {
    return (Clob) m_aValue;
  }

  @Nullable
  public Date getAsDate ()
  {
    return (Date) m_aValue;
  }

  @Nullable
  public NClob getAsNClob ()
  {
    return (NClob) m_aValue;
  }

  @Nullable
  public RowId getAsRowId ()
  {
    return (RowId) m_aValue;
  }

  @Nullable
  public Time getAsTime ()
  {
    return (Time) m_aValue;
  }

  @Nullable
  public Timestamp getAsTimestamp ()
  {
    return (Timestamp) m_aValue;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("columnName", m_sColumnName)
                                       .append ("columnType", m_nColumnType)
                                       .append ("value", m_aValue)
                                       .toString ();
  }
}