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
package com.phloc.db.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.string.StringHelper;
import com.phloc.db.jpa.eclipselink.JPALogger;
import com.phloc.db.jpa.eclipselink.JPASessionCustomizer;
import com.phloc.scopes.singleton.GlobalSingleton;

/**
 * Ensure that the JPA manager is correctly handled upon AppSrv startup and
 * shutdown.<br>
 * Must be public to be instantiated via reflection.
 * 
 * @author philip
 */
@SuppressWarnings ("deprecation")
public abstract class AbstractJPASingleton extends GlobalSingleton implements IEntityManagerProvider
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractJPASingleton.class);

  static
  {
    // Check if all existing META-INF/persistence.xml files reference existing
    // classes
    PersistenceXmlUtils.checkPersistenceXMLValidity ();
  }

  private final String m_sPersistenceUnitName;
  private EntityManagerFactory m_aFactory;
  private EntityManager m_aEntityManager;

  /*
   * Constructor. Never initialize manually!
   */
  protected AbstractJPASingleton (@Nonnull @Nonempty final String sJdbcDriver,
                                  @Nonnull @Nonempty final String sJdbcURL,
                                  @Nullable final String sUser,
                                  @Nullable final String sPassword,
                                  @Nonnull final Class <? extends DatabasePlatform> aPlatformClass,
                                  @Nonnull @Nonempty final String sPersistenceUnitName,
                                  @Nullable final Map <String, Object> aAdditionalFactoryProps)
  {
    if (StringHelper.hasNoText (sJdbcDriver))
      throw new IllegalArgumentException ("JDBCDriver");
    if (StringHelper.hasNoText (sJdbcURL))
      throw new IllegalArgumentException ("JDBC URL");
    if (aPlatformClass == null)
      throw new NullPointerException ("platformClass");
    if (StringHelper.hasNoText (sPersistenceUnitName))
      throw new IllegalArgumentException ("persistenceUnitName");

    s_aLogger.info ("Using JDBC URL " + sJdbcURL + " with JDBC driver " + sJdbcDriver + " and user '" + sUser + "'");

    final Map <String, Object> aFactoryProps = new HashMap <String, Object> ();
    aFactoryProps.put (PersistenceUnitProperties.JDBC_DRIVER, sJdbcDriver);
    aFactoryProps.put (PersistenceUnitProperties.JDBC_URL, sJdbcURL);
    aFactoryProps.put (PersistenceUnitProperties.JDBC_USER, sUser);
    aFactoryProps.put (PersistenceUnitProperties.JDBC_PASSWORD, sPassword);

    aFactoryProps.put (PersistenceUnitProperties.LOGGING_LOGGER, JPALogger.class.getName ());
    aFactoryProps.put (PersistenceUnitProperties.SESSION_CUSTOMIZER, JPASessionCustomizer.class.getName ());
    aFactoryProps.put (PersistenceUnitProperties.TARGET_DATABASE, aPlatformClass.getName ());

    // Not desired to have default values for
    // PersistenceUnitProperties.DDL_GENERATION,
    // PersistenceUnitProperties.CREATE_JDBC_DDL_FILE and
    // PersistenceUnitProperties.DROP_JDBC_DDL_FILE, when multiple JPA
    // configurations are present

    // Add parameter properties
    if (aAdditionalFactoryProps != null)
      aFactoryProps.putAll (aAdditionalFactoryProps);

    // Consistency check if no explicit DDL generation mode is specified!
    if (aFactoryProps.containsKey (PersistenceUnitProperties.DDL_GENERATION) &&
        !aFactoryProps.containsKey (PersistenceUnitProperties.DDL_GENERATION_MODE))
    {
      final String sDDLGeneration = (String) aFactoryProps.get (PersistenceUnitProperties.DDL_GENERATION);
      if (!PersistenceUnitProperties.NONE.equals (sDDLGeneration))
      {
        s_aLogger.warn ("DDL generation is set to '" +
                        sDDLGeneration +
                        "' but no DDL generation mode is defined, which defaults to '" +
                        PersistenceUnitProperties.DDL_DATABASE_GENERATION +
                        "' - defaulting to '" +
                        PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION +
                        "'!!!");
        aFactoryProps.put (PersistenceUnitProperties.DDL_GENERATION_MODE,
                           PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION);
      }
    }

    m_sPersistenceUnitName = sPersistenceUnitName;

    // Create entity manager factory
    final EntityManagerFactory aFactory = Persistence.createEntityManagerFactory (sPersistenceUnitName, aFactoryProps);
    if (aFactory == null)
      throw new IllegalStateException ("Failed to create entity manager factory for persistence unit '" +
                                       sPersistenceUnitName +
                                       "' with properties " +
                                       aFactoryProps.toString () +
                                       "!");

    // Wrap in a factory with listener support
    m_aFactory = new EntityManagerFactoryWithListener (aFactory);
    s_aLogger.info ("Created entity manager factory for persistence unit '" + sPersistenceUnitName + "'");

    // Consistency check after creation!
    final Map <String, Object> aRealProps = m_aFactory.getProperties ();
    if (aRealProps.containsKey (PersistenceUnitProperties.DDL_GENERATION) &&
        !aRealProps.containsKey (PersistenceUnitProperties.DDL_GENERATION_MODE))
    {
      final String sDDLGeneration = (String) aRealProps.get (PersistenceUnitProperties.DDL_GENERATION);
      if (!PersistenceUnitProperties.NONE.equals (sDDLGeneration))
      {
        throw new IllegalStateException ("DDL generation is set to '" +
                                         sDDLGeneration +
                                         "' but no DDL generation mode is defined, which defaults to '" +
                                         PersistenceUnitProperties.DDL_DATABASE_GENERATION +
                                         "'!!!\nEffective property are: " +
                                         aRealProps.toString ());
      }
    }

    // Create entity manager
    m_aEntityManager = m_aFactory.createEntityManager (null);
    if (m_aEntityManager == null)
      throw new IllegalStateException ("Failed to create entity manager from factory " + m_aFactory + "!");
    s_aLogger.info ("Created entity manager for persistence unit '" + sPersistenceUnitName + "'");
  }

  @Nonnull
  public final EntityManager getEntityManager ()
  {
    return m_aEntityManager;
  }

  /**
   * Called when the global scope is destroyed (upon servlet context shutdown)
   * 
   * @throws Exception
   *         if closing fails
   */
  @Override
  protected final void onDestroy () throws Exception
  {
    // Destroy entity manager
    if (m_aEntityManager != null)
    {
      if (m_aEntityManager.isOpen ())
      {
        // Clear cache
        m_aEntityManager.clear ();
        // Close
        m_aEntityManager.close ();
      }
      m_aEntityManager = null;
    }

    // Destroy factory
    if (m_aFactory != null)
    {
      if (m_aFactory.isOpen ())
      {
        // Clear cache
        m_aFactory.getCache ().evictAll ();
        // Close
        m_aFactory.close ();
      }
      m_aFactory = null;
    }
    s_aLogger.info ("Closed JPAManager '" + m_sPersistenceUnitName + "'");
  }
}
