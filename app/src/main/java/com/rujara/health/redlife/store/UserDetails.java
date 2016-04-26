package com.rujara.health.redlife.store;

/**
 * Created by deep.patel on 9/22/15.
 */
public class UserDetails {

    private static UserDetails userDetails = null;
    private String name;
    private String eventRegistrationId;
    private String emailId;
    private String phoneNumber;
    private String bloodGroup;
    private String serverAuthToken;
    private String dob;

    private UserDetails() {
    }

    public static UserDetails getInstance() {
        if (userDetails == null)
            userDetails = new UserDetails();
        return userDetails;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventRegistrationId() {
        return eventRegistrationId;
    }

    public void setEventRegistrationId(String eventRegistrationId) {
        this.eventRegistrationId = eventRegistrationId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup.toLowerCase();
    }

    public String getServerAuthToken() {
        return serverAuthToken;
    }

    public void setServerAuthToken(String serverAuthToken) {
        this.serverAuthToken = serverAuthToken;
    }

    public void resetUser() {
        userDetails = null;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", eventRegistrationId="
                + eventRegistrationId + ", emailId=" + emailId + ", phoneNumber=" + phoneNumber + ", bloodGroup="
                + bloodGroup + ", serverAuthToken=" + serverAuthToken + "]";
    }
}
