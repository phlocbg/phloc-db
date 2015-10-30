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
package com.phloc.db.jpa.eclipselink.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;

import com.phloc.commons.locale.LocaleCache;
import com.phloc.db.jpa.eclipselink.converter.JPALocaleConverter;

/**
 * Test class for class {@link JPALocaleConverter}.
 * 
 * @author Philip Helger
 */
public final class JPALocaleConverterTest
{
  @Test
  public void testAll ()
  {
    final Locale aLocale = LocaleCache.getLocale ("de_AT");
    final JPALocaleConverter aConverter = new JPALocaleConverter ();
    final String aDataValue = aConverter.convertObjectValueToDataValue (aLocale, null);
    assertNotNull (aDataValue);
    assertEquals (aLocale, aConverter.convertDataValueToObjectValue (aDataValue, null));
  }
}
