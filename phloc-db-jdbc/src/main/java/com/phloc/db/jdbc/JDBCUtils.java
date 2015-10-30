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
package com.phloc.db.jdbc;

import java.sql.Types;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.phloc.db.api.jdbc.JDBCHelper;

/**
 * Small class for safe SQL-as-usual methods.
 * 
 * @author Philip Helger
 */
@Immutable
public final class JDBCUtils
{
  private JDBCUtils ()
  {}

  /**
   * Determine the JDBC type from the passed class.
   * 
   * @param aClass
   *        The class to check. May not be <code>null</code>.
   * @return {@link Types#JAVA_OBJECT} if the type could not be determined.
   * @see "http://java.sun.com/j2se/1.4.2/docs/guide/jdbc/getstart/mapping.html"
   */
  public static int getJDBCTypeFromClass (@Nonnull final Class <?> aClass)
  {
    if (aClass == null)
      throw new NullPointerException ("class");

    // phloc specific
    if (aClass.equals (org.joda.time.DateTime.class))
      return Types.TIMESTAMP;

    if (aClass.equals (org.joda.time.LocalDate.class))
      return Types.DATE;

    if (aClass.equals (org.joda.time.LocalTime.class))
      return Types.TIME;

    return JDBCHelper.getJDBCTypeFromClass (aClass);
  }
}
