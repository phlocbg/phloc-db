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
package com.phloc.db.api;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * JDBC constants for MySQL
 * 
 * @author Philip Helger
 */
@Immutable
public final class CJDBC_MySQL
{
  /** Default JDBC URL prefix */
  public static final String CONNECTION_PREFIX = "jdbc:mysql:";
  public static final String DEFAULT_JDBC_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CJDBC_MySQL s_aInstance = new CJDBC_MySQL ();

  private CJDBC_MySQL ()
  {}
}
