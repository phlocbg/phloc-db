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
package com.phloc.db.api.h2;

public enum EH2LockMode
{
  READ_COMMITTED (3),
  SERIALIZABLE (1),
  READ_UNCOMMITED (0);

  /** Default lock mode: read committed */
  public static final EH2LockMode DEFAULT = READ_COMMITTED;

  private final int m_nValue;

  private EH2LockMode (final int i)
  {
    m_nValue = i;
  }

  public int getValue ()
  {
    return m_nValue;
  }
}
