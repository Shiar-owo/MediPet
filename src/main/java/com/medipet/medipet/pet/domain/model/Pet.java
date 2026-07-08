package com.medipet.medipet.pet.domain.model;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Pet {

    private PetId id;
    private String name;
    private String species;
    private String breed;
    private LocalDate dateOfBirth;
    private String sex;
    private String color;
    private BigDecimal weight;
    private String microchipId;
    private String tattooNumber;
    private String photoUrl;
    private boolean active;
    private ClientId clientId;

    private Pet() {
    }

    private Pet(PetId id, String name, String species, String breed, LocalDate dateOfBirth,
                String sex, String color, BigDecimal weight, String microchipId,
                String tattooNumber, String photoUrl, boolean active, ClientId clientId) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.color = color;
        this.weight = weight;
        this.microchipId = microchipId;
        this.tattooNumber = tattooNumber;
        this.photoUrl = photoUrl;
        this.active = active;
        this.clientId = clientId;
    }

    public static Pet create(String name, String species, String breed, LocalDate dateOfBirth,
                             String sex, String color, BigDecimal weight, String microchipId,
                             String tattooNumber, String photoUrl, ClientId clientId) {
        return new Pet(PetId.generate(), name, species, breed, dateOfBirth, sex, color,
                weight, microchipId, tattooNumber, photoUrl, true, clientId);
    }

    public static Pet withId(PetId id, String name, String species, String breed, LocalDate dateOfBirth,
                             String sex, String color, BigDecimal weight, String microchipId,
                             String tattooNumber, String photoUrl, boolean active, ClientId clientId) {
        return new Pet(id, name, species, breed, dateOfBirth, sex, color, weight,
                microchipId, tattooNumber, photoUrl, active, clientId);
    }

    public PetId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getMicrochipId() {
        return microchipId;
    }

    public void setMicrochipId(String microchipId) {
        this.microchipId = microchipId;
    }

    public String getTattooNumber() {
        return tattooNumber;
    }

    public void setTattooNumber(String tattooNumber) {
        this.tattooNumber = tattooNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ClientId getClientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
