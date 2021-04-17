/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antalarm;

import Entities.Alarm;
import Entities.Music;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

/**
 *
 * @author neman
 */
public class CreateAlarm implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSProducer producer;
    
    public CreateAlarm(JMSContext context, EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "CreateAlarm");
        this.emf = emf;
        producer = this.context.createProducer();
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
                    System.out.println("Trying to create");
                    String songName = msg.getStringProperty("SongName");
                    List<Music> listM = Main.createSong(em, songName);
                    int repeat = msg.getIntProperty("Repeat");
                    int userID = msg.getIntProperty("UserID");
                    Date date = (Date) msg.getObject();
                    Main.joinTransaction(em);
                    em.createNativeQuery("Insert into Alarm (MusicId,RepeatEveryDay,StartTime,UserID) values(?,?,?,?)").setParameter(1, listM.get(0).getIdMusic()).setParameter(2, repeat).setParameter(3, date).setParameter(4, userID).executeUpdate();
                    List<Alarm> resultList = em.createNamedQuery("Alarm.findAll", Alarm.class).getResultList();
                    int alarmID = resultList.get(resultList.size() - 1).getIdAlarm();
                    msg = context.createObjectMessage();
                    msg.setIntProperty("AlarmId", alarmID);
                    msg.setBooleanProperty("CreatedAlarmId", true);
                    producer.send(Main.topic, msg);
                    em.flush();
                    em.clear();
                    em.close();
                }
            }
            catch(Exception e)
            {
                System.out.println("Greska kod create");
                e.printStackTrace();
            }
        }
    }
    
}
