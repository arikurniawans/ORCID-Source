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
package org.orcid.persistence.jpa.entities;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.Iso3166Country;

/**
 * orcid-entities - Dec 6, 2011 - WorkEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "work")
public class WorkEntity extends WorkBaseEntity implements Comparable<WorkEntity>, ProfileAware, DisplayIndexInterface, SourceAware {

    private static final long serialVersionUID = 1L;

    private String citation;
    private Iso3166Country iso2Country;
    private CitationType citationType;
    private String contributorsJson;
    private ProfileEntity profile;
    private Date addedToProfileDate;

    @Column(name = "citation", length = 5000)
    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "citation_type", length = 100)
    public CitationType getCitationType() {
        return citationType;
    }

    public void setCitationType(CitationType citationType) {
        this.citationType = citationType;
    }

    @Column(name = "contributors_json")
    public String getContributorsJson() {
        return contributorsJson;
    }

    public void setContributorsJson(String contributorsJson) {
        this.contributorsJson = contributorsJson;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "iso2_country", length = 2)
    public Iso3166Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso3166Country iso2Country) {
        this.iso2Country = iso2Country;
    }

    /**
     * @return the profile
     */
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "orcid", nullable = true)
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile
     *            the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Column(name = "added_to_profile_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getAddedToProfileDate() {
        return addedToProfileDate;
    }

    public void setAddedToProfileDate(Date addedToProfileDate) {
        this.addedToProfileDate = addedToProfileDate;
    }

    @Override
    public int compareTo(WorkEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        int comparison = compareOrcidId(other);
        if (comparison == 0) {
            comparison = comparePublicationDate(other);
            if (comparison == 0) {
                comparison = compareTitles(other);
                if (comparison == 0) {
                    return compareIds(other);
                }
            }
        }

        return comparison;
    }

    private int compareTitles(WorkEntity other) {
        if (other.getTitle() == null) {
            if (title == null) {
                return 0;
            } else {
                return 1;
            }
        }
        if (title == null) {
            return -1;
        }
        return title.compareToIgnoreCase(other.getTitle());
    }

    private int compareIds(WorkEntity other) {
        if (other.getId() == null) {
            if (id == null) {
                if (equals(other)) {
                    return 0;
                } else {
                    // If can't determine preferred order, then be polite and
                    // say 'after you!'
                    return -1;
                }
            } else {
                return 1;
            }
        }
        if (id == null) {
            return -1;
        }
        return id.compareTo(other.getId());
    }

    private int comparePublicationDate(WorkEntity other) {
        if (other.getPublicationDate() == null) {
            if (this.publicationDate == null) {
                return 0;
            } else {
                return 1;
            }
        } else if (this.publicationDate == null) {
            return -1;
        }

        return this.publicationDate.compareTo(other.getPublicationDate());
    }

    private int compareOrcidId(WorkEntity other) {
        if (this.getProfile() == null) {
            if (other.getProfile() == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (other.getProfile() == null) {
            return 1;
        } else {
            return this.getProfile().getId().compareTo(other.getProfile().getId());
        }
    }

    public static class ChronologicallyOrderedWorkEntityComparator implements Comparator<WorkEntity> {
        public int compare(WorkEntity work1, WorkEntity work2) {
            if (work2 == null) {
                throw new NullPointerException("Can't compare with null");
            }

            // Negate the result (Multiply it by -1) to reverse the order.
            int comparison = work1.comparePublicationDate(work2) * -1;

            if (comparison == 0) {
                comparison = work1.compareTitles(work2);
                if (comparison == 0) {
                    return work1.compareIds(work2);
                }
            }

            return comparison;
        }
    }

    /**
     * Clean simple fields so that entity can be reused.
     */
    public void clean() {
        title = null;
        subtitle = null;
        description = null;
        workUrl = null;
        citation = null;
        citationType = null;
        workType = null;
        publicationDate = null;
        journalTitle = null;
        languageCode = null;
        iso2Country = null;
    }

}
