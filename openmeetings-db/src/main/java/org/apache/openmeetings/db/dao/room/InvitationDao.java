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
package org.apache.openmeetings.db.dao.room;

import static org.apache.openmeetings.db.util.DaoHelper.getRoot;
import static org.apache.openmeetings.db.util.DaoHelper.only;
import static org.apache.openmeetings.util.CalendarHelper.getZoneId;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.DaoHelper;
import org.apache.openmeetings.util.CalendarHelper;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class InvitationDao implements IDataProviderDao<Invitation> {
	private static final List<String> searchFields = List.of("invitee.firstname", "invitee.lastname", "invitee.login");
	private static final Logger log = LoggerFactory.getLogger(InvitationDao.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public Invitation get(Long invId) {
		return only(em.createNamedQuery("getInvitationbyId", Invitation.class)
				.setParameter("id", invId).getResultList());
	}

	@Override
	public List<Invitation> get(long start, long count) {
		return get(null, start, count, null);
	}

	@Override
	public List<Invitation> get(String search, long start, long count, SortParam<String> sort) {
		return DaoHelper.get(em, Invitation.class, false, search, searchFields, true, null, sort, start, count);
	}

	@Override
	public long count() {
		return count(null);
	}

	@Override
	public long count(String search) {
		return DaoHelper.count(em, Invitation.class, search, searchFields, true, null);
	}

	private Predicate getGroupFilter(Long userId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Subquery<Long> subquery = query.subquery(Long.class);
		Root<GroupUser> root = subquery.from(GroupUser.class);
		subquery.select(root.get("user").get("id"));
		subquery.where(builder.in(root.get("group").get("id")).value(DaoHelper.groupAdminQuery(userId, builder, subquery)));

		Root<Invitation> mainRoot = getRoot(query, Invitation.class);
		return builder.in(mainRoot.get("invitedBy").get("id")).value(subquery);
	}

	public List<Invitation> getGroup(String search, long start, long count, Long userId, SortParam<String> sort) {
		return DaoHelper.get(em, Invitation.class, false, search, searchFields, true
				, (builder, query) -> getGroupFilter(userId, builder, query)
				, sort, start, count);
	}

	public long countGroup(String search, Long userId) {
		return DaoHelper.count(em, Invitation.class, search, searchFields, true
				, (builder, query) -> getGroupFilter(userId, builder, query));
	}

	private Predicate getUserFilter(Long userId, CriteriaBuilder builder, CriteriaQuery<?> query) {
		Root<Invitation> root = getRoot(query, Invitation.class);
		return builder.equal(root.get("invitedBy").get("id"), userId);
	}

	public List<Invitation> getUser(String search, long start, long count, Long userId, SortParam<String> sort) {
		return DaoHelper.get(em, Invitation.class, false, search, searchFields, true
				, (builder, query) -> getUserFilter(userId, builder, query)
				, sort, start, count);
	}

	public long countUser(String search, Long userId) {
		return DaoHelper.count(em, Invitation.class, search, searchFields, true
				, (builder, query) -> getUserFilter(userId, builder, query));
	}

	public Invitation update(Invitation invitation) {
		// [OPENMEETINGS-2441] in life cycle state  unmanaged while cascading persistence via field
		invitation.setInvitedBy(em.find(User.class, invitation.getInvitedBy().getId()));
		if (invitation.getRoom() != null) {
			invitation.setRoom(em.find(Room.class, invitation.getRoom().getId()));
		}
		if (invitation.getRecording() != null) {
			invitation.setRecording(em.find(Recording.class, invitation.getRecording().getId()));
		}
		if (invitation.getId() == null) {
			em.persist(invitation);
		} else {
			invitation = em.merge(invitation);
		}
		return invitation;
	}

	@Override
	public Invitation update(Invitation entity, Long userId) {
		return update(entity);
	}

	@Override
	public void delete(Invitation entity, Long userId) {
		entity.setDeleted(true);
		update(entity, userId);
	}

	public void markUsed(Invitation i) {
		if (Valid.ONE_TIME == i.getValid()) {
			i.setUsed(true);
			update(i);
			em.flush(); // flash is required to eliminate 'detach' effect
		}
	}

	private Invitation get(String hash) {
		Invitation i = only(em.createNamedQuery("getInvitationByHashCode", Invitation.class)
				.setParameter("hashCode", hash).getResultList());
		return i != null && i.getHash().equals(hash) ? i : null;
	}

	public Invitation getByHash(String hash, boolean hidePass) {
		Invitation i = get(hash);
		if (i != null) {
			switch (i.getValid()) {
				case ONE_TIME:
					// one-time invitation
					i.setAllowEntry(!i.isUsed());
					break;
				case PERIOD:
					String tzId = i.getInvitee().getTimeZoneId();
					if (Strings.isEmpty(tzId) && i.getAppointment() != null) {
						log.warn("User with NO timezone found: {}", i.getInvitee().getId());
						tzId = i.getAppointment().getOwner().getTimeZoneId();
					}
					if (Strings.isEmpty(tzId)) {
						log.warn("Unable to obtain valid timezone, setting SYSTEM TZ");
						tzId = TimeZone.getDefault().getID();
					}
					LocalDateTime now = ZonedDateTime.now(getZoneId(tzId)).toLocalDateTime();
					LocalDateTime from = CalendarHelper.getDateTime(i.getValidFrom(), tzId);
					LocalDateTime to = CalendarHelper.getDateTime(i.getValidTo(), tzId);
					i.setAllowEntry(now.isAfter(from) && now.isBefore(to));
					break;
				case ENDLESS:
				default:
					i.setAllowEntry(true);
					break;
			}
			em.detach(i); // required to disable password update
			if (hidePass) {
				i.setPassword(null);
			}
		}
		return i;
	}
}
