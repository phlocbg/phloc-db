package com.phloc.db.jpa;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import com.phloc.commons.callback.IThrowingRunnableWithParameter;

public class AdapterRunnableToRunnableWithParam implements IThrowingRunnableWithParameter <EntityManager>
{
  private final Runnable m_aRunnable;

  public AdapterRunnableToRunnableWithParam (@Nonnull final Runnable aRunnable)
  {
    if (aRunnable == null)
      throw new NullPointerException ("Runnable");
    m_aRunnable = aRunnable;
  }

  @Nonnull
  public void run (@Nonnull final EntityManager aParam) throws Exception
  {
    m_aRunnable.run ();
  }

  @Nonnull
  public static AdapterRunnableToRunnableWithParam create (@Nonnull final Runnable aRunnable)
  {
    return new AdapterRunnableToRunnableWithParam (aRunnable);
  }
}
