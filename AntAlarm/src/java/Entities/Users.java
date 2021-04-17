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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "users")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u"),
    @NamedQuery(name = "Users.findByIdUsers", query = "SELECT u FROM Users u WHERE u.idUsers = :idUsers"),
    @NamedQuery(name = "Users.findByName", query = "SELECT u FROM Users u WHERE u.name = :name"),
    @NamedQuery(name = "Users.findBySurname", query = "SELECT u FROM Users u WHERE u.surname = :surname"),
    @NamedQuery(name = "Users.findByUsername", query = "SELECT u FROM Users u WHERE u.username = :username"),
    @NamedQuery(name = "Users.findByPassword", query = "SELECT u FROM Users u WHERE u.password = :password")})
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idUsers")
    private Integer idUsers;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Surname")
    private String surname;
    @Basic(optional = false)
    @Column(name = "Username")
    private String username;
    @Basic(optional = false)
    @Column(name = "Password")
    private String password;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userID")
    private List<Obligation> obligationList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userID")
    private List<Alreadyplayed> alreadyplayedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userID")
    private List<Alarm> alarmList;
    @JoinColumn(name = "HouseAddress", referencedColumnName = "idAddress")
    @ManyToOne(optional = false)
    private Addresses houseAddress;

    public Users() {
    }

    public Users(Integer idUsers) {
        this.idUsers = idUsers;
    }

    public Users(Integer idUsers, String name, String surname, String username, String password) {
        this.idUsers = idUsers;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
    }

    public Integer getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(Integer idUsers) {
        this.idUsers = idUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlTransient
    public List<Obligation> getObligationList() {
        return obligationList;
    }

    public void setObligationList(List<Obligation> obligationList) {
        this.obligationList = obligationList;
    }

    @XmlTransient
    public List<Alreadyplayed> getAlreadyplayedList() {
        return alreadyplayedList;
    }

    public void setAlreadyplayedList(List<Alreadyplayed> alreadyplayedList) {
        this.alreadyplayedList = alreadyplayedList;
    }

    @XmlTransient
    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public Addresses getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(Addresses houseAddress) {
        this.houseAddress = houseAddress;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsers != null ? idUsers.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Users)) {
            return false;
        }
        Users other = (Users) object;
        if ((this.idUsers == null && other.idUsers != null) || (this.idUsers != null && !this.idUsers.equals(other.idUsers))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Users[ idUsers=" + idUsers + " ]";
    }
    
}
