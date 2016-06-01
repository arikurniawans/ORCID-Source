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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.jpa.entities.OrcidClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

public class SSOCredentials implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 3L;
    
    private List<String> errors = new ArrayList<String>();       
    
    Text clientName;
    Text clientDescription;
    Text clientWebsite;
    Text clientOrcid;
    Text clientSecret;
    Set<RedirectUri> redirectUris;
    
    public static SSOCredentials toSSOCredentials(OrcidClientDetailsEntity clientDetails) {
        SSOCredentials result = new SSOCredentials();        
        if(clientDetails != null) {
            result.setClientName(Text.valueOf(clientDetails.getClientName()));
            result.setClientDescription(Text.valueOf(clientDetails.getClientDescription()));
            result.setClientWebsite(Text.valueOf(clientDetails.getClientWebsite()));
            if(clientDetails.getClientSecrets() != null) {
                for(ClientSecretEntity clientSecret : clientDetails.getClientSecrets()){
                    if(clientSecret.isPrimary())
                        result.setClientSecret(Text.valueOf(clientSecret.getDecryptedClientSecret()));
                }
            }
            result.setClientOrcid(Text.valueOf(clientDetails.getClientId()));            
            if(clientDetails.getClientRegisteredRedirectUris() != null && !clientDetails.getClientRegisteredRedirectUris().isEmpty()) {
                result.redirectUris = new TreeSet<RedirectUri>();
                for(ClientRedirectUriEntity redirectUri : clientDetails.getClientRegisteredRedirectUris()) {
                    if(RedirectUriType.SSO_AUTHENTICATION.value().equals(redirectUri.getRedirectUriType())) {
                        RedirectUri rUri = new RedirectUri();
                        rUri.setValue(Text.valueOf(redirectUri.getRedirectUri()));
                        result.redirectUris.add(rUri);
                    }                    
                }
            }
        }
        return result;
    }
    
    public Text getClientName() {
        return clientName;
    }
    public void setClientName(Text clientName) {
        this.clientName = clientName;
    }
    public Text getClientDescription() {
        return clientDescription;
    }
    public void setClientDescription(Text clientDescription) {
        this.clientDescription = clientDescription;
    }
    
    public Text getClientWebsite() {
        return clientWebsite;
    }

    public void setClientWebsite(Text clientWebsite) {
        this.clientWebsite = clientWebsite;
    }

    public Text getClientSecret() {
        return clientSecret;
    }
    public void setClientSecret(Text clientSecret) {
        this.clientSecret = clientSecret;
    }    
    
    public Set<RedirectUri> getRedirectUris() {
        return redirectUris;
    }
    public void setRedirectUris(Set<RedirectUri> redirectUris) {
        this.redirectUris = redirectUris;
    }
    public Text getClientOrcid() {
        return clientOrcid;
    }
    public void setClientOrcid(Text clientOrcid) {
        this.clientOrcid = clientOrcid;
    }
    public List<String> getErrors() {
        return errors;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
}
