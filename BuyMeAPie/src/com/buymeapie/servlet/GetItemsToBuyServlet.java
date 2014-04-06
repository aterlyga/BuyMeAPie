package com.BuyMeAPie;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.BuyMeAPie.Error;

/**
* Retrieves and returns all available items to buy
*/
public class GetItemsToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {

		Collection<ItemToBuy> itemsToBuyCollectionResponse = new ArrayList<ItemToBuy>();
		
		try {
			// Connecting to DB
			Connection connection = DatabaseConnection.getConnect();

			PreparedStatement selectItemsToBuyStatement = connection.prepareStatement("SELECT * FROM item_to_buy;");
			ResultSet resultSet = selectItemsToBuyStatement.executeQuery();

			while (resultSet.next()) {
				ItemToBuy itemToBuy = new ItemToBuy();
				
				itemToBuy.setId(resultSet.getInt("id"));
				itemToBuy.setName(resultSet.getString("name"));
				itemToBuy.setAmount(resultSet.getString("amount"));
				itemToBuy.setPurchased(resultSet.getInt("purchased"));

				itemsToBuyCollectionResponse.add(itemToBuy);
			}

			String jsonResponse = GsonParser.getInstance.toJson(itemsToBuyCollectionResponse);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(jsonResponse);
			out.flush();
			out.close();

		} catch (Exception e) {
			// write to log file
			e.printStackTrace();

			processError(Error.ERROR_INTERNAL_SERVER, response);
		}
	}
}
