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
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.annotations.Nonempty;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.commons.annotations.ReturnsMutableCopy;
import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.string.StringHelper;
import com.phloc.db.jpa.eclipselink.EclipseLinkLogger;
import com.phloc.db.jpa.eclipselink.EclipseLinkSessionCustomizer;
import com.phloc.db.jpa.utils.PersistenceXmlUtils;
import com.phloc.scopes.singleton.GlobalSingleton;

/**
 * Abstract global singleton to handle a single persistence unit.
 * 
 * @author Philip Helger
 */
public abstract class AbstractEntityManagerFactorySingleton extends GlobalSingleton
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AbstractEntityManagerFactorySingleton.class);

  static
  {
    // Check if all existing META-INF/persistence.xml files reference existing
    // classes
    PersistenceXmlUtils.checkPersistenceXMLValidity ();
  }

  private final String m_sPersistenceUnitName;
  private final Map <String, Object> m_aFactoryProps;
  private EntityManagerFactory m_aFactory;

  /**
   * Constructor
   * 
   * @param sJdbcDriverClass
   *        Name of the JDBC driver class. Must be a class implementing
   *        java.sql.Driver.
   * @param sJdbcURL
   *        JDBC URL
   * @param sUserName
   *        User name to access the DB. May be <code>null</code>.
   * @param sPassword
   *        Password to access the DB. May be <code>null</code>.
   * @param sPlatformClass
   *        The EclipseLink platform name. May either be a fully qualified
   *        class-name of a recognized abbreviation.
   * @param sPersistenceUnitName
   *        The name of the persistence unit as stated in the persistence.xml
   * @param aAdditionalFactoryProperties
   *        An optional Map with properties for {@link EntityManagerFactory}.
   *        This can even be used to overwrite the settings specified as
   *        explicit parameters, so be careful. This map is applied after the
   *        special properties are set! May be <code>null</code>.
   */
  protected AbstractEntityManagerFactorySingleton (@Nonnull @Nonempty final String sJdbcDriverClass,
                                                   @Nonnull @Nonempty final String sJdbcURL,
                                                   @Nullable final String sUserName,
                                                   @Nullable final String sPassword,
                                                   @Nonnull @Nonempty final String sPlatformClass,
                                                   @Nonnull @Nonempty final String sPersistenceUnitName,
                                                   @Nullable final Map <String, Object> aAdditionalFactoryProperties)
  {
    if (StringHelper.hasNoText (sJdbcDriverClass))
      throw new NullPointerException ("JdbcDriverClass");
    if (StringHelper.hasNoText (sJdbcURL))
      throw new IllegalArgumentException ("JdbcURL");
    if (StringHelper.hasNoText (sPlatformClass))
      throw new NullPointerException ("PlatformClass");
    if (StringHelper.hasNoText (sPersistenceUnitName))
      throw new IllegalArgumentException ("PersistenceUnitName");

    s_aLogger.info ("Using JDBC URL " +
                    sJdbcURL +
                    " with JDBC driver " +
                    sJdbcDriverClass +
                    " and user '" +
                    sUserName +
                    "'");

    final Map <String, Object> aFactoryProps = new HashMap <String, Object> ();
    aFactoryProps.put (PersistenceUnitProperties.JDBC_DRIVER, sJdbcDriverClass);
    aFactoryProps.put (PersistenceUnitProperties.JDBC_URL, sJdbcURL);
    aFactoryProps.put (PersistenceUnitProperties.JDBC_USER, sUserName);
    aFactoryProps.put (PersistenceUnitProperties.JDBC_PASSWORD, sPassword);

    aFactoryProps.put (PersistenceUnitProperties.LOGGING_LOGGER, EclipseLinkLogger.class.getName ());
    aFactoryProps.put (PersistenceUnitProperties.SESSION_CUSTOMIZER, EclipseLinkSessionCustomizer.class.getName ());
    aFactoryProps.put (PersistenceUnitProperties.TARGET_DATABASE, sPlatformClass);

    // Not desired to have default values for
    // PersistenceUnitProperties.DDL_GENERATION,
    // PersistenceUnitProperties.CREATE_JDBC_DDL_FILE and
    // PersistenceUnitProperties.DROP_JDBC_DDL_FILE, when multiple JPA
    // configurations are present

    // Add parameter properties
    if (aAdditionalFactoryProperties != null)
      aFactoryProps.putAll (aAdditionalFactoryProperties);

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
    m_aFactoryProps = aFactoryProps;
  }

  @Nonnull
  @OverrideOnDemand
  protected EntityManagerFactory customizeEntityManagerFactory (@Nonnull final EntityManagerFactory aEMF)
  {
    return aEMF;
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onAfterInstantiation ()
  {
    // Create entity manager factory
    final EntityManagerFactory aFactory = Persistence.createEntityManagerFactory (m_sPersistenceUnitName,
                                                                                  m_aFactoryProps);
    if (aFactory == null)
      throw new IllegalStateException ("Failed to create entity manager factory for persistence unit '" +
                                       m_sPersistenceUnitName +
                                       "' with properties " +
                                       m_aFactoryProps.toString () +
                                       "!");

    // Wrap in a factory with listener support
    m_aFactory = customizeEntityManagerFactory (aFactory);
    s_aLogger.info ("Created EntityManagerFactory for persistence unit '" + m_sPersistenceUnitName + "'");

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
  }

  /**
   * Called when the global scope is destroyed (upon servlet context shutdown)
   * 
   * @throws Exception
   *         if closing fails
   */
  @Override
  @OverridingMethodsMustInvokeSuper
  protected void onDestroy () throws Exception
  {
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
    s_aLogger.info ("Closed EntityManagerFactory for persistence unit '" + m_sPersistenceUnitName + "'");
  }

  /**
   * @return The persistence unit name. Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public final String getPersistenceUnitName ()
  {
    return m_sPersistenceUnitName;
  }

  /**
   * @return The EntityManagerFactory creation properties. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public final Map <String, Object> getAllFactoryProperties ()
  {
    return ContainerHelper.newMap (m_aFactoryProps);
  }

  @Nonnull
  public final EntityManagerFactory getEntityManagerFactory ()
  {
    if (m_aFactory == null)
      throw new IllegalStateException ("No EntityManagerFactory present!");
    return m_aFactory;
  }

  @Nonnull
  public final EntityManager createEntityManager ()
  {
    return createEntityManager (null);
  }

  @Nonnull
  public EntityManager createEntityManager (@SuppressWarnings ("rawtypes") final Map aMap)
  {
    // Create entity manager
    final EntityManager aEntityManager = m_aFactory.createEntityManager (aMap);
    if (aEntityManager == null)
      throw new IllegalStateException ("Failed to create EntityManager from factory " + m_aFactory + "!");
    return aEntityManager;
  }
}
