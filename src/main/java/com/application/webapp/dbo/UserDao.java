package com.application.webapp.dbo;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

	@PersistenceContext
	private EntityManager entityManager;

	public Long addNewUser(UserList data) {
		entityManager.persist(data);
		entityManager.merge(data);
		return data.getId();
	}


	public UserList getUserByUsername(String username) {
		String hql = "from UserList where username =?1";
		TypedQuery<UserList> query = entityManager.createQuery(hql, UserList.class);
		query.setParameter(1, username);
		return getSingleResult(query);
	}

	public List<UserList> getUserList() {
		String hql = "from UserList where status=?1";
		TypedQuery<UserList> query = entityManager.createQuery(hql, UserList.class);
		query.setParameter(1, "active");
		return query.getResultList();
	}

	private <T> T getSingleResult(TypedQuery<T> query) {
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}