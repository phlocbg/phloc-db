package com.phloc.db.api;

import javax.annotation.concurrent.Immutable;

import com.phloc.commons.annotations.PresentForCodeCoverage;

/**
 * JDBC constants for H2
 * 
 * @author Philip Helger
 */
@Immutable
public final class CJDBC_H2
{
  /** Default JDBC URL prefix */
  public static final String CONNECTION_PREFIX = "jdbc:h2:";
  public static final String DEFAULT_JDBC_DRIVER_CLASS_NAME = "org.h2.Driver";

  @PresentForCodeCoverage
  @SuppressWarnings ("unused")
  private static final CJDBC_H2 s_aInstance = new CJDBC_H2 ();

  private CJDBC_H2 ()
  {}
}
