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

public class GetItemsToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Collection<ItemToBuy> itemsToBuyCollectionResponse = new ArrayList<ItemToBuy>();
		GsonParser gsonParser = GsonParser.getGsonParserInstance();

		PreparedStatement itemsToBuy = null;
		ResultSet itemsToBuyFromDb = null;
		try {
			// Connecting to DB
			Connection connection = DatabaseConnection.getConnect();

			itemsToBuy = connection.prepareStatement("SELECT * FROM item_to_buy;");
			itemsToBuyFromDb = itemsToBuy.executeQuery();

			while (itemsToBuyFromDb.next()) {
				ItemToBuy itemToBuy = new ItemToBuy();

				Integer id = itemsToBuyFromDb.getInt("id");
				String name = itemsToBuyFromDb.getString("name");
				String amount = itemsToBuyFromDb.getString("amount");
				Integer purchased = itemsToBuyFromDb.getInt("purchased");

				itemToBuy.setId(id);
				itemToBuy.setName(name);
				itemToBuy.setAmount(amount);
				itemToBuy.setPurchased(purchased);

				itemsToBuyCollectionResponse.add(itemToBuy);
			}

			String jsonResponse = gsonParser.createJsonForResponse(itemsToBuyCollectionResponse);
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
