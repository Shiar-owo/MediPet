package com.medipet.medipet.client.domain.model;

import com.medipet.medipet.client.domain.valueobject.ClientId;

import java.util.Objects;

public class Client {

    private ClientId id;
    private String name;
    private String dni;
    private String phone;
    private String email;
    private String address;
    private boolean active;

    private Client() {
    }

    private Client(ClientId id, String name, String dni, String phone, String email, String address, boolean active) {
        this.id = id;
        this.name = name;
        this.dni = dni;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.active = active;
    }

    public static Client create(String name, String dni, String phone, String email, String address) {
        Client client = new Client();
        client.id = ClientId.generate();
        client.name = name;
        client.dni = dni;
        client.phone = phone;
        client.email = email;
        client.address = address;
        client.active = true;
        return client;
    }

    public static Client withId(ClientId id, String name, String dni, String phone, String email, String address, boolean active) {
        return new Client(id, name, dni, phone, email, address, active);
    }

    public ClientId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
