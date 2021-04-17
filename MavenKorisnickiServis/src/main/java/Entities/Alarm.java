/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author neman
 */
@Entity
@Table(name = "alarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alarm.findAll", query = "SELECT a FROM Alarm a"),
    @NamedQuery(name = "Alarm.findByIdAlarm", query = "SELECT a FROM Alarm a WHERE a.idAlarm = :idAlarm"),
    @NamedQuery(name = "Alarm.findByStartTime", query = "SELECT a FROM Alarm a WHERE a.startTime = :startTime"),
    @NamedQuery(name = "Alarm.findByRepeatEveryDay", query = "SELECT a FROM Alarm a WHERE a.repeatEveryDay = :repeatEveryDay")})
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAlarm")
    private Integer idAlarm;
    @Basic(optional = false)
    @Column(name = "StartTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Basic(optional = false)
    @Column(name = "RepeatEveryDay")
    private int repeatEveryDay;
    @OneToMany(mappedBy = "alarmID")
    private List<Obligation> obligationList;
    @JoinColumn(name = "MusicID", referencedColumnName = "idMusic")
    @ManyToOne(optional = false)
    private Music musicID;
    @JoinColumn(name = "UserID", referencedColumnName = "idUsers")
    @ManyToOne(optional = false)
    private Users userID;

    public Alarm() {
    }

    public Alarm(Integer idAlarm) {
        this.idAlarm = idAlarm;
    }

    public Alarm(Integer idAlarm, Date startTime, int repeatEveryDay) {
        this.idAlarm = idAlarm;
        this.startTime = startTime;
        this.repeatEveryDay = repeatEveryDay;
    }

    public Integer getIdAlarm() {
        return idAlarm;
    }

    public void setIdAlarm(Integer idAlarm) {
        this.idAlarm = idAlarm;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getRepeatEveryDay() {
        return repeatEveryDay;
    }

    public void setRepeatEveryDay(int repeatEveryDay) {
        this.repeatEveryDay = repeatEveryDay;
    }

    @XmlTransient
    public List<Obligation> getObligationList() {
        return obligationList;
    }

    public void setObligationList(List<Obligation> obligationList) {
        this.obligationList = obligationList;
    }

    public Music getMusicID() {
        return musicID;
    }

    public void setMusicID(Music musicID) {
        this.musicID = musicID;
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
        hash += (idAlarm != null ? idAlarm.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alarm)) {
            return false;
        }
        Alarm other = (Alarm) object;
        if ((this.idAlarm == null && other.idAlarm != null) || (this.idAlarm != null && !this.idAlarm.equals(other.idAlarm))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Alarm[ idAlarm=" + idAlarm + " ]";
    }
    
}
