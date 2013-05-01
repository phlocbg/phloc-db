package com.phloc.db.jpa;

import javax.annotation.Nonnull;

import com.phloc.commons.callback.IThrowingRunnableWithParameter;

//FIXME replace with version from phloc-commons > 4.0.4
public final class AdapterRunnableToRunnableWithParam <PARAMTYPE> implements IThrowingRunnableWithParameter <PARAMTYPE>
{
  private final Runnable m_aRunnable;

  public AdapterRunnableToRunnableWithParam (@Nonnull final Runnable aRunnable)
  {
    if (aRunnable == null)
      throw new NullPointerException ("Runnable");
    m_aRunnable = aRunnable;
  }

  @Nonnull
  public void run (@Nonnull final PARAMTYPE aParam) throws Exception
  {
    m_aRunnable.run ();
  }
}
