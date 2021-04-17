/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author neman
 */
@Entity
@Table(name = "addresses")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Addresses.findAll", query = "SELECT a FROM Addresses a"),
    @NamedQuery(name = "Addresses.findByIdAddress", query = "SELECT a FROM Addresses a WHERE a.idAddress = :idAddress"),
    @NamedQuery(name = "Addresses.findByCountry", query = "SELECT a FROM Addresses a WHERE a.country = :country"),
    @NamedQuery(name = "Addresses.findByCity", query = "SELECT a FROM Addresses a WHERE a.city = :city"),
    @NamedQuery(name = "Addresses.findByZip", query = "SELECT a FROM Addresses a WHERE a.zip = :zip"),
    @NamedQuery(name = "Addresses.findByStreet", query = "SELECT a FROM Addresses a WHERE a.street = :street")})
public class Addresses implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAddress")
    private Integer idAddress;
    @Basic(optional = false)
    @Column(name = "Country")
    private String country;
    @Basic(optional = false)
    @Column(name = "City")
    private String city;
    @Basic(optional = false)
    @Column(name = "Zip")
    private String zip;
    @Basic(optional = false)
    @Column(name = "Street")
    private String street;
    @OneToMany(mappedBy = "destination")
    private List<Obligation> obligationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "houseAddress")
    private List<Users> usersList;

    public Addresses() {
    }

    public Addresses(Integer idAddress) {
        this.idAddress = idAddress;
    }

    public Addresses(Integer idAddress, String country, String city, String zip, String street) {
        this.idAddress = idAddress;
        this.country = country;
        this.city = city;
        this.zip = zip;
        this.street = street;
    }

    public Integer getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Integer idAddress) {
        this.idAddress = idAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @XmlTransient
    public List<Obligation> getObligationList() {
        return obligationList;
    }

    public void setObligationList(List<Obligation> obligationList) {
        this.obligationList = obligationList;
    }

    @XmlTransient
    public List<Users> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<Users> usersList) {
        this.usersList = usersList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAddress != null ? idAddress.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Addresses)) {
            return false;
        }
        Addresses other = (Addresses) object;
        if ((this.idAddress == null && other.idAddress != null) || (this.idAddress != null && !this.idAddress.equals(other.idAddress))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Addresses[ idAddress=" + idAddress + " ]";
    }
    
}
