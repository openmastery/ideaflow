# To Run

```
git clone git@github.com:ideaflow/ifm-publisher.git
./gradlew bootRun
```

There is one available endpoint:

http://localhost:8080/timeline/task/{taskId}

There are four available timelines with the taskId equal to the below options:

* basic
* trial
* learning
* detailed

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
