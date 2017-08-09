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

package jhi.germinate.shared.exception;

import java.io.*;

import jhi.germinate.shared.datastructure.*;

/**
 * {@link LoginRegistrationException} extends {@link BaseException} to indicate that the credentials used to log in do not match those in the
 * database.
 *
 * @author Sebastian Raubach
 */
public class LoginRegistrationException extends BaseException implements Serializable
{
	public enum Reason
	{
		USERNAME_PASSWORD_WRONG,
		USERNAME_DOESNT_EXIST,
		INSUFFICIENT_PERMISSIONS,
		LOGIN_UNAVAILABLE,
		REGISTRATION_UNAVAILABLE,
		USER_SUSPENDED,
		USER_ALREADY_HAS_ACCESS,
		USER_ALREADY_REQUESTED_ACCESS,
		USERNAME_ALREADY_EXISTS,
		GATEKEEPER_UNAVAILABLE,
		GATEKEEPER_EMAIL_FAILED,
		GATEPEEKER_REJECTED_REQUEST,
		UNKNOWN
	}

	private Reason   reason;
	private UserAuth userAuth;

	public LoginRegistrationException()
	{
	}

	public LoginRegistrationException(Reason reason)
	{
		this.reason = reason;
	}

	public LoginRegistrationException(Reason reason, UserAuth userAuth)
	{
		this.reason = reason;
		this.userAuth = userAuth;
	}

	public Reason getReason()
	{
		return reason;
	}

	public LoginRegistrationException setReason(Reason reason)
	{
		this.reason = reason;
		return this;
	}

	public UserAuth getUserAuth()
	{
		return userAuth;
	}

	public LoginRegistrationException setUserAuth(UserAuth userAuth)
	{
		this.userAuth = userAuth;
		return this;
	}
}
