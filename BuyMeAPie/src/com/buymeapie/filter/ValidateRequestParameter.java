package com.buymeapie;

import java.util.Collection;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
* Request parameters validator
*/
public class ValidateRequestParameter {
	
	/**
	* Validates a collection of items to buy
	* @param itemsTobuy
	*			Respective items to buy collection
	* @return
	* 			True if collection is valid or false if collection is invalid
	*/
	public static boolean validateItemsToBuy(Collection<ItemToBuy> itemsToBuy) {
		Iterator<ItemToBuy> iterator = itemsToBuy.iterator();
		while (iterator.hasNext()) {
			ItemToBuy itemToBuy = iterator.next();
			
			String name = itemToBuy.getName();
			String amount = itemToBuy.getAmount();
			Integer purchused = itemToBuy.getPurchased();

			if (itemToBuy.getId() != null) {
				if (Jsoup.isValid(Integer.toString(itemToBuy.getId()), Whitelist.none()) == false
					) {
					return false;
				}
			}

			if (itemToBuy.getName() != null) {
				if (Jsoup.isValid(itemToBuy.getName(), Whitelist.none()) == false) {
					return false;
				}
			}

			if (itemToBuy.getAmount() != null) {
				if (Jsoup.isValid(itemToBuy.getAmount(), Whitelist.none()) == false) {
					return false;
				}
			}

			if (itemToBuy.getPurchased() != null) {
				if (Jsoup.isValid(Integer.toString(itemToBuy.getPurchased()), Whitelist.none()) == false) {
					return false;
				}
			}
		}

		return true;
	}
}