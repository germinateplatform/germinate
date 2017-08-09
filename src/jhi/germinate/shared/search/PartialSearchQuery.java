/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.shared.search;

import java.util.*;
import java.util.stream.*;

/**
 * {@link PartialSearchQuery} class represents a collection of {@link SearchCondition}s and {@link LogicalOperator}s that join the {@link
 * SearchCondition}s. The combination of conditions and logical operators can then be turned into a SQL string representation that can be used in the
 * where clause of a query.
 *
 * @author Toby Philp
 * @author Sebastian Raubach
 */
public class PartialSearchQuery extends OverallSearchQuery<SearchCondition>
{
	private static final long serialVersionUID = 6352959904324552644L;

	/**
	 * Creates a set of the column names used in this search query that may be required for table display.
	 *
	 * @return HashSet<String> of column names
	 */
	public Set<String> getColumnNames()
	{
		return conditions.stream()
						 .map(SearchCondition::getColumnName)
						 .collect(Collectors.toCollection(HashSet::new));
	}
}
