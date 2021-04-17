/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiuredjaj;

import java.util.ArrayList;
import java.util.List;
import static mavenkorisnickiuredjaj.Obligations.obligations;

/**
 *
 * @author neman
 */
public class Alarms {
    
    public static List<Alarms> alarms = new ArrayList<Alarms>();
    
    public static void clearAlarmsList()
    {
        alarms = new ArrayList<Alarms>();
    }
    
    private int id;
    private String date;
    private int repeat;
    private String songName;

    public Alarms(int id, String date, int repeat, String songName) {
        this.id = id;
        this.date = date;
        this.repeat = repeat;
        this.songName = songName;
        alarms.add(this);
    }

    public static List<Alarms> getAlarms() {
        return alarms;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getRepeat() {
        return repeat;
    }

    public String getSongName() {
        return songName;
    }

    @Override
    public String toString() {
        return date + " " + repeat + " " + songName;
    }
    
    
}
