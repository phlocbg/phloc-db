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
package com.phloc.db.jdbc.h2;

import org.junit.Test;

import com.phloc.db.jdbc.h2.H2FileConnector;

/**
 * Test class for class {@link H2FileConnector}.
 * 
 * @author Philip Helger
 */
public final class H2FileConnectorTest
{
  @Test
  public void testAll ()
  {
    final H2FileConnector h2c = new H2FileConnector (".", "test", "sa", "");
    h2c.close ();
    // Close again
    h2c.close ();
  }
}
