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
package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.RecordNameEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameManager {
    RecordNameEntity getRecordName(String orcid, long lastModified);

    RecordNameEntity findByCreditName(String creditName);
    
    boolean updateRecordName(RecordNameEntity recordName);

    void createRecordName(RecordNameEntity recordName);
}
