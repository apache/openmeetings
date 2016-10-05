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
package org.apache.openmeetings.service.calendar.caldav;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.calendar.OmCalendarDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.calendar.OmCalendar;
import org.apache.openmeetings.db.entity.calendar.OmCalendar.SyncType;
import org.apache.openmeetings.service.calendar.caldav.handler.CalendarHandler;
import org.apache.openmeetings.service.calendar.caldav.handler.CtagHandler;
import org.apache.openmeetings.service.calendar.caldav.handler.EtagsHandler;
import org.apache.openmeetings.service.calendar.caldav.handler.WebDAVSyncHandler;
import org.apache.wicket.util.string.Strings;
import org.osaf.caldav4j.CalDAVConstants;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

/**
 * Class which does syncing and provides respective API's required for performing CalDAV Operations.
 * @author Ankush Mishra
 */
public class AppointmentManager {
	private static final Logger log = Red5LoggerFactory.getLogger(AppointmentManager.class, webAppRootKey);

	//HttpClient and ConnectionManager Params
	private static final int IDLE_CONNECTION_TIMEOUT = 30000; // 30 seconds
	private static final int MAX_HOST_CONNECTIONS = 6; // Number of simultaneous connections to one host
	private static final int MAX_TOTAL_CONNECTIONS = 10; // Max Connections, at one time in memory.
	private static final int CONNECTION_MANAGER_TIMEOUT = 1000; // Waits for 1 sec if no empty connection exists

	private MultiThreadedHttpConnectionManager connmanager = null;

	@Autowired
	private OmCalendarDao calendarDao;
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private iCalUtils utils;

	/**
	 * Returns a new HttpClient with the inbuilt connection manager in this.
	 *
	 * @return HttpClient object that was created.
	 */
	public HttpClient createHttpClient() {
		if (connmanager == null) {
			connmanager = new MultiThreadedHttpConnectionManager();
			HttpConnectionManagerParams params = new HttpConnectionManagerParams();
			params.setDefaultMaxConnectionsPerHost(MAX_HOST_CONNECTIONS);
			params.setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
			connmanager.setParams(params);
		}

		HttpClientParams clientParams = new HttpClientParams();
		clientParams.setConnectionManagerTimeout(CONNECTION_MANAGER_TIMEOUT);
		return new HttpClient(connmanager);
	}

	/**
	 * Returns the Path from the Calendar.
	 *
	 * @param client   Client which makes the connection.
	 * @param calendar Calendar who's URL is used to get the path from.
	 * @return Path component of the URL.
	 */
	public String getPathfromCalendar(HttpClient client, OmCalendar calendar) {
		URI temp = URI.create(calendar.getHref());
		client.getHostConfiguration().setHost(temp.getHost(), temp.getPort(), temp.getScheme());
		return temp.getPath();
	}

	/**
	 * Ensure the URL ends with a, trailing slash, i.e. "/"
	 *
	 * @param str String URL to check.
	 * @return String which has a trailing slash.
	 */
	private static String ensureTrailingSlash(String str) {
		return str.endsWith("/") || str.endsWith("\\") ? str : str + "/";
	}

	/**
	 * Adds the Credentials provided to the given client on the Calendar's URL.
	 *
	 * @param client      Client which makes the connection.
	 * @param calendar    Calendar whose Host the Credentials are for.
	 * @param credentials Credentials to add
	 */
	public void provideCredentials(HttpClient client, OmCalendar calendar, Credentials credentials) {
		if (!Strings.isEmpty(calendar.getHref()) && credentials != null) {
			URI temp = URI.create(calendar.getHref());
			client.getHostConfiguration().setHost(temp.getHost(), temp.getPort(), temp.getScheme());
			client.getState().setCredentials(new AuthScope(temp.getHost(), temp.getPort()), credentials);
		}
	}

	/**
	 * Tests if the Calendar's URL can be accessed, or not.
	 *
	 * @param client   Client which makes the connection.
	 * @param calendar Calendar whose URL is to be accessed.
	 * @return Returns true for HTTP Status 200, or 204, else false.
	 */
	public boolean testConnection(HttpClient client, OmCalendar calendar) {
		cleanupIdleConnections();

		OptionsMethod optionsMethod = null;
		try {
			String path = getPathfromCalendar(client, calendar);
			optionsMethod = new OptionsMethod(path);
			optionsMethod.setRequestHeader("Accept", "*/*");
			client.executeMethod(optionsMethod);
			int status = optionsMethod.getStatusCode();
			if (status == SC_OK || status == SC_NO_CONTENT) {
				return true;
			}
		} catch (IOException e) {
			log.error("Error executing OptionsMethod during testConnection.", e);
		} catch (Exception e) {
			//Should not ever reach here.
			log.error("Severe Error in executing OptionsMethod during testConnection.", e);
		} finally {
			if (optionsMethod != null) {
				optionsMethod.releaseConnection();
			}
		}
		return false;
	}

	/**
	 * Create or Update calendar on the database.
	 *
	 * @param calendar Calendar to update or create
	 */
	public boolean createCalendar(HttpClient client, OmCalendar calendar) {
		if (calendar.getId() == null && calendar.getSyncType() != SyncType.GOOGLE_CALENDAR) {
			return discoverCalendars(client, calendar);
		}
		calendarDao.update(calendar);
		return true;
	}

	/**
	 * Deletes the calendar from the local database.
	 *
	 * @param calendar Calendar to delete
	 */
	public void deleteCalendar(OmCalendar calendar) {
		calendarDao.delete(calendar);
	}

	public List<OmCalendar> getCalendars() {
		return calendarDao.get();
	}

	/**
	 * @see OmCalendarDao#getByUser(Long)
	 */
	public List<OmCalendar> getCalendars(Long userid) {
		return calendarDao.getByUser(userid);
	}

	/**
	 * @see OmCalendarDao#getGoogleCalendars(Long)
	 */
	public List<OmCalendar> getGoogleCalendars(Long userId) {
		return calendarDao.getGoogleCalendars(userId);
	}

	/**
	 * Function which when called performs syncing based on the type of Syncing detected.
	 *
	 * @param calendar Calendar who's sync has to take place
	 */
	public void syncItem(HttpClient client, OmCalendar calendar) {
		cleanupIdleConnections();

		if (calendar.getSyncType() != SyncType.NONE) {
			CalendarHandler calendarHandler;
			String path = getPathfromCalendar(client, calendar);

			switch (calendar.getSyncType()) {
				case WEBDAV_SYNC:
					calendarHandler = new WebDAVSyncHandler(path, calendar, client, appointmentDao, utils);
					break;
				case CTAG:
					calendarHandler = new CtagHandler(path, calendar, client, appointmentDao, utils);
					break;
				case ETAG:
				default: //Default is the EtagsHandler.
					calendarHandler = new EtagsHandler(path, calendar, client, appointmentDao, utils);
					break;
			}

			calendarHandler.syncItems();
			calendarDao.update(calendar);
		}
	}

	/**
	 * Syncs all the calendars currrently present on the DB.
	 */
	public void syncItems(HttpClient client, Long userId) {
		List<OmCalendar> calendars = getCalendars(userId);
		for (OmCalendar calendar : calendars) {
			syncItem(client, calendar);
		}
	}

	/**
	 * Function which finds all the calendars of the Principal URL of the calendar
	 */
	private boolean discoverCalendars(HttpClient client, OmCalendar calendar) {
		cleanupIdleConnections();

		if (calendar.getSyncType() == SyncType.NONE) {
			PropFindMethod propFindMethod = null;
			String userPath = null, homepath = null;

			DavPropertyName curUserPrincipal = DavPropertyName.create("current-user-principal"),
					calHomeSet = DavPropertyName.create("calendar-home-set", CalDAVConstants.NAMESPACE_CALDAV),
					suppCalCompSet = DavPropertyName.create("supported-calendar-component-set", CalDAVConstants.NAMESPACE_CALDAV);

			//Find out whether it's a calendar or if we can find the calendar-home or current-user url
			try {
				String path = getPathfromCalendar(client, calendar);

				DavPropertyNameSet properties = new DavPropertyNameSet();
				properties.add(curUserPrincipal);
				properties.add(calHomeSet);
				properties.add(DavPropertyName.RESOURCETYPE);

				propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
				client.executeMethod(propFindMethod);

				if (propFindMethod.succeeded()) {
					for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
						DavPropertySet set = response.getProperties(SC_OK);
						DavProperty<?> calhome = set.get(calHomeSet), curPrinci = set.get(curUserPrincipal),
								resourcetype = set.get(DavPropertyName.RESOURCETYPE);

						if (checkCalendarResourceType(resourcetype)) {
							//This is a calendar and thus initialize and return
							return initCalendar(client, calendar);
						}

						//Else find all the calendars on the Principal and return.
						if (calhome != null) {
							//Calendar Home Path
							homepath = getTextValuefromProperty(calhome);
							break;
						} else if (curPrinci != null) {
							//Current User Principal Path
							userPath = getTextValuefromProperty(curPrinci);
							break;
						}
					}
				} else {
					return false;
				}

				if (homepath == null && userPath != null) {
					//If calendar home path wasn't set, then we get it
					DavPropertyNameSet props = new DavPropertyNameSet();
					props.add(calHomeSet);
					propFindMethod = new PropFindMethod(userPath, props, DavConstants.DEPTH_0);
					client.executeMethod(propFindMethod);

					if (propFindMethod.succeeded()) {
						for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
							DavPropertySet set = response.getProperties(SC_OK);
							DavProperty<?> calhome = set.get(calHomeSet);

							if (calhome != null) {
								homepath = getTextValuefromProperty(calhome);
								break;
							}
						}
					} else {
						return false;
					}
				}

				if (homepath != null) {
					DavPropertyNameSet props = new DavPropertyNameSet();
					props.add(DavPropertyName.RESOURCETYPE);
					props.add(suppCalCompSet);
					props.add(DavPropertyName.DISPLAYNAME);

					propFindMethod = new PropFindMethod(homepath, props, DavConstants.DEPTH_1);

					client.executeMethod(propFindMethod);

					if (propFindMethod.succeeded()) {
						boolean success = false;
						for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
							boolean isVevent = false, isCalendar;

							DavPropertySet set = response.getProperties(SC_OK);
							DavProperty<?> p = set.get(suppCalCompSet),
									resourcetype = set.get(DavPropertyName.RESOURCETYPE),
									displayname = set.get(DavPropertyName.DISPLAYNAME);

							isCalendar = checkCalendarResourceType(resourcetype);

							if (p != null) {
								for (Object o : (Collection<?>) p.getValue()) {
									if (o instanceof Element) {
										Element e = (Element) o;
										String name = DomUtil.getAttribute(e, "name", null);
										if (name.equals("VEVENT")) {
											isVevent = true;
										}
									}
								}
							}

							if (isCalendar && isVevent) {
								success = true;
								//Get New Calendar
								OmCalendar tempCalendar = new OmCalendar();

								if (displayname != null) {
									tempCalendar.setTitle(displayname.getValue().toString());
								}

								tempCalendar.setHref(client.getHostConfiguration().getHostURL() + response.getHref());

								tempCalendar.setDeleted(false);
								tempCalendar.setOwner(calendar.getOwner());

								calendarDao.update(tempCalendar);
								initCalendar(client, tempCalendar);
							}
						}
						return success;
					}
				}

			} catch (IOException e) {
				log.error("Error executing PROPFIND Method, during testConnection.", e);
			} catch (Exception e) {
				log.error("Severe Error in executing PROPFIND Method, during testConnection.", e);
			} finally {
				if (propFindMethod != null) {
					propFindMethod.releaseConnection();
				}
			}
		}

		return false;
	}

	private static String getTextValuefromProperty(DavProperty<?> property) {
		String value = null;

		if (property != null) {
			for (Object o : (Collection<?>) property.getValue()) {
				if (o instanceof Element) {
					Element e = (Element) o;
					value = DomUtil.getTextTrim(e);
					break;
				}
			}
		}
		return value;
	}

	/**
	 * Returns true if the resourcetype Property has a Calendar Element under it.
	 *
	 * @param resourcetype ResourceType Property
	 * @return True if, resource is Calendar, else false.
	 */
	private static boolean checkCalendarResourceType(DavProperty<?> resourcetype) {
		boolean isCalendar = false;

		if (resourcetype != null) {
			DavPropertyName calProp = DavPropertyName.create("calendar", CalDAVConstants.NAMESPACE_CALDAV);

			for (Object o : (Collection<?>) resourcetype.getValue()) {
				if (o instanceof Element) {
					Element e = (Element) o;
					if (e.getLocalName().equals(calProp.getName())) {
						isCalendar = true;
					}
				}
			}
		}
		return isCalendar;
	}

	/**
	 * Function to initialize the Calendar on the type of syncing and whether it can be used or not.
	 */
	private boolean initCalendar(HttpClient client, OmCalendar calendar) {

		if (calendar.getToken() == null || calendar.getSyncType() == SyncType.NONE) {
			calendarDao.update(calendar);

			PropFindMethod propFindMethod = null;

			try {
				String path = getPathfromCalendar(client, calendar);

				DavPropertyNameSet properties = new DavPropertyNameSet();
				properties.add(DavPropertyName.RESOURCETYPE);
				properties.add(DavPropertyName.DISPLAYNAME);
				properties.add(CtagHandler.DNAME_GETCTAG);
				properties.add(WebDAVSyncHandler.DNAME_SYNCTOKEN);

				propFindMethod = new PropFindMethod(path, properties, CalDAVConstants.DEPTH_0);
				client.executeMethod(propFindMethod);

				if (propFindMethod.succeeded()) {

					for (MultiStatusResponse response : propFindMethod.getResponseBodyAsMultiStatus().getResponses()) {
						DavPropertySet set = response.getProperties(SC_OK);

						if (calendar.getTitle() == null) {
							DavProperty<?> property = set.get(DavPropertyName.DISPLAYNAME);
							calendar.setTitle(property == null ? null : property.getValue().toString());
						}

						DavProperty<?> ctag = set.get(CtagHandler.DNAME_GETCTAG),
								syncToken = set.get(WebDAVSyncHandler.DNAME_SYNCTOKEN);
						if (syncToken != null) {
							calendar.setSyncType(SyncType.WEBDAV_SYNC);
						} else if (ctag != null) {
							calendar.setSyncType(SyncType.CTAG);
						} else {
							calendar.setSyncType(SyncType.ETAG);
						}
					}

					syncItem(client, calendar);
					return true;
				} else {
					log.error("Error executing PROPFIND Method, with status Code: {}", propFindMethod.getStatusCode());
					calendar.setSyncType(SyncType.NONE);
				}

			} catch (IOException e) {
				log.error("Error executing OptionsMethod during testConnection.", e);
			} catch (Exception e) {
				log.error("Severe Error in executing OptionsMethod during testConnection.", e);
			} finally {
				if (propFindMethod != null) {
					propFindMethod.releaseConnection();
				}
			}
		}

		return false;
	}

	/**
	 * Function for create/updating multiple appointment on the server.
	 * Performs modification alongside of creation new events on the server.
	 *
	 * @param appointment Appointment to create/update.
	 */
	public boolean updateItem(HttpClient client, Appointment appointment) {
		cleanupIdleConnections();

		OmCalendar calendar = appointment.getCalendar();
		SyncType type = calendar.getSyncType();
		if (type != SyncType.NONE && type != SyncType.GOOGLE_CALENDAR) {
			CalendarHandler calendarHandler;
			String path = ensureTrailingSlash(getPathfromCalendar(client, calendar));

			switch (type) {
				case WEBDAV_SYNC:
				case CTAG:
				case ETAG:
					calendarHandler = new EtagsHandler(path, calendar, client, appointmentDao, utils);
					break;
				default:
					return false;
			}
			return calendarHandler.updateItem(appointment);
		}
		return false;
	}

	/**
	 * Delete Appointment on the CalDAV server.
	 * Delete's on the Server only if the ETag of the Appointment is the one on the server,
	 * i.e. only if the Event hasn't changed on the Server.
	 *
	 * @param appointment Appointment to Delete
	 */
	public boolean deleteItem(HttpClient client, Appointment appointment) {
		cleanupIdleConnections();

		OmCalendar calendar = appointment.getCalendar();
		SyncType type = calendar.getSyncType();

		if (type != SyncType.NONE && type != SyncType.GOOGLE_CALENDAR) {
			CalendarHandler calendarHandler;
			String path = getPathfromCalendar(client, calendar);

			switch (type) {
				case WEBDAV_SYNC:
				case CTAG:
				case ETAG:
					calendarHandler = new EtagsHandler(path, calendar, client, appointmentDao, utils);
					break;
				default:
					return false;
			}

			return calendarHandler.deleteItem(appointment);
		}
		return false;
	}

	/**
	 * Returns the String value of the property, else null.
	 *
	 * @param property Property who's string value is to be returned.
	 * @return String representation of the Property Value.
	 */
	public static String getTokenFromProperty(DavProperty<?> property) {
		return (property == null) ? null : property.getValue().toString();
	}

	/**
	 * Cleans up unused idle connections.
	 */
	public void cleanupIdleConnections() {
		if (connmanager != null) {
			connmanager.closeIdleConnections(IDLE_CONNECTION_TIMEOUT);
		}
	}

	/**
	 * Method which is called when the Context is destroyed.
	 */
	public void destroy() {
		MultiThreadedHttpConnectionManager.shutdownAll();
		connmanager = null;
	}
}
