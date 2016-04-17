# API Notes

## API Model

Each user has an CompositeIdeaFlowState at any given moment that represents a specific IdeaFlowState in the IdeaFlowStateMachine.
The IdeaFlowStateMachine holds the activeState for the user in a variable and has an API that restricts the possible transitions.

IdeaFlowStateMachine {
	IdeaFlowState activeState

	toLearning()

	toRework()

	toConflict()

	toProgress()

}

IdeaFlowState {
	IdeaFlowStateType [Troubleshooting, Learning, Rework, Progress, ReworkConflict, LearningConflict]

    String taskId

	Timestamp start
	Timestamp end

	String startingComment
	String endingComment

}

IdeaFlowStateType {
	List<IdeaFlowStateType> allowedTransitions
}

### ALL LEGAL TRANSITIONS

Progress -> Troubleshooting
Progress -> Learning
Progress -> Rework

Learning -> Progress
Learning -> Learning-Conflict (nested)
Learning -> Rework (linked)

Rework -> Progress
Rework -> Rework-Conflict (nested)
Rework -> Learning (linked)

Conflict -> Progress
Conflict -> Learning (link)
Conflict -> Rework (link)

Rework-Conflict (nested) -> Rework
Learning-Conflict (nested) -> Learning

Rework-Conflict (nested) -> Conflict (unnested)
Learning-Conflict (nested) -> Conflict (unnested)

The output of the IdeaFlowStateMachine is an AnnotatedTimelineStream which includes the full history of state transitions.
We will provide a reference implementation of the state machine that can be easily ported to most any programming language.
And a list of requirement specifications for testing.

## ACCESSING THE DATA

AnnotatedTimelineStream is an iterable composite stream that will be able to block and receive streaming timeline data so that a UI
can display live updates in Idea Flow Maps, or enable streaming Idea Flow dashboards at the aggregate level.  In the short-term,
we won't be using streams but will be designing an iterable resource to make it easy to move this direction later.

AnnotatedTimelineStream implements Iterable {
	IdeaFlowActivityStream ideaFlowActivityStream
	IdleActivityStream idleActivityStream
	TaskActivationEventStream taskActivationEventStream
	UserEventStream userEventStream
}

ActivityStream is an iterable stream of activity that occurs in parallel of the IdeaFlowStateMachine.  Example implementations:

IdeaFlowActivityStream extends ActivityStream {
	List<IdeaFlowState> ideaFlowActivities
}

EditorActivityStream extends ActivityStream {
    List<EditorActivity> editorActivities
}

EditorActivity {
	String fileName
	String folderPath
	boolean isModified
	String navigateFromLink (Did the developer decide to "zoom in" from the previous file?)
	Long duration
}

ExecutionActivityStream extends ActivityStream {
    List<ExecutionActivity> executionActivities
}

ExecutionActivity {
    String executionTarget
    Long duration
}

BrowserActivityStream extends ActivityStream {
	List<BrowserActivity> browserActivities
}

IdleActivityStream extends ActivityStream {
	List<IdleActivity> idleActivities
}

"Activities" are different from "Events" in that one has duration, and another is a moment of time,
but events are really just a special kind of activity, that can be streamed like anything ActivityStream.

GitEventStream extends ActivityStream {
	List<GitEvent> gitEvents (commit, branch, push, etc)
}

UserEventStream extends ActivityStream {
	List<UserEvent> userEvents (comment, subtask)
}

TaskActivationEventStream extends ActivityStream {
	List<TaskActivationEvent> taskActivationEvents (taskId)

# OTHER STUFF

The plugin will need to be able to work in "offline mode" whenever it gets disconnected from the internet.  The messages
should spool to a file, then send a batch of updates with local timestamps to the servers to catch up.  We may need to have
a time-adjustment algorithm to translate local time into server time when we write the batch records.

When the timeline thumbnail is rendered, we will need the AnnotatedTimelineStream for display.


## How does a developer know when they are doing lots of task-switching?

By visualizing the AnnotatedTimelineStream for a span of time (IdeaFlow for the day),
the timeline will show multiple labeled segments with each task segment labeled.

Overnight breaks in the day can be shown with labeled dates to show when a new day starts.
Idle time can optionally be un-collapsed, to show breaks and disruptions throughout the day.


### Plugin-based automatic state transitions include:

* The ability to automatically transition from the Learning state to the Progress state when the developer makes a
modification to a file.  Do we really want to do this automatically?  What if you're adding a print statement
in order to learn?

* The ability to automatically transition from the Progress state to the Validate state (new state) when the
developer executes the code.  (This will happen in the derived model only)

* The ability to automatically transition from the Validate state to the Progress state when the developer
makes a modification to a file. (This will happen in the derived model only)

## Conflict "detail mode".

When a user starts a conflict, they can optionally run in "detail mode", which changes the behavior of the tool:

* The ability to automatically transition from the Troubleshooting state to the Rework state when the developer makes a modification to a file.
* The ability to automatically transition from the Rework state to the Validate state when the developer executes the code.
* The ability to automatically transition from the Validate state to the Troubleshooting state when execution stops and the developer doesn't make a modification or resume execution for a short period of time.

## Resume from Idle state

* The ability to automatically transition from Idle to Validate when the developer executes the code.
* The ability to automatically transition from Idle to Progress when the developer modifies the code.

## How does a developer know when a task is blocked?

If a user is in the middle of troubleshooting, learning, and rework, then task-switches in the middle of the friction,
the task-switch is could be caused by either a block, a disruption, or taking a break because the problem is hard.
The visualization should show an indicator that there was a task-switch in the middle of the friction, even though the idle time is collapsed.

