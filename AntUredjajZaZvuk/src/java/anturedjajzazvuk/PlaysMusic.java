/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package anturedjajzazvuk;

import Entities.Alreadyplayed;
import Entities.Music;
import Entities.Users;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
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
public class PlaysMusic implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    
    public PlaysMusic(JMSContext context , EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "UredjajZaZvuk");
        this.emf = emf;
    }
    
    @Override
    public void run() {
        while(true)
        {
            try
            {    
                ObjectMessage msg = (ObjectMessage)consumer.receive();
                synchronized(Main.sync)
                {
                    emf.getCache().evictAll();
                    EntityManager em = emf.createEntityManager();
                    int numOfSongs = msg.getIntProperty("NumberOfSongs");
                    for (int i = 0; i < numOfSongs; i++) 
                    {
                        String songName = msg.getStringProperty("SongName" + i);
                        int userID = msg.getIntProperty("UserID" + i);
                        Socket socket = new Socket("localhost", 5000);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
                        pout.println(songName);
                        String rec = in.readLine();
                        socket.close();
                        rec = "https://www.youtube.com/watch?v=" + rec;
                        Desktop.getDesktop().browse(new URL(rec).toURI());
                        List<Music> listM = em.createNamedQuery("Music.findByName", Music.class).setParameter("name", songName).getResultList();
                        if (listM.isEmpty()) {
                            Main.joinTransaction(em);
                            em.createNativeQuery("Insert into Music (name) values(?)").setParameter(1, songName).executeUpdate();
                            listM = em.createNamedQuery("Music.findByName", Music.class).setParameter("name", songName).getResultList();
                        }
                        Music musicParam = listM.get(0);
                        System.out.println(userID);
                        List<Users> listU = em.createNamedQuery("Users.findByIdUsers", Users.class).setParameter("idUsers", userID).getResultList();
                        Users usersParam = listU.get(0);
                        List<Alreadyplayed> listA = em.createQuery("SELECT a FROM Alreadyplayed a WHERE a.musicID = :musicID AND a.userID = :userID", Alreadyplayed.class).setParameter("userID", usersParam).setParameter("musicID", musicParam).getResultList();
                        if (listA.isEmpty()) {
                            Main.joinTransaction(em);
                            em.createNativeQuery("Insert into alreadyplayed (musicID,userID) values(?,?)").setParameter(1, musicParam.getIdMusic()).setParameter(2, usersParam.getIdUsers()).executeUpdate();
                        }
                    }
                    em.close();
                }
            }
            catch(Exception e)
            {
                System.out.println("Exception kod pustanja");
                e.printStackTrace();
            }
        }
    }
    
}
