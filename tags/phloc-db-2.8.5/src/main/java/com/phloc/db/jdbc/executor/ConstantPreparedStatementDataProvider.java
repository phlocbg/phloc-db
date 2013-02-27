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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ArrayHelper;
import com.phloc.commons.collections.ContainerHelper;

/**
 * A simple implementation of the {@link IPreparedStatementDataProvider} that
 * takes a list of objects and returns theses objects as they are.
 * 
 * @author philip
 */
public final class ConstantPreparedStatementDataProvider implements IPreparedStatementDataProvider
{
  private final List <Object> m_aValues;

  public ConstantPreparedStatementDataProvider ()
  {
    m_aValues = new ArrayList <Object> ();
  }

  public ConstantPreparedStatementDataProvider (@Nonnull final Iterable <?> aValues)
  {
    m_aValues = ContainerHelper.newList (aValues);
  }

  public ConstantPreparedStatementDataProvider (@Nonnull @Nonempty final Object... aValues)
  {
    if (ArrayHelper.isEmpty (aValues))
      throw new IllegalArgumentException ("Passed value array may not be empty");
    m_aValues = ContainerHelper.newList (aValues);
  }

  @Nonnull
  public ConstantPreparedStatementDataProvider addValue (final Object aValue)
  {
    m_aValues.add (aValue);
    return this;
  }

  @Nonnegative
  public int getValueCount ()
  {
    return m_aValues.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <Object> getObjectValues ()
  {
    return ContainerHelper.newList (m_aValues);
  }
}
