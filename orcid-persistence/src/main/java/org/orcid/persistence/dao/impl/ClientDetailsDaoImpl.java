/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.OrcidClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman
 */
@PersistenceContext(unitName = "orcid")
public class ClientDetailsDaoImpl extends GenericDaoImpl<OrcidClientDetailsEntity, String> implements ClientDetailsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsDaoImpl.class);

    public ClientDetailsDaoImpl() {
        super(OrcidClientDetailsEntity.class);
    }

    @Override
    @Cacheable(value = "client-details", key = "#clientId.concat('-').concat(#lastModified)")
    public OrcidClientDetailsEntity findByClientId(String clientId, long lastModified) {
        TypedQuery<OrcidClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where id = :clientId", OrcidClientDetailsEntity.class);
        query.setParameter("clientId", clientId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No client found for {}", clientId, e);
            return null;
        }
    }

    @Override
    public Date getLastModified(String clientId) {
        TypedQuery<Date> query = entityManager.createQuery("select lastModified from ClientDetailsEntity where id = :clientId", Date.class);
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateLastModified(String clientId) {
        Query updateQuery = entityManager.createQuery("update ClientDetailsEntity set lastModified = now() where id = :clientId");
        updateQuery.setParameter("clientId", clientId);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public boolean removeClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery("delete from client_secret where client_details_id=:clientId and client_secret=:clientSecret");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean createClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager
                .createNativeQuery("INSERT INTO client_secret (client_details_id, client_secret, date_created, last_modified) VALUES (:clientId, :clientSecret, now(), now())");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }

    @Override
    public List<ClientSecretEntity> getClientSecretsByClientId(String clientId) {
        TypedQuery<ClientSecretEntity> query = entityManager.createQuery("From ClientSecretEntity WHERE client_details_id=:clientId", ClientSecretEntity.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Override
    public boolean exists(String clientId) {
        TypedQuery<Long> query = entityManager.createQuery("select count(*) from ClientDetailsEntity where client_details_id=:clientId", Long.class);
        query.setParameter("clientId", clientId);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public boolean belongsTo(String clientId, String groupId) {
        TypedQuery<OrcidClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where id = :clientId and groupProfileId = :groupId",
                OrcidClientDetailsEntity.class);
        query.setParameter("clientId", clientId);
        query.setParameter("groupId", groupId);
        try {
            query.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void updateClientType(ClientType clientType, String clientId) {
        Query updateQuery = entityManager.createQuery("update ClientDetailsEntity set clientType = :clientType where id = :clientId");
        updateQuery.setParameter("clientType", clientType);
        updateQuery.setParameter("clientId", clientId);
        updateQuery.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OrcidClientDetailsEntity> findByGroupId(String groupId) {
        Query query = entityManager.createQuery("from ClientDetailsEntity where groupProfileId = :groupId");
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void removeClient(String clientId) {
        OrcidClientDetailsEntity clientDetailsEntity = this.find(clientId);
        this.remove(clientDetailsEntity);
    }

    /**
     * Get the public client that belongs to the given orcid ID
     * 
     * @param ownerId
     *            The user or group id
     * @return the public client that belongs to the given user
     * */
    @Override
    public OrcidClientDetailsEntity getPublicClient(String ownerId) {
        TypedQuery<OrcidClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where groupProfileId = :ownerId and clientType = :clientType",
                OrcidClientDetailsEntity.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("clientType", ClientType.PUBLIC_CLIENT);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            LOGGER.warn("There is not public client for " + ownerId);
            return null;
        }
    }

    /**
     * Get member name
     * 
     * @param clientId
     *            The client id
     * @return the name of the member owner of the given client
     * */
    public String getMemberName(String clientId) {
        TypedQuery<String> query = entityManager.createQuery(
                "select creditName from RecordNameEntity where profile.id = (select groupProfileId from ClientDetailsEntity where id=:clientId)", String.class);
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }

}
