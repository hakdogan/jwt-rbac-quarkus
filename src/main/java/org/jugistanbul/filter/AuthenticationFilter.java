package org.jugistanbul.filter;

import io.vertx.core.http.HttpServerRequest;

import org.jugistanbul.resource.SignInResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 16.07.2021
 **/
@Provider
@Secured
public class AuthenticationFilter implements ContainerRequestFilter
{

    private static final Logger LOG = LoggerFactory.getLogger(SignInResource.class);

    @Context
    HttpServerRequest httpServerRequest;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {

        final String method = context.getMethod();
        UriInfo uriInfo = context.getUriInfo();

        final String path = uriInfo.getPath();
        final String remoteAddress = httpServerRequest.remoteAddress().toString();
        boolean auth = null != context.getHeaders().getFirst("Authorization");

        if(!auth) {
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        LOG.info(String.format("Request %s %s from IP %s User %s", method, path, remoteAddress,
                context.getSecurityContext().getUserPrincipal().getName()));
    }
}
