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

package jhi.germinate.server.manager;

import java.util.*;

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class CollaboratorManager extends AbstractManager<Collaborator>
{
	private static final String SELECT_FOR_DATASET_ID = "SELECT * FROM datasetcollaborators LEFT JOIN collaborators ON datasetcollaborators.collaborator_id = collaborators.id LEFT JOIN institutions ON institutions.id = collaborators.institution_id LEFT JOIN countries ON countries.id = institutions.country_id WHERE datasetcollaborators.dataset_id = ?";

	public static ServerResult<List<Collaborator>> getForDatasetId(UserAuth user, Long id) throws DatabaseException
	{
		return new DatabaseObjectQuery<Collaborator>(SELECT_FOR_DATASET_ID, user)
				.setLong(id)
				.run()
				.getObjects(Collaborator.Parser.Inst.get(), true);
	}

	@Override
	protected String getTable()
	{
		return "collaborators";
	}

	@Override
	protected DatabaseObjectParser<Collaborator> getParser()
	{
		return Collaborator.Parser.Inst.get();
	}
}
