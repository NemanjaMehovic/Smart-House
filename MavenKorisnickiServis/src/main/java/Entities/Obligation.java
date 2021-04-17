/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neman
 */
@Entity
@Table(name = "obligation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Obligation.findAll", query = "SELECT o FROM Obligation o"),
    @NamedQuery(name = "Obligation.findByIdObligation", query = "SELECT o FROM Obligation o WHERE o.idObligation = :idObligation"),
    @NamedQuery(name = "Obligation.findByName", query = "SELECT o FROM Obligation o WHERE o.name = :name"),
    @NamedQuery(name = "Obligation.findByBeginning", query = "SELECT o FROM Obligation o WHERE o.beginning = :beginning"),
    @NamedQuery(name = "Obligation.findByTravelTimeStart", query = "SELECT o FROM Obligation o WHERE o.travelTimeStart = :travelTimeStart"),
    @NamedQuery(name = "Obligation.findByDuration", query = "SELECT o FROM Obligation o WHERE o.duration = :duration")})
public class Obligation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idObligation")
    private Integer idObligation;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Beginning")
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginning;
    @Basic(optional = false)
    @Column(name = "TravelTimeStart")
    @Temporal(TemporalType.TIMESTAMP)
    private Date travelTimeStart;
    @Basic(optional = false)
    @Column(name = "Duration")
    @Temporal(TemporalType.TIME)
    private Date duration;
    @JoinColumn(name = "Destination", referencedColumnName = "idAddress")
    @ManyToOne
    private Addresses destination;
    @JoinColumn(name = "AlarmID", referencedColumnName = "idAlarm")
    @ManyToOne
    private Alarm alarmID;
    @JoinColumn(name = "UserID", referencedColumnName = "idUsers")
    @ManyToOne(optional = false)
    private Users userID;

    public Obligation() {
    }

    public Obligation(Integer idObligation) {
        this.idObligation = idObligation;
    }

    public Obligation(Integer idObligation, String name, Date beginning, Date travelTimeStart, Date duration) {
        this.idObligation = idObligation;
        this.name = name;
        this.beginning = beginning;
        this.travelTimeStart = travelTimeStart;
        this.duration = duration;
    }

    public Integer getIdObligation() {
        return idObligation;
    }

    public void setIdObligation(Integer idObligation) {
        this.idObligation = idObligation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBeginning() {
        return beginning;
    }

    public void setBeginning(Date beginning) {
        this.beginning = beginning;
    }

    public Date getTravelTimeStart() {
        return travelTimeStart;
    }

    public void setTravelTimeStart(Date travelTimeStart) {
        this.travelTimeStart = travelTimeStart;
    }

    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date duration) {
        this.duration = duration;
    }

    public Addresses getDestination() {
        return destination;
    }

    public void setDestination(Addresses destination) {
        this.destination = destination;
    }

    public Alarm getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(Alarm alarmID) {
        this.alarmID = alarmID;
    }

    public Users getUserID() {
        return userID;
    }

    public void setUserID(Users userID) {
        this.userID = userID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idObligation != null ? idObligation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Obligation)) {
            return false;
        }
        Obligation other = (Obligation) object;
        if ((this.idObligation == null && other.idObligation != null) || (this.idObligation != null && !this.idObligation.equals(other.idObligation))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Obligation[ idObligation=" + idObligation + " ]";
    }
    
}
