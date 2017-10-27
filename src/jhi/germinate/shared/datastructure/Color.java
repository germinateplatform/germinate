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

import jhi.germinate.shared.*;


/**
 * {@link Color} is a simple representation for colors. It contains constants for the main colors.
 *
 * @author Sebastian Raubach
 */
public class Color implements Serializable
{
	private static final long serialVersionUID = -743067887812755653L;

	private static final double FACTOR = 0.7;

	/** White color constant: rgb(255, 255, 255) */
	public static final Color WHITE      = new Color(255, 255, 255);
	/** Light gray color constant: rgb(192, 192, 192) */
	public static final Color LIGHT_GRAY = new Color(192, 192, 192);
	/** Gray color constant: rgb(128, 128, 128) */
	public static final Color GRAY       = new Color(128, 128, 128);
	/** Dark gray color constant: rgb(64, 64, 64) */
	public static final Color DARK_GRAY  = new Color(64, 64, 64);
	/** Black color constant: rgb(0, 0, 0) */
	public static final Color BLACK      = new Color(0, 0, 0);
	/** Red color constant: rgb(255, 0, 0) */
	public static final Color RED        = new Color(255, 0, 0);
	/** Pink color constant: rgb(255, 175, 175) */
	public static final Color PINK       = new Color(255, 175, 175);
	/** Orange color constant: rgb(255, 200, 0) */
	public static final Color ORANGE     = new Color(255, 200, 0);
	/** Yellow color constant: rgb(255, 255, 0) */
	public static final Color YELLOW     = new Color(255, 255, 0);
	/** Green color constant: rgb(0, 255, 0) */
	public static final Color GREEN      = new Color(0, 255, 0);
	/** Magenta color constant: rgb(255, 0, 255) */
	public static final Color MAGENTA    = new Color(255, 0, 255);
	/** Cyan color constant: rgb(0, 255, 255) */
	public static final Color CYAN       = new Color(0, 255, 255);
	/** Blue color constant: rgb(0, 0, 255) */
	public static final Color BLUE       = new Color(0, 0, 255);

	/** Pomegranate color constant: rgb(192, 57, 43) */
	public static final Color MODERN_RED_POMEGRANATE  = new Color(192, 57, 43);
	/** Sunflower color constant: rgb(241, 196, 15) */
	public static final Color MODERN_YELLOW_SUNFLOWER = new Color(241, 196, 15);

	private int red   = 0;
	private int green = 0;
	private int blue  = 0;

	public Color()
	{
	}

	/**
	 * Creates a new Color based on the given r, g, and b values
	 *
	 * @param r The amount of red (0 - 255)
	 * @param g The amount of green (0 - 255)
	 * @param b The amound of blue (0 - 255)
	 */
	public Color(int r, int g, int b)
	{
		this.red = r;
		this.green = g;
		this.blue = b;
	}

	public int getRed()
	{
		return red;
	}

	public int getGreen()
	{
		return green;
	}

	public int getBlue()
	{
		return blue;
	}

	/**
	 * Returns a string representation of the color as a hex value (with leading hash)
	 *
	 * @return A string representation of the color as a hex value (with leading hash)
	 */
	public String toHexValue()
	{
		return "#" + pad(Integer.toHexString(red)) + pad(Integer.toHexString(green)) + pad(Integer.toHexString(blue));
	}

	/**
	 * Pads the given input {@link String} to a length of exactly two. <p/> <b>Examples:</b> <ul> <li><code>null</code> -> "00"</li>
	 * <li><code>""</code> -> "00"</li> <li><code>"A"</code> -> "0A"</li> <li><code>"AA"</code> -> "AA"</li> </ul>
	 *
	 * @param in The {@link String} to pad
	 * @return The padded {@link String}
	 */
	private String pad(String in)
	{
		if (in == null)
			return "00";
		else if (in.length() == 0)
			return "00";
		else if (in.length() == 1)
			return "0" + in;
		else
			return in;
	}

	/**
	 * Applies the given alpha value [0, 1] to this {@link Color} but returns a new {@link Color} instance. This {@link Color} remains untouched.
	 *
	 * @param p The alpha value between 0 and 1
	 * @return The new {@link Color} instance
	 */
	public Color toTransparency(float p)
	{
		/* Sanitize the transparency value */
		if (p > 1)
			p = 1;
		else if (p < 0)
			p = 0;

		Color background = WHITE;

		int red = Math.round((1 - p) * background.red + p * this.red);
		int green = Math.round((1 - p) * background.green + p * this.green);
		int blue = Math.round((1 - p) * background.blue + p * this.blue);

		return new Color(red, green, blue);
	}

	/**
	 * Returns a string representation of the color in the format: <code>rgb(R, G, B)</code>
	 *
	 * @return A string representation of the color in the format: <code>rgb(R, G, B)</code>
	 */
	public String toRGBValue()
	{
		return "rgb(" + red + "," + green + "," + blue + ")";
	}

	@Override
	public String toString()
	{
		return "red=" + red + ", green=" + green + ", blue=" + blue;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + blue;
		result = prime * result + green;
		result = prime * result + red;
		return result;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Color)
		{
			Color otherColor = (Color) other;
			return red == otherColor.red && blue == otherColor.blue && green == otherColor.green;
		}
		return false;
	}

	/**
	 * Creates a new Color that is a darker version of this Color. This method applies an arbitrary scale factor to each of the three RGB components
	 * of this Color to create a darker version of this Color. Although brighter and darker are inverse operations, the results of a series of
	 * invocations of these two methods might be inconsistent because of rounding errors.
	 *
	 * @return a new Color object that is a darker version of this Color
	 */
	public Color darker()
	{
		return new Color(Math.max((int) (red * FACTOR), 0),
				Math.max((int) (green * FACTOR), 0),
				Math.max((int) (blue * FACTOR), 0));
	}

	/**
	 * Creates a new Color that is a brighter version of this Color. This method applies an arbitrary scale factor to each of the three RGB components
	 * of this Color to create a brighter version of this Color. Although brighter and darker are inverse operations, the results of a series of
	 * invocations of these two methods might be inconsistent because of rounding errors.
	 *
	 * @return a new Color object that is a brighter version of this Color
	 */
	public Color brighter()
	{
		int r = red;
		int g = green;
		int b = blue;

		/* From 2D group:
		 * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
		int i = (int) (1.0 / (1.0 - FACTOR));
		if (r == 0 && g == 0 && b == 0)
		{
			return new Color(i, i, i);
		}
		if (r > 0 && r < i) r = i;
		if (g > 0 && g < i) g = i;
		if (b > 0 && b < i) b = i;
		return new Color(Math.min((int) (r / FACTOR), 255),
				Math.min((int) (g / FACTOR), 255),
				Math.min((int) (b / FACTOR), 255));
	}

	/**
	 * Determines the text color to use for the given background HEX value. <p/> This method will either return the HEX value of {@link #BLACK} or
	 * {@link #WHITE} depending on whether the background is dark or light.
	 *
	 * @param hex The HEX value of the color (including the hash)
	 * @return The recommended text color
	 */
	public static String getTextColor(String hex)
	{
		if (StringUtils.isEmpty(hex) || !isHexColor(hex))
			throw new RuntimeException("Invalid color value");

		int r = Integer.valueOf(hex.substring(1, 3), 16);
		int g = Integer.valueOf(hex.substring(3, 5), 16);
		int b = Integer.valueOf(hex.substring(5, 7), 16);

		if ((r + g + b) / 3 > 128)
			return Color.BLACK.toHexValue();
		else
			return Color.WHITE.toHexValue();
	}

	/**
	 * Checks if a given {@link String} represents a hex color
	 *
	 * @param potentialHex The potential hex color
	 * @return <code>true</code> if it represents a hex color
	 */
	public static boolean isHexColor(String potentialHex)
	{
		return potentialHex.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
	}

	/**
	 * Tries to create a {@link Color} instance from the given hex value
	 *
	 * @param hex The hex color
	 * @return The parsed {@link Color} instance or {@link #WHITE} if the parsing failed
	 */
	public static Color fromHex(String hex)
	{
		if (!isHexColor(hex))
		{
			return WHITE;
		}
		else if (hex.length() == 7)
		{
			return new Color(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16));
		}
		else if (hex.length() == 4)
		{
			return new Color(Integer.valueOf(hex.substring(1, 2) + hex.substring(1, 2), 16), Integer.valueOf(hex.substring(2, 3) + hex.substring(2, 3), 16), Integer.valueOf(
					hex.substring(3, 4) + hex.substring(3, 4), 16));
		}
		else
		{
			return WHITE;
		}
	}

	public boolean isDark()
	{
		return (red + blue + green) / 3f < 128;
	}
}
