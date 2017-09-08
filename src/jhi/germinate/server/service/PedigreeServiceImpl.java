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

package jhi.germinate.server.service;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link PedigreeServiceImpl} is the implementation of {@link PedigreeService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/pedigree"})
public class PedigreeServiceImpl extends BaseRemoteServiceServlet implements PedigreeService
{
	private static final long serialVersionUID = 8785242708560655262L;

	private static final int PEDIGREE_LEVEL_LIMIT = 5;

	private static final String SELECT_PEDIGREE_PARENTS  = "SELECT A.name AS node, B.name AS parent, pedigrees.relationship_type AS type FROM pedigrees LEFT JOIN germinatebase A ON A.id = pedigrees.germinatebase_id LEFT JOIN germinatebase B ON B.id = pedigrees.parent_id ORDER BY node, pedigrees.relationship_type";
	private static final String SELECT_PEDIGREE_CHILDREN = "SELECT A.name AS node, B.name AS child,  pedigrees.relationship_type AS type FROM pedigrees LEFT JOIN germinatebase A ON A.id = pedigrees.parent_id LEFT JOIN germinatebase B ON B.id = pedigrees.germinatebase_id ORDER BY node, pedigrees.relationship_type";

	@Override
	public ServerResult<List<PedigreeDefinition>> getPedigreeDefinitions(RequestProperties properties, Long accessionId) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return PedigreeDefinitionManager.getByAccessionId(userAuth, accessionId);
	}

	public PaginatedServerResult<List<Pedigree>> get(RequestProperties properties, Long accessionId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return PedigreeManager.getAllByAccessionId(userAuth, accessionId, pagination);
	}

	@Override
	public PaginatedServerResult<List<Pedigree>> getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return PedigreeManager.getAllForFilter(userAuth, filter, pagination);
	}

	@Override
	public ServerResult<String> exportToHelium(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		/* Get the whole data with a streamer */
		DefaultStreamer streamer = new DefaultQuery(SELECT_PEDIGREE_PARENTS, userAuth)
				.getStreamer();

		File resultFile = createTemporaryFile("helium", "helium");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile)))
		{
			/* Write the headers */
			bw.write("# heliumInput = PEDIGREE");
			bw.newLine();
			bw.write("LineName\tParent\tParentType");


			DatabaseResult inputRow;

			/* Then write the data one relation at a time */
			while ((inputRow = streamer.next()) != null)
			{
				String node = inputRow.getString("node");
				String parent = inputRow.getString("parent");
				String type = inputRow.getString("type");

				bw.newLine();
				bw.write(node + "\t" + parent + "\t" + type);
			}
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		/* Return the filename */
		return new ServerResult<>(streamer.getDebugInfo(), resultFile.getName());
	}

	@Override
	public ServerResult<String> exportToHelium(RequestProperties properties, Long accessionId, Pedigree.PedigreeQuery queryType) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		/* Get the parent data; mapping: node -> [parents] */
		ServerResult<GerminateTable> parents = new GerminateTableQuery(SELECT_PEDIGREE_PARENTS, userAuth, null).run();
		/* Get the child data; mapping: node -> [children] */
		ServerResult<GerminateTable> children = new GerminateTableQuery(SELECT_PEDIGREE_CHILDREN, userAuth, null).run();

		/* Map the data into a nice data structure */
		Map<String, List<PedigreePair>> parentPairData = parents.getServerResult()
																.parallelStream()
																.collect(Collectors.groupingByConcurrent(r -> r.get("node"),
																		Collectors.mapping(r -> new PedigreePair(r.get("parent"), r.get("type")), Collectors.toList())));
		Map<String, List<PedigreePair>> childPairData = children.getServerResult()
																.parallelStream()
																.collect(Collectors.groupingByConcurrent(r -> r.get("node"),
																		Collectors.mapping(r -> new PedigreePair(r.get("child"), r.get("type")), Collectors.toList())));

		parents.getDebugInfo().addAll(children.getDebugInfo());

		File resultFile = createTemporaryFile("helium", "helium");
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile)))
		{
			bw.write("# heliumInput = PEDIGREE");
			bw.newLine();
			bw.write("LineName\tParent\tParentType");

			/* Get the accession by its id */
			Accession accession = new AccessionManager().getById(userAuth, accessionId).getServerResult();

			/* Get the pedigree up the tree (parents, grandparents, ...) */
			new PedigreeWriter(bw, parentPairData, true, queryType == Pedigree.PedigreeQuery.UP_DOWN_RECURSIVE ? PEDIGREE_LEVEL_LIMIT : 1)
					.run(accession.getName(), 1);
			/* Get the pedigree down the tree (children, grandchildren, ...) */
			new PedigreeWriter(bw, childPairData, false, queryType == Pedigree.PedigreeQuery.UP_DOWN_RECURSIVE ? PEDIGREE_LEVEL_LIMIT : 1)
					.run(accession.getName(), 1);

		}
		catch (InsufficientPermissionsException e)
		{
			return new ServerResult<>(null, null);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		/* Return the filename */
		return new ServerResult<>(parents.getDebugInfo(), resultFile.getName());
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, InvalidArgumentException, InvalidColumnException, InvalidSearchQueryException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = PedigreeManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-pedigree", FileType.txt.name());

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, result);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		return new ServerResult<>(streamer.getDebugInfo(), result.getName());
	}

	@Override
	public ServerResult<Boolean> exists(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		String query;

		if (id != null)
		{
			query = "SELECT COUNT(1) AS count FROM pedigrees WHERE pedigrees.parent_id = ? OR pedigrees.germinatebase_id = ?";
		}
		else
		{
			query = "SELECT COUNT(1) AS count FROM pedigrees";
		}

		ValueQuery q = new ValueQuery(query, userAuth);

		if (id != null)
			q.setLong(id)
			 .setLong(id);

		ServerResult<Integer> count = q
				.run("count")
				.getInt(0);

		return new ServerResult<>(count.getDebugInfo(), count.getServerResult() > 0);
	}

	public static class PedigreePair
	{
		public String name;
		public String type;

		public PedigreePair(String name, String type)
		{
			this.name = name;
			this.type = type;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			PedigreePair that = (PedigreePair) o;

			if (!name.equals(that.name)) return false;
			return type.equals(that.type);
		}

		@Override
		public int hashCode()
		{
			int result = name.hashCode();
			result = 31 * result + type.hashCode();
			return result;
		}
	}
}