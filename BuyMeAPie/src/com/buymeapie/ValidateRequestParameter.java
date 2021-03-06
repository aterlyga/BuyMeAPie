package com.buymeapie;

import java.util.Collection;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class ValidateRequestParameter {
	protected static int validateItemToBuy(Collection<ItemToBuy> items) {

		Iterator<ItemToBuy> iterator = items.iterator();
		while (iterator.hasNext()) {
			ItemToBuy itemToBuy = iterator.next();
			Integer id = itemToBuy.getId();
			String name = itemToBuy.getName();
			String amount = itemToBuy.getAmount();
			Integer purchused = itemToBuy.getPurchased();

			if (id != null) {
				if (Jsoup.isValid(Integer.toString(id), Whitelist.none()) == false) {
					return 0;
				}
			}

			if (name != null) {
				if (Jsoup.isValid(name, Whitelist.none()) == false) {
					return 0;
				}
			}

			if (amount != null) {
				if (Jsoup.isValid(amount, Whitelist.none()) == false) {
					return 0;
				}
			}

			if (purchused != null) {
				if (Jsoup.isValid(Integer.toString(purchused), Whitelist.none()) == false) {
					return 0;
				}
			}
		}

		return 1;
	}

	protected static int validateItemToBuy(String itemName) {

		if (itemName != null) {
			if (Jsoup.isValid(itemName, Whitelist.none()) == false) {
				return 0;
			}
		}

		return 1;
	};
}
