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

package jhi.germinate.shared.datastructure.database;

import com.google.gwt.core.shared.*;
import com.google.gwt.safehtml.shared.*;

import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.regex.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.database.query.writer.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Compound extends DatabaseObject
{
	private static final long serialVersionUID = -1373268800192085814L;

	public static final String ID                = "compounds.id";
	public static final String NAME              = "compounds.name";
	public static final String DESCRIPTION       = "compounds.description";
	public static final String MOLECULAR_FORMULA = "compounds.molecular_formula";
	public static final String MONOISOTOPIC_MASS = "compounds.monoisotopic_mass";
	public static final String AVERAGE_MASS      = "compounds.average_mass";
	public static final String COMPOUND_CLASS    = "compounds.compound_class";
	public static final String UNIT_ID           = "compounds.unit_id";
	public static final String CREATED_ON        = "compounds.created_on";
	public static final String UPDATED_ON        = "compounds.updated_on";

	private String name;
	private String description;
	private String molecularFormula;
	private String molecularFormulaHtml;
	private Double monoisotopicMass;
	private Double averageMass;
	private String compoundClass;
	private Unit   unit;
	private Long   createdOn;
	private Long   updatedOn;

	public Compound()
	{
	}

	public Compound(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public Compound setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Compound setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public String getMolecularFormula()
	{
		return molecularFormula;
	}

	public Compound setMolecularFormula(String molecularFormula)
	{
		this.molecularFormula = molecularFormula;
		return this;
	}

	public Double getMonoisotopicMass()
	{
		return monoisotopicMass;
	}

	public Compound setMonoisotopicMass(Double monoisotopicMass)
	{
		this.monoisotopicMass = monoisotopicMass;
		return this;
	}

	public Double getAverageMass()
	{
		return averageMass;
	}

	public Compound setAverageMass(Double averageMass)
	{
		this.averageMass = averageMass;
		return this;
	}

	public String getCompoundClass()
	{
		return compoundClass;
	}

	public Compound setCompoundClass(String compoundClass)
	{
		this.compoundClass = compoundClass;
		return this;
	}

	public Unit getUnit()
	{
		return unit;
	}

	public Compound setUnit(Unit unit)
	{
		this.unit = unit;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Compound setCreatedOn(Date createdOn)
	{
		if (createdOn == null)
			this.createdOn = null;
		else
			this.createdOn = createdOn.getTime();
		return this;
	}

	public Long getUpdatedOn()
	{
		return updatedOn;
	}

	public Compound setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	@GwtIncompatible
	private Compound setFormattedMolecularFormula()
	{
		try
		{
			Pattern pattern = Pattern.compile("([A-Z][a-z]*)(\\d+)");
			Matcher matcher = pattern.matcher(molecularFormula);

			StringBuilder output = new StringBuilder();
			boolean atLeastOne = false;
			while (matcher.find())
			{
				int start = matcher.start();
				int end = matcher.end();
				output.append(matcher.group(1))
					  .append("<sub>")
					  .append(molecularFormula.substring(start + 1, end))
					  .append("</sub>");
				atLeastOne = true;
			}

			if (atLeastOne)
				molecularFormulaHtml = output.toString();
			else
				molecularFormulaHtml = molecularFormula;
		}
		catch (Exception e)
		{
			molecularFormulaHtml = molecularFormula;
		}

		return this;
	}

	public SafeHtml getFormattedMolecularFormula()
	{
		SafeHtml html;

		if (!StringUtils.isEmpty(molecularFormulaHtml))
			html = SafeHtmlUtils.fromTrustedString(molecularFormulaHtml);
		else
			html = SafeHtmlUtils.fromTrustedString("");

		return html;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Compound>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE},
			 * not before.
			 * <p/>
			 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
			 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
			 *
			 * @author Sebastian Raubach
			 */
			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}

			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		private static DatabaseObjectCache<Unit> UNIT_CACHE;

		private Parser()
		{
			UNIT_CACHE = createCache(Unit.class, UnitManager.class);
		}

		@Override
		public Compound parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
				{
					return null;
				}
				else
				{
					Compound compound = new Compound(id)
							.setName(row.getString(NAME))
							.setDescription(row.getString(DESCRIPTION))
							.setMolecularFormula(row.getString(MOLECULAR_FORMULA))
							.setFormattedMolecularFormula()
							.setMonoisotopicMass(row.getDouble(MONOISOTOPIC_MASS))
							.setAverageMass(row.getDouble(AVERAGE_MASS))
							.setCompoundClass(row.getString(COMPOUND_CLASS))
							.setUnit(UNIT_CACHE.get(user, row.getLong(UNIT_ID), row, foreignsFromResultSet))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					try
					{
						compound.setExtra(COUNT, row.getLong(COUNT));
					}
					catch (Exception e)
					{
					}

					return compound;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Compound>
	{
		public static final class Inst
		{
			public static Writer get()
			{
				return Writer.Inst.InstanceHolder.INSTANCE;
			}

			private static final class InstanceHolder
			{
				private static final Writer INSTANCE = new Writer();
			}
		}

		@Override
		public void write(Database database, Compound object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `compounds` (" + NAME + ", " + DESCRIPTION + ", " + MOLECULAR_FORMULA + ", " + MONOISOTOPIC_MASS + ", " + AVERAGE_MASS + ", " + COMPOUND_CLASS + ", " + UNIT_ID + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setString(object.getName())
					.setString(object.getDescription())
					.setString(object.getMolecularFormula())
					.setDouble(object.getMonoisotopicMass())
					.setDouble(object.getAverageMass())
					.setString(object.getCompoundClass())
					.setLong(object.getUnit() != null ? object.getUnit().getId() : null);

			if (object.getCreatedOn() != null)
				query.setTimestamp(new Date(object.getCreatedOn()));
			else
				query.setNull(Types.TIMESTAMP);
			if (object.getUpdatedOn() != null)
				query.setTimestamp(new Date(object.getUpdatedOn()));
			else
				query.setNull(Types.TIMESTAMP);

			ServerResult<List<Long>> ids = query.execute(false);

			if (ids != null && !CollectionUtils.isEmpty(ids.getServerResult()))
				object.setId(ids.getServerResult().get(0));
		}
	}
}
