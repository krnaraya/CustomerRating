package com.redhat.experience.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import com.redhat.experience.model.JsonPassengerReader;
import com.redhat.experience.model.Passenger;

public class CustomerService {
	
	private String resourceName;
	private JsonPassengerReader reader;
	static final String SAMPLE_PASSENGER_JSON_RESOURCE = "passengerlist.json";
	static final String DEFAULT_WS_URL = "http://localhost:9080/passengers/flight/";
	
	public CustomerService() {
		this.resourceName = DEFAULT_WS_URL;
		reader = new JsonPassengerReader(resourceName);
	}
	
	public CustomerService(String resourceName) {
		this.resourceName = resourceName;
		reader = new JsonPassengerReader(resourceName);
	}
	
	public List<Passenger> retrievePassengersFromFile(String flightNumber) throws IOException {
		// dummy argument flightNumber, hard coded response in demo
		return reader.readFromFile();
	}
	
	public List<Passenger> retrievePassengersFromResource(String flightNumber) throws IOException {
		// dummy argument flightNumber, hard coded response in demo
		return reader.readFromResource();
	}
	
	public List<Passenger> retrievePassengersFromRest(String flightNumber) throws IOException {
		// dummy argument flightNumber, hard coded response in demo
		Client client = ClientBuilder.newClient();
		String content =  client.target(resourceName + flightNumber)
				.request(MediaType.APPLICATION_JSON)
				.get(String.class);
		// hack to force convert "pcv" to "PCV"!
//		System.out.println("***********");
//		System.out.println(content);
		return JsonPassengerReader.read(content.replaceAll("\"pcv\"", "PCV"));
	}
	public void sortSmallestFirst(List<Passenger> passengers) {
		Collections.sort(passengers, new SortbyPcv());
	}
	
	public List<Passenger> getNLowestPcvCustomers(List<Passenger> passengers, int count) {
		sortSmallestFirst(passengers);
		if (count > passengers.size()) count = passengers.size();
		List<Passenger> topList = new ArrayList<Passenger>();
		for (int i = 0; i < count; i++) {
			topList.add(passengers.get(i));
		}
		return topList;
	}
	
	public List<Passenger> getNLowestPcvCustomersFromFile(String flightNumber, int count) throws IOException {
		List<Passenger> passengers = retrievePassengersFromFile(flightNumber);
		return getNLowestPcvCustomers(passengers, count);
	}
	
	public List<Passenger> getNLowestPcvCustomersFromResource(String flightNumber, int count) throws IOException {
		List<Passenger> passengers = retrievePassengersFromResource(flightNumber);
		return getNLowestPcvCustomers(passengers, count);
	}
	
	public List<Passenger> getNLowestPcvCustomersFromRest(String flightNumber, int count) throws IOException {
		List<Passenger> passengers = retrievePassengersFromRest(flightNumber);
		return getNLowestPcvCustomers(passengers, count);
	}
	
	class SortbyPcv implements Comparator<Passenger>
	{
	    // Used for sorting in ascending order of
	    // PCV
	    public int compare(Passenger a, Passenger b)
	    {
	        return (int) (a.getPCV() - b.getPCV());
	    }
	}
	
	public void sendVipList(String flightNumber, int count) throws IOException {
		Client client = ClientBuilder.newClient();
		List<Passenger> topList = new ArrayList<Passenger>();
		topList = getNLowestPcvCustomersFromRest(flightNumber, count);
		client.target(resourceName + flightNumber).request()
				.post(Entity.entity(JsonPassengerReader.toJson(topList), MediaType.APPLICATION_JSON));

	}
	
	public static void main(String[] args) throws IOException {
		List<Passenger> topList = new ArrayList<Passenger>();
		CustomerService service = new CustomerService(SAMPLE_PASSENGER_JSON_RESOURCE);
		topList = service.getNLowestPcvCustomersFromResource("81", 3);
		System.out.println("***********");
		System.out.println("Top N List:...");
		System.out.println("***********");
		for (Passenger passenger : topList) {
			System.out.println(passenger);
			System.out.println("-----------");
		}
		
		System.out.println("***********");
		System.out.println("******Testing sendVipList*****");
		CustomerService restService = new CustomerService();
		restService.sendVipList("83", 3);
	}

}
