package org.openmeetings.app.sip.api.request;

/**
 * Create User Request
 */
public class SIPCreateUserRequest implements ISIPRequest {

    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String email;
    protected String login;
    protected String password;


    public SIPCreateUserRequest(String firstName, String middleName, String lastName, String email, String login, String password) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public void withFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void withMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void withLastName(String lastName) {
        this.lastName = lastName;
    }

    public void withEmail(String email) {
        this.email = email;
    }

    public void withLogin(String login) {
        this.login = login;
    }

    public void withPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
