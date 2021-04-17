/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiservis.resources;

import Entities.Alarm;
import Entities.Obligation;
import Entities.Users;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Predicate;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author neman
 */
@Path("Alarm")
public class AlarmAPI {
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    @Resource(lookup = "ProjekatFactory")
    ConnectionFactory factory;
    @Resource(lookup = "ProjekatTopic")
    Topic topic;
    
    @POST
    @Path("createAlarm")
    public Response createAlarm(@FormParam("songName") String songName,@FormParam("repeat") String repeat,@FormParam("date") String date)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH);
            Date setDate = formatter.parse(date);
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "CreatedAlarmId");
            ObjectMessage msg = context.createObjectMessage(setDate);
            msg.setBooleanProperty("CreateAlarm", true);
            msg.setIntProperty("UserID", UsersAPI.userID);
            msg.setIntProperty("Repeat", Integer.parseInt(repeat));
            msg.setStringProperty("SongName", songName);
            producer.send(topic, msg);
            msg = (ObjectMessage) consumer.receive(10000);
            return Response.ok(msg.getIntProperty("AlarmId")).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @POST
    @Path("updateAlarm")
    public Response updateAlarm(@FormParam("songName") String songName,@FormParam("repeat") String repeat,@FormParam("date") String date,@FormParam("alarmID") String id)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.ENGLISH);
            Date setDate = formatter.parse(date);
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            ObjectMessage msg = context.createObjectMessage(setDate);
            msg.setIntProperty("UserID",UsersAPI.userID);
            msg.setBooleanProperty("UpdateAlarm", true);
            msg.setBooleanProperty("DeleteAlarm", false);
            msg.setIntProperty("Repeat", Integer.parseInt(repeat));
            msg.setStringProperty("SongName", songName);
            msg.setIntProperty("AlarmID", Integer.parseInt(id));
            producer.send(topic, msg);
            return Response.ok("Alarm updated").build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @DELETE
    @Path("{alarmID}")
    public Response deleteAlarm(@PathParam("alarmID") String alarmid)
    {
        try
        {
            int alarmID = Integer.parseInt(alarmid);
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            ObjectMessage msg = context.createObjectMessage();
            msg.setIntProperty("UserID",UsersAPI.userID);
            msg.setBooleanProperty("UpdateAlarm", true);
            msg.setBooleanProperty("DeleteAlarm", true);
            msg.setIntProperty("AlarmID", alarmID);
            producer.send(topic, msg);
            return Response.ok("Alarm deleted").build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @GET
    @Transactional
    public Response getAll()
    {
        try
        {
            em.getEntityManagerFactory().getCache().evictAll();
            Users user = em.createNamedQuery("Users.findByIdUsers",Users.class).setParameter("idUsers", UsersAPI.userID).getResultList().get(0);
            StringBuilder builder = new StringBuilder();
            for(Alarm i : user.getAlarmList())
            {
                if(user.getObligationList().stream().anyMatch((Obligation t) -> t.getAlarmID() == i))
                    continue;
                String date = (i.getStartTime().getYear()+1900)+"-"+(i.getStartTime().getMonth()+1)+"-"+i.getStartTime().getDate()+" "+i.getStartTime().getHours()+":"+i.getStartTime().getMinutes()+":"+i.getStartTime().getSeconds();
                builder.append(i.getIdAlarm()+"%"+date+"%"+i.getRepeatEveryDay()+"%"+i.getMusicID().getName());
                builder.append(System.lineSeparator());
            }
            return Response.ok(builder.toString()).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
}
