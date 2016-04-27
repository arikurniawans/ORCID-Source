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
package org.orcid.core.adapter.impl;

import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.pojo.IdentifierType;

public class IdentifierTypeConverter {

    public IdentifierTypeEntity fromPojo(IdentifierType id){
        IdentifierTypeEntity entity = new IdentifierTypeEntity();
        entity.setId(id.getPutCode());
        entity.setName(id.getName());
        entity.setIsDeprecated(id.getDeprecated());
        entity.setResolutionPrefix(id.getResolutionPrefix());
        entity.setValidationRegex(id.getValidationRegex());   
        entity.setDateCreated(id.getDateCreated());
        entity.setLastModified(id.getLastModified());
        entity.setSourceClient(id.getSourceClient());
        return entity;
    }
    
    public IdentifierType fromEntity(IdentifierTypeEntity entity){
        IdentifierType id = new IdentifierType();
        id.setPutCode(entity.getId());
        id.setName(entity.getName());
        id.setDeprecated(entity.getIsDeprecated());
        id.setResolutionPrefix(entity.getResolutionPrefix());
        id.setValidationRegex(entity.getValidationRegex());   
        id.setDateCreated(entity.getDateCreated());
        id.setLastModified(entity.getLastModified());
        id.setSourceClient(entity.getSourceClient());
        return id;
    }
}