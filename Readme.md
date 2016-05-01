# To Run

```
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
