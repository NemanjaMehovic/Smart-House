/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiservis.resources;

import Entities.Addresses;
import Entities.Users;
import java.util.List;
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
import javax.ws.rs.core.Response;

/**
 *
 * @author neman
 */
@Path("User")
public class UsersAPI {
    
    public static int userID;
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    @Resource(lookup = "ProjekatFactory")
    ConnectionFactory factory;
    @Resource(lookup = "ProjekatTopic")
    Topic topic;
    
    @GET
    @Path("playedMusic")
    public Response getPlayed()
    {
        try
        {
            em.getEntityManagerFactory().getCache().evictAll();
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "AnswerAlreadyPlayed");
            ObjectMessage msg = context.createObjectMessage();
            msg.setBooleanProperty("GetAlreadyPlayed", true);
            msg.setIntProperty("UserID", userID);
            producer.send(topic, msg);
            msg = (ObjectMessage) consumer.receive(10000);
            String answer = msg.getStringProperty("Played");
            return Response.ok(answer).build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }
    
    @POST
    @Path("playMusic")
    public Response playMusic(@FormParam("songName") String songName)
    {
        JMSContext context = factory.createContext();
        JMSProducer producer = context.createProducer();
        ObjectMessage msg = context.createObjectMessage();
        try
        {
            msg.setBooleanProperty("UredjajZaZvuk", true);
            msg.setIntProperty("NumberOfSongs",1);
            msg.setStringProperty("SongName0", songName);
            msg.setIntProperty("UserID0", userID);
            producer.send(topic, msg);
            return Response.ok().build();
        }
        catch(Exception e)
        {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }
    
    @GET
    @Path("logIn")
    public Response logIn()
    {
        return Response.ok().build();
    }
    
    @POST
    @Transactional
    public Response createUser(@FormParam("username") String username, @FormParam("password") String password, @FormParam("name") String name, @FormParam("surname") String surname,
            @FormParam("country") String country, @FormParam("city") String city, @FormParam("zip") String zip, @FormParam("street") String street)
    {
        List<Users> check = em.createNamedQuery("Users.findByUsername",Users.class).setParameter("username", username).getResultList();
        if(!check.isEmpty())
            return Response.status(Response.Status.CONFLICT).entity("Username taken").build();
        Addresses ret;
        List<Addresses> addresses = em.createQuery("select a from Addresses a where a.country = :country and a.city = :city and a.zip = :zip and a.street = :street", Addresses.class)
                .setParameter("country", country).setParameter("city", city).setParameter("zip", zip).setParameter("street", street).getResultList();
        if(addresses.isEmpty())
        {
            ret = new Addresses();
            ret.setCity(city);
            ret.setCountry(country);
            ret.setZip(zip);
            ret.setStreet(street);
            em.persist(ret);
        }
        else
            ret = addresses.get(0);
        Users users = new Users();
        users.setName(name);
        users.setSurname(surname);
        users.setUsername(username);
        users.setPassword(password);
        users.setHouseAddress(ret);
        em.persist(users);
        return Response.ok("User created").build();
    }
    
    @DELETE
    @Transactional
    public Response deleteUser()
    {
        em.createQuery("delete from Users u where u.idUsers = :userID").setParameter("userID", userID).executeUpdate();
        return Response.ok("User deleted").build();
    }
}
