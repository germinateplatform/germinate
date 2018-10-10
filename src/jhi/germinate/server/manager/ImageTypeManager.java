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

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class ImageTypeManager extends AbstractManager<ImageType>
{
	private static final String SELECT_ALL_WITH_IMAGES = "SELECT * FROM `imagetypes` WHERE EXISTS (SELECT 1 FROM `images` WHERE `images`.`imagetype_id` = `imagetypes`.`id` LIMIT 1)";

	@Override
	protected String getTable()
	{
		return "imagetypes";
	}

	@Override
	protected DatabaseObjectParser<ImageType> getParser()
	{
		return ImageType.Parser.Inst.get();
	}

	public static ServerResult<List<ImageType>> getAllWithImages(UserAuth userAuth) throws DatabaseException
	{
		return new DatabaseObjectQuery<ImageType>(SELECT_ALL_WITH_IMAGES, userAuth)
				.run()
				.getObjects(ImageType.Parser.Inst.get(), true);
	}
}
