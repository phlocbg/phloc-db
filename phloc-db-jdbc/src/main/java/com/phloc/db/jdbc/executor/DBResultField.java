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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.typeconvert.TypeConverter;
import com.phloc.commons.typeconvert.TypeConverterException;
import com.phloc.db.api.jdbc.JDBCHelper;

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
    return JDBCHelper.getJDBCTypeName (m_nColumnType);
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
  public String getAsString () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, String.class);
  }

  @Nullable
  public BigDecimal getAsBigDecimal () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, BigDecimal.class);
  }

  @Nullable
  public BigInteger getAsBigInteger () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, BigInteger.class);
  }

  public boolean getAsBoolean () throws TypeConverterException
  {
    return TypeConverter.convertToBoolean (m_aValue);
  }

  public boolean getAsBoolean (final boolean bDefault)
  {
    return m_aValue == null ? bDefault : getAsBoolean ();
  }

  public byte getAsByte () throws TypeConverterException
  {
    return TypeConverter.convertToByte (m_aValue);
  }

  public byte getAsByte (final byte nDefault)
  {
    return m_aValue == null ? nDefault : getAsByte ();
  }

  public char getAsChar () throws TypeConverterException
  {
    return TypeConverter.convertToChar (m_aValue);
  }

  public char getAsChar (final char cDefault)
  {
    return m_aValue == null ? cDefault : getAsChar ();
  }

  public double getAsDouble () throws TypeConverterException
  {
    return TypeConverter.convertToDouble (m_aValue);
  }

  public double getAsDouble (final double dDefault)
  {
    return m_aValue == null ? dDefault : getAsDouble ();
  }

  public float getAsFloat () throws TypeConverterException
  {
    return TypeConverter.convertToFloat (m_aValue);
  }

  public float getAsFloat (final float fDefault)
  {
    return m_aValue == null ? fDefault : getAsFloat ();
  }

  public int getAsInt () throws TypeConverterException
  {
    return TypeConverter.convertToInt (m_aValue);
  }

  public int getAsInt (final int nDefault)
  {
    return m_aValue == null ? nDefault : getAsInt ();
  }

  public long getAsLong () throws TypeConverterException
  {
    return TypeConverter.convertToLong (m_aValue);
  }

  public long getAsLong (final long nDefault)
  {
    return m_aValue == null ? nDefault : getAsLong ();
  }

  public short getAsShort () throws TypeConverterException
  {
    return TypeConverter.convertToShort (m_aValue);
  }

  public short getAsShort (final short nDefault)
  {
    return m_aValue == null ? nDefault : getAsShort ();
  }

  @Nullable
  public Boolean getAsBooleanObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Boolean.class);
  }

  @Nullable
  public Byte getAsByteObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Byte.class);
  }

  @Nullable
  public Character getAsCharObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Character.class);
  }

  @Nullable
  public Double getAsDoubleObj ()
  {
    return TypeConverter.convertIfNecessary (m_aValue, Double.class);
  }

  @Nullable
  public Float getAsFloatObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Float.class);
  }

  @Nullable
  public Integer getAsIntObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Integer.class);
  }

  @Nullable
  public Long getAsLongObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Long.class);
  }

  @Nullable
  public Short getAsShortObj () throws TypeConverterException
  {
    return TypeConverter.convertIfNecessary (m_aValue, Short.class);
  }

  @Nullable
  public Blob getAsBlob () throws ClassCastException
  {
    return (Blob) m_aValue;
  }

  @Nullable
  public Clob getAsClob () throws ClassCastException
  {
    return (Clob) m_aValue;
  }

  @Nullable
  public Date getAsDate () throws ClassCastException
  {
    return (Date) m_aValue;
  }

  @Nullable
  public NClob getAsNClob () throws ClassCastException
  {
    return (NClob) m_aValue;
  }

  @Nullable
  public RowId getAsRowId () throws ClassCastException
  {
    return (RowId) m_aValue;
  }

  @Nullable
  public Time getAsTime () throws ClassCastException
  {
    return (Time) m_aValue;
  }

  @Nullable
  public Timestamp getAsTimestamp () throws ClassCastException
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
