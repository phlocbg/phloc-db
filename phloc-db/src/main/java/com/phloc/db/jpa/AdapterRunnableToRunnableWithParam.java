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

import javax.annotation.Nonnull;

import com.phloc.commons.callback.IThrowingRunnableWithParameter;

//FIXME replace with version from phloc-commons > 4.0.4
final class AdapterRunnableToRunnableWithParam <PARAMTYPE> implements IThrowingRunnableWithParameter <PARAMTYPE>
{
  private final Runnable m_aRunnable;

  public AdapterRunnableToRunnableWithParam (@Nonnull final Runnable aRunnable)
  {
    if (aRunnable == null)
      throw new NullPointerException ("Runnable");
    m_aRunnable = aRunnable;
  }

  @Nonnull
  public void run (@Nonnull final PARAMTYPE aParam) throws Exception
  {
    m_aRunnable.run ();
  }
}
