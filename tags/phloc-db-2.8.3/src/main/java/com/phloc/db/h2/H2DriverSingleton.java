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
package com.phloc.db.h2;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.UsedViaReflection;
import com.phloc.scopes.nonweb.singleton.GlobalSingleton;

/**
 * A wrapper around the H2 driver, that gets automatically deregistered, when
 * the global scope is closed.
 * 
 * @author philip
 */
public final class H2DriverSingleton extends GlobalSingleton
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (H2DriverSingleton.class);

  @Deprecated
  @UsedViaReflection
  public H2DriverSingleton ()
  {
    s_aLogger.info ("Loading org.h2.Driver");
    org.h2.Driver.load ();
  }

  public static H2DriverSingleton getInstance ()
  {
    return getGlobalSingleton (H2DriverSingleton.class);
  }

  @Nonnull
  public String getDriverName ()
  {
    return org.h2.Driver.class.getName ();
  }

  @Override
  protected void onDestroy () throws Exception
  {
    org.h2.Driver.unload ();
    s_aLogger.info ("Unloaded org.h2.Driver");
  }
}
