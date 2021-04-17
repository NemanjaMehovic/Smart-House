/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antplaner;

import Entities.Addresses;
import Entities.Obligation;
import Entities.Users;
import static antplaner.Main.CreateAddress;
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
public class UpdateObligation implements Runnable{

    private EntityManagerFactory emf;
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSConsumer alarmCOnsumer;
    private JMSProducer producer;   
    
    public UpdateObligation(JMSContext context, EntityManagerFactory emf)
    {
        this.context = context;
        consumer = this.context.createConsumer(Main.topic, "UpdateObligation");
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
                    System.out.println("Trying to update");
                    String name = msg.getStringProperty("ObligationName");
                    int userID = msg.getIntProperty("UserID");
                    int obligationID = msg.getIntProperty("ObligationID");
                    Date startTime = (Date) msg.getObject();
                    int durationInSeconds = msg.getIntProperty("Duration");
                    String City,Zip,Country,Street;
                    Addresses addressDestination,addressCurr;
                    Obligation target = em.find(Obligation.class, obligationID);
                    em.refresh(target);
                    Users user = target.getUserID();
                    String oldDuration = target.getDuration().getHours()+":"+target.getDuration().getMinutes()+":"+target.getDuration().getSeconds();
                    long tmp = target.getBeginning().getTime();
                    Date oldBeginning = new Date(tmp);
                    System.out.println("OLD beginning  "+oldBeginning);
                    System.out.println("OLD duration  "+oldDuration);
                    if(user.getIdUsers() != userID)
                    {
                        msg = context.createObjectMessage();
                        msg.setBooleanProperty("UpdateObligationAnswer", true);
                        msg.setBooleanProperty("ObligationUpdatedSuccessfully", false);
                        producer.send(Main.topic, msg);
                        em.flush();
                        em.clear();
                        em.close();
                        continue;
                    }
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
                    List<Obligation> obligationsBefore = em.createNativeQuery("Select * from obligation where beginning < ? and UserID = ? and idObligation != ? order by ADDTIME(beginning,duration) desc", Obligation.class)
                            .setParameter(1, startTime).setParameter(2, userID).setParameter(3, obligationID).getResultList();
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
                    List<Obligation> checkList = em.createNativeQuery("select * from projekat.obligation where (( ?TrebaDaKrene <= Beginning and Beginning <= addtime(?TrebaDaPocne,?Trajanje)) or ( ?TrebaDaKrene <= addtime(Beginning,Duration) and addtime(Beginning,Duration) <= addtime(?TrebaDaPocne,?Trajanje)) or ( ?TrebaDaKrene <= TravelTimeStart and TravelTimeStart <= addtime(?TrebaDaPocne,?Trajanje))) and UserID = ?UserID and idObligation != ?ObligationID", Obligation.class)
                            .setParameter("TrebaDaKrene", travelTimeStart).setParameter("TrebaDaPocne", startTime).setParameter("Trajanje", duration).setParameter("UserID", userID).setParameter("ObligationID", obligationID).getResultList();
                    if(!checkList.isEmpty())
                    {
                        msg = context.createObjectMessage();
                        msg.setBooleanProperty("UpdateObligationAnswer", true);
                        msg.setBooleanProperty("ObligationUpdatedSuccessfully", false);
                        producer.send(Main.topic, msg);
                        em.flush();
                        em.clear();
                        em.close();
                        continue;
                    }
                    List<Obligation> obligationsAfter = em.createNativeQuery("Select * from obligation where beginning > addtime(?,?) and UserID = ? and idObligation != ? order by beginning asc", Obligation.class)
                            .setParameter(1, startTime).setParameter(2, duration).setParameter(3, user.getIdUsers()).setParameter(4, obligationID).getResultList();
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
                            msg.setBooleanProperty("UpdateObligationAnswer", true);
                            msg.setBooleanProperty("ObligationUpdatedSuccessfully", false);
                            producer.send(Main.topic, msg);
                            em.flush();
                            em.clear();
                            em.close();
                            continue;
                        }
                    }
                    int alarmID = 0;
                    System.out.println("Alarm :" + target.getAlarmID());
                    if(target.getAlarmID() != null && msg.getBooleanProperty("HasAlarm"))
                    {
                        ObjectMessage alarmMsg = context.createObjectMessage(travelTimeStart);
                        alarmMsg.setStringProperty("SongName", msg.getStringProperty("SongName"));
                        alarmMsg.setIntProperty("UserID", userID);
                        alarmMsg.setIntProperty("AlarmID", target.getAlarmID().getIdAlarm());
                        alarmMsg.setIntProperty("Repeat", msg.getIntProperty("Repeat"));
                        alarmMsg.setBooleanProperty("UpdateAlarm", true);
                        alarmMsg.setBooleanProperty("DeleteAlarm", false);
                        alarmID = target.getAlarmID().getIdAlarm();
                        producer.send(Main.topic,alarmMsg);
                    }
                    else if (target.getAlarmID() != null && !msg.getBooleanProperty("HasAlarm"))
                    {
                        ObjectMessage alarmMsg = context.createObjectMessage();
                        alarmMsg.setBooleanProperty("UpdateAlarm", true);
                        alarmMsg.setBooleanProperty("DeleteAlarm",true);
                        alarmMsg.setIntProperty("UserID", userID);
                        alarmMsg.setIntProperty("AlarmID", target.getAlarmID().getIdAlarm());
                        producer.send(Main.topic, alarmMsg);
                    }
                    else if (target.getAlarmID() == null && msg.getBooleanProperty("HasAlarm"))
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
                        alarmID = alarmMsg.getIntProperty("AlarmId");
                    }
                    System.out.println("Alarm id :"+alarmID);
                    Main.joinTransaction(em);
                    emf.getCache().evictAll();
                    if(alarmID != 0)
                    {
                        em.createNativeQuery("Update obligation set Name = ?name , Beginning = ?beginning , TravelTimeStart = ?travelTimeStart , Duration = ?duration , Destination = ?destination , AlarmID = ?alarmID where idObligation = ?idobligation and UserID = ?UserID")
                                .setParameter("name", name).setParameter("beginning", startTime).setParameter("travelTimeStart", travelTimeStart).setParameter("duration", duration).setParameter("destination", addressDestination.getIdAddress())
                                .setParameter("alarmID", alarmID).setParameter("idobligation", obligationID).setParameter("UserID", userID).executeUpdate();
                    }
                    else
                    {
                        em.createNativeQuery("Update obligation set Name = ?name , Beginning = ?beginning , TravelTimeStart = ?travelTimeStart , Duration = ?duration , Destination = ?destination , AlarmID = ?alarmID where idObligation = ?idobligation and UserID = ?UserID")
                                .setParameter("name", name).setParameter("beginning", startTime).setParameter("travelTimeStart", travelTimeStart).setParameter("duration", duration).setParameter("destination", addressDestination.getIdAddress())
                                .setParameter("alarmID", null).setParameter("idobligation", obligationID).setParameter("UserID", userID).executeUpdate();
                    }
                    System.out.println("beginning  "+startTime);
                    System.out.println("duration  "+duration);
                    UpdateObligationAfter(em, oldBeginning, user, oldDuration, context, producer);
                    UpdateObligationAfter(em, startTime, user, duration, context, producer);
                    msg = context.createObjectMessage();
                    msg.setBooleanProperty("UpdateObligationAnswer", true);
                    msg.setBooleanProperty("ObligationUpdatedSuccessfully", true);
                    producer.send(Main.topic, msg);
                    em.flush();
                    em.clear();
                    em.close();
               }
            }
            catch(Exception e)
            {
                System.out.println("Greska kod update");
                e.printStackTrace();
            }
        }
    }
    
}
