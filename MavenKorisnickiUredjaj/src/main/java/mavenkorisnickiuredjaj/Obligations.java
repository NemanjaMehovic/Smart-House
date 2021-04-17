/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiuredjaj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author neman
 */
public class Obligations {
    
    public static List<Obligations> obligations = new ArrayList<Obligations>();
    
    public static void clearObligationsList()
    {
        obligations = new ArrayList<Obligations>();
    }
    
    private int id;
    private String name;
    private String date;
    private int durationH;
    private int durationM;
    private int durationS;
    private String country;
    private String city;
    private String zip;
    private String street;
    private String songName;
    
    public Obligations(int id, String name, String date, int durationH, int durationM, int durationS, String country, String city, String zip, String street, String songName) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.durationH = durationH;
        this.durationM = durationM;
        this.durationS = durationS;
        this.country = country;
        this.city = city;
        this.zip = zip;
        this.street = street;
        this.songName = songName;
        obligations.add(this);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getDurationH() {
        return durationH;
    }

    public int getDurationM() {
        return durationM;
    }

    public int getDurationS() {
        return durationS;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public String getStreet() {
        return street;
    }

    public String getSongName() {
        return songName;
    }

    @Override
    public String toString() {
        return  name + ", " + date + ", " + durationH + ":" + durationM + ":" + durationS;
    }
    
    
}
