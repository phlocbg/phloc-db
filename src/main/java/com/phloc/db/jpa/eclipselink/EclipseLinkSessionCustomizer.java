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
package com.phloc.db.jpa.eclipselink;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

import com.phloc.commons.CGlobal;

/**
 * Class for customizing JPA sessions.<br>
 * Set the class name in the property
 * <code>eclipselink.session.customizer</code><br>
 * Should have a public no-argument ctor
 * 
 * @author Philip Helger
 */
public final class EclipseLinkSessionCustomizer implements SessionCustomizer
{
  private static final AtomicInteger s_aLogLevel = new AtomicInteger (CGlobal.ILLEGAL_UINT);

  public EclipseLinkSessionCustomizer ()
  {}

  /**
   * See {@link SessionLog} for the available log levels
   * 
   * @param nLogLevel
   */
  public static void setGlobalLogLevel (final int nLogLevel)
  {
    if (nLogLevel >= SessionLog.ALL && nLogLevel <= SessionLog.OFF)
      s_aLogLevel.set (nLogLevel);
  }

  public static int getGlobalLogLevel ()
  {
    return s_aLogLevel.get ();
  }

  public void customize (final Session aSession) throws Exception
  {
    final int nLogLevel = getGlobalLogLevel ();
    if (nLogLevel != CGlobal.ILLEGAL_UINT)
    {
      // create a custom logger and assign it to the session
      final SessionLog aCustomLogger = new EclipseLinkLogger ();
      aCustomLogger.setLevel (nLogLevel);
      aSession.setSessionLog (aCustomLogger);
    }
  }
}
