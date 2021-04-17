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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;

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
    
    @Transactional
    public static Addresses CreateAddress(EntityManager em, String Country, String City, String Zip, String Street)
    {
        Addresses ret;
        Main.joinTransaction(em);
        List<Addresses> addresses = em.createQuery("select a from Addresses a where a.country = :country and a.city = :city and a.zip = :zip and a.street = :street", Addresses.class)
                .setParameter("country", Country).setParameter("city", City).setParameter("zip", Zip).setParameter("street", Street).getResultList();
        if(addresses.isEmpty())
        {
            Main.joinTransaction(em);
            em.createNativeQuery("Insert into addresses (Country,City,Zip,Street) values (?,?,?,?)")
                    .setParameter(1, Country).setParameter(2, City).setParameter(3, Zip).setParameter(4, Street).executeUpdate();
            List<Addresses> resultList = em.createNamedQuery("Addresses.findAll",Addresses.class).getResultList();
            ret = resultList.get(resultList.size()-1);
        }
        else
            ret = addresses.get(0);
        return ret;
    }
    
    public static int TimeNeeded(Addresses addressCurr,Addresses addressDestination) throws IOException
    {
        int travelTime;
        if (addressCurr.getIdAddress() == addressDestination.getIdAddress())
        {
            travelTime = 0;
        } 
        else 
        {
            String from, to;
            from = addressCurr.getStreet() + "," + addressCurr.getZip() + " " + addressCurr.getCity() + "," + addressCurr.getCountry();
            to = addressDestination.getStreet() + "," + addressDestination.getZip() + " " + addressDestination.getCity() + "," + addressDestination.getCountry();
            Socket socket = new Socket("localhost", 5001);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
            pout.println(from);
            pout.println(to);
            String rec = in.readLine();
            socket.close();
            travelTime = Integer.parseInt(rec);
        }      
        return travelTime;
    }
    @Transactional
    public static void UpdateObligationAfter(EntityManager em, Date beginning, Users user, String duration, JMSContext context, JMSProducer producer) throws IOException, JMSException
    {
        Main.joinTransaction(em);
        List<Obligation> obligationsBefore = em.createNativeQuery("Select * from obligation where beginning <= ? and UserID = ? order by ADDTIME(beginning,duration) desc", Obligation.class)
                .setParameter(1, beginning).setParameter(2, user.getIdUsers()).getResultList();
        Main.joinTransaction(em);
        List<Obligation> obligationsAfter = em.createNativeQuery("Select * from obligation where beginning > addtime(?,?) and UserID = ? order by beginning asc", Obligation.class)
                .setParameter(1, beginning).setParameter(2, duration).setParameter(3, user.getIdUsers()).getResultList();
        if(!obligationsAfter.isEmpty())
        {
            Obligation updateObligation = obligationsAfter.get(0);
            Alarm updateAlarm = updateObligation.getAlarmID();
            Addresses addressDestination,addressCurr;
            addressDestination = updateObligation.getDestination();
            if(obligationsBefore.isEmpty())
            {
                addressCurr = user.getHouseAddress();
            }
            else
            {
                addressCurr = obligationsBefore.get(0).getDestination();
            }
            int travelTime = TimeNeeded(addressCurr,addressDestination);
            Date startTime = updateObligation.getBeginning();
            Date travelTimeStart = new Date(startTime.getTime()-travelTime*1000);
            Main.joinTransaction(em);
            em.createNativeQuery("update obligation set TravelTimeStart = ? where idObligation = ?").setParameter(1, travelTimeStart).setParameter(2, updateObligation.getIdObligation()).executeUpdate();
            if(updateAlarm != null)
            {
                ObjectMessage alarmMsg = context.createObjectMessage(travelTimeStart);
                alarmMsg.setBooleanProperty("UpdateAlarm", true);
                alarmMsg.setStringProperty("SongName", updateAlarm.getMusicID().getName());
                alarmMsg.setIntProperty("Repeat", updateAlarm.getRepeatEveryDay());
                alarmMsg.setIntProperty("AlarmID", updateAlarm.getIdAlarm());
                alarmMsg.setIntProperty("UserID", user.getIdUsers());
                alarmMsg.setBooleanProperty("DeleteAlarm", false);
                producer.send(Main.topic, alarmMsg);
            }
        }
    }
    
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
        JMSContext contextThread3 = factory.createContext();
        JMSContext contextThread4 = factory.createContext();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AntPlanerPU");
        Thread createObligation = new Thread(new CreateObligation(contextThread1, emf));
        Thread deleteObligation = new Thread(new DeleteObligation(contextThread2, emf));
        Thread updateObligation = new Thread(new UpdateObligation(contextThread3, emf));
        Thread calculator = new Thread(new Calculator(contextThread4, emf));
        createObligation.start();
        deleteObligation.start();
        updateObligation.start();
        calculator.start();
        try
        {
            createObligation.join();
            deleteObligation.join();
            updateObligation.join();
            calculator.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        emf.close();
    }
    
}
