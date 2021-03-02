# chitchat 
Toy project to learn oidc, websockets and more quarkus

Realtime chat application with private and group chats.

- java 11
- quarkus backend
- keycloak as authentication provider
- vuejs 3 frontend

## Development
### Keycloak 
The chat app uses keycloak as an authentication provider.

`docker run --name keycloak -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8180:8080 -p 8543:8443 quay.io/keycloak/keycloak:12.0.4`

After that you can use `docker start keycloak`

#### Configure keycloak:
Open `localhost:8180` and login with the `admin:admin` then add a new realm and import the `chitchat-realm.json`

### Server
Before you start the server make sure keycloak is up and running.

Inside the server module run `./mvnw quarkus:dev` to start the server with hot reload.
You can reach the server under localhost:8080

### Frontend
 - run `npm install` to install all dependencies
 - run `npm serve` to start the frontend with hot module replacement (available under localhost:8083)
 - run `npm build` to compile the frontend for production use

You can use maven to build the frontend for production. Run `mvn package`

## Docker
run `mvn clean package` in the project root to build the frontend and backend for production.


run `docker-compose build` and `docker-compose up` to build and start the frontend and backend servers.
Again, make sure you start keycloak before.

