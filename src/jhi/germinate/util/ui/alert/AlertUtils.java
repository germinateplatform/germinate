/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.util.ui.alert;

import java.util.*;

import javax.swing.*;

import jhi.germinate.util.ui.*;

/**
 * @author Sebastian Raubach
 */
public class AlertUtils
{
	/**
	 * Asks the user to specify the {@link TemplateType} of the given file.
	 *
	 * @param frame The parent {@link JFrame}
	 * @return The {@link TemplateType} the user selects (or <code>null</code>).
	 */
	public static TemplateType askForTemplateType(JFrame frame)
	{
		List<TemplateType> types = new ArrayList<>(Arrays.asList(TemplateType.values()));
		types.remove(TemplateType.unknown);

		return (TemplateType) JOptionPane.showInputDialog(
				frame,
				"Couldn't determine template type automatically.\nPlease tell us what template type this file is:",
				"Template type",
				JOptionPane.QUESTION_MESSAGE,
				null,
				types.toArray(),
				TemplateType.mcpd);
	}
}
