package com.phloc.db.api.h2;

public enum EH2LockMode
{
  READ_COMMITTED (3),
  SERIALIZABLE (1),
  READ_UNCOMMITED (0);

  /** Default lock mode: read committed */
  public static final EH2LockMode DEFAULT = READ_COMMITTED;

  private int m_nValue;

  private EH2LockMode (final int i)
  {
    m_nValue = i;
  }

  public int getValue ()
  {
    return m_nValue;
  }
}