package com.buymeapie;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The purpose of this filter is to parse data in json using Gson library.</br>
 * Depending on <code>Type</code> of data inclosed in request parameter using
 * different</br> deserializing methods</br>
 * 
 */
public class JsonItemToBuyParserFilter implements Filter {

	private FilterConfig filterConfig = null;

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * 1. JSON parses and generates by Gson library. Gson instantiated using
	 * Singleton pattern.</br> 2. Depending on request parameter "action"
	 * specific deserializing of json using</br> 3. Passing data to
	 * ValidaterFilter for validating json on XSS</br>
	 * 
	 * @param request
	 *            request received from client-side
	 * @param response
	 *            response that will be passed-through
	 * @param filterChain
	 *            for call next filter or target servlet
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		GsonParser gsonParser = GsonParser.getGsonParserInstance();

		gsonParser.setItemToBuy(request.getParameter("items_to_buy"));
		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
	}
}
