package org.jugistanbul.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 16.07.2021
 **/
@Provider
@SecuredForAdmin
public class AdminAuthorizationFilter implements ContainerRequestFilter
{

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        if(!context.getSecurityContext().isUserInRole("admin")) {
            context.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
