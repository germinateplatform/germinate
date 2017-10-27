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

package jhi.germinate.client.widget.input;

import com.google.gwt.editor.client.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.form.error.*;
import org.gwtbootstrap3.client.ui.form.validator.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;

/**
 * @author Sebastian Raubach
 */
public class IntegerTextBox extends TextBox implements Validator<String>
{
	public IntegerTextBox()
	{
		super();

		addValidator(this);
		setValidateOnBlur(true);
	}

	public void setValue(Integer value)
	{
		if (value == null)
			clear();
		else
			setValue(NumberUtils.INTEGER_FORMAT.format(value));
	}

	public Integer getIntegerValue()
	{
		try
		{
			return Integer.parseInt(getValue());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	public int getPriority()
	{
		return 0;
	}

	@Override
	public List<EditorError> validate(Editor<String> editor, String value)
	{
		List<EditorError> result = new ArrayList<>();

		try
		{
			Integer.parseInt(value);
		}
		catch (Exception e)
		{
			result.add(new BasicEditorError(this, value, Text.LANG.notificationNotANumber()));
		}

		return result;
	}
}
