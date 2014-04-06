package com.buymeapie;

import com.buymeapie.object.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
* Serializes and deserializes the application data. Singleton
*/
public class GsonParser {
	
	private static GsonParser gsonParser = null;
	private Gson gson = null;
	
	// Closing constructor from external access
	private GsonParser() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	/**
		Returns an instance of GsonParser
	*/
	public static GsonParser getInstance() {
		if (gsonParser == null) {
			gsonParser = new GsonParser();
		}
		return gsonParser;
	}
		
	/**
	* Serializes an object to JSon format
	* @param object
	*			An object to serialize
	* @return
	* 			JSon string
	*/
	public String toJson(Object object) {
		return gson.toJson(object);
	}
	
	/**
		Parses JSon with a single Item instance
		and returns its respective object representation
		@param parameter
					Single item in JSon notation
		@return
					Instance of Item
	*/
	public Item deserializeItem(String parameter) {
		Type collectionType = new TypeToken<Collection<ItemToBuy>>(){}.getType();
		return gson.fromJson(parameter, Item.class);
	}
	
	/**
		Parses JSon with collection of ItemToBuy instances 
		and returns its respective object representation
		@param parameter
					Collection of items to buy in JSon notation
		@return
					Instance of Collection interface
	*/
	public Collection<ItemToBuy> deserializeItemsToBuy(String parameter) {
		Type collectionType = new TypeToken<Collection<ItemToBuy>>(){}.getType();
		return gson.fromJson(parameter, collectionType);
	}
}
