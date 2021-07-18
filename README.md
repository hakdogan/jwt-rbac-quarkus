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

        var method = context.getMethod();
        var uriInfo = context.getUriInfo();

        var path = uriInfo.getPath();
        var remoteAddress = httpServerRequest.remoteAddress().toString();
        var auth = null != context.getHeaderString(HttpHeaders.AUTHORIZATION);

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
## Usage examples

```shell
export token=$(http POST localhost:8080/api/signIn/guest/12345)
http POST http://localhost:8080/api/secured/addUser \
"username"="testUser", \
"password"="12345", \
"role"="user" 'Authorization: Bearer '$token
HTTP/1.1 401 Unauthorized
Content-Length: 0

export token=$(http POST localhost:8080/api/signIn/hakdogan/12345)
http POST http://localhost:8080/api/secured/addUser \
"username"="testUser", \
"password"="12345", \
"role"="user" 'Authorization: Bearer '$token
HTTP/1.1 200 OK
Content-Length: 119
Content-Type: application/json

{
    "id": 4,
    "password": "$2a$10$.x9NaYIin1EqI/C5nsxAD.6cisP4HghRgDNmfG/N0nQkk8AeAGAcW",
    "role": "user",
    "username": "testUser,"
}
```

## Requirements

- JDK 11 or later
- Maven 3.8.1 or later
- Docker (for Postgresql)  

## How to run
```shell
#You must run rsaKeyPair.sh script before running the application
mvn quarkus:dev
```
