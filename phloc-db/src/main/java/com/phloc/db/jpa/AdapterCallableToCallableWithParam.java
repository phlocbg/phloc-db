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

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import com.phloc.commons.callback.IThrowingCallableWithParameter;

// FIXME replace with version from phloc-commons > 4.0.4
final class AdapterCallableToCallableWithParam <DATATYPE, PARAMTYPE> implements IThrowingCallableWithParameter <DATATYPE, PARAMTYPE>
{
  private final Callable <DATATYPE> m_aCallable;

  public AdapterCallableToCallableWithParam (@Nonnull final Callable <DATATYPE> aCallable)
  {
    if (aCallable == null)
      throw new NullPointerException ("Callable");
    m_aCallable = aCallable;
  }

  @Nonnull
  public DATATYPE call (@Nonnull final PARAMTYPE aParam) throws Exception
  {
    return m_aCallable.call ();
  }
}
