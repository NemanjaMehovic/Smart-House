/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author neman
 */
@Entity
@Table(name = "alreadyplayed")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alreadyplayed.findAll", query = "SELECT a FROM Alreadyplayed a"),
    @NamedQuery(name = "Alreadyplayed.findByIdAlreadyPlayed", query = "SELECT a FROM Alreadyplayed a WHERE a.idAlreadyPlayed = :idAlreadyPlayed")})
public class Alreadyplayed implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAlreadyPlayed")
    private Integer idAlreadyPlayed;
    @JoinColumn(name = "MusicID", referencedColumnName = "idMusic")
    @ManyToOne(optional = false)
    private Music musicID;
    @JoinColumn(name = "UserID", referencedColumnName = "idUsers")
    @ManyToOne(optional = false)
    private Users userID;

    public Alreadyplayed() {
    }

    public Alreadyplayed(Integer idAlreadyPlayed) {
        this.idAlreadyPlayed = idAlreadyPlayed;
    }

    public Integer getIdAlreadyPlayed() {
        return idAlreadyPlayed;
    }

    public void setIdAlreadyPlayed(Integer idAlreadyPlayed) {
        this.idAlreadyPlayed = idAlreadyPlayed;
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
        hash += (idAlreadyPlayed != null ? idAlreadyPlayed.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alreadyplayed)) {
            return false;
        }
        Alreadyplayed other = (Alreadyplayed) object;
        if ((this.idAlreadyPlayed == null && other.idAlreadyPlayed != null) || (this.idAlreadyPlayed != null && !this.idAlreadyPlayed.equals(other.idAlreadyPlayed))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.Alreadyplayed[ idAlreadyPlayed=" + idAlreadyPlayed + " ]";
    }
    
}
