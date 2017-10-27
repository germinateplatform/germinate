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

package jhi.germinate.server.service;

import java.util.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link ImageServiceImpl} is the implementation of {@link ImageService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/image-list"})
public class ImageServiceImpl extends BaseRemoteServiceServlet implements ImageService
{
	private static final long serialVersionUID = 7192904708637210440L;

	@Override
	public PaginatedServerResult<List<Image>> getForId(RequestProperties properties, GerminateDatabaseTable table, Long id, Pagination pagination) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return ImageManager.getForId(userAuth, table, id, pagination);
	}

	@Override
	public ServerResult<List<ImageType>> getTypes(RequestProperties properties) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return ImageTypeManager.getAllWithImages(userAuth);
	}

	@Override
	public PaginatedServerResult<List<Image>> getForType(RequestProperties properties, ImageType imageType, Pagination pagination) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return ImageManager.getForType(userAuth, imageType, pagination);
	}
}
