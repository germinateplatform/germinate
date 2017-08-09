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

package jhi.germinate.shared.datastructure.database;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class Mcpd implements Serializable
{
	private Accession     accession;
	private List<Storage> storage;
	private AttributeData remarks;

	public Mcpd()
	{
	}

	public Accession getAccession()
	{
		return accession;
	}

	public Mcpd setAccession(Accession accession)
	{
		this.accession = accession;
		return this;
	}

	public List<Storage> getStorage()
	{
		return storage;
	}

	public Mcpd setStorage(List<Storage> storage)
	{
		this.storage = storage;
		return this;
	}

	public AttributeData getRemarks()
	{
		return remarks;
	}

	public Mcpd setRemarks(AttributeData remarks)
	{
		this.remarks = remarks;
		return this;
	}
}
