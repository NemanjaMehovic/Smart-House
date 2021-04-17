/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mavenkorisnickiservis.resources;

import Entities.Users;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author neman
 */
@Provider
public class Filter implements ContainerRequestFilter{

    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> authHeaderValues = requestContext.getHeaders().get("Authorization");
        
        String method = requestContext.getMethod();
        UriInfo uriInfo = requestContext.getUriInfo();
        List<PathSegment> pathSegments = uriInfo.getPathSegments();
        String endpointName = pathSegments.get(0).getPath();
        if(endpointName.equals("User") && "POST".equals(method) && pathSegments.size() < 2)
            return;
        
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
            
            List<Users> users = em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", username).getResultList();
            
            if(users.size() != 1){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Korisnicko ime ili sifra nije ispravno.").build();
                requestContext.abortWith(response);
                return;
            }
            
            Users user = users.get(0);
            
            if(!user.getPassword().equals(password)){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Korisnicko ime ili sifra nije ispravno.").build();
                requestContext.abortWith(response);
                return;
            }
            UsersAPI.userID = user.getIdUsers();
            return;
        }
         
        Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Posaljite kredencijale.").build();
        requestContext.abortWith(response);
        return;
    }
    
}
