# How to Use JWT RBAC in Quarkus

This repository is a tutorial for `JUG Istanbul`'s `How to use JWT RBAC with Quarkus` meetup that showing how to verify JSON Web Tokens and provide secured access to the HTTP endpoints using Bearer Token Authorization and Role-Based Access Control in Quarkus.


```java
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

```

```java
@Path("/api/secured")
@RequestScoped
@Secured
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizedUserResource {
 ....   
}
```

```java
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
```

```java
@POST
@Path("/addUser")
@SecuredForAdmin
@Transactional
public User addUser(final UserDTO dto){
    return User.add(dto);
}
```

## Requirements

- JDK 11 or later
- Maven 3.8.1 or later
- Docker (for Postgresql)  

## How to run
```shell
#You must run generateRSA.sh script before running the application
mvn quarkus:dev
```
