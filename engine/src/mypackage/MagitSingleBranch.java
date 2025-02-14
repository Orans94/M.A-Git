
package mypackage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}name"/>
 *         &lt;element name="pointed-commit">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tracking-after" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="is-remote" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="tracking" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "pointedCommit",
    "trackingAfter"
})
@XmlRootElement(name = "MagitSingleBranch")
public class MagitSingleBranch {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(name = "pointed-commit", required = true)
    protected MagitSingleBranch.PointedCommit pointedCommit;
    @XmlElement(name = "tracking-after")
    protected String trackingAfter;
    @XmlAttribute(name = "is-remote")
    protected Boolean isRemote;
    @XmlAttribute(name = "tracking")
    protected Boolean tracking;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the pointedCommit property.
     * 
     * @return
     *     possible object is
     *     {@link MagitSingleBranch.PointedCommit }
     *     
     */
    public MagitSingleBranch.PointedCommit getPointedCommit() {
        return pointedCommit;
    }

    /**
     * Sets the value of the pointedCommit property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitSingleBranch.PointedCommit }
     *     
     */
    public void setPointedCommit(MagitSingleBranch.PointedCommit value) {
        this.pointedCommit = value;
    }

    /**
     * Gets the value of the trackingAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrackingAfter() {
        return trackingAfter;
    }

    /**
     * Sets the value of the trackingAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrackingAfter(String value) {
        this.trackingAfter = value;
    }

    /**
     * Gets the value of the isRemote property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsRemote() {
        if (isRemote == null) {
            return false;
        } else {
            return isRemote;
        }
    }

    /**
     * Sets the value of the isRemote property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRemote(Boolean value) {
        this.isRemote = value;
    }

    /**
     * Gets the value of the tracking property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isTracking() {
        if (tracking == null) {
            return false;
        } else {
            return tracking;
        }
    }

    /**
     * Sets the value of the tracking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTracking(Boolean value) {
        this.tracking = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PointedCommit {

        @XmlAttribute(name = "id", required = true)
        protected String id;

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

    }

}
