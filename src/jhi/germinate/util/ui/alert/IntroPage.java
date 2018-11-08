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

import java.awt.*;

import javax.swing.*;

import jhi.germinate.util.ui.*;

/**
 * @author Sebastian Raubach
 */
public class IntroPage extends JWizardPage
{
	public IntroPage(TemplateType type)
	{
		super();

		setLayout(new BorderLayout());
		add(new JLabel("<html>This wizard will guide you through the import process.<br/>You selected the Germinate template for: " + type.getDescription() + "</html>"), BorderLayout.CENTER);
	}

	@Override
	String getTitle()
	{
		return "Introduction";
	}

	@Override
	boolean canContinue()
	{
		return true;
	}
}
