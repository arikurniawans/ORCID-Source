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

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.record_rc2.ExternalID;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FundingExternalIDConverter extends BidirectionalConverter<ExternalID, String> {

    @Override
    public ExternalID convertFrom(String externalIdentifiersAsString, Type<ExternalID> arg1) {
        org.orcid.pojo.FundingExternalIdentifier jpaExtId = JsonUtils.readObjectFromJsonString(externalIdentifiersAsString, org.orcid.pojo.FundingExternalIdentifier.class);
        ExternalID result = jpaExtId.toRecordPojo();
        return result;
    }

    @Override
    public String convertTo(ExternalID externalID, Type<String> arg1) {
        org.orcid.pojo.FundingExternalIdentifier jpaExternalIdentifier = org.orcid.pojo.FundingExternalIdentifier.fromRecordPojo(externalID);
        return JsonUtils.convertToJsonString(jpaExternalIdentifier);
    }

}