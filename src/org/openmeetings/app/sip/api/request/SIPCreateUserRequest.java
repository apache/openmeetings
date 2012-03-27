/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
