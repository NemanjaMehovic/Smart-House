/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antalarm;

import Entities.Alarm;
import Entities.Music;
import Entities.Obligation;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author neman
 */
public class CheckAlarm implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSProducer producer;
    
    public CheckAlarm(JMSContext context, EntityManagerFactory emf)
    {
        this.context = context;
        this.emf = emf;
        producer = this.context.createProducer();
    }
    
    @Override
    public void run() {
        while(true)
        {
            try
            {
                synchronized (Main.sync)
                {
                    emf.getCache().evictAll();
                    EntityManager em = emf.createEntityManager();
                    List<Alarm> listA = em.createNamedQuery("Alarm.findAll", Alarm.class).getResultList();
                    List<Alarm> tmpList = listA.stream().filter(new Predicate<Alarm>() {
                        @Override
                        public boolean test(Alarm t) {
                            Date curr = new Date();
                            Date working = t.getStartTime();
                            if (curr.getYear() == working.getYear() && curr.getMonth() == working.getMonth() && curr.getDay() == working.getDay() && curr.getHours() == working.getHours() && curr.getMinutes() == working.getMinutes()) {
                                return true;
                            } else if (t.getRepeatEveryDay() == 1 && curr.getHours() == working.getHours() && curr.getMinutes() == working.getMinutes()) {
                                return true;
                            }
                            return false;
                        }
                    }).collect(Collectors.toList());
                    ObjectMessage msg = context.createObjectMessage();
                    msg.setBooleanProperty("UredjajZaZvuk", true);
                    int k,i;
                    for (i = 0,k = 0; i < tmpList.size(); i++,k++)
                    {
                        msg.setStringProperty("SongName"+k, tmpList.get(i).getMusicID().getName());
                        msg.setIntProperty("UserID"+k, tmpList.get(i).getUserID().getIdUsers());
                    }
                    msg.setIntProperty("NumberOfSongs",k);
                    producer.send(Main.topic,msg);
                    em.clear();
                    em.close();
                }
                Date curr = new Date();
                Thread.sleep((60-curr.getSeconds())*1000);
            }
            catch(Exception e)
            {
                System.out.println("Greska kod check");
                e.printStackTrace();
            }
        }
    }
    
}
