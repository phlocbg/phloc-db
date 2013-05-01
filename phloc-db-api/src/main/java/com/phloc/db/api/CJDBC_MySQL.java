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
