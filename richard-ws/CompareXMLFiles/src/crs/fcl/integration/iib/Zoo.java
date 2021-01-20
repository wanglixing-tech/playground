package crs.fcl.integration.iib;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Zoo {
    private Long zooID;
    private String zooName;
    private String phone;
    private String zooAddress;

    public Long getZooID() {
        return zooID;
    }

    @XmlElement(name = "zooId")
    public void setZooID(Long zooID) {
        this.zooID = zooID;
    }

    public String getZooName() {
        return zooName;
    }

    @XmlElement
    public void setZooName(String zooName) {
        this.zooName = zooName;
    }

    public String getPhone() {
        return phone;
    }

    @XmlElement
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZooAddress() {
        return zooAddress;
    }

    @XmlElement
    public void setZooAddress(String zooAddress) {
        this.zooAddress = zooAddress;
    }

    @Override
    public String toString() {
        return "Zoo [zooID=" + zooID + ", zooName=" + zooName + ", phone=" + phone + ", zooAddress=" + zooAddress + "]";
    }

    public Zoo getDiff(Zoo that) {

        Zoo diff = new Zoo();

        if (that.getZooID() == null || (this.getZooID() != null && !this.getZooID().equals(that.getZooID()))) {
            diff.setZooID(this.getZooID());
        }

        if (that.getZooName() == null || (this.getZooName() != null && !this.getZooName().equals(that.getZooName()))) {
            diff.setZooName(this.getZooName());
        }

        if (that.getPhone() == null || (this.getPhone() != null && !this.getPhone().equals(that.getPhone()))) {
            diff.setPhone(this.getPhone());
        }

        if (that.getZooAddress() == null || (this.getZooAddress() != null && !this.getZooAddress().equals(that.getZooAddress()))) {
            diff.setZooAddress(this.getZooAddress());
        }

        return diff;

    }

}