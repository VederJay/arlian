# Purpose
The purpose of this project is creating a site with a number of specialized 
functions.

# Prerequisites
 ## PostgreSQL
 You'll need a PostgreSQL database for this application. You can e.g. quickly spin 
 one up in a local docker:
 
 `docker run --name postgres_arlian -e POSTGRES_PASSWORD=password -e POSTGRES_USER=user -e POSTGRES_DB=db_arlian_org -p 5432:5432 -d postgres`
 
 Set the credentials and url using environment variables:
 - `spring.datasource.username=user`
 - `spring.datasource.password=password`
 - `spring.datasource.url=jdbc:postgresql://localhost:5432/db_arlian_org`
 
 
 ## OAuth
 You'll need a client-id and client-secret from Google for use of the OAuth2 authentication. Log on 
 to https://console.developers.google.com/apis/credentials/oauthclient/ to set it up (OAuth 2.0 Client IDs).
 Provide them as environment variables:
 - `spring.security.oauth2.client.registration.google.client-id=....apps.googleusercontent.com`
 - `spring.security.oauth2.client.registration.google.client-secret=...`


# Getting development started

## Running in IntelliJ

Run the application as a _Spring Boot_ application. Set the environment variables for 
the database and OAuth. 


## Running in a local docker - from git

There's two Dockerfiles available at the root of the project: one for the develop branch and one for the master branch.
Add a configuration for these files and set the environment variables for the database and OAuth. Note that, if you're
running the database in a docker, you'll need to update the hostname to be able to access that docker from this docker, 
i.e. the url would then be:
- `spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/db_arlian_org`

For ease of use, set the container name to _arlian_edge_.

Add a _Before launch_ item:
- Call to an _External tool_. Create a new External tool calling the program `docker` with arguments 
`rm --force arlian_edge`. In other words, the previous container is removed before creating it anew.  

**Important note** is that these docker files will pull the code from git and then build that. They do **not** take 
into account your local, non-pushed changes. If you want to run a docker from your local code, use the next option.


## Running in a local docker - from local code

Run the application as a _Docker Image_. Set the image ID to 
`site:0.2.0-SNAPSHOT` (update the version to align with the version in `pom.xml`).
Set the environment variables for the database and OAuth. Note that, if you're
running the database in a docker, you'll need to update the hostname to be able
to access that docker from this docker, i.e. the url would then be:
- `spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/db_arlian_org`

For ease of use, set the container name to _arlian_edge_.

Add a number of _Before launch_ items, in this order:
- Call to an _External tool_. Create a new External tool calling the program `docker` with arguments 
`rm --force arlian_edge`. In other words, the previous container is removed before creating it anew.  
- Run the Maven _clean_ goal.
- Run the Maven _spring-boot:build-image -DskipTests_ goal. We skip tests to speed up 
deployment, but of course you'll have run the tests beforehand.


# Running in production
The application is designed to be run as a docker. It may be possible to run it in other ways, e.g. 
deployed to a stand-alone TomCat or JBoss EAP or something similar, but that hasn't been tested.

The steps to take are identical to those for running in a local docker, except of course that you'll
need to set up stronger credentials, and it would be advisable to use the docker from the master git branch.


## Running on Unraid
You'll need to have an account on DockerHub. Push your docker image there (replace _vederjay/arlian_ by your 
own repo name on DockerHub), e.g. after creating the docker image locally:
- `docker tag site:0.2.0-SNAPSHOT vederjay/arlian:latest`
- `docker push vederjay/arlian:latest`

Alternatively, you can set up DockerHub to automatically pull your code from GitHub and use a Dockerfile to 
build the image - this is the case for the vederjay/arlian DockerHub image.