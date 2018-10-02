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

import java.io.*;
import java.util.*;

import jhi.germinate.client.util.*;

/**
 * {@link Gradient} is a class representing a color gradient backed by an array of {@link Color}s.
 *
 * @author Sebastian Raubach
 */
public class Gradient implements Serializable
{
	private static final long serialVersionUID = 1348208804095134273L;

	private double  maxValue;
	private double  minValue;
	private Color[] gradient;

	public Gradient()
	{

	}

	/**
	 * Creates an Inst of Gradient using an array of Color objects. It uses a linear interpolation between each pair of points. The parameter numSteps
	 * defines the total number of colors in the returned array, not the number of colors per segment.
	 *
	 * @param colors   An array of Color objects used for the gradient. The Color at index 0 will be the lowest color.
	 * @param numSteps The number of steps in the gradient. 250 is a good number.
	 * @param minValue the minimal of all representable values
	 * @param maxValue the maximum of all representable values
	 */
	public Gradient(Color[] colors, int numSteps, double minValue, double maxValue)
	{
		this.minValue = minValue;
		this.maxValue = maxValue;

		this.gradient = createMultiGradient(colors, numSteps);
	}

	/**
	 * Creates an Inst of Gradient using one of the classes constants.
	 *
	 * @param gradient One of the class constants
	 * @param minValue the minimal of all representable values
	 * @param maxValue the maximum of all representable values
	 */
	public Gradient(Color[] gradient, double minValue, double maxValue)
	{
		if (gradient == null || gradient.length < 2)
			throw new IllegalArgumentException("Invalid number of colors");

		this.minValue = minValue;
		this.maxValue = maxValue;

		this.gradient = gradient;
	}

	public static Color[] getPrimaryColorGradient()
	{
		Color[] colorArray = {Color.WHITE, Color.fromHex(GerminateSettingsHolder.getCategoricalColor(0))};

		return createMultiGradient(colorArray, 50);
	}

	public static Color[] getTemplateGradient()
	{
		List<String> colors = GerminateSettingsHolder.get().templateGradientColors.getValue();

		Color[] colorArray = new Color[colors.size()];
		for (int i = 0; i < colors.size(); i++)
		{
			colorArray[i] = Color.fromHex(colors.get(i));
		}

		return createMultiGradient(colorArray, 200);
	}

	public static Color[] createMultiGradient(Color[] colors, int numSteps)
	{
		if (colors == null || colors.length < 2)
			throw new IllegalArgumentException("Invalid number of colors");

		/* We assume a linear gradient, with equal spacing between colors. The
		 * final gradient will be made up of n 'sections', where n =
		 * colors.length - 1 */
		int numSections = colors.length - 1;
		int gradientIndex = 0;

		Color[] gradient = new Color[numSteps];
		Color[] temp;

		if (numSections < 1)
		{
			throw new IllegalArgumentException("You must pass in at least 2 colors in the array!");
		}

		for (int section = 0; section < numSections; section++)
		{
			/* we divide the gradient into (n - 1) sections, and do a regular
			 * gradient for each */
			temp = createGradient(colors[section], colors[section + 1], numSteps / numSections);
			for (Color color : temp)
			{
				/* copy the sub-gradient into the overall gradient */
				gradient[gradientIndex++] = color;
			}
		}

		if (gradientIndex < numSteps)
		{
			/* The rounding didn't work out in our favor, and there is at least
			 * one unfilled slot in the gradient[] array. We can just copy the
			 * final color there */
			for (/* nothing to initialize */; gradientIndex < numSteps; gradientIndex++)
			{
				gradient[gradientIndex] = colors[colors.length - 1];
			}
		}

		return gradient;
	}

	/**
	 * Creates an array of Color objects for use as a gradient, using a linear interpolation between the two specified colors.
	 *
	 * @param one      Color used for the bottom of the gradient
	 * @param two      Color used for the top of the gradient
	 * @param numSteps The number of steps in the gradient. 250 is a good number.
	 */
	public static Color[] createGradient(final Color one, final Color two, final int numSteps)
	{
		int r1 = one.getRed();
		int g1 = one.getGreen();
		int b1 = one.getBlue();

		int r2 = two.getRed();
		int g2 = two.getGreen();
		int b2 = two.getBlue();

		int newR;
		int newG;
		int newB;

		Color[] gradient = new Color[numSteps];
		float iNorm;
		for (int i = 0; i < numSteps; i++)
		{
			iNorm = i / (float) (numSteps - 1); // a normalized [0:1] variable
			newR = (int) (r1 + iNorm * (r2 - r1));
			newG = (int) (g1 + iNorm * (g2 - g1));
			newB = (int) (b1 + iNorm * (b2 - b1));
			gradient[i] = new Color(newR, newG, newB);
		}

		return gradient;
	}

	public Color[] getColors()
	{
		return gradient;
	}

	public void setMax(double maxValue)
	{
		this.maxValue = maxValue;
	}

	public void setMin(double minValue)
	{
		this.minValue = minValue;
	}

	/**
	 * Determines the text color of a given value. The color is based on the average of R, G and B. An average above 128 will result in the color
	 * white, and black otherwise.
	 *
	 * @param valueString The value (String representation of number)
	 * @return The text color
	 */
	public Color getTextColor(String valueString)
	{
		try
		{
			return getTextColor(Double.parseDouble(valueString));
		}
		catch (NumberFormatException e)
		{
			return Color.WHITE;
		}
	}

	/**
	 * Determines the text color of a given value. The color is based on the average of R, G and B. An average above 128 will result in the color
	 * white, and black otherwise.
	 *
	 * @param value The value
	 * @return The text color
	 */
	public Color getTextColor(double value)
	{
		if (maxValue == minValue)
			return Color.BLACK;

		double range = maxValue - minValue;
		double norm = (value - minValue) / range; // 0 < norm < 1
		int colorIndex = (int) Math.floor(norm * (gradient.length - 1));

		colorIndex = Math.max(0, colorIndex);
		colorIndex = Math.min(colorIndex, gradient.length - 1);

		Color temp = gradient[colorIndex];

		if ((temp.getBlue() + temp.getRed() + temp.getGreen()) / 3 > 128)
			return Color.BLACK;
		else
			return Color.WHITE;
	}

	/**
	 * Determines the color of the given value based on the precomputed gradient
	 *
	 * @param valueString The value (String representation of a number)
	 * @return The color of this value from the gradient
	 */
	public Color getColor(String valueString)
	{
		try
		{
			return getColor(Double.parseDouble(valueString));
		}
		catch (NumberFormatException e)
		{
			return Color.WHITE;
		}
	}

	/**
	 * Determines the color of the given value based on the precomputed gradient
	 *
	 * @param value The value
	 * @return The color of this value from the gradient
	 */
	public Color getColor(double value)
	{
		if (maxValue == minValue)
			return Color.WHITE;

		double range = maxValue - minValue;
		double norm = (value - minValue) / range; // 0 < norm < 1
		int colorIndex = (int) Math.floor(norm * (gradient.length - 1));

		colorIndex = Math.max(0, colorIndex);
		colorIndex = Math.min(colorIndex, gradient.length - 1);

		return gradient[colorIndex];
	}

	public double getMin()
	{
		return minValue;
	}

	public double getMax()
	{
		return maxValue;
	}
}
