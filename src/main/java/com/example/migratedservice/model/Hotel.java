package com.example.migratedservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.util.Objects;

@Entity
@Table(name = "HOTELS")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long id;

    @Column(name = "hotel_name", nullable = false, length = 100)
    private String name;

    @Column(name = "hotel_address", nullable = false, length = 250)
    private String address;

    @Column(name = "zip", nullable = false, length = 15)
    private String zip;

    // No-argument constructor (required by JPA)
    public Hotel() {
    }

    // Constructor with all fields
    public Hotel(String name, String address, String zip) {
        this.name = name;
        this.address = address;
        this.zip = zip;
    }
    
    // Constructor with all fields including id (useful for some cases)
    public Hotel(Long id, String name, String address, String zip) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.zip = zip;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hotel hotel = (Hotel) o;
        return Objects.equals(id, hotel.id) &&
               Objects.equals(name, hotel.name) &&
               Objects.equals(address, hotel.address) &&
               Objects.equals(zip, hotel.zip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, zip);
    }

    @Override
    public String toString() {
        return "Hotel{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", address='" + address + '\'' +
               ", zip='" + zip + '\'' +
               '}';
    }
}
