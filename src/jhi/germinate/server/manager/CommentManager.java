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
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * CommentManager handles all interactions with the database that concern user comments. This class will check user permissions and check the
 * Germinate Read-Mode setting.
 *
 * @author Sebastian Raubach
 */
public class CommentManager extends AbstractManager<Comment>
{
	private static final String SELECT_ALL_PAGINATED = "SELECT comments.* FROM comments LEFT JOIN commenttypes ON comments.commenttype_id = commenttypes.id WHERE commenttypes.reference_table = ? AND reference_id = ? AND visibility = 1 ORDER BY comments.created_on DESC LIMIT ?, ?";

	private static final String UPDATE_VISIBILITY = "UPDATE comments SET visibility = ? WHERE id = ?";

	private static final String INSERT = "INSERT INTO comments (commenttype_id, user_id, visibility, description, reference_id, created_on) VALUES (?, ?, 1, ?, ?, NOW())";

	private static final String SELECT_ALL_FOR_FILTER = "SELECT * FROM comments LEFT JOIN commenttypes ON comments.commenttype_id = commenttypes.id {{FILTER}} AND visibility = 1 %s LIMIT ?, ?";

	@Override
	protected String getTable()
	{
		return "comments";
	}

	@Override
	protected DatabaseObjectParser<Comment> getParser()
	{
		return Comment.Parser.Inst.get();
	}

	/**
	 * Sets the visibility of a comment
	 *
	 * @param user    The current user
	 * @param comment The comment in question
	 * @param visible The new visibility
	 * @throws DatabaseException                Thrown if the interaction with the database fails
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions for this action
	 * @throws SystemInReadOnlyModeException    Thrown if the system is in readAll-only mode (no modifications allowed)
	 */
	public static void setVisibility(UserAuth user, Comment comment, boolean visible) throws DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		if (user == null || user.getId() == null)
		{
			throw new InsufficientPermissionsException();
		}
		else
		{
			try
			{
				if (!Objects.equals(user.getId(), comment.getCreatedBy()))
					throw new InsufficientPermissionsException();
			}
			catch (Exception e)
			{
				throw new InsufficientPermissionsException();
			}
		}

		new ValueQuery(UPDATE_VISIBILITY, user)
				.setBoolean(visible)
				.setLong(comment.getId())
				.execute();
	}

	/**
	 * Adds a new comment to the database
	 *
	 * @param user        The current user
	 * @param type        The {@link CommentType}
	 * @param referenceId The id of the reference object
	 * @param description The actual comment content
	 * @return The {@link DebugInfo}
	 * @throws DatabaseException                Thrown if the interaction with the database fails
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions for this action
	 * @throws SystemInReadOnlyModeException    Thrown if the system is in readAll-only mode (no modifications allowed)
	 */
	public static DebugInfo add(UserAuth user, CommentType type, Long referenceId, String description) throws DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		/* Check if the username is valid */
		if (user == null || user.getId() == null)
			throw new InsufficientPermissionsException();

		return new ValueQuery(INSERT, user)
				.setLong(type.getId())
				.setLong(user.getId())
				.setString(description)
				.setLong(referenceId)
				.execute()
				.getDebugInfo();
	}

	public static PaginatedServerResult<List<Comment>> getForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(CommentService.COLUMNS_SORTABLE, Comment.ID);

		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Comment>getFilteredDatabaseObjectQuery(user, filter, formatted, CommentService.COLUMNS_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Comment.Parser.Inst.get(), true);
	}
}
