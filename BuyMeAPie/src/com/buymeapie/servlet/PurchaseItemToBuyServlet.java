package com.buymeapie.servlet;

import com.buymeapie.*;
import com.buymeapie.object.*;
import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* Marks an item to buy as "purchased" or vice versa
*/
public class PurchaseItemToBuyServlet extends BuyMeAPieServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {

		// Retrieve the passed instances of items to buy
		Collection<ItemToBuy> items = 
			GsonParser.getInstance().deserializeItemsToBuy(
				request.getParameter("items_to_buy")
				);

		// This variable will be used to store the successfully updated items to buy
		Collection<ItemToBuy> updatedItemsToBuy = new ArrayList<ItemToBuy>();
		
		try {
			// Connecting to DB
			Connection connection = DatabaseConnection.getConnect();
			connection.setAutoCommit(false);

			PreparedStatement updateItemStatement = connection.prepareStatement("UPDATE item_to_buy SET purchased=? WHERE ID=?;");
			PreparedStatement selectUpdatedItemStatement = connection.prepareStatement("SELECT id, purchased from item_to_buy WHERE ID=?;");

			// Iterating through array of received instances and executing
			// statement
			Iterator<ItemToBuy> iterator = items.iterator();
			while (iterator.hasNext()) {
				ItemToBuy itemToBuy = iterator.next();
				
				if (itemToBuy.getPurchased() == 0) {
					updateItemStatement.setInt(1, 1);
				} else {
					updateItemStatement.setInt(1, 0);
				}
				updateItemStatement.setInt(2, itemToBuy.getId());
				updateItemStatement.executeUpdate();
				
				selectUpdatedItemStatement.setInt(1, itemToBuy.getId());
			}

			// Executing statement and iterating through ResultSet
			ResultSet resultSet = selectUpdatedItemStatement.executeQuery();
			while (resultSet.next()) {
				ItemToBuy itemToBuy = new ItemToBuy();
				itemToBuy.setId(resultSet.getInt("id"));
				itemToBuy.setPurchased(resultSet.getInt("purchased"));
				updatedItemsToBuy.add(itemToBuy);
			}

			connection.commit();

			String jsonResponse = GsonParser.getInstance().toJson(updatedItemsToBuy);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print(jsonResponse);
			out.flush();
			out.close();

		} catch (Exception e) {
			// Rolling back the transaction
			try {
                connection.rollback();
            } catch(SQLException e1) {
                // write to log file
				e1.printStackTrace();
            }
			
			// write to log file
			e.printStackTrace();

			processError(Error.ERROR_INTERNAL_SERVER, response);
		} finally {
			// Restoring the auto-commit mode
			connection.setAutoCommit(true);
		}
	};
}
