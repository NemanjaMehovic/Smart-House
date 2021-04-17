/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antplaner;

import Entities.Addresses;
import Entities.Obligation;
import Entities.Users;
import static antplaner.Main.TimeNeeded;
import static antplaner.Main.UpdateObligationAfter;
import static antplaner.Main.CreateAddress;
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
public class CreateObligation implements Runnable{

    
    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSConsumer alarmCOnsumer;
    private JMSProducer producer;   
    
    public CreateObligation(JMSContext context, EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "CreateObligation");
        
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
                    String name = msg.getStringProperty("ObligationName");
                    int userID = msg.getIntProperty("UserID");
                    Date startTime = (Date) msg.getObject();
                    int durationInSeconds = msg.getIntProperty("Duration");
                    String City,Zip,Country,Street;
                    Addresses addressDestination,addressCurr;
                    Users user = em.createNamedQuery("Users.findByIdUsers", Users.class).setParameter("idUsers", userID).getResultList().get(0);
                    if(msg.getBooleanProperty("HasAddress"))
                    {
                        Country = msg.getStringProperty("Country");
                        City = msg.getStringProperty("City");
                        Zip = msg.getStringProperty("Zip");
                        Street = msg.getStringProperty("Street");
                        addressDestination = CreateAddress(em, Country, City, Zip, Street);
                    }
                    else
                    {
                        addressDestination = user.getHouseAddress();
                    }
                    Main.joinTransaction(em);
                    List<Obligation> obligationsBefore = em.createNativeQuery("Select * from obligation where beginning < ? and UserID = ? order by ADDTIME(beginning,duration) desc", Obligation.class)
                            .setParameter(1, startTime).setParameter(2, userID).getResultList();
                    if(obligationsBefore.isEmpty())
                    {
                        addressCurr = user.getHouseAddress();
                    }
                    else
                    {
                        addressCurr = obligationsBefore.get(0).getDestination();
                    }
                    int travelTime = TimeNeeded(addressCurr,addressDestination);
                    Date travelTimeStart = new Date(startTime.getTime()-travelTime*1000);
                    int sec = durationInSeconds % 60;
                    durationInSeconds = (durationInSeconds - sec)/60;
                    int min = durationInSeconds % 60;
                    int h = (durationInSeconds - min)/60;
                    String duration = h+":"+min+":"+sec;
                    Main.joinTransaction(em);
                    List<Obligation> checkList = em.createNativeQuery("select * from projekat.obligation where (( ?TrebaDaKrene <= Beginning and Beginning <= addtime(?TrebaDaPocne,?Trajanje)) or ( ?TrebaDaKrene <= addtime(Beginning,Duration) and addtime(Beginning,Duration) <= addtime(?TrebaDaPocne,?Trajanje)) or ( ?TrebaDaKrene <= TravelTimeStart and TravelTimeStart <= addtime(?TrebaDaPocne,?Trajanje))) and UserID = ?UserID", Obligation.class)
                            .setParameter("TrebaDaKrene", travelTimeStart).setParameter("TrebaDaPocne", startTime).setParameter("Trajanje", duration).setParameter("UserID", userID).getResultList();
                    if(!checkList.isEmpty())
                    {
                        msg = context.createObjectMessage();
                        msg.setBooleanProperty("CreateObligationAnswer", true);
                        msg.setBooleanProperty("ObligationCreatedSuccessfully", false);
                        producer.send(Main.topic, msg);
                        em.flush();
                        em.clear();
                        em.close();
                        continue;
                    }
                    List<Obligation> obligationsAfter = em.createNativeQuery("Select * from obligation where beginning > addtime(?,?) and UserID = ? order by beginning asc", Obligation.class)
                            .setParameter(1, startTime).setParameter(2, duration).setParameter(3, user.getIdUsers()).getResultList();
                    if(!obligationsAfter.isEmpty())
                    {
                        Obligation nextObligation = obligationsAfter.get(0);
                        Addresses tmpAddressesDestination,tmpAddressesCurr;
                        tmpAddressesDestination = nextObligation.getDestination();
                        tmpAddressesCurr = addressDestination;
                        int tmpTravelTime = TimeNeeded(tmpAddressesCurr, tmpAddressesDestination);
                        Date tmpTravelTimeStart = new Date(nextObligation.getBeginning().getTime()-tmpTravelTime*1000);
                        if(travelTimeStart.getTime() <= tmpTravelTimeStart.getTime() && tmpTravelTimeStart.getTime() <= (msg.getIntProperty("Duration")*1000 + startTime.getTime()))
                        {
                            msg = context.createObjectMessage();
                            msg.setBooleanProperty("CreateObligationAnswer", true);
                            msg.setBooleanProperty("ObligationCreatedSuccessfully", false);
                            producer.send(Main.topic, msg);
                            em.flush();
                            em.clear();
                            em.close();
                            continue;
                        }
                    }
                    emf.getCache().evictAll();
                    if(msg.getBooleanProperty("HasAlarm"))
                    {
                        ObjectMessage alarmMsg = context.createObjectMessage(travelTimeStart);
                        alarmMsg.setStringProperty("SongName", msg.getStringProperty("SongName"));
                        alarmMsg.setIntProperty("UserID", userID);
                        alarmMsg.setIntProperty("Repeat", msg.getIntProperty("Repeat"));
                        alarmMsg.setBooleanProperty("CreateAlarm", true);
                        alarmCOnsumer = this.context.createConsumer(Main.topic, "CreatedAlarmId");
                        producer.send(Main.topic,alarmMsg);
                        alarmMsg = (ObjectMessage) alarmCOnsumer.receive();
                        alarmCOnsumer.close();
                        int alarmID = alarmMsg.getIntProperty("AlarmId");
                        System.out.println(alarmID);
                        Main.joinTransaction(em);
                        em.createNativeQuery("insert into obligation (Name,Beginning,TravelTimeStart,Duration,Destination,UserID,AlarmID) Values(?,?,?,?,?,?,?)")
                                .setParameter(1, name).setParameter(2, startTime).setParameter(3, travelTimeStart).setParameter(4, duration).setParameter(5, addressDestination.getIdAddress())
                                .setParameter(6, userID).setParameter(7, alarmID).executeUpdate();
                    }
                    else
                    {
                        Main.joinTransaction(em);
                        em.createNativeQuery("insert into obligation (Name,Beginning,TravelTimeStart,Duration,Destination,UserID) Values(?,?,?,?,?,?)")
                                .setParameter(1, name).setParameter(2, startTime).setParameter(3, travelTimeStart).setParameter(4, duration).setParameter(5, addressDestination.getIdAddress()).setParameter(6, userID).executeUpdate();
                    }
                    UpdateObligationAfter(em, startTime, user, duration, context, producer);
                    msg = context.createObjectMessage();
                    msg.setBooleanProperty("CreateObligationAnswer", true);
                    msg.setBooleanProperty("ObligationCreatedSuccessfully", true);
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
