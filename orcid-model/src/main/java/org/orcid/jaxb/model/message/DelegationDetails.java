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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.09 at 01:52:56 PM BST 
//

package org.orcid.jaxb.model.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}approval-date"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}delegate-summary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "approvalDate", "delegateSummary" })
@XmlRootElement(name = "delegation-details")
public class DelegationDetails implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "approval-date", required = true)
    protected ApprovalDate approvalDate;
    @XmlElement(name = "delegate-summary", required = true)
    protected DelegateSummary delegateSummary;

    /**
     * Gets the value of the approvalDate property.
     * 
     * @return
     *     possible object is
     *     {@link ApprovalDate }
     *     
     */
    public ApprovalDate getApprovalDate() {
        return approvalDate;
    }

    /**
     * Sets the value of the approvalDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApprovalDate }
     *     
     */
    public void setApprovalDate(ApprovalDate value) {
        this.approvalDate = value;
    }

    /**
     * Gets the value of the delegateSummary property.
     * 
     * @return
     *     possible object is
     *     {@link DelegateSummary }
     *     
     */
    public DelegateSummary getDelegateSummary() {
        return delegateSummary;
    }

    /**
     * Sets the value of the delegateSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link DelegateSummary }
     *     
     */
    public void setDelegateSummary(DelegateSummary value) {
        this.delegateSummary = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DelegationDetails)) {
            return false;
        }

        DelegationDetails that = (DelegationDetails) o;

        if (approvalDate != null ? !approvalDate.equals(that.approvalDate) : that.approvalDate != null) {
            return false;
        }
        if (delegateSummary != null ? !delegateSummary.equals(that.delegateSummary) : that.delegateSummary != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = approvalDate != null ? approvalDate.hashCode() : 0;
        result = 31 * result + (delegateSummary != null ? delegateSummary.hashCode() : 0);
        return result;
    }
}
