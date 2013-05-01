package com.phloc.db.jpa.utils;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;

import com.phloc.commons.callback.IThrowingCallableWithParameter;

// FIXME replace with version from phloc-commons > 4.0.4
public final class AdapterCallableToCallableWithParam <DATATYPE, PARAMTYPE> implements IThrowingCallableWithParameter <DATATYPE, PARAMTYPE>
{
  private final Callable <DATATYPE> m_aCallable;

  public AdapterCallableToCallableWithParam (@Nonnull final Callable <DATATYPE> aCallable)
  {
    if (aCallable == null)
      throw new NullPointerException ("Callable");
    m_aCallable = aCallable;
  }

  @Nonnull
  public DATATYPE call (@Nonnull final PARAMTYPE aParam) throws Exception
  {
    return m_aCallable.call ();
  }
}
