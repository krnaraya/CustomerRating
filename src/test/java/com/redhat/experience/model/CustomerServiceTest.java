package com.redhat.experience.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.redhat.experience.service.CustomerService;

import demo.rules.experience.Passenger;

public class CustomerServiceTest {

	@Test
	public void ServiceTestIndividual() throws IOException {
		CustomerService service = new CustomerService("src/test/data/paxlist-100.json");
		List<Passenger> passengers = service.retrievePassengersFromFile("81");
		System.out.println("***********");
		System.out.println("Before sorting:...");
		System.out.println("***********");
		for (int i = 0; i < 3; i++) {
			System.out.println(passengers.get(i));
			System.out.println("-----------");
		}
		
		List<Passenger> topList = new ArrayList<Passenger>();
		topList = service.getNLowestPcvCustomers(passengers, 3);
		System.out.println("***********");
		System.out.println("After sorting Top N List:...");
		System.out.println("***********");
		for (Passenger passenger : topList) {
			System.out.println(passenger);
			System.out.println("-----------");
		}
		
		assertEquals(topList.get(0).getPCV(), 176);
		assertEquals(topList.get(1).getPCV(), 1043);
		assertEquals(topList.get(2).getPCV(), 1227);
	}
	
	@Test
	public void ServiceFileTest() throws IOException {
		List<Passenger> topList = new ArrayList<Passenger>();
		CustomerService service = new CustomerService("src/test/data/paxlist-100.json");
		topList = service.getNLowestPcvCustomersFromFile("81", 3);
		System.out.println("***********");
		System.out.println("Top N List:...");
		System.out.println("***********");
		for (Passenger passenger : topList) {
			System.out.println(passenger);
			System.out.println("-----------");
		}
		
		assertEquals(topList.get(0).getPCV(), 176);
		assertEquals(topList.get(1).getPCV(), 1043);
		assertEquals(topList.get(2).getPCV(), 1227);
		
	}
	
	@Test
	public void ServiceResourceTest() throws IOException {
		List<Passenger> topList = new ArrayList<Passenger>();
		CustomerService service = new CustomerService("passengerlist.json");
		topList = service.getNLowestPcvCustomersFromResource("81", 3);
		System.out.println("***********");
		System.out.println("Top N List:...");
		System.out.println("***********");
		for (Passenger passenger : topList) {
			System.out.println(passenger);
			System.out.println("-----------");
		}
		
		assertEquals(topList.get(0).getPCV(), 176);
		assertEquals(topList.get(1).getPCV(), 1043);
		assertEquals(topList.get(2).getPCV(), 1227);
		
	}

}
