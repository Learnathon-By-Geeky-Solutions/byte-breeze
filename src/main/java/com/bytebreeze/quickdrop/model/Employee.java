 package com.bytebreeze.quickdrop.model;

public class Employee {
    private int id;
    private String name;
    private String salary;
    private String country;
    private String city;

    public Employee(int id, String name, String salary, String country, String city) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.country = country;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSalary() {
        return salary;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}
