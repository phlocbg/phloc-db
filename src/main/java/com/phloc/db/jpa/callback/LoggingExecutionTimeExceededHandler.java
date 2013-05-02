/**
 * Copyright (C) 2006-2013 phloc systems
 * http://www.phloc.com
 * office[at]phloc[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phloc.db.jpa.callback;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.string.ToStringGenerator;

public class LoggingExecutionTimeExceededHandler implements IExecutionTimeExceededHandler
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (LoggingExecutionTimeExceededHandler.class);

  private final boolean m_bEmitStackTrace;

  public LoggingExecutionTimeExceededHandler (final boolean bEmitStackTrace)
  {
    m_bEmitStackTrace = bEmitStackTrace;
  }

  public void onExecutionTimeExceeded (@Nonnull final String sMsg, @Nonnegative final long nExecutionMillis)
  {
    s_aLogger.warn (sMsg + " took " + nExecutionMillis + "ms", m_bEmitStackTrace ? new Exception () : null);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("emitStackTraces", m_bEmitStackTrace).toString ();
  }
}
