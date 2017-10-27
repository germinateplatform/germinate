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
import jhi.germinate.shared.search.*;

/**
 * {@link CommentServiceImpl} is the implementation of {@link CommentService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/comment"})
public class CommentServiceImpl extends BaseRemoteServiceServlet implements CommentService
{
	private static final long serialVersionUID = -8616536189132171506L;

	@Override
	public DebugInfo add(RequestProperties properties, CommentType type, Long referenceId, String description) throws InvalidSessionException, DatabaseException,
			InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return CommentManager.add(userAuth, type, referenceId, description);
	}

	@Override
	public ServerResult<List<CommentType>> getTypes(RequestProperties properties, GerminateDatabaseTable reference) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return CommentTypeManager.getAll(userAuth, reference);
	}

	@Override
	public void disable(RequestProperties properties, Comment comment) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		CommentManager.setVisibility(userAuth, comment, false);
	}

	@Override
	public PaginatedServerResult<List<Comment>> getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination) throws InvalidSessionException, InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return CommentManager.getForFilter(userAuth, filter, pagination);
	}
}
