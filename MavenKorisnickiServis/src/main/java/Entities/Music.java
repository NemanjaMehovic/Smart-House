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
@Table(name = "music")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Music.findAll", query = "SELECT m FROM Music m"),
    @NamedQuery(name = "Music.findByIdMusic", query = "SELECT m FROM Music m WHERE m.idMusic = :idMusic"),
    @NamedQuery(name = "Music.findByName", query = "SELECT m FROM Music m WHERE m.name = :name")})
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idMusic")
    private Integer idMusic;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "musicID")
    private List<Alreadyplayed> alreadyplayedList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "musicID")
    private List<Alarm> alarmList;

    public Music() {
    }

    public Music(Integer idMusic) {
        this.idMusic = idMusic;
    }

    public Music(Integer idMusic, String name) {
        this.idMusic = idMusic;
        this.name = name;
    }

    public Integer getIdMusic() {
        return idMusic;
    }

    public void setIdMusic(Integer idMusic) {
        this.idMusic = idMusic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMusic != null ? idMusic.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Music)) {
            return false;
        }
        Music other = (Music) object;
        if ((this.idMusic == null && other.idMusic != null) || (this.idMusic != null && !this.idMusic.equals(other.idMusic))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Music[ idMusic=" + idMusic + " ]";
    }
    
}
