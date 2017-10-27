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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Collaborator extends DatabaseObject
{
	public static final String ID             = "collaborators.id";
	public static final String FIRST_NAME     = "collaborators.first_name";
	public static final String LAST_NAME      = "collaborators.last_name";
	public static final String EMAIL          = "collaborators.email";
	public static final String PHONE          = "collaborators.phone";
	public static final String INSTITUTION_ID = "collaborators.institution_id";

	private String      firstName;
	private String      lastName;
	private String      email;
	private String      phone;
	private Institution institution;
	private Long        createdOn;
	private Long        updatedOn;

	public Collaborator()
	{
	}

	public Collaborator(Long id)
	{
		super(id);
	}

	public String getFirstName()
	{
		return firstName;
	}

	public Collaborator setFirstName(String firstName)
	{
		this.firstName = firstName;
		return this;
	}

	public String getLastName()
	{
		return lastName;
	}

	public Collaborator setLastName(String lastName)
	{
		this.lastName = lastName;
		return this;
	}

	public String getEmail()
	{
		return email;
	}

	public Collaborator setEmail(String email)
	{
		this.email = email;
		return this;
	}

	public String getPhone()
	{
		return phone;
	}

	public Collaborator setPhone(String phone)
	{
		this.phone = phone;
		return this;
	}

	public Institution getInstitution()
	{
		return institution;
	}

	public Collaborator setInstitution(Institution institution)
	{
		this.institution = institution;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Collaborator setCreatedOn(Date createdOn)
	{
		if (createdOn == null)
			this.createdOn = null;
		else
			this.createdOn = createdOn.getTime();
		return this;
	}

	public Collaborator setCreatedOn(Long createdOn)
	{
		this.createdOn = createdOn;
		return this;
	}

	public Long getUpdatedOn()
	{
		return updatedOn;
	}

	public Collaborator setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	public Collaborator setUpdatedOn(Long updatedOn)
	{
		this.updatedOn = updatedOn;
		return this;
	}

	@Override
	public String toString()
	{
		return "Collaborator{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", institution=" + institution +
				", createdOn=" + createdOn +
				", updatedOn=" + updatedOn +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Collaborator.Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Collaborator>
	{
		private static DatabaseObjectCache<Institution> INSTITUTION_CACHE;

		private Parser()
		{
			INSTITUTION_CACHE = createCache(Institution.class, InstitutionManager.class);
		}

		@Override
		public Collaborator parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
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
					Collaborator collaborator = new Collaborator(id)
							.setFirstName(row.getString(FIRST_NAME))
							.setLastName(row.getString(LAST_NAME))
							.setEmail(row.getString(EMAIL))
							.setPhone(row.getString(PHONE))
							.setInstitution(INSTITUTION_CACHE.get(user, row.getLong(INSTITUTION_ID), row, foreignsFromResultSet))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					return collaborator;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		public static final class Inst
		{
			public static Parser get()
			{
				return Parser.Inst.InstanceHolder.INSTANCE;
			}

			/**
			 * {@link Comment.Parser.Inst.InstanceHolder} is loaded on the first execution of {@link Comment.Parser.Inst#get()} or the first access to
			 * {@link Comment.Parser.Inst.InstanceHolder#INSTANCE}, not before.
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
		}
	}
}
