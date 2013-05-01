package com.phloc.db.jpa;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

import com.phloc.commons.callback.IThrowingCallableWithParameter;

public class AdapterCallableToCallableWithParam <DATATYPE> implements IThrowingCallableWithParameter <DATATYPE, EntityManager>
{
  private final Callable <DATATYPE> m_aCallable;

  public AdapterCallableToCallableWithParam (@Nonnull final Callable <DATATYPE> aCallable)
  {
    if (aCallable == null)
      throw new NullPointerException ("Callable");
    m_aCallable = aCallable;
  }

  @Nonnull
  public DATATYPE call (@Nonnull final EntityManager aParam) throws Exception
  {
    return m_aCallable.call ();
  }

  @Nonnull
  public static <T> AdapterCallableToCallableWithParam <T> create (@Nonnull final Callable <T> aCallable)
  {
    return new AdapterCallableToCallableWithParam <T> (aCallable);
  }
}
