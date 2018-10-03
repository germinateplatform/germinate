/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
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

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class ImageManager extends AbstractManager<Image>
{
	private static final String COMMON_TABLES         = " images LEFT JOIN imagetypes ON imagetypes.id = images.imagetype_id ";
	private static final String SELECT_ALL            = "SELECT images.* FROM " + COMMON_TABLES + " WHERE imagetypes.reference_table = ? AND images.foreign_id = ? LIMIT ?, ?";
	private static final String SELECT_FOR_UNKNOWN    = "SELECT * FROM (SELECT images.*, germinatebase.name AS " + ImageService.IMAGE_REFERENCE_NAME + " FROM " + COMMON_TABLES + " LEFT JOIN germinatebase ON germinatebase.id = images.foreign_id WHERE imagetypes.reference_table LIKE 'germinatebase' UNION SELECT images.*, compounds.name AS " + ImageService.IMAGE_REFERENCE_NAME + " FROM " + COMMON_TABLES + " LEFT JOIN compounds ON compounds.id = images.foreign_id WHERE imagetypes.reference_table LIKE 'compounds') images  LIMIT ?, ?";
	private static final String SELECT_FOR_ACCESSION  = "SELECT images.*, germinatebase.name AS " + ImageService.IMAGE_REFERENCE_NAME + " FROM " + COMMON_TABLES + " LEFT JOIN germinatebase ON germinatebase.id = images.foreign_id WHERE imagetypes.reference_table LIKE ? LIMIT ?, ?";
	private static final String SELECT_FOR_COMPOUNDS  = "SELECT images.*, compounds.name AS " + ImageService.IMAGE_REFERENCE_NAME + " FROM " + COMMON_TABLES + " LEFT JOIN compounds ON compounds.id = images.foreign_id WHERE imagetypes.reference_table LIKE ? LIMIT ?, ?";
	private static final String SELECT_FOR_PHENOTYPES = "SELECT images.*, phenotypes.name AS " + ImageService.IMAGE_REFERENCE_NAME + " FROM " + COMMON_TABLES + " LEFT JOIN phenotypes ON phenotypes.id = images.foreign_id WHERE imagetypes.reference_table LIKE ? LIMIT ?, ?";

	@Override

	protected String getTable()
	{
		return "images";
	}

	@Override
	protected DatabaseObjectParser<Image> getParser()
	{
		return Image.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<Image>> getForId(UserAuth user, GerminateDatabaseTable referenceTable, Long id, Pagination pagination) throws DatabaseException
	{
		return new DatabaseObjectQuery<Image>(SELECT_ALL, user)
				.setFetchesCount(pagination.getResultSize())
				.setString(referenceTable.name())
				.setLong(id)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Image.Parser.Inst.get());
	}

	public static PaginatedServerResult<List<Image>> getForType(UserAuth userAuth, ImageType imageType, Pagination pagination) throws DatabaseException
	{
		String type;

		if (imageType == null || imageType.getId() < 1)
			type = null;
		else
			type = imageType.getReferenceTable().name();

		String query = SELECT_FOR_UNKNOWN;

		if (imageType != null && imageType.getReferenceTable() != null)
		{
			switch (imageType.getReferenceTable())
			{
				case germinatebase:
					query = SELECT_FOR_ACCESSION;
					break;
				case compounds:
					query = SELECT_FOR_COMPOUNDS;
					break;
				case phenotypes:
					query = SELECT_FOR_PHENOTYPES;
					break;
			}
		}

		DatabaseObjectQuery<Image> q = new DatabaseObjectQuery<Image>(query, userAuth)
				.setFetchesCount(pagination.getResultSize());

		if (type != null)
			q.setString(type);

		return q.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Image.Parser.Inst.get());
	}
}
