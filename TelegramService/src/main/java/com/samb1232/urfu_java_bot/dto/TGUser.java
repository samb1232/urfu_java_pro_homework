package com.samb1232.urfu_java_bot.dto;

public class TGUser {
    private final Long id;
    private final String userName;
    private final String firstName;
    private final String lastName;

    public TGUser(Long id, String userName, String firstName, String lastName) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}