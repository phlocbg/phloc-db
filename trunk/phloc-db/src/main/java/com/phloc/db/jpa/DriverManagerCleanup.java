package com.phloc.db.jpa;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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
