/**
 * Copyright (C) 2006-2012 phloc systems
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
package com.phloc.db.config;

import javax.annotation.Nullable;

import com.phloc.commons.annotations.IsSPIImplementation;
import com.phloc.commons.thirdparty.ELicense;
import com.phloc.commons.thirdparty.IThirdPartyModule;
import com.phloc.commons.thirdparty.IThirdPartyModuleProviderSPI;
import com.phloc.commons.thirdparty.ThirdPartyModule;
import com.phloc.commons.version.Version;

/**
 * Implement this SPI interface if your JAR file contains external third party
 * modules.
 * 
 * @author philip
 */
@IsSPIImplementation
public final class ThirdPartyModuleProvider_phloc_db implements IThirdPartyModuleProviderSPI
{
  public static final IThirdPartyModule COMMONS_POOL = new ThirdPartyModule ("Apache Commons Pool",
                                                                             "Apache",
                                                                             ELicense.APACHE2,
                                                                             new Version (1, 6, 0),
                                                                             "http://commons.apache.org/pool/",
                                                                             true);
  public static final IThirdPartyModule COMMONS_DBCP = new ThirdPartyModule ("Apache Commons DBCP",
                                                                             "Apache",
                                                                             ELicense.APACHE2,
                                                                             new Version (1, 4, 0),
                                                                             "http://commons.apache.org/dbcp/",
                                                                             true);
  public static final IThirdPartyModule H2 = new ThirdPartyModule ("H2 Database Engine",
                                                                   "Eclipse Foundation",
                                                                   ELicense.EPL10,
                                                                   new Version (1, 3, 169),
                                                                   "http://www.h2database.com/");
  public static final IThirdPartyModule MYSQL = new ThirdPartyModule ("MySQL Connector/J",
                                                                      "Oracle",
                                                                      ELicense.GPL20,
                                                                      new Version (5, 1, 21),
                                                                      "http://www.mysql.com/",
                                                                      true);
  private static final IThirdPartyModule ECLIPSE_LINK = new ThirdPartyModule ("EclipseLink",
                                                                              "Eclipse Foundation",
                                                                              ELicense.EPL10,
                                                                              new Version (2, 4, 1),
                                                                              "http://www.eclipse.org/eclipselink/");

  @Nullable
  public IThirdPartyModule [] getAllThirdPartyModules ()
  {
    return new IThirdPartyModule [] { COMMONS_POOL, COMMONS_DBCP, H2, MYSQL, ECLIPSE_LINK };
  }
}
