package com.redhat.experience.model;

import org.json.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import demo.rules.experience.Passenger;

public class JsonPassengerReader {
	
	private String resourceName;
	
	public List<Passenger> readFromFile() throws IOException {
	    File file = new File(resourceName);
	    return read(FileUtils.readFileToString(file, "utf-8"));
	}
	
	public List<Passenger> readFromResource() throws IOException {
//		System.out.println("***********" + resourceName);
	    InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
	    String content = IOUtils.toString(is, "utf-8");
//		System.out.println("Content:" + content);
	    return read(content);
	}
	    
	public static List<Passenger> read(String content) throws IOException {
	    List<Passenger> list = new ArrayList<Passenger>();
	    
		JSONArray array = new JSONArray(content);
		for (int i = 0; i < array.length(); i++) {  
		     JSONObject obj = array.getJSONObject(i);

		list.add( new Passenger(obj.getString("prefix"),
				obj.getString("firstName"),
				obj.getString("middleName"),
				obj.getString("surName"),
				obj.getLong("PCV")
				));
		}
		
		return list;
	}
	
	public static String toJson(List<Passenger> list) throws IOException {

		JSONArray array = new JSONArray();
		for (Passenger passenger : list) {  
		     JSONObject obj = new JSONObject();
		     obj.put("prefix", passenger.getPrefix())
		     	.put("firstName", passenger.getFirstName())
		     	.put("middleName", passenger.getMiddleName())
		     	.put("surName", passenger.getSurName())
		     	.put("PCV", passenger.getPCV()
		     	);
		     array.put(obj);
		}
		System.out.println(array.toString());
		return array.toString();
	}
	
	public static String toJson(Passenger passenger) throws IOException {

	     JSONObject obj = new JSONObject();
	     obj.put("prefix", passenger.getPrefix())
	     	.put("firstName", passenger.getFirstName())
	     	.put("middleName", passenger.getMiddleName())
	     	.put("surName", passenger.getSurName())
	     	.put("PCV", passenger.getPCV()
	     	);

		System.out.println(obj.toString());
		return obj.toString();
	}
	
	public JsonPassengerReader(String resourceName) {
		this.resourceName = resourceName;
	}



	public List<Passenger> createEventFromJsonResource() throws IOException {
		return readFromResource();
	}


	public List<Passenger> createEventFromJsonFile() throws IOException {
		return readFromFile();
	}
	
	
}
