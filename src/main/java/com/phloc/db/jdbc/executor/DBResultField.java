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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.commons.string.ToStringGenerator;
import com.phloc.commons.typeconvert.TypeConverter;

/**
 * Represents a single DB query result value within a result row.
 * 
 * @author philip
 */
@Nonnull
public final class DBResultField
{
  private final String m_sColumnName;
  private final Object m_aValue;

  public DBResultField (@Nonnull @Nonempty final String sColumnName, @Nullable final Object aValue)
  {
    if (StringHelper.hasNoText (sColumnName))
      throw new IllegalArgumentException ("columnName");
    m_sColumnName = sColumnName;
    m_aValue = aValue;
  }

  @Nonnull
  @Nonempty
  public String getColumnName ()
  {
    return m_sColumnName;
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

  public boolean getAsBoolean ()
  {
    return TypeConverter.convertToBoolean (m_aValue);
  }

  public byte getAsByte ()
  {
    return TypeConverter.convertToByte (m_aValue);
  }

  public char getAsChar ()
  {
    return TypeConverter.convertToChar (m_aValue);
  }

  public double getAsDouble ()
  {
    return TypeConverter.convertToDouble (m_aValue);
  }

  public float getAsFloat ()
  {
    return TypeConverter.convertToFloat (m_aValue);
  }

  public int getAsInt ()
  {
    return TypeConverter.convertToInt (m_aValue);
  }

  public long getAsLong ()
  {
    return TypeConverter.convertToLong (m_aValue);
  }

  public short getAsShort ()
  {
    return TypeConverter.convertToShort (m_aValue);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("columnName", m_sColumnName).append ("value", m_aValue).toString ();
  }
}
