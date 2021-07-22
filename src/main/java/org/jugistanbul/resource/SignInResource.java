package org.jugistanbul.resource;

import io.smallrye.jwt.build.Jwt;
import io.vertx.core.http.HttpServerRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jugistanbul.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 17.07.2021
 **/
@Path("/api")
@RequestScoped
public class SignInResource {
    private static final Logger LOG = LoggerFactory.getLogger(SignInResource.class);

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://jugistanbul.org/issuer")
    String issuer;

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public Response info(@Context SecurityContext context) {
        var result = context.getUserPrincipal() == null ? "Anonymouse" : context.getUserPrincipal().getName();
        return Response.ok(result, MediaType.TEXT_PLAIN_TYPE).build();
    }

    @POST
    @Path("/signIn/{username}/{password}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response signIn(@PathParam String username, @PathParam String password,
                         @Context HttpServerRequest httpServerRequest) {

        var user = User.findByUsername(username);
        return user != null && verifyBCryptPassword(user.password, password)
                ? createSuccessLoginResponse(user, httpServerRequest) : createFailedLoginResponse();

    }

    public static boolean verifyBCryptPassword(String bCryptPasswordHash, String passwordToVerify) {

        try {
            var provider = new WildFlyElytronPasswordProvider();
            var passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
            var userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);
            var userPasswordRestored = passwordFactory.translate(userPasswordDecoded);
            return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            LOG.error("Verification failed!", e);
        }
        return false;
    }

    private Response createSuccessLoginResponse(final User user, final HttpServerRequest httpServerRequest) {
        var token = Jwt.issuer(issuer)
                .upn(user.username)
                .claim("userId", user.id)
                .groups(user.role)
                .expiresIn(Duration.ofMinutes(30))
                .sign();

        return Response.ok(token, MediaType.TEXT_PLAIN_TYPE)
                .header(HttpHeaders.AUTHORIZATION, token)
                .expires(Date.from(Instant.now().plus(Duration.ofMinutes(30))))
                .cookie(new NewCookie("auth-token", token, "/", httpServerRequest.remoteAddress().hostName(),
                        null, 60 * 30, false)).build();

    }

    private Response createFailedLoginResponse() {
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
