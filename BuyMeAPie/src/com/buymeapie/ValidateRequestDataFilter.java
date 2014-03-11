package com.buymeapie;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Servlet Filter implementation class ValidateDataFilter
 */
public class ValidateRequestDataFilter implements Filter {

	GsonParser gsonParser = GsonParser.getGsonParserInstance();
	private FilterConfig filterConfig = null;

	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {


		if (gsonParser.getItem() != null) {
			Item item = gsonParser.getItem();
			if (ValidateRequestParameter.validateItem(item) == 1) {
				// pass the request along the filter chain
				filterChain.doFilter(request, response);
			}
		}

		if (gsonParser.getItemToBuy() != null) {
			Collection<ItemToBuy> itemsToBuy = gsonParser.getItemToBuy();
			if (ValidateRequestParameter.validateItemToBuy(itemsToBuy) == 1) {
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
