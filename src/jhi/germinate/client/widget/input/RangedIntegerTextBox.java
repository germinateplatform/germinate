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

package jhi.germinate.client.widget.input;

import com.google.gwt.editor.client.*;

import org.gwtbootstrap3.client.ui.form.error.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;

/**
 * @author Sebastian Raubach
 */
public class RangedIntegerTextBox extends IntegerTextBox
{
	private int min;
	private int max;

	public RangedIntegerTextBox()
	{
		this(-Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public RangedIntegerTextBox(int min, int max)
	{
		super();

		this.min = min;
		this.max = max;

		addValidator(this);
	}

	public void setMin(String min)
	{
		try
		{
			this.min = Integer.parseInt(min);
		}
		catch (Exception e)
		{
		}
	}

	public void setMax(String max)
	{
		try
		{
			this.max = Integer.parseInt(max);
		}
		catch (Exception e)
		{
		}
	}

	public void setMin(int min)
	{
		this.min = min;
	}

	public void setMax(int max)
	{
		this.max = max;
	}

	public void setValue(Integer value)
	{
		if (value == null)
			clear();
		else
			setValue(NumberUtils.INTEGER_FORMAT.format(value));
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
			int i = Integer.parseInt(value);

			if (i > max || i < min)
				result.add(new BasicEditorError(this, value, Text.LANG.notificationNumberNotInRange(i, min, max)));
		}
		catch (Exception e)
		{
			result.add(new BasicEditorError(this, value, Text.LANG.notificationNotANumber()));
		}

		return result;
	}
}
