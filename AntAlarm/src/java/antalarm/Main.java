/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antalarm;

import Entities.Music;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author neman
 */
public class Main {
    
    public static final Boolean sync = true;
    @Resource(lookup = "ProjekatFactory")
    public static ConnectionFactory factory;
    @Resource(lookup = "ProjekatTopic")
    public static Topic topic;
    public static CheckAlarm checkAlarm;
    
    public static void joinTransaction(EntityManager em)
    {
        if(!em.isJoinedToTransaction())
        {
            try{
                em.joinTransaction();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static List<Music> createSong(EntityManager em, String songName)
    {
        List<Music> listM = em.createNamedQuery("Music.findByName", Music.class).setParameter("name", songName).getResultList();
        if (listM.isEmpty())
        {
            joinTransaction(em);
            em.createNativeQuery("Insert into Music (name) values(?)").setParameter(1, songName).executeUpdate();
            listM = em.createNamedQuery("Music.findByName", Music.class).setParameter("name", songName).getResultList();
        }
        return listM;
    }
    
    public static void main(String[] args) {
        JMSContext contextThread1 = factory.createContext();
        JMSContext contextThread2 = factory.createContext();
        JMSContext contextThread3 = factory.createContext();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AntAlarmPU");
        Thread createAlarm = new Thread(new CreateAlarm(contextThread1, emf));
        Thread updateAlarm = new Thread(new UpdateAlarm(contextThread3, emf));
        checkAlarm = new CheckAlarm(contextThread2, emf);
        Thread checkAlarmT = new Thread(checkAlarm);
        createAlarm.start();
        checkAlarmT.start();
        updateAlarm.start();
        try {
            createAlarm.join();
            checkAlarmT.join();
            updateAlarm.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();
    }
    
}
