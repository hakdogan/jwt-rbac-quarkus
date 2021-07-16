package org.jugistanbul.resource;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jugistanbul.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 17.07.2021
 **/
@Path("/api")
@RequestScoped
public class SignInResource
{
    private static final Logger LOG = LoggerFactory.getLogger(SignInResource.class);

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://jugistanbul.org/issuer")
    String issuer;

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String info(@Context SecurityContext context){
        return context.getUserPrincipal() == null?"Anonymouse":context.getUserPrincipal().getName();
    }

    @POST
    @Path("/signIn/{username}/{password}")
    @Produces(MediaType.TEXT_PLAIN)
    public String signIn(@PathParam String username, @PathParam String password) {

            User user = User.findByUsername(username);
            return user != null && verifyBCryptPassword(user.password, password)?Jwt.issuer(issuer)
                        .upn(username)
                        .claim("userId", user.id)
                        .groups(user.role)
                        .claim("userId", user.id)
                        .expiresIn(Duration.ofMinutes(30))
                        .sign():"Login failed!";

    }

    public static boolean verifyBCryptPassword(String bCryptPasswordHash, String passwordToVerify) {

            try {
                WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();

                PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
                Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);
                Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);
                return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());

            } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
                LOG.error("Verification failed!", e);
            }
            return false;
        }
}
