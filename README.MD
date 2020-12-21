# Purpose
The purpose of this project is creating a site with a number of specialized 
functions.


## Getting development started

### Prerequisites
Provide a connection to a PostgreSQL database, e.g. through a local docker:

`docker run --name postgres_arlian -e POSTGRES_PASSWORD=password -e POSTGRES_USER=user -e POSTGRES_DB=db_arlian_org -p 5432:5432 -d postgres`

You can then run the application either from IntelliJ or in a local docker. 


### Running in IntelliJ

Use the provided run configuration _SiteApplication_. In it, you will find environment variables
for the datasource, i.e. the database:
- `spring.datasource.username=user`
- `spring.datasource.password=password`
- `spring.datasource.url=jdbc:postgresql://localhost:5432/db_arlian_org`

If you change the environment variables given to the postgres docker, you'll also need to update 
them here.


### Running in a local docker

Use the provided run configuration _run docker_. In it, you will find environment variables
for the datasource, i.e. the database:
- `spring.datasource.username=user`
- `spring.datasource.password=password`
- `spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/db_arlian_org`

If you change the environment variables given to the postgres docker, you'll also need to update 
them here.

Note that the docker run contains a _Before launch_ call to an _External tool_. This is nothing
more than a call to the program `docker` with arguments `rm --force arlian_edge`. In other words,
the previous container is removed before creating it anew. 


## Running in production
The application is designed to be run as a docker. It may be possible to run it in other ways, e.g. 
deployed to a stand-alone TomCat or JBoss EAP or something similar, but that hasn't been tested.

The steps to take are identical to those for running in a local docker, except of course that you'll
need to set up stronger credentials. 