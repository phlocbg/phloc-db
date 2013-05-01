package com.phloc.db.api;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * JDBC constants for DB2
 * 
 * @author Philip Helger
 */
@Immutable
public final class CJDBC_DB2
{
  /** Default JDBC URL prefix */
  public static final String CONNECTION_PREFIX = "jdbc:db2://";
  public static final String DEFAULT_JDBC_DRIVER_CLASS = "com.ibm.db2.jcc.DB2Driver";

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CJDBC_DB2 s_aInstance = new CJDBC_DB2 ();

  private CJDBC_DB2 ()
  {}
}
