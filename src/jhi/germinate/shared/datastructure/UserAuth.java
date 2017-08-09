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

package jhi.germinate.shared.datastructure;

import com.google.gwt.core.shared.*;

import java.io.*;

import jhi.germinate.server.util.*;

/**
 * {@link UserAuth} is a simple bean class containing user id, session id and the path of the application
 *
 * @author Sebastian Raubach
 */
public class UserAuth implements Serializable
{
	private static final long serialVersionUID = -4829970355425355192L;

	private Long id = -1000L;
	private String  username;
	private boolean isAdmin;
	private String  sessionId;
	private String  path;
	private int     cookieLifespanMinutes;

	public UserAuth()
	{
	}

	public UserAuth(Long id, String username, boolean isAdmin, String sessionId, String path, int cookieLifespanMinutes)
	{
		this.id = id;
		this.username = username;
		this.isAdmin = isAdmin;
		this.sessionId = sessionId;
		this.path = path;
		this.cookieLifespanMinutes = cookieLifespanMinutes;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isAdmin()
	{
		return isAdmin;
	}

	public void setIsAdmin(boolean isAdmin)
	{
		this.isAdmin = isAdmin;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public int getCookieLifespanMinutes()
	{
		return cookieLifespanMinutes;
	}

	public void setCookieLifespanMinutes(int cookieLifespanMinutes)
	{
		this.cookieLifespanMinutes = cookieLifespanMinutes;
	}

	@GwtIncompatible
	public static UserAuth getFromSession(BaseRemoteServiceServlet servlet, RequestProperties properties)
	{
		UserAuth userAuth = (UserAuth) servlet.getRequest().getSession().getAttribute(Session.USER);

		if (userAuth == null)
			userAuth = new UserAuth();

		if (userAuth != null && userAuth.getId() == null)
		{
			try
			{
				userAuth.setId(Long.parseLong(properties.getUserId()));
			}
			catch (NullPointerException | NumberFormatException e)
			{
			}
		}

		return userAuth;
	}

	@Override
	public String toString()
	{
		return "UserAuth{" +
				"id='" + id + '\'' +
				", username='" + username + '\'' +
				", isAdmin=" + isAdmin +
				", sessionId='" + sessionId + '\'' +
				", path='" + path + '\'' +
				", cookieLifespanMinutes=" + cookieLifespanMinutes +
				'}';
	}
}
