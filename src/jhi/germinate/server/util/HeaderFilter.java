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

package jhi.germinate.server.util;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/**
 * {@link HeaderFilter} is a {@link Filter} taking care of IE compatibility.
 *
 * @author Toby Philp
 * @author Sebastian Raubach
 */
@WebFilter(filterName = "HeaderFilter", urlPatterns = "/*")
public class HeaderFilter implements Filter
{
	@Override
	public void init(FilterConfig conf) throws ServletException
	{
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		/*
		 * Makes sure that the browser document mode for Internet Explorer is
         * set to IE edge
         */
		HttpServletResponse httpRes = (HttpServletResponse) res;
		httpRes.addHeader("X-UA-Compatible", "IE=edge");
		if (chain != null)
			chain.doFilter(req, res);
	}
}
