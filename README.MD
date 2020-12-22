# Purpose
The purpose of this project is creating a site with a number of specialized 
functions.

# Prerequisites
 ## PostgreSQL
 You'll need a PostgreSQL database for this application. You can e.g. quickly spin 
 one up in a local docker:
 
 `docker run --name postgres_arlian -e POSTGRES_PASSWORD=password -e POSTGRES_USER=user -e POSTGRES_DB=db_arlian_org -p 5432:5432 -d postgres`
 
 ## OAuth
 
 You'll need a client-id and client-secret from Google for use of the OAuth2 authentication.
 Provide them as environment variables:
 - `spring.security.oauth2.client.registration.google.client-id=....apps.googleusercontent.com`
 - `spring.security.oauth2.client.registration.google.client-secret=...`


# Getting development started

## Running in IntelliJ

Use the provided run configuration _IntelliJ run_. In it, you will find environment variables
for the datasource, i.e. the database:
- `spring.datasource.username=user`
- `spring.datasource.password=password`
- `spring.datasource.url=jdbc:postgresql://localhost:5432/db_arlian_org`

If you change the environment variables given to the postgres docker, you'll also need to update 
them here.


## Running in a local docker

Use the provided run configuration _Docker run_. In it, you will find environment variables
for the datasource, i.e. the database:
- `spring.datasource.username=user`
- `spring.datasource.password=password`
- `spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/db_arlian_org`

If you change the environment variables given to the postgres docker, you'll also need to update 
them here.

Note that the docker run contains a _Before launch_ call to an _External tool_. This is nothing
more than a call to the program `docker` with arguments `rm --force arlian_edge`. In other words,
the previous container is removed before creating it anew. 


# Running in production
The application is designed to be run as a docker. It may be possible to run it in other ways, e.g. 
deployed to a stand-alone TomCat or JBoss EAP or something similar, but that hasn't been tested.

The steps to take are identical to those for running in a local docker, except of course that you'll
need to set up stronger credentials. 