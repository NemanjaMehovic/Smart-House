/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anturedjajzazvuk;

import Entities.Alreadyplayed;
import Entities.Music;
import Entities.Users;
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
public class GetAlreadyPlayed implements Runnable {

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSProducer producer;

    public GetAlreadyPlayed(JMSContext context, EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "GetAlreadyPlayed");
        this.emf = emf;
        producer = this.context.createProducer();
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                ObjectMessage msg = (ObjectMessage) consumer.receive();
                synchronized(Main.sync)
                {
                    emf.getCache().evictAll();
                    EntityManager em = emf.createEntityManager();
                    int userID = msg.getIntProperty("UserID");
                    List<Users> listU = null;
                    listU = em.createNamedQuery("Users.findByIdUsers", Users.class).setParameter("idUsers", userID).getResultList();
                    Users user = listU.get(0);
                    StringBuilder strBuilder = new StringBuilder();
                    for (Alreadyplayed i : user.getAlreadyplayedList())
                    {
                        strBuilder.append(i.getMusicID().getName());
                        strBuilder.append(System.lineSeparator());
                    }
                    ObjectMessage ans = context.createObjectMessage();
                    ans.setBooleanProperty("AnswerAlreadyPlayed", true);
                    ans.setStringProperty("Played", strBuilder.toString());
                    producer.send(Main.topic, ans);
                    em.close();
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception kod vracanja");
                e.printStackTrace();
            }
        }
    }
}
