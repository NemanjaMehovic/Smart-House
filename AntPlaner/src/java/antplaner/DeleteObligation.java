/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antplaner;

import Entities.Addresses;
import Entities.Alarm;
import Entities.Obligation;
import Entities.Users;
import static antplaner.Main.TimeNeeded;
import static antplaner.Main.UpdateObligationAfter;
import java.util.Date;
import java.util.List;
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
public class DeleteObligation implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSProducer producer;
    
    public DeleteObligation(JMSContext context , EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "DeleteObligation");
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
                    System.out.println("Trying to delete");
                    int obligationID = msg.getIntProperty("ObligationID");
                    int userIDReceived = msg.getIntProperty("UserID");
                    Obligation target = em.createNamedQuery("Obligation.findByIdObligation",Obligation.class).setParameter("idObligation", obligationID).getResultList().get(0);
                    Users user = target.getUserID();
                    if(user.getIdUsers() != userIDReceived)
                    {
                        em.flush();
                        em.clear();
                        em.close();
                        continue;
                    }
                    String duration = target.getDuration().getHours()+":"+target.getDuration().getMinutes()+":"+target.getDuration().getSeconds();
                    Date beginning = target.getBeginning();
                    if(target.getAlarmID() != null)
                    {
                        ObjectMessage alarmMsg = context.createObjectMessage();
                        alarmMsg.setBooleanProperty("UpdateAlarm", true);
                        alarmMsg.setBooleanProperty("DeleteAlarm",true);
                        alarmMsg.setIntProperty("UserID", userIDReceived);
                        alarmMsg.setIntProperty("AlarmID", target.getAlarmID().getIdAlarm());
                        producer.send(Main.topic, alarmMsg);
                    }
                    Main.joinTransaction(em);
                    em.createNativeQuery("delete from obligation where idobligation = ?").setParameter(1, obligationID).executeUpdate();
                    UpdateObligationAfter(em, beginning, user, duration, context, producer);
                    em.flush();
                    em.clear();
                    em.close();
                    msg = context.createObjectMessage();
                    msg.setBooleanProperty("ObligationDeleted", true);
                    producer.send(Main.topic, msg);
                }
            }
            catch(Exception e)
            {
                System.out.println("Greska kod brisanja");
                e.printStackTrace();
            }
        }
    }
    
    
}
