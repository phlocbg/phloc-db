/**
 * Copyright (C) 2006-2014 phloc systems
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
package com.phloc.db.jdbc;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * A simple provider interface for {@link DataSource} objects.
 * 
 * @author Philip Helger
 */
public interface IDataSourceProvider
{
  /**
   * Retrieve a data source. It is up to the implementation whether the same
   * data source is returned or whether a new data source object is created all
   * the time.
   * 
   * @return A non-<code>null</code> data source.
   */
  @Nonnull
  DataSource getDataSource ();
}
