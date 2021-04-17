/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antplaner;

import Entities.Addresses;
import Entities.Obligation;
import Entities.Users;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
public class Calculator implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSProducer producer;   
    
    public Calculator(JMSContext context, EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "Calculator");
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
                    int userID = msg.getIntProperty("UserID");
                    String locB = msg.getStringProperty("LocationB");
                    String locA;
                    boolean useCurrLocation = msg.getBooleanProperty("UseCurrLocation");
                    if(useCurrLocation)
                    {
                        Date startTime = new Date();
                        Addresses adr;
                        Users user = em.find(Users.class, userID);
                        Main.joinTransaction(em);
                        List<Obligation> obligationsBefore = em.createNativeQuery("Select * from obligation where beginning < ? and UserID = ? order by ADDTIME(beginning,duration) desc", Obligation.class)
                            .setParameter(1, startTime).setParameter(2, userID).getResultList();
                        if(obligationsBefore.isEmpty())
                        {
                            adr = user.getHouseAddress();
                        }
                        else
                        {
                            adr = obligationsBefore.get(0).getDestination();
                        }
                        locA = adr.getStreet() + "," + adr.getZip() + " " + adr.getCity() + "," + adr.getCountry();
                    }
                    else
                    {
                        locA = msg.getStringProperty("LocationA");
                    }
                    Socket socket = new Socket("localhost", 5001);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
                    pout.println(locA);
                    pout.println(locB);
                    String rec = in.readLine();
                    socket.close();
                    int travelTime = Integer.parseInt(rec);
                    int s = travelTime%60;
                    travelTime = (travelTime-s)/60;
                    int m = travelTime%60;
                    int h = (travelTime-m)/60;
                    String res = h+":"+m+":"+s;
                    msg =  context.createObjectMessage();
                    msg.setBooleanProperty("CalculatorResults", true);
                    msg.setStringProperty("Duration", res);
                    producer.send(Main.topic, msg);
                }
            }
            catch(Exception e)
            {
                System.out.println("Greska calculator");
                e.printStackTrace();
            }
        }
    }
    
}
