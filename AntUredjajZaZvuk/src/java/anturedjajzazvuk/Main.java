/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anturedjajzazvuk;

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
    
    public static void main(String[] args) {
        JMSContext contextThread1 = factory.createContext();
        JMSContext contextThread2 = factory.createContext();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AntUredjajZaZvukPU");
        Thread player = new Thread(new PlaysMusic(contextThread1,emf));
        Thread alreadyPlayed = new Thread(new GetAlreadyPlayed(contextThread2, emf));
        player.start();
        alreadyPlayed.start();
        try {
            player.join();
            alreadyPlayed.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf.close();     
    }
}
