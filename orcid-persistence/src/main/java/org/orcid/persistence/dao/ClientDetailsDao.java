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
package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.jpa.entities.OrcidClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

/**
 * 
 * @author Declan Newman
 * 
 */
public interface ClientDetailsDao extends GenericDao<OrcidClientDetailsEntity, String> {

    OrcidClientDetailsEntity findByClientId(String clientId, long lastModified);

    Date getLastModified(String clientId);
    
    void updateLastModified(String clientId);
    
    void updateClientType(ClientType clientType, String clientId);
    
    boolean removeClientSecret(String clientId, String clientSecret);
    
    boolean createClientSecret(String clientId, String clientSecret);
    
    List<ClientSecretEntity> getClientSecretsByClientId(String clientId);
    
    boolean exists(String clientId);
    
    boolean belongsTo(String clientId, String groupId);
    
    List<OrcidClientDetailsEntity> findByGroupId(String groupId);
    
    public void removeClient(String clientId);
    
    public OrcidClientDetailsEntity getPublicClient(String ownerId);
    
    String getMemberName(String clientId);
}
