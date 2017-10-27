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

package jhi.germinate.client.util.handler;

import jhi.germinate.client.*;
import jhi.germinate.client.util.event.*;

/**
 * @author Sebastian Raubach
 */
public class PageNavigationHandler implements PageNavigationEvent.PageNavigationEventHandler
{
	@Override
	public void onPageNavigation(PageNavigationEvent event)
	{
		ModuleCore.onPageNavigation(event);
	}
}
