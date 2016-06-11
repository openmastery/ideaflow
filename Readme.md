# To Run

Install/setup docker (instructions below), then:

```
git clone https://github.com/ideaflow/common-rest.git
./gradlew publishLocal

git clone git@github.com:ideaflow/ifm-publisher.git
./gradlew bootRun
```

There is one available endpoint:

http://localhost:8080/timeline/task/{taskId}

There are four available timelines with the taskId equal to the below options:

* "basic" : A Timeline with three bands and a subtask (subtasks divide into multiple timeline segments)
* "learning" : A timeline with nested bands within a learning band (red displayed on top of blue)
* "trial" : A Timeline with trial and error linked together, thus contained within a TimelineGroup.  Grouped bands also contain nested bands.
* "detailed" : A Timeline with an example detailed conflict (conflict linked to rework, then rework contains nested conflicts)

The current version does not yet include events in the timeline, we're still working on that.  However, the timeline is split into multiple segments according to subtask. 

# To Build

This project requires 
 - Java 1.8
 - IntelliJ Lombok plugin installed
 - IntelliJ annotation processing enabled

```
git clone git@github.com:ideaflow/ifm-publisher.git

cd ifm-publisher

./gradlew clean check

./gradlew idea
```

Import project or module into IDEA and get to coding.

## Install/Setup Docker

To install, follow instructions here:

https://docs.docker.com/machine/install-machine/

Then to start the machine:

```
docker-machine start default
```

To enable Docker in your current shell, run:

```
eval "$(docker-machine env default)"
```

Finally, to configure the machine, you'll need to add an entry to your /etc/hosts
configuration.  First, run:

```
docker-machine env default
```

And copy the IP address from the configuration line that looks like this:

```
export DOCKER_HOST="tcp://<ip-address>:2376"
```

Edit your /etc/hosts file and add:

```
<ip-address>	local.docker
```

## Postgres

Postres is required to run the component tests.  

As a one-time operation, pull and create the postgres container

```
docker pull postgres:9.4

docker create --name=postgres --publish=5432:5432 --env="POSTGRES_USER=postgres" --env="POSTGRES_PASSWORD=postgres" postgres:9.4
```

To start the container before running tests

`docker start postgres`

To re-create the postgres container

```
docker rm -f postgres

docker create --name=postgres --publish=5432:5432 --env="POSTGRES_USER=postgres" --env="POSTGRES_PASSWORD=postgres" postgres:9.4

docker start postgres
```

## Troubleshooting Setup

If you get an error on starting up the app that looks like:

"org.postgresql.util.PSQLException: Connection refused." 

That means the application is unable to connect to Docker.  
Run through the docker setup/install steps.