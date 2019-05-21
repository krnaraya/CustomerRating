package com.redhat.experience.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.redhat.experience.model.JsonPassengerReader;
//import com.redhat.experience.model.Passenger;
import demo.rules.experience.Passenger;


public class ReadJsonDataTest {

	
	@Test
	public void testReadPassengerJsonFromFile() throws IOException {
		JsonPassengerReader reader = new JsonPassengerReader("src/test/data/paxlist-100.json");
		List<Passenger> passengers = reader.readFromFile();
		assertEquals(passengers.get(0).getPrefix(), "master");
		assertEquals(passengers.get(0).getFirstName(), "Lenore");
		assertEquals(passengers.get(0).getMiddleName(), "Carr");
		assertEquals(passengers.get(0).getSurName(), "Church");
		assertEquals(passengers.get(0).getPCV(), 70374);
	}
	
	@Test
	public void testReadPassengerJsonFromResource() throws IOException {
		JsonPassengerReader reader = new JsonPassengerReader("passengerlist.json");
		List<Passenger> passengers = reader.readFromResource();
		assertEquals(passengers.get(0).getPrefix(), "master");
		assertEquals(passengers.get(0).getFirstName(), "Lenore");
		assertEquals(passengers.get(0).getMiddleName(), "Carr");
		assertEquals(passengers.get(0).getSurName(), "Church");
		assertEquals(passengers.get(0).getPCV(), 70374);
	}
}
