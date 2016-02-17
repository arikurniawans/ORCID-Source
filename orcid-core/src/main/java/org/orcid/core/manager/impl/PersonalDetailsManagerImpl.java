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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.core.manager.NameManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PersonalDetailsManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.version.impl.LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc2.CreditName;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.FamilyName;
import org.orcid.jaxb.model.record_rc2.GivenNames;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;
import org.orcid.persistence.jpa.entities.NameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class PersonalDetailsManagerImpl implements PersonalDetailsManager {
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OtherNameManager otherNameManager;
    
    @Resource
    private NameManager nameManager;
    
    
    @Override
    public Name getName(String orcid) {
        NameEntity nameEntity = nameManager.getName(orcid);
        Name name = new Name();
        if (nameEntity != null) {
            Visibility nameVisibility = Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
            if(nameEntity.getVisibility() != null) {
                nameVisibility = Visibility.fromValue(nameEntity.getVisibility().value());
            }            
            name.setVisibility(nameVisibility);            
            if (!PojoUtil.isEmpty(nameEntity.getCreditName())) {
                name.setCreditName(new CreditName(nameEntity.getCreditName()));
            }
            if (!PojoUtil.isEmpty(nameEntity.getFamilyName())) {
                name.setFamilyName(new FamilyName(nameEntity.getFamilyName()));
            }
            if (!PojoUtil.isEmpty(nameEntity.getGivenName())) {
                name.setGivenNames(new GivenNames(nameEntity.getGivenName()));
            }                        
        }
        return name;
    }
    
    @Override
    public PersonalDetails getPersonalDetails(String orcid) {
        PersonalDetails personalDetails = new PersonalDetails();
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);                        
        Name name = getName(orcid);
        Biography bio = new Biography();
        if(!PojoUtil.isEmpty(profileEntity.getBiography())) {
            bio.setContent(profileEntity.getBiography());
            Visibility bioVisibility = Visibility.fromValue(OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value());
            if(profileEntity.getBiographyVisibility() != null) {
                bioVisibility = Visibility.fromValue(profileEntity.getBiographyVisibility().value());
            }
            bio.setVisibility(bioVisibility);
        }
        
        Date lastModified = profileEntity.getLastModified();
        long lastMofieiedTime = (lastModified == null) ? 0 : lastModified.getTime();
        
        OtherNames otherNames = otherNameManager.getMinimizedOtherNames(orcid, lastMofieiedTime);
        
        if(bio != null) {
            personalDetails.setBiography(bio);
        }
        if(name != null) {
            personalDetails.setName(name);
        }                               
        if(otherNames != null && otherNames.getOtherNames() != null) {
        	XMLGregorianCalendar latest = LastModifiedDatesHelper.calculateLatest(otherNames);
        	personalDetails.setLastModifiedDate(new LastModifiedDate(latest));
        	personalDetails.setOtherNames(otherNames);
        }
                
        return personalDetails;
    }   
    
    @Override
    public PersonalDetails getPublicPersonalDetails(String orcid) {
        PersonalDetails personalDetails = getPersonalDetails(orcid);
        if(personalDetails.getBiography() != null && !Visibility.PUBLIC.equals(personalDetails.getBiography().getVisibility())) {
            personalDetails.setBiography(null);
        }
        
        if(personalDetails.getName() != null && !Visibility.PUBLIC.equals(personalDetails.getName().getVisibility())) {
            personalDetails.setName(null);
        }
        
        if(personalDetails.getOtherNames() != null && personalDetails.getOtherNames().getOtherNames() != null && !personalDetails.getOtherNames().getOtherNames().isEmpty()) {
            List<OtherName> publicOtherNames = new ArrayList<OtherName>();
            for(OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
                if(Visibility.PUBLIC.equals(otherName.getVisibility())) {
                    publicOtherNames.add(otherName);
                }
            }
            if(publicOtherNames.isEmpty()) {
                personalDetails.setOtherNames(null);
            } else {
                personalDetails.getOtherNames().setOtherNames(publicOtherNames);
            }            
        } else {
            personalDetails.setOtherNames(null);
        }
        return personalDetails;
    }
}
