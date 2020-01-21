package com.example.vcard;

public class Person {
    private String name;
    private String faculty;

    public Person(String name, String faculty) {
        this.setName(name);
        this.setFaculty(faculty);
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
}