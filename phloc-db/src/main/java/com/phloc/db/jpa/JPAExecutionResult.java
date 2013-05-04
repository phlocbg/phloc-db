package com.phloc.db.jpa;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phloc.commons.equals.EqualsUtils;
import com.phloc.commons.hash.HashCodeGenerator;
import com.phloc.commons.lang.CGStringHelper;
import com.phloc.commons.state.ESuccess;
import com.phloc.commons.state.ISuccessIndicator;
import com.phloc.commons.state.impl.SuccessWithValue;
import com.phloc.commons.string.ToStringGenerator;

/**
 * Represents the result of a single transaction/select within this module. It
 * consists of 3 total fields:
 * <ul>
 * <li>Success/Failure</li>
 * <li>Return object - mostly in case of success</li>
 * <li>Throwable/Exception - mostly in case of error</li>
 * </ul>
 * 
 * @author Philip Helger
 * @param <DATATYPE>
 *        Main object value type.
 */
public class JPAExecutionResult <DATATYPE> extends SuccessWithValue <DATATYPE>
{
  private final Throwable m_aThrowable;

  public JPAExecutionResult (@Nonnull final ISuccessIndicator aSuccessIndicator,
                             @Nullable final DATATYPE aObj,
                             @Nullable final Throwable aThrowable)
  {
    super (aSuccessIndicator, aObj);
    m_aThrowable = aThrowable;
  }

  /**
   * @return The exception passed in the constructor. May be <code>null</code>.
   */
  @Nullable
  public Throwable getThrowable ()
  {
    return m_aThrowable;
  }

  /**
   * @return <code>true</code> if an exception is present, <code>false</code> if
   *         not.
   */
  public boolean hasThrowable ()
  {
    return m_aThrowable != null;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!super.equals (o))
      return false;
    final JPAExecutionResult <?> rhs = (JPAExecutionResult <?>) o;
    return EqualsUtils.equals (CGStringHelper.getSafeClassName (m_aThrowable),
                               CGStringHelper.getSafeClassName (rhs.m_aThrowable));
  }

  @Override
  public int hashCode ()
  {
    return HashCodeGenerator.getDerived (super.hashCode ())
                            .append (CGStringHelper.getSafeClassName (m_aThrowable))
                            .getHashCode ();
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("throwable", m_aThrowable).toString ();
  }

  @Nonnull
  public static <T> JPAExecutionResult <T> createSuccess (@Nonnull final T aObj)
  {
    return new JPAExecutionResult <T> (ESuccess.SUCCESS, aObj, null);
  }

  @Nonnull
  public static <T> JPAExecutionResult <T> createFailure (@Nullable final Throwable t)
  {
    return new JPAExecutionResult <T> (ESuccess.FAILURE, null, t);
  }
}
