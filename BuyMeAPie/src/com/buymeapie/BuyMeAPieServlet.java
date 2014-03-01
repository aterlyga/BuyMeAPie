package com.buymeapie;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class BuyMeAPieServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Serializes to JSON and adds error object to response
	 * @param errorCode The number of an error
	 * @param response The HttpServletResponse instance, to which the error has to be added
	 */
	protected void processError(Integer errorCode, HttpServletResponse response) throws IOException {
		
		// create error object
		Error error = new Error(errorCode);
		
		// serialize and print to response
		Gson gson = new Gson();
		String errorJsonResponse = gson.toJson(error);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print(errorJsonResponse);
	}
};
