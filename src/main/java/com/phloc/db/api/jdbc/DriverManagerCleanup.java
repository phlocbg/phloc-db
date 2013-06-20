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
package com.phloc.db.api.jdbc;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple class that deregisters all {@link Driver}'s registered in the
 * central {@link DriverManager}.
 * 
 * @author Philip Helger
 */
@Immutable
public final class DriverManagerCleanup
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (DriverManagerCleanup.class);

  private DriverManagerCleanup ()
  {}

  /**
   * Deregister all JDBC drivers, still registered in the {@link DriverManager}.
   * This method should be called upon application shutdown!
   */
  public static void deregisterAllDrivers ()
  {
    final Enumeration <Driver> aAllDrivers = DriverManager.getDrivers ();
    while (aAllDrivers.hasMoreElements ())
    {
      final Driver aDriver = aAllDrivers.nextElement ();
      try
      {
        DriverManager.deregisterDriver (aDriver);
        s_aLogger.info ("Deregistered JDBC driver " + aDriver);
      }
      catch (final SQLException ex)
      {
        s_aLogger.error ("Failed to deregister JDBC driver " + aDriver, ex);
      }
    }
  }
}
