package com.redhat.experience.model;

public class Passenger{


	private final String prefix;
	private final String firstName;
	private final String middleName;
	private final String surName;
	private final long PCV;

	public Passenger(String prefix, String firstName, String middleName, String surName, long PCV) {
		this. prefix = prefix;
		this.firstName = firstName;
		this.middleName = middleName;
		this.surName = surName;
		this.PCV = PCV;
	}
	
	
	public String getPrefix() {
		return prefix;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getSurName() {	
		return surName;
	}

	public long getPCV() {
		return PCV;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("prefix: "); builder.append(prefix); builder.append('\n');
		builder.append("firstName: "); builder.append(firstName); builder.append('\n');
		builder.append("middleName: "); builder.append(middleName); builder.append('\n');
		builder.append("surName: "); builder.append(surName); builder.append('\n');
		builder.append("PCV: "); builder.append(PCV); builder.append('\n');
		return builder.toString();
	}
}
