/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antalarm;

import Entities.Music;
import java.util.Date;
import java.util.List;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author neman
 */
public class UpdateAlarm implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    
    public UpdateAlarm(JMSContext context , EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "UpdateAlarm");
        this.emf = emf;
    }
    
    @Override
    public void run() {
        while(true)
        {
            try
            {
                ObjectMessage msg = (ObjectMessage) consumer.receive();
                synchronized(Main.sync)
                {
                    emf.getCache().evictAll();
                    EntityManager em = emf.createEntityManager();
                    boolean deleteAlarm = msg.getBooleanProperty("DeleteAlarm");
                    int alarmID = msg.getIntProperty("AlarmID");
                    int userID = msg.getIntProperty("UserID");
                    if(deleteAlarm)
                    {
                        System.out.println("Trying to delete");
                        //ovo je ovde zbog black magic tranzakcije koja ne radi
                        List<Music> listM = em.createNamedQuery("Music.findByName", Music.class).setParameter("name", "Despacito").getResultList();
                        Main.joinTransaction(em);
                        em.createNativeQuery("delete from alarm  where idAlarm = ?idAlarm and UserID = ?userid").setParameter("idAlarm", alarmID).setParameter("userid", userID).executeUpdate();
                    }
                    else
                    {
                        System.out.println("Trying to update");
                        String songName = msg.getStringProperty("SongName");
                        List<Music> listM = Main.createSong(em, songName);
                        int repeat = msg.getIntProperty("Repeat");
                        Date date = (Date) msg.getObject();
                        Main.joinTransaction(em);
                        em.createQuery("update Alarm a set a.repeatEveryDay = :RepeatEveryDay, a.musicID = :MusicID, a.startTime = :StartTime where a.idAlarm = :idAlarm and a.userID.idUsers = :userid")
                                .setParameter("RepeatEveryDay", repeat).setParameter("MusicID", listM.get(0)).setParameter("StartTime", date).setParameter("idAlarm", alarmID).setParameter("userid", userID).executeUpdate();
                    }
                    em.flush();
                    em.clear();
                    em.close();
                }
            }
            catch(Exception e)
            {
                System.out.println("Error kod update");
                e.printStackTrace();
            }
        }
    }
    
    
    
}
