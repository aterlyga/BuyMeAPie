package com.buymeapie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.buymeapie.object.*;

public class GsonParser {
	
	private static GsonParser gsonParser = null;
	private Gson gson = null;
	private String jsonResponse = null;
	private Item item = null;
	private Collection<ItemToBuy> itemToBuy = null;
	
	private GsonParser() {
		// closing constructor from external accessing
		getGsonInstance();
	}

	public static GsonParser getGsonParserInstance() {
		if (gsonParser == null) {
			gsonParser = new GsonParser();
		}
		return gsonParser;
	}
	
	private Gson getGsonInstance() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}
	
	// Serializing json
	public String createJsonForResponse(Collection<ItemToBuy> forJsonResponse) {
		jsonResponse = gson.toJson(forJsonResponse);
		return jsonResponse;
	}
	
	public String createJsonForResponse(ArrayList<Item> forJsonResponse) {
		jsonResponse = gson.toJson(forJsonResponse);
		return jsonResponse;
	}
	
	public String createJsonForResponse(Error forJsonResponse) {
		jsonResponse = gson.toJson(forJsonResponse);
		return jsonResponse;
	}
	
	public String createJsonForResponse(String[] truncateDB) {
		jsonResponse = gson.toJson(truncateDB);
		return jsonResponse;
	}

	// Deserializing json for autocomlete servlet
	public void setItem(String parameter) {
		item = gson.fromJson(parameter, Item.class);
	}
	
	public Item getItem() {
		return item;
	}
	
	// deserializing json for other sevlets with "action" parameter
	public void setItemToBuy(String parameter) {
		Type collectionType = new TypeToken<Collection<ItemToBuy>>(){}.getType();
		itemToBuy = gson.fromJson(parameter, collectionType);
	}
	
	public Collection<ItemToBuy> getItemToBuy() {
		return itemToBuy;
	}
}
