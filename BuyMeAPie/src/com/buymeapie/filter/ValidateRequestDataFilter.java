package com.buymeapie;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Validates submitted parameters
 */
public class ValidateRequestDataFilter implements Filter {

	GsonParser gsonParser = GsonParser.getGsonParserInstance();
	@SuppressWarnings("unused")
	private FilterConfig filterConfig = null;

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) 
		throws IOException, ServletException {
		
		// Retrieve the passed instances of items to buy
		Collection<ItemToBuy> items = 
			GsonParser.getInstance().deserializeItemsToBuy(
				request.getParameter("items_to_buy")
				);
		
		// Validate the passed list of items to buy
		if (items != null) {
			if (ValidateRequestParameter.validateItemToBuy(items)) {
				// pass the request along the filter chain
				filterChain.doFilter(request, response);
			}
		}
	}

	@Override
	public void destroy() {
		this.filterConfig = null;
		
	}
}
