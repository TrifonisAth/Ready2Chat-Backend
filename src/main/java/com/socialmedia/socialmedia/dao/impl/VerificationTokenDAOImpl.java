package com.socialmedia.socialmedia.dao.impl;

import com.socialmedia.socialmedia.dao.interfaces.VerificationTokenDAO;
import com.socialmedia.socialmedia.entities.VerificationToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class VerificationTokenDAOImpl implements VerificationTokenDAO {
    private final EntityManager entityManager;

public VerificationTokenDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(VerificationToken token) {
        entityManager.persist(token);
    }

    @Override
    @Transactional
    public void delete(long id) {
        VerificationToken token = entityManager.find(VerificationToken.class, id);
        entityManager.remove(token);
    }

    @Override
    @Transactional
    public void update(VerificationToken token) {
        entityManager.merge(token);
    }

    @Override
    public VerificationToken findTokenById(long id) {
        return entityManager.find(VerificationToken.class, id);
    }

    @Override
    public VerificationToken findByUserId(long id) {
        TypedQuery<VerificationToken> query = entityManager.createQuery("SELECT t FROM VerificationToken t WHERE t.id = :id", VerificationToken.class);
        query.setParameter("id", id);
        if (query.getResultList().isEmpty()) return null;
        return query.getSingleResult();
    }
}
