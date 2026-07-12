package com.medipet.medipet.user.domain.model;

import com.medipet.medipet.user.domain.valueobject.UserId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    private UserId id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean active;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String avatar;
    private LocalDateTime lastLoginAt;

    private User() {
    }

    private User(UserId id, String email, String password, String firstName, String lastName,
                 Role role, boolean active, String phone, String address,
                 LocalDate dateOfBirth, String avatar, LocalDateTime lastLoginAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.active = active;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.avatar = avatar;
        this.lastLoginAt = lastLoginAt;
    }

    public static User create(String email, String password, String firstName, String lastName, Role role,
                              String phone, String address, LocalDate dateOfBirth) {
        User user = new User();
        user.id = UserId.generate();
        user.email = email;
        user.password = password;
        user.firstName = firstName;
        user.lastName = lastName;
        user.role = role;
        user.active = true;
        user.phone = phone;
        user.address = address;
        user.dateOfBirth = dateOfBirth;
        return user;
    }

    public static User withId(UserId id, String email, String password, String firstName, String lastName,
                              Role role, boolean active, String phone, String address,
                              LocalDate dateOfBirth, String avatar, LocalDateTime lastLoginAt) {
        return new User(id, email, password, firstName, lastName,
                role, active, phone, address, dateOfBirth, avatar, lastLoginAt);
    }

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
