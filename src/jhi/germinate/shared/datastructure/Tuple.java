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

public class Tuple
{
	/**
	 * {@link Pair} is a simple implementation of a tuple.
	 *
	 * @param <A> The data type of the first element
	 * @param <B> The data type of the second element
	 * @author Sebastian Raubach
	 */
	public static class Pair<A, B> implements Serializable
	{
		private static final long serialVersionUID = 2227889296819366011L;

		private A first;
		private B second;

		public Pair()
		{
			first = null;
			second = null;
		}

		public Pair(A first, B second)
		{
			this.first = first;
			this.second = second;
		}

		public A getFirst()
		{
			return first;
		}

		public B getSecond()
		{
			return second;
		}

		public void setFirst(A first)
		{
			this.first = first;
		}

		public void setSecond(B second)
		{
			this.second = second;
		}

		@Override
		public String toString()
		{
			return "Pair [first=" + first + ", second=" + second + "]";
		}
	}

	/**
	 * {@link Triple} is a simple implementation of a triple.
	 *
	 * @param <A> The data type of the first element
	 * @param <B> The data type of the second element
	 * @param <C> The data type of the third element
	 * @author Sebastian Raubach
	 */
	public static class Triple<A, B, C> implements Serializable
	{
		private static final long serialVersionUID = -7673178610581632736L;

		private A first;
		private B second;
		private C third;

		public Triple()
		{
			first = null;
			second = null;
			third = null;
		}

		public Triple(A first, B second, C third)
		{
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public A getFirst()
		{
			return first;
		}

		public B getSecond()
		{
			return second;
		}

		public C getThird()
		{
			return third;
		}

		public void setFirst(A first)
		{
			this.first = first;
		}

		public void setSecond(B second)
		{
			this.second = second;
		}

		public void setThird(C third)
		{
			this.third = third;
		}

		@Override
		public String toString()
		{
			return "Triple [first=" + first + ", second=" + second + ", third=" + third + "]";
		}
	}
}
