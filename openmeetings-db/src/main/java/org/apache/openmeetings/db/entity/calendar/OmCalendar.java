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
package org.apache.openmeetings.db.entity.calendar;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.user.User;
import org.simpleframework.xml.Element;

import javax.persistence.*;

@Entity
@Table(name = "om_calendar")
@NamedQueries({
        @NamedQuery(name = "getCalendars", query = "SELECT c FROM OmCalendar c WHERE c.deleted = false ORDER BY c.id"),
        @NamedQuery(name = "getCalendarbyUser", query = "SELECT c FROM OmCalendar c WHERE c.owner.id = :userId"),
        , @NamedQuery(name = "getAppointmentsbyCalendarinRange",
            query="SELECT a FROM Appointment a "
                + "WHERE a.deleted = false "
                + "	AND ( "
                + "		(a.start BETWEEN :start AND :end) "
                + "		OR (a.end BETWEEN :start AND :end) "
                + "		OR (a.start < :start AND a.end > :end) "
                + "	)"
                + "	AND a.owner.id = :userId AND a.calendar.id = :calId  ")
        ,@NamedQuery(name = "getCalendarbyId",
            query = "SELECT c FROM OmCalendar c WHERE c.deleted = false AND c.id = :calId")
})
public class OmCalendar implements IDataProviderEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "href")
    private String href;

    @Column(name = "sync_token")
    private String token;

    @Column(name = "deleted")
    @Element(data=true)
    private boolean deleted;

    public enum SyncType{
        NONE,
        ETAG,
        CTAG,
        WEBDAV_SYNC
    }

    @Column(name = "sync_type")
    @Enumerated(EnumType.STRING)
    private SyncType syncType = SyncType.NONE;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    @ForeignKey(enabled = true)
    @Element(name="users_id", data=true, required=false)
    private User owner;


    //Getters + Setters
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public SyncType getSyncType() {
        return syncType;
    }

    public void setSyncType(SyncType syncType) {
        this.syncType = syncType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String toString(){
        return "Calendar [ id=" + id + ", title=" + title + ", token=" + token + ", href="
                + href + ", SyncType=" + syncType + ", deleted=" + deleted + ", login=" + login
                + ", owner=" + owner + " ]";
    }
}
