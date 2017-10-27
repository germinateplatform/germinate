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

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;

import java.io.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Map;

import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.database.Database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link NewsServiceImpl} is the implementation of {@link NewsService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/user"})
public class UserServiceImpl extends BaseRemoteServiceServlet implements UserService
{
	private static final long serialVersionUID = -945917674720942283L;

	private static final String GATEKEEPER_URL_PATTERN = "gatekeeper/germinate";

	private static final String GATEKEEPER_ERROR_EMAIL        = "GATEKEEPER_ERROR_EMAIL";
	private static final String GATEKEEPER_ERROR_INVALID_DATA = "GATEKEEPER_ERROR_INVALID_DATA";

	private static final String INSERT_UNAPPROVED_USER = "INSERT INTO unapproved_users (user_username, user_password, user_full_name, user_email_address, institution_id, institution_name, institution_acronym, institution_address, database_system_id, created_on, activation_key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM database_systems WHERE server_name = ? AND system_name = ?), NOW(), ?)";
	private static final String DELETE_UNAPPROVED_USER = "DELETE FROM unapproved_users WHERE id IN (%s)";

	private static final String QUERY_USER_HAS_ACCESS_TO_DATABASE  = "SELECT 1 AS `exists` FROM user_has_access_to_databases WHERE user_id = ?       AND database_id =        (SELECT id FROM database_systems WHERE system_name = ? AND server_name = ?)";
	private static final String QUERY_USER_HAS_REQUESTED_ACCESS    = "SELECT 1 AS `exists` FROM unapproved_users             WHERE user_username = ? AND database_system_id = (SELECT id FROM database_systems WHERE system_name = ? AND server_name = ?)";
	private static final String INSERT_USER_HAS_ACCESS_TO_DATABASE = "INSERT INTO user_has_access_to_databases (user_id, database_id, user_type_id) VALUES (?, (SELECT id FROM database_systems WHERE system_name = ? AND server_name = ?), 2)";
	private static final String INSERT_ACCESS_REQUEST              = "INSERT INTO access_requests (user_id, database_system_id, activation_key, created_on) VALUE (?, (SELECT id FROM database_systems WHERE system_name = ? AND server_name = ?), ?, NOW())";
	private static final String DELETE_ACCESS_REQUEST              = "DELETE FROM access_requests WHERE id IN (%s)";

	private static final String QUERY_USERNAME_EXISTS = "(SELECT DISTINCT username FROM users) UNION (SELECT DISTINCT user_username FROM unapproved_users WHERE has_been_rejected != 1)";

	private static final String QUERY_INSTITUTES = "SELECT DISTINCT * FROM institutions ORDER BY institutions.name ASC";
	private static final String INSERT_INSTITUTE = "INSERT INTO institutions (name, acronym, address) VALUES (?, ?, ?)";

	private static final Map<Long, HttpSession> SESSIONS = new HashMap<>();

	private static final int SESSION_LIFETIME_MIN = PropertyReader.getInteger(ServerProperty.GERMINATE_COOKIE_LIFESPAN_MINUTES);

	@Override
	public void logout(RequestProperties properties) throws InvalidSessionException, LoginRegistrationException
	{
		Session.checkSession(properties, this);

		HttpSession session = getThreadLocalRequest().getSession();
		session.removeAttribute(Session.USER);
		session.invalidate();
	}

	@Override
	public UserAuth login(RequestProperties properties, UserCredentials credentials) throws LoginRegistrationException, DatabaseException
	{
		/* Get the relative path of the current instance of Germinate3 */
		String path = PropertyReader.getContextPath(getThreadLocalRequest());

		/* Get the previous authentication information (if exists) */
		UserAuth oldUserAuth = getOrCreate(properties, credentials);
		oldUserAuth.setPath(path);
		oldUserAuth.setCookieLifespanMinutes(SESSION_LIFETIME_MIN);

		/* Check the session */
		boolean sessionIsValid = true;
		try
		{
			Session.checkSession(properties, this);
		}
		catch (InvalidSessionException e)
		{
			sessionIsValid = false;
		}

        /* If we don't use authentication, throw an Exception */
		if (!PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION))
		{
			/* Generate a new session id */
			if (!sessionIsValid)
			{
				properties.setSessionId(Session.generateSessionId());
			}

			/* Store everything in the session */
			oldUserAuth.setSessionId(properties.getSessionId());
			oldUserAuth.setCookieLifespanMinutes(Integer.MAX_VALUE);
			storeInSession(Session.SID, properties.getSessionId());
			getThreadLocalRequest().getSession().setMaxInactiveInterval(-1);

			/* Then throw an exception */
			throw new LoginRegistrationException(LoginRegistrationException.Reason.LOGIN_UNAVAILABLE, oldUserAuth);
		}

        /* If the session id is invalid */
		if (!sessionIsValid)
		{
			/* Generate a new one but check the user credentials first */
			properties.setSessionId(Session.generateSessionIdAndCheckDetails(credentials.getUsername(), credentials.getPassword()));

            /* If this doesn't work, just return */
			if (StringUtils.isEmpty(properties.getSessionId()))
				return null;
		}

        /* Extend the session */
		storeInSession(Session.SID, properties.getSessionId());
		storeInSession(Session.USER, oldUserAuth);
		getThreadLocalRequest().getSession().setMaxInactiveInterval(SESSION_LIFETIME_MIN * 60);

		SESSIONS.put(oldUserAuth.getId(), getThreadLocalRequest().getSession());

		/* Set a cookie */
		setCookie(getThreadLocalResponse(), Session.SID, properties.getSessionId(), oldUserAuth);

		return oldUserAuth;
	}

	private synchronized UserAuth getOrCreate(RequestProperties properties, UserCredentials credentials) throws DatabaseException
	{
		UserAuth auth = UserAuth.getFromSession(this, properties);

		String providedUsername = credentials.getUsername();
		String sessionUsername = auth == null ? null : auth.getUsername();

		if (auth == null || (!Objects.equals(providedUsername, "") && !Objects.equals(providedUsername, sessionUsername)))
		{
			GatekeeperUserWithPassword user = GatekeeperUserManager.getForUsernameAndSystem(credentials.getUsername());

			auth = new UserAuth();
			auth.setUsername(credentials.getUsername());

			if (user != null)
			{
				auth.setId(user.getId());
				auth.setIsAdmin(user.isAdmin());
			}
		}

		return auth;
	}

	private static void setCookie(HttpServletResponse response, String key, String value, UserAuth userAuth)
	{
		if (!StringUtils.isEmpty(key, value) && userAuth != null)
		{
			Cookie cookie = new Cookie(key, value);
//			cookie.setHttpOnly(true);
			cookie.setPath(userAuth.getPath());
			cookie.setMaxAge(PropertyReader.getInteger(ServerProperty.GERMINATE_COOKIE_LIFESPAN_MINUTES) * 60);
			response.addCookie(cookie);
		}
	}

	public static void invalidateSessionAttributes()
	{
		for (HttpSession session : SESSIONS.values())
		{
			session.removeAttribute(Session.SID);
			session.removeAttribute(Session.USER);
		}
	}

	@Override
	public void register(RequestProperties properties, UnapprovedUser user) throws DatabaseException, LoginRegistrationException, SystemInReadOnlyModeException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		if (user.toRegister)
		{
			registerNewUser(properties, user);
		}
		else
		{
			registerForInstance(properties, user);
		}
	}

	private void registerNewUser(RequestProperties properties, UnapprovedUser user) throws DatabaseException, LoginRegistrationException
	{
		/* Check if we actually allow registration */
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) && PropertyReader.getBoolean(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_ENABLED))
		{
			List<String> usernames = new ValueQuery(QUERY_USERNAME_EXISTS)
					.setQueryType(QueryType.AUTHENTICATION)
					.run("username")
					.getStrings()
					.getServerResult();

			if (usernames.contains(user.userUsername))
				throw new LoginRegistrationException(LoginRegistrationException.Reason.USERNAME_ALREADY_EXISTS);

			String gatekeeperUrl = getGatekeeperUrl();

			Integer rounds = PropertyReader.getInteger(ServerProperty.GERMINATE_GATEKEEPER_BCRYPT_ROUNDS);
			String hashed = BCrypt.hashpw(user.userPassword, BCrypt.gensalt(rounds == null ? BCrypt.GENSALT_DEFAULT_LOG2_ROUNDS : rounds));

            /* If the registration needs approval, insert into the
			 * unapproved_users table */
			ValueQuery query = new ValueQuery(INSERT_UNAPPROVED_USER)
					.setQueryType(QueryType.AUTHENTICATION)
					.setString(user.userUsername)
					.setString(hashed)
					.setString(user.userFullName)
					.setString(user.userEmailAddress);

			if (user.institutionId != null)
			{
				query.setLong(user.institutionId)
					 .setNull(Types.VARCHAR)
					 .setNull(Types.VARCHAR)
					 .setNull(Types.VARCHAR);
			}
			else
			{
				query.setNull(Types.INTEGER)
					 .setString(user.institutionName)
					 .setString(user.institutionAcronym)
					 .setString(user.institutionAddress);
			}

			String uuid = UUID.randomUUID().toString();

			List<Long> ids = query.setString(PropertyReader.get(ServerProperty.DATABASE_SERVER))
								  .setString(PropertyReader.get(ServerProperty.DATABASE_NAME))
								  .setString(uuid)
								  .execute()
								  .getServerResult();

			if (CollectionUtils.isEmpty(ids))
				throw new DatabaseException("Registration failed");

			boolean needsApproval = PropertyReader.getBoolean(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_NEEDS_APPROVAL);

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(gatekeeperUrl);
			try
			{
				List<NameValuePair> nameValuePairs = new ArrayList<>(4);
				nameValuePairs.add(new BasicNameValuePair("activationKey", uuid));
				nameValuePairs.add(new BasicNameValuePair("needsApproval", Boolean.toString(needsApproval)));
				nameValuePairs.add(new BasicNameValuePair("userId", Long.toString(ids.get(0))));
				nameValuePairs.add(new BasicNameValuePair("isNewUser", Boolean.toString(true)));
				nameValuePairs.add(new BasicNameValuePair("locale", properties.getLocale()));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = client.execute(post);
				StatusLine status = response.getStatusLine();

				if (status.getStatusCode() != HttpStatus.SC_OK)
				{
					/* Get the response message */
					HttpEntity entity = response.getEntity();

					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line;

					StringBuilder builder = new StringBuilder();

					while ((line = reader.readLine()) != null)
					{
						builder.append(line);
					}

					reader.close();

					String msg = builder.toString();

					undoNewUserInsert(ids);

					handleError(msg);
				}
			}
			catch (IOException e)
			{
				undoNewUserInsert(ids);
				e.printStackTrace();
				throw new LoginRegistrationException(LoginRegistrationException.Reason.GATEKEEPER_UNAVAILABLE);
			}
		}
		else
			throw new LoginRegistrationException(LoginRegistrationException.Reason.REGISTRATION_UNAVAILABLE);
	}

	private void handleError(String msg) throws LoginRegistrationException
	{
		/* Check if it's something we might expect */
		if (msg.contains(GATEKEEPER_ERROR_EMAIL))
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.GATEKEEPER_EMAIL_FAILED);
		}
		else if (msg.contains(GATEKEEPER_ERROR_INVALID_DATA))
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.GATEPEEKER_REJECTED_REQUEST);
		}
		else
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.GATEKEEPER_UNAVAILABLE);
		}
	}

	private static String getGatekeeperUrl() throws LoginRegistrationException
	{
		/* Get the Gatekeeper URL */
		String gatekeeperURL = PropertyReader.get(ServerProperty.GERMINATE_GATEKEEPER_URL);

		if (StringUtils.isEmpty(gatekeeperURL))
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.GATEKEEPER_UNAVAILABLE);
		}
		else
		{
			if (!gatekeeperURL.endsWith("/"))
				gatekeeperURL += "/";

			gatekeeperURL += GATEKEEPER_URL_PATTERN;
		}

		return gatekeeperURL;
	}

	/**
	 * Deletes the created database items based on their id
	 *
	 * @param ids The ids
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	private static void undoNewUserInsert(List<Long> ids) throws DatabaseException
	{
		/* Delete the created entries again */
		String formatted = String.format(DELETE_UNAPPROVED_USER, Util.generateSqlPlaceholderString(ids.size()));
		new ValueQuery(formatted)
				.setQueryType(QueryType.AUTHENTICATION)
				.setLongs(ids)
				.execute();
	}

	/**
	 * Deletes the created database items based on their id
	 *
	 * @param ids The ids
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	private static void undoRequestInsert(List<Long> ids) throws DatabaseException
	{
		/* Delete the created entries again */
		String formatted = String.format(DELETE_ACCESS_REQUEST, Util.generateSqlPlaceholderString(ids.size()));
		new ValueQuery(formatted)
				.setQueryType(QueryType.AUTHENTICATION)
				.setLongs(ids)
				.execute();
	}

	@Override
	public List<Institution> getInstitutions() throws DatabaseException
	{
		return new DatabaseObjectQuery<Institution>(QUERY_INSTITUTES, null)
				.setQueryType(QueryType.AUTHENTICATION)
				.run()
				.getObjects(Institution.Parser.Inst.get())
				.getServerResult();
	}

	@Override
	public void addInstitution(Institution institution) throws DatabaseException
	{
		new ValueQuery(INSERT_INSTITUTE)
				.setQueryType(QueryType.AUTHENTICATION)
				.setString(institution.getName())
				.setString(institution.getAcronym())
				.setString(institution.getAddress())
				.execute();
	}

	public void registerForInstance(RequestProperties properties, UnapprovedUser user) throws DatabaseException, LoginRegistrationException, SystemInReadOnlyModeException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		/* Check if we actually allow registration */
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) && PropertyReader.getBoolean(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_ENABLED))
		{
			String server = PropertyReader.get(ServerProperty.DATABASE_SERVER);
			String database = PropertyReader.get(ServerProperty.DATABASE_NAME);

			GatekeeperUserWithPassword userDetails = GatekeeperUserManager.getForUsernameGlobal(user.userUsername);

            /* User doesn't exist */
			if (userDetails == null)
				throw new LoginRegistrationException(LoginRegistrationException.Reason.USERNAME_PASSWORD_WRONG);

            /* Invalid password */
			if (!BCrypt.checkpw(user.userPassword, userDetails.getPassword()))
				throw new LoginRegistrationException(LoginRegistrationException.Reason.USERNAME_PASSWORD_WRONG);

			Boolean hasAccess = new ValueQuery(QUERY_USER_HAS_ACCESS_TO_DATABASE)
					.setQueryType(QueryType.AUTHENTICATION)
					.setLong(userDetails.getId())
					.setString(database)
					.setString(server)
					.run("exists")
					.getBoolean(false)
					.getServerResult();

			if (hasAccess)
				throw new LoginRegistrationException(LoginRegistrationException.Reason.USER_ALREADY_HAS_ACCESS);

			Boolean hasRequestedAccess = new ValueQuery(QUERY_USER_HAS_REQUESTED_ACCESS)
					.setQueryType(QueryType.AUTHENTICATION)
					.setLong(userDetails.getId())
					.setString(database)
					.setString(server)
					.run("exists")
					.getBoolean(false)
					.getServerResult();

			if (hasRequestedAccess)
				throw new LoginRegistrationException(LoginRegistrationException.Reason.USER_ALREADY_REQUESTED_ACCESS);

			String uuid = UUID.randomUUID().toString();

			boolean needsApproval = PropertyReader.getBoolean(ServerProperty.GERMINATE_GATEKEEPER_REGISTRATION_NEEDS_APPROVAL);

			if (needsApproval)
			{
				String gatekeeperUrl = getGatekeeperUrl();

                /* If the registration needs approval, insert into the
				 * access_requests table */
				List<Long> ids = new ValueQuery(INSERT_ACCESS_REQUEST)
						.setQueryType(QueryType.AUTHENTICATION)
						.setLong(userDetails.getId())
						.setString(database)
						.setString(server)
						.setString(uuid)
						.execute()
						.getServerResult();

				HttpClient client = HttpClientBuilder.create()
													 .setRedirectStrategy(new LaxRedirectStrategy())
													 .build();

				HttpPost post = new HttpPost(gatekeeperUrl);
				try
				{
					List<NameValuePair> nameValuePairs = new ArrayList<>(4);
					nameValuePairs.add(new BasicNameValuePair("activationKey", uuid));
					nameValuePairs.add(new BasicNameValuePair("needsApproval", Boolean.toString(needsApproval)));
					nameValuePairs.add(new BasicNameValuePair("userId", Long.toString(userDetails.getId())));
					nameValuePairs.add(new BasicNameValuePair("isNewUser", Boolean.toString(false)));
					nameValuePairs.add(new BasicNameValuePair("locale", properties.getLocale()));
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = client.execute(post);

					StatusLine status = response.getStatusLine();

					if (status.getStatusCode() != HttpStatus.SC_OK)
					{
						/* Get the response message */
						HttpEntity entity = response.getEntity();

						BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
						String line;

						StringBuilder builder = new StringBuilder();

						while ((line = reader.readLine()) != null)
						{
							builder.append(line);
						}

						reader.close();

						String msg = builder.toString();

						undoRequestInsert(ids);

						handleError(msg);
					}
				}
				catch (IOException e)
				{
					undoRequestInsert(ids);
					e.printStackTrace();
					throw new LoginRegistrationException(LoginRegistrationException.Reason.GATEKEEPER_UNAVAILABLE);
				}
			}
			else
			{
				/* Insert the permissions straight away */
				new ValueQuery(INSERT_USER_HAS_ACCESS_TO_DATABASE)
						.setQueryType(QueryType.AUTHENTICATION)
						.setLong(userDetails.getId())
						.setString(database)
						.setString(server)
						.execute();
			}
		}
		else
			throw new LoginRegistrationException(LoginRegistrationException.Reason.REGISTRATION_UNAVAILABLE);
	}
}
