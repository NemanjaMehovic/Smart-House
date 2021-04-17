/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiservis.resources;

import Entities.Obligation;
import Entities.Users;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
@Path("Obligation")
public class ObligationAPI {
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    @Resource(lookup = "ProjekatFactory")
    ConnectionFactory factory;
    @Resource(lookup = "ProjekatTopic")
    Topic topic;
    
    @POST
    @Path("createObligation")
    public Response createObligation(@FormParam("date") String date,@FormParam("obligationName") String name,@FormParam("duration") String durationS,@FormParam("hasAddress") String hasAddressS,@FormParam("hasAlarm") String hasAlarmS,
            @FormParam("country") String country, @FormParam("city") String city, @FormParam("zip") String zip, @FormParam("street") String street,@FormParam("songName") String songName)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            Date setDate = formatter.parse(date);
            int duration = Integer.parseInt(durationS);
            int repeat = 0;
            boolean hasAddress = Boolean.parseBoolean(hasAddressS);
            boolean hasAlarm = Boolean.parseBoolean(hasAlarmS);
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic,"CreateObligationAnswer");
            ObjectMessage msg = context.createObjectMessage(setDate);
            msg.setBooleanProperty("CreateObligation", true);
            msg.setIntProperty("UserID", UsersAPI.userID);
            msg.setStringProperty("ObligationName", name);
            msg.setIntProperty("Duration", duration);
            msg.setBooleanProperty("HasAddress", hasAddress);
            msg.setStringProperty("Country", country);
            msg.setStringProperty("City", city);
            msg.setStringProperty("Zip", zip);
            msg.setStringProperty("Street", street);
            msg.setIntProperty("Repeat", repeat);
            msg.setStringProperty("SongName", songName);
            msg.setBooleanProperty("HasAlarm", hasAlarm);
            producer.send(topic, msg);
            msg = (ObjectMessage) consumer.receive();
            if(msg.getBooleanProperty("ObligationCreatedSuccessfully"))
                return Response.ok("Obligation created").build();
            else
                return Response.status(Response.Status.FORBIDDEN).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @POST
    @Path("updateObligation")
    public Response updateObligation(@FormParam("date") String date,@FormParam("obligationName") String name,@FormParam("duration") String durationS,@FormParam("hasAddress") String hasAddressS,@FormParam("hasAlarm") String hasAlarmS,
            @FormParam("country") String country, @FormParam("city") String city, @FormParam("zip") String zip, @FormParam("street") String street,@FormParam("songName") String songName,@FormParam("obligationID") String obligationIDS)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            Date setDate = formatter.parse(date);
            int duration = Integer.parseInt(durationS);
            int repeat = 0;
            int obligationID = Integer.parseInt(obligationIDS);
            boolean hasAddress = Boolean.parseBoolean(hasAddressS);
            boolean hasAlarm = Boolean.parseBoolean(hasAlarmS);
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic,"UpdateObligationAnswer");
            ObjectMessage msg = context.createObjectMessage(setDate);
            msg.setBooleanProperty("UpdateObligation", true);
            msg.setIntProperty("UserID", UsersAPI.userID);
            msg.setIntProperty("ObligationID", obligationID);
            msg.setStringProperty("ObligationName", name);
            msg.setIntProperty("Duration", duration);
            msg.setBooleanProperty("HasAddress", hasAddress);
            msg.setStringProperty("Country", country);
            msg.setStringProperty("City", city);
            msg.setStringProperty("Zip", zip);
            msg.setStringProperty("Street", street);
            msg.setIntProperty("Repeat", repeat);
            msg.setStringProperty("SongName", songName);
            msg.setBooleanProperty("HasAlarm", hasAlarm);
            producer.send(topic, msg);
            msg = (ObjectMessage) consumer.receive();
            if(msg.getBooleanProperty("ObligationUpdatedSuccessfully"))
                return Response.ok("Obligation updated").build();
            else
                return Response.status(Response.Status.FORBIDDEN).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @POST
    @Path("calculator")
    public Response calculator(@FormParam("locationA")String locA, @FormParam("locationB")String locB, @FormParam("UseCurrLocation")String useCurrLocation)
    {
        JMSContext context = factory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(topic, "CalculatorResults");
        ObjectMessage msg = context.createObjectMessage();
        try
        {
            msg.setBooleanProperty("Calculator", true);
            msg.setStringProperty("LocationA", locA);
            msg.setStringProperty("LocationB", locB);
            msg.setIntProperty("UserID", UsersAPI.userID);
            msg.setBooleanProperty("UseCurrLocation", Boolean.parseBoolean(useCurrLocation));
            producer.send(topic, msg);
            msg = (ObjectMessage) consumer.receive();
            String res = msg.getStringProperty("Duration");
            return Response.ok(res).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @DELETE
    @Path("{obligationid}")
    public Response deleteObligation(@PathParam("obligationid") String id)
    {
        try
        {
            int obligationID = Integer.parseInt(id);
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic,"ObligationDeleted");
            ObjectMessage msg = context.createObjectMessage();
            msg.setBooleanProperty("DeleteObligation", true);
            msg.setIntProperty("ObligationID", obligationID);
            msg.setIntProperty("UserID", UsersAPI.userID);
            producer.send(topic, msg);
            consumer.receive();
            return Response.ok("Obligation deleted").build();
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
            for(Obligation i : user.getObligationList())
            {
                String durString = i.getDuration().getHours()+":"+i.getDuration().getMinutes()+":"+i.getDuration().getSeconds();
                String destination = i.getDestination().getCountry()+"%"+i.getDestination().getZip()+"%"+i.getDestination().getCity()+"%"+i.getDestination().getStreet();
                String date = (i.getBeginning().getYear()+1900)+"-"+(i.getBeginning().getMonth()+1)+"-"+i.getBeginning().getDate()+" "+i.getBeginning().getHours()+":"+i.getBeginning().getMinutes()+":"+i.getBeginning().getSeconds();
                String add = "";
                if(i.getAlarmID() != null)
                    add = i.getAlarmID().getMusicID().getName();
                builder.append(i.getIdObligation()+"%"+i.getName()+"%"+date+"%"+destination+"%"+durString+"%"+add);
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
