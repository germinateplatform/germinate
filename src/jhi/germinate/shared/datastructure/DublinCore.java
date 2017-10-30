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

package jhi.germinate.shared.datastructure;

import com.google.gwt.core.shared.*;
import com.google.web.bindery.autobean.shared.*;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public interface DublinCore
{
	List<String> getTitle();

	void setTitle(List<String> title);

	List<String> getCreator();

	void setCreator(List<String> creator);

	List<String> getSubject();

	void setSubject(List<String> subject);

	List<String> getDescription();

	void setDescription(List<String> description);

	List<String> getPublisher();

	void setPublisher(List<String> publisher);

	List<String> getContributor();

	void setContributor(List<String> contributor);

	List<String> getDate();

	void setDate(List<String> date);

	List<String> getType();

	void setType(List<String> type);

	List<String> getFormat();

	void setFormat(List<String> format);

	List<String> getIdentifier();

	void setIdentifier(List<String> identifier);

	List<String> getSource();

	void setSource(List<String> source);

	List<String> getLanguage();

	void setLanguage(List<String> language);

	List<String> getRelation();

	void setRelation(List<String> relation);

	List<String> getCoverage();

	void setCoverage(List<String> coverage);

	List<String> getRights();

	void setRights(List<String> rights);

	public interface DublinCoreFactory extends AutoBeanFactory
	{
		AutoBean<DublinCore> dublinCore();

		final class Inst
		{
			public static DublinCoreFactory get()
			{
				return DublinCoreFactory.Inst.InstanceHolder.INSTANCE;
			}

			private static final class InstanceHolder
			{
				private static final DublinCoreFactory INSTANCE = GWT.create(DublinCoreFactory.class);
			}

		}
	}
}


