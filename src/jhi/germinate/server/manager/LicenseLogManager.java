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

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class LicenseLogManager extends AbstractManager<LicenseLog>
{
	private static final String SELECT_FOR_USER_AND_LICENSE = "SELECT * FROM `licenselogs` WHERE `license_id` = ? AND `user_id` = ?";
	private static final String INSERT                      = "INSERT INTO `licenselogs` (`user_id`, `license_id`, `accepted_on`) VALUES (?, ?, ?)";

	@Override
	protected String getTable()
	{
		return "licenselogs";
	}

	@Override
	protected DatabaseObjectParser<LicenseLog> getParser()
	{
		return LicenseLog.Parser.Inst.get();
	}

	public static ServerResult<LicenseLog> getForUserAndLicense(Long licenseId, UserAuth user) throws DatabaseException
	{
		return new DatabaseObjectQuery<LicenseLog>(SELECT_FOR_USER_AND_LICENSE, user)
				.setLong(licenseId)
				.setLong(user.getId())
				.run()
				.getObject(LicenseLog.Parser.Inst.get());
	}

	public static ServerResult<Boolean> update(UserAuth userAuth, List<LicenseLog> logs) throws DatabaseException
	{
		ServerResult<Boolean> result = new ServerResult<>(DebugInfo.create(userAuth), false);

		for (LicenseLog log : logs)
		{
			// If authentication is disabled, cache the accepted licenses in the session
			if (!PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION))
			{
				userAuth.addAcceptedLicenseId(log.getLicense());
			}
			else
			{
				if (log.getUser() != null && log.getUser() > 0)
				{
					ServerResult<List<Long>> ids = new ValueQuery(INSERT, userAuth)
							.setLong(log.getUser())
							.setLong(log.getLicense())
							.setTimestamp(new Date(log.getAcceptedOn()))
							.execute();

					result.getDebugInfo().addAll(ids.getDebugInfo());
					result.setServerResult(result.getServerResult() || ids.getServerResult().size() > 0);
				}
			}
		}
		return result;
	}
}
