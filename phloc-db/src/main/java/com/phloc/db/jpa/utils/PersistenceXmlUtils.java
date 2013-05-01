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
package com.phloc.db.jpa.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phloc.commons.io.resource.URLResource;
import com.phloc.commons.lang.ClassHelper;
import com.phloc.commons.lang.GenericReflection;
import com.phloc.commons.microdom.IMicroDocument;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.serialize.MicroReader;
import com.phloc.commons.string.StringHelper;

@Immutable
public final class PersistenceXmlUtils
{
  /** The JPA consifuguration file relative to the classpath */
  public static final String PATH_PERSISTENCE_XML = "META-INF/persistence.xml";
  private static final Logger s_aLogger = LoggerFactory.getLogger (PersistenceXmlUtils.class);

  private PersistenceXmlUtils ()
  {}

  /**
   * Read all {@value #PATH_PERSISTENCE_XML} files in the class path and check
   * if the referenced classes are in the classpath.
   * 
   * @throws IllegalStateException
   *         in case of an error
   */
  public static void checkPersistenceXMLValidity ()
  {
    // Check if all persistence.xml files contain valid classes!
    try
    {
      final String sNamespaceURI = "http://java.sun.com/xml/ns/persistence";
      int nCount = 0;
      final Enumeration <URL> e = ClassHelper.getDefaultClassLoader ().getResources (PATH_PERSISTENCE_XML);
      while (e.hasMoreElements ())
      {
        nCount++;
        final URL u = e.nextElement ();
        final IMicroDocument aDoc = MicroReader.readMicroXML (new URLResource (u));
        if (aDoc == null || aDoc.getDocumentElement () == null)
          throw new IllegalStateException ("No XML file: " + u);
        for (final IMicroElement ePU : aDoc.getDocumentElement ().getAllChildElements (sNamespaceURI,
                                                                                       "persistence-unit"))
          for (final IMicroElement eClass : ePU.getAllChildElements (sNamespaceURI, "class"))
          {
            final String sClass = eClass.getTextContent ();
            if (StringHelper.hasNoTextAfterTrim (sClass))
              throw new IllegalStateException ("Persistence file " + u + ": class name is missing!");
            GenericReflection.getClassFromName (sClass.trim ());
          }
      }
      s_aLogger.info ("All " + PATH_PERSISTENCE_XML + " files are valid (count=" + nCount + ")");
    }
    catch (final IOException ex)
    {
      // From getResources
      throw new IllegalStateException ("Failed to read " + PATH_PERSISTENCE_XML + " file", ex);
    }
    catch (final ClassNotFoundException ex)
    {
      // If forName fails
      throw new IllegalStateException ("Failed to resolve class", ex);
    }
  }
}
