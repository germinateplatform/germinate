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

package jhi.germinate.server.util;

import java.util.*;

import javax.servlet.http.*;

import jhi.germinate.server.manager.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.InvalidSessionException.*;

/**
 * {@link Session} is a class containing methods to check session ids and user credentials.
 *
 * @author Sebastian Raubach
 */
public class Session
{
	public static final String SID  = "sid";
	public static final String USER = "user";

	public static final String GENOTYPE_MAP  = "genotypeMap";
	public static final String GENOTYPE_DATA = "genotypeData";

	/**
	 * Checks if the current session is still valid
	 *
	 * @param payloadSessionId The session id (transmitted in the request as the payload)
	 * @param request          The http request containing the cookies
	 */
	public static void checkSession(String payloadSessionId, HttpServletRequest request) throws InvalidSessionException
	{
		String cookieSessionId = null;
		String sessionSessionId = null;

        /*
		 * For IE and Chrome we have to make sure the HTTPServletRequest and the
         * Cookies are really set
         */
		if (request != null)
		{
			sessionSessionId = (String) request.getSession().getAttribute(SID);

			if (request.getCookies() != null)
			{
				Optional<Cookie> match = Arrays.stream(request.getCookies())
											   .filter(cookie -> Objects.equals(cookie.getName(), SID))
											   .findFirst();

				if (match.isPresent())
					cookieSessionId = match.get().getValue();
			}
		}

        /* Check if one of the properties is missing altogether */
		if (StringUtils.isEmpty(cookieSessionId))
			throw new InvalidSessionException(InvalidProperty.INVALID_COOKIE);
		else if (StringUtils.isEmpty(payloadSessionId))
			throw new InvalidSessionException(InvalidProperty.INVALID_PAYLOAD);
		else if (StringUtils.isEmpty(sessionSessionId))
			throw new InvalidSessionException(InvalidProperty.INVALID_SESSION);

        /* If they are all identical, just return */
		else if (StringUtils.areEqual(payloadSessionId, cookieSessionId, sessionSessionId))
			return;

        /* If they are all distinct, we don't know which one is the wrong one */
		else if (StringUtils.areDistinct(payloadSessionId, cookieSessionId, sessionSessionId))
			throw new InvalidSessionException(InvalidProperty.UNKNOWN);

        /* Else one of them has to be the odd one, find out which one */
		else if (StringUtils.areEqual(payloadSessionId, payloadSessionId))
			throw new InvalidSessionException(InvalidProperty.INVALID_COOKIE);
		else if (StringUtils.areEqual(cookieSessionId, sessionSessionId))
			throw new InvalidSessionException(InvalidProperty.INVALID_PAYLOAD);
		else if (StringUtils.areEqual(cookieSessionId, payloadSessionId))
			throw new InvalidSessionException(InvalidProperty.INVALID_SESSION);

        /* If we get here, everything is fine... */
	}

	/**
	 * Checks if the current session is still valid
	 *
	 * @param requestProperties The {@link RequestProperties}
	 * @param request           The http request containing the cookies
	 * @param response          The http response
	 */
	public static void checkSession(RequestProperties requestProperties, HttpServletRequest request) throws InvalidSessionException
	{
		checkSession(requestProperties.getSessionId(), request);
	}

	public static void checkSession(RequestProperties properties, BaseRemoteServiceServlet servlet) throws InvalidSessionException
	{
		checkSession(properties, servlet.getRequest());
	}

	/**
	 * Generates and returns a session id (UUID)
	 *
	 * @param username The username
	 * @param password The password
	 * @return The universally unique id
	 * @throws LoginRegistrationException Thrown if any of the settings aren't correct
	 * @throws DatabaseException          Thrown if the query fails on the server
	 */
	public static String generateSessionIdAndCheckDetails(String username, String password) throws LoginRegistrationException, DatabaseException
	{
		GatekeeperUserWithPassword userGlobal = GatekeeperUserManager.getForUsernameGlobal(username);
		GatekeeperUserWithPassword userLocal = GatekeeperUserManager.getForUsernameAndSystem(username);

        /* Check if the user exists at all */
		if (userGlobal == null)
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.USERNAME_PASSWORD_WRONG);
		}
		/* Check if the user has access to this database */
		else if (userLocal == null)
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.INSUFFICIENT_PERMISSIONS);
		}
		/* Check if the user is suspended */
		else if (userLocal.isSuspended())
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.USER_SUSPENDED);
		}
		/* Compare supplied password with stored password */
		else if (userLocal.getPassword() == null || !BCrypt.checkpw(password, userLocal.getPassword()))
		{
			throw new LoginRegistrationException(LoginRegistrationException.Reason.USERNAME_PASSWORD_WRONG);
		}
		/* Else everything is ok, return the new session id */
		else
		{
			/* The salt might have changed in the meantime, update the password */
			GatekeeperUserManager.updatePassword(userLocal.getId(), password);

			return generateSessionId();
		}
	}

	/**
	 * Generates a new session id
	 *
	 * @return The session id
	 */
	public static String generateSessionId()
	{
		return UUID.randomUUID().toString();
	}
}
