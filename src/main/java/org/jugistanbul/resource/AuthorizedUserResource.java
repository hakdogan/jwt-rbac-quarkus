package org.jugistanbul.resource;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jugistanbul.dto.UserDTO;
import org.jugistanbul.entity.User;
import org.jugistanbul.filter.Secured;
import org.jugistanbul.filter.SecuredForAdmin;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 16.07.2021
 **/
@Path("/api/secured")
@RequestScoped
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizedUserResource
{
    @GET
    @Path("/allUsers")
    public List<User> fetchAllUsers(){
        return User.listAll();
    }

    @GET
    @Path("/findUsersByRole/{role}")
    public List<User> findUsersByRole(@PathParam String role){
        return User.findByRole(role);
    }

    @POST
    @Path("/addUser")
    @SecuredForAdmin
    @Transactional
    public User addUser(final UserDTO dto){
        return User.add(dto);
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                    .add("exceptionType", exception.getClass().getName())
                    .add("code", code);

            if (exception.getMessage() != null) {
                entityBuilder.add("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(entityBuilder.build())
                    .build();
        }
    }
}
