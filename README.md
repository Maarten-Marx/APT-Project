# Advanced Programming Topics - Project

## Theme

I have chosen to build my project around the theme of a forum, complete with threads, comments, reactions, and users.
Logged-in users can create threads, and leave comments or reactions on them. They can also get achievements in doing so.

## Components

This project contains 3 microservices:
1. [Thread Service](thread-service)
2. [Interaction Service](interaction-service) *(Implemented in Kotlin)*
3. [User Service](user-service)

These microservices are publicly accessible through the [gateway](gateway) service.

Some shared code, like the DTOs, is kept in a [Common](common) project, which is imported by all microservices.

> [!WARNING]  
> IntelliJ is sometimes unable to resolve the references to the Common project in the Interaction Service.
> This can be fixed by applying the quick fix it suggests: `Add dependency on module '….common.main'`. The project will compile regardless.

### Databases

The Thread- and Interaction Services both make use of a MySQL database, as their data is fairly structured.  
The User Service uses a MongoDB database instead, which allows it to store less structured data, like a list of achievements for example.
This also opens up the possibility to add more dynamic features to user profiles, like social links, badges, etc.

### Tests

I've written unit tests for all services relating to an entity in the database:
- [ThreadService](thread-service/src/test/java/me/maartenmarx/threadservice/ThreadServiceUnitTest.java)
- [CommentService & ReactionService](interaction-service/src/test/java/me/maartenmarx/interactionservice/InteractionServiceUnitTest.java)
- [UserService](user-service/src/test/java/me/maartenmarx/userservice/UserServiceUnitTest.java)

> [!NOTE]  
> I've not written any tests for the [`AchievementService`](user-service/src/main/java/me/maartenmarx/userservice/service/AchievementService.java) class,
> as I didn't consider it on the same level as the other services. Its methods also don't return any meaningful data, they only update an entry in the user database.

### Additional Features

- [x] **2.5** Add an event-driven service using a message broker
  - [x] **2.5.1** Use Kafka as a message broker

## Diagram

![project-structure](public/project-structure.drawio.png)
*(This image can be imported and edited on [draw.io](https://draw.io))*

## API Requests

> [!NOTE]  
> I'm using IntelliJ's built-in [HTTP client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html).
> To reproduce these requests:
> 1. Get a Bearer token using Postman, the way it was described in the course material.
> 2. Create a file named `http-client.private.env.json` with the content below:
>   ```json
>   {
>     "dev": {
>       "bearer": "<YOUR TOKEN>"
>     }
>   }
>   ```
> 3. Select the `dev` environment in the `.http` files in [`gateway/http`](gateway/http).
> 4. Run the requests in the files.
> 
> *You might need to change some of the IDs in the URLs depending on the IDs of your entities.*

### [Threads](gateway/http/thread-service.http)

**`GET` All Threads**

![GET-threads.png](public/http/GET-threads.png)

**`GET` Thread by ID**

![GET-thread-by-id.png](public/http/GET-thread-by-id.png)

**`GET` Threads by User**

![GET-threads-by-user.png](public/http/GET-threads-by-user.png)

**`POST` New Thread**

![POST-thread.png](public/http/POST-thread.png)

**`PUT` Thread by ID**

![PUT-thread-by-id.png](public/http/PUT-thread-by-id.png)

**`DELETE` Thread by ID**

![DELETE-thread-by-id.png](public/http/DELETE-thread-by-id.png)

### [Interactions](gateway/http/interaction-service.http)

**`POST` New Comment**

![POST-comment.png](public/http/POST-comment.png)

**`POST` New Comment**

![POST-reaction.png](public/http/POST-reaction.png)

### [Users](gateway/http/user-service.http)

**`GET` User by ID**

![GET-user-by-id.png](public/http/GET-user-by-id.png)
