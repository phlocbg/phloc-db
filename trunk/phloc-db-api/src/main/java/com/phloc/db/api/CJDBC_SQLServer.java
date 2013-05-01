package com.phloc.db.api;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * JDBC constants for Microsoft SQL Server
 * 
 * @author Philip Helger
 */
@Immutable
public final class CJDBC_SQLServer
{
  /** Default JDBC URL prefix */
  public static final String CONNECTION_PREFIX = "jdbc:sqlserver://";
  public static final String DEFAULT_JDBC_DRIVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CJDBC_SQLServer s_aInstance = new CJDBC_SQLServer ();

  private CJDBC_SQLServer ()
  {}
}
