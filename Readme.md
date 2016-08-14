# To Run

Install/setup docker (instructions below), then:

```
git clone https://github.com/ideaflow/common-rest.git
./gradlew publishLocal

git clone git@github.com:ideaflow/ifm-publisher.git
./gradlew bootRun
```

There are three available endpoints:

http://localhost:8980/task/{taskId}
http://localhost:8980/timeline/band?taskId={taskId}
http://localhost:8980/timeline/tree?taskId={taskId}

There are four available timelines with the taskId equal to the below options:

* taskId:1, taskName: "basic" - A Timeline with three bands and a subtask (subtasks divide into multiple timeline segments)
* taskId:2, taskName: "learning" - A timeline with nested bands within a learning band (red displayed on top of blue)
* taskId:3, taskName: "trial" -  A Timeline with trial and error linked together, thus contained within a TimelineGroup.  Grouped bands also contain nested bands.
* taskId:4, taskName: "detailed" - A Timeline with an example detailed conflict (conflict linked to rework, then rework contains nested conflicts)

The current version does not yet include timeline detail APIs, but we've got support for the timeband visualization (band view), and support for the structured tree model (tree view) with band groups, nested bands, events, etc. The timeline is split into multiple segments according to subtask in the tree. 

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

If you've got Docker for Mac, or Docker for Windows installed, you can *skip this section* completely.  If you get any errors when running Gradle build commands for postgres, run `docker ps` to make sure docker is on your path.

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

`./gradlew pullPostgres createPostgres`

To start the container before running tests

`./gradlew startPostgres`

To re-create the postgres container

`./gradlew refreshPostgres`

## Troubleshooting Setup

If you get an error on starting up the app that looks like:

"org.postgresql.util.PSQLException: Connection refused." 

That means the application is unable to connect to Docker.  
Run through the docker setup/install steps.

### After a Restart, SpringBoot Hangs

After a restart SpringBoot will hang while it tries to connect to
a non-existent VM.

Run:

```
docker-machine start default
eval "$(docker-machine env default)"
docker start postgres
```

Then bounce the server.
