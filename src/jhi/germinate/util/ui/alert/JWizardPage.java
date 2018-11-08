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

import javax.swing.*;

/**
 * A page that can be added to a {@link JWizardDialog} by calling {@link JWizardDialog#addPage(JWizardPage)}.
 *
 * @author Sebastian Raubach
 */
public abstract class JWizardPage extends JPanel
{
	/**
	 * The title of the current page
	 *
	 * @return The title of the current page
	 */
	abstract String getTitle();

	/**
	 * Checks if the wizard dialog can proceed to the next page.
	 *
	 * @return <code>true</code> if the wizard dialog can proceed to the next page.
	 */
	abstract boolean canContinue();
}
