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
import java.util.Map;
import java.util.function.*;
import java.util.stream.*;

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Locale;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class LicenseDataManager extends AbstractManager<LicenseData>
{
	private static final String SELECT_FOR_LICENSE = "SELECT * FROM `licensedata` LEFT JOIN `licenses` ON `licenses`.`id` = `licensedata`.`license_id` LEFT JOIN `locales` ON `locales`.`id` = `licensedata`.`license_id` WHERE `licenses`.`id` = ?";

	@Override
	protected String getTable()
	{
		return "licensedata";
	}

	@Override
	protected DatabaseObjectParser<LicenseData> getParser()
	{
		return LicenseData.Parser.Inst.get();
	}

	public static ServerResult<Map<Locale, LicenseData>> getForLicense(UserAuth user, Long id) throws DatabaseException
	{
		ServerResult<List<LicenseData>> data = new DatabaseObjectQuery<LicenseData>(SELECT_FOR_LICENSE, user)
				.setLong(id)
				.run()
				.getObjects(LicenseData.Parser.Inst.get());

		Map<Locale, LicenseData> result = data.getServerResult().stream()
											  .collect(Collectors.toMap(LicenseData::getLocale, Function.identity()));

		return new ServerResult<>(data.getDebugInfo(), result);
	}
}
