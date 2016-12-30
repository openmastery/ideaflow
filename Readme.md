# IdeaFlow Metrics Server 

The "/ideaflow" service acts as a centralized endpoint for IdeaFlow data collection (@see https://plugins.jetbrains.com/idea/plugin/9239) and low-level metrics profiling on individual Idea Flow Maps (@see github.com/openmastery/ideaflow-profiler).

The service is implemented with SpringBoot, and Gradle, and designed to be run as a SaaS platform or run on-prem.  The project is OSS under a GPL 3.0 license.  The client-tools.jar that connects to the ideaflow server is under Apache 2.0 license.

## What does the service do?

The following endpoints are available (swagger docs coming soon):

* POST http://localhost:8980/ideaflow/collect/batch (publish your IdeaFlow data to the server)

* GET http://localhost:8980/ideaflow/task/ (list tasks available for viewing)

* GET http://localhost:8980/ideaflow/timeline/task/{taskId} (get metrics profile for a task)
* GET http://localhost:8980/ideaflow/timeline/subtask/{subtaskId} (get detailed metrics profile for a subtask)

Stub data is loaded on startup in the demo@openmastery.org account that you can use to explore the API.
The API-Key for the demo account is printed on the screen at startup.  You'll need to pass the API-Key as a header to all API calls.  To setup new user accounts, see instructions below.

# To Run

Install/setup docker (instructions below), then:

```
git clone git@github.com:openmastery/ideaflow.git
./gradlew bootRun
```

# User Access

All APIs require access using an API-KEY header.  When you access the API
via the plugin, you need to configure preferences to use the API Key.

To generate an API-Key for a user:

```
curl -X POST -H "X-API-KEY: <api-key>" <server-url>/user?email=abc@gmail.com
```

Where <api-key> is the default user API-KEY between the *******'s on ideaflow component startup

# To Build

This project requires 
 - Java 1.8
 - IntelliJ Lombok plugin installed
 - IntelliJ annotation processing enabled

```
git clone git@github.com:openmastery/ideaflow.git

cd ideaflow

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


## Heroku

This application is currently deployed on Heroku at http://om-ideaflow.herokuapp.com.  For an API-Key on the shared service, please contact janelle@openmastery.org.  We will have account creation setup soon, but while this project is still in early development, accounts are created manually.

To deploy to Heroku:

Install heroku cli (google)

Log into heroku

`heroku login`

Create the heroku application

`heroku create om-ideaflow`

Or, if the application has already been created, initialize the remote

`heroku git:remote -a om-ideaflow`

Create the database (can upgrade to hobby-basic just by associating credit card w/ account)

`heroku addons:create heroku-postgresql:hobby-dev`

Deploy the application on heroku

`git push heroku master`

