package com.phloc.db.api.h2;

public enum EH2Log
{
  DISABLE (0),
  LOG (1),
  LOG_AND_SYNC (2);

  /** Default log mode: log and sync */
  public static final EH2Log DEFAULT = LOG_AND_SYNC;

  private int m_nValue;

  private EH2Log (final int i)
  {
    m_nValue = i;
  }

  public int getValue ()
  {
    return m_nValue;
  }
}