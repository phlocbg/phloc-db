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

import java.util.List;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.CGlobal;
import com.phloc.commons.GlobalDebug;
import com.phloc.commons.string.StringHelper;

/**
 * A logging adapter that can be hooked into JPA and forwards all logging
 * requests to phloc logging.
 * 
 * @author philip
 */
public final class JPALogger extends AbstractSessionLog
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (JPALogger.class);

  @Override
  public void log (final SessionLogEntry aSessionLogEntry)
  {
    final int nLogLevel = aSessionLogEntry.getLevel ();
    // JPA uses the System property for adding line breaks
    final List <String> aMsgLines = StringHelper.getExploded (CGlobal.LINE_SEPARATOR, aSessionLogEntry.getMessage ());
    final int nMaxIndex = aMsgLines.size ();
    for (int i = 0; i < nMaxIndex; ++i)
    {
      final String sMsg = aMsgLines.get (i);
      final Throwable t = i == nMaxIndex - 1 ? aSessionLogEntry.getException () : null;
      if (nLogLevel >= SessionLog.SEVERE)
        s_aLogger.error (sMsg, t);
      else
        if (nLogLevel >= SessionLog.WARNING)
          s_aLogger.warn (sMsg, t);
        else
          if (nLogLevel >= SessionLog.CONFIG || GlobalDebug.isDebugMode ())
          {
            if (s_aLogger.isInfoEnabled ())
              s_aLogger.info (sMsg, t);
          }
          else
          {
            if (s_aLogger.isDebugEnabled ())
              s_aLogger.debug (sMsg, t);
          }
    }
  }
}
