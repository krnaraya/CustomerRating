package com.redhat.experience.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.redhat.experience.model.JsonPassengerReader;

import demo.rules.experience.Passenger;
//import com.redhat.experience.model.Passenger;

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
	
	// ---------------------------- used in external REST call demo ------------------------------------
	public List<Passenger> retrievePassengersFromRest(String flightNumber) throws IOException {
		// dummy argument flightNumber, hard coded response in demo
/*		Client client = ClientBuilder.newClient();
		String content =  client.target(resourceName + flightNumber)
				.request(MediaType.APPLICATION_JSON)
				.get(String.class);*/
		// hack to force convert "pcv" to "PCV"!
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(resourceName + flightNumber);
		getRequest.addHeader("accept", "application/json");

		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}

		String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		httpClient.getConnectionManager().shutdown();

		System.out.println("***********");
		System.out.println(content);
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
/*		Client client = ClientBuilder.newClient();
		List<Passenger> topList = new ArrayList<Passenger>();
		topList = getNLowestPcvCustomersFromRest(flightNumber, count);
		client.target(resourceName + flightNumber).request()
				.post(Entity.entity(JsonPassengerReader.toJson(topList), MediaType.APPLICATION_JSON));*/
		
		List<Passenger> topList = new ArrayList<Passenger>();
		topList = getNLowestPcvCustomersFromRest(flightNumber, count);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(resourceName + flightNumber);

		StringEntity input = new StringEntity(JsonPassengerReader.toJson(topList));
		input.setContentType("application/json");
		postRequest.setEntity(input);

		HttpResponse response = httpClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		System.out.println("***********");
		System.out.println(content);
		httpClient.getConnectionManager().shutdown();



	}

/* new methods for illustrating calling external services
 * 
 */
	// ---------------------------- used in external REST call demo ------------------------------------
	public void sendVip(String flightNumber, Passenger passenger) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(resourceName + "vip/" + flightNumber);

		StringEntity input = new StringEntity(JsonPassengerReader.toJson(passenger));
		input.setContentType("application/json");
		postRequest.setEntity(input);

		HttpResponse response = httpClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatusLine().getStatusCode());
		}
		
		String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		System.out.println("***********");
		System.out.println(content);
		httpClient.getConnectionManager().shutdown();

	}
	
	// ---------------------------- used in external REST call demo ------------------------------------
	public List<Passenger> getTopList(List<Passenger> passengerList, long count) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(resourceName + "sort/" + count);
		StringEntity input = new StringEntity(JsonPassengerReader.toJson(passengerList));
		input.setContentType("application/json");
		postRequest.setEntity(input);
		HttpResponse response = httpClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}

		String content = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
		httpClient.getConnectionManager().shutdown();

		System.out.println("***********");
		System.out.println(content);
		return JsonPassengerReader.read(content.replaceAll("\"pcv\"", "PCV"));
	}

	
/* new methods for illustrating calling external services
 * 
 */
	public static void main(String[] args) throws IOException {
		List<Passenger> topList = new ArrayList<Passenger>();
/*		CustomerService service = new CustomerService(SAMPLE_PASSENGER_JSON_RESOURCE);
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
		restService.sendVipList("83", 3);*/
		
		CustomerService service = new CustomerService();
		topList = service.retrievePassengersFromRest("81");
		System.out.println("***********");
		System.out.println("Passenger List:...");
		System.out.println("***********");
		for (Passenger passenger : topList) {
			System.out.println(passenger);
			System.out.println("-----------");
		}
		
		Passenger passenger = new Passenger("master",
				"Lenore", "Carr", "Church", 70374);
		service.sendVip("83", passenger);
		
		topList = service.getTopList(topList, 3);
		System.out.println("***********");
		System.out.println("Top Passenger List:...");
		System.out.println("***********");
		for (Passenger topPassenger : topList) {
			System.out.println(topPassenger);
			System.out.println("-----------");
		}
	}

}
