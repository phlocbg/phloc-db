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
package com.phloc.db.jpa.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Time;

import org.joda.time.LocalTime;
import org.junit.Test;

import com.phloc.datetime.PDTFactory;

/**
 * Test class for class {@link JPAJodaLocalTimeConverter}.
 * 
 * @author philip
 */
public final class JPAJodaLocalTimeConverterTest
{
  @Test
  public void testAll ()
  {
    final LocalTime aNow = PDTFactory.getCurrentLocalTime ();
    final JPAJodaLocalTimeConverter aConverter = new JPAJodaLocalTimeConverter ();
    final Time aDataValue = aConverter.convertObjectValueToDataValue (aNow, null);
    assertNotNull (aDataValue);
    assertEquals (aNow, aConverter.convertDataValueToObjectValue (aDataValue, null));
  }
}
