package org.ideaflow.publisher.resources;

import org.ideaflow.publisher.api.event.NewEvent;
import org.ideaflow.publisher.api.event.EventType;
import org.ideaflow.publisher.api.ResourcePaths;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Path(ResourcePaths.EVENT_PATH)
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

	@POST
	@Path(ResourcePaths.NOTE_PATH)
	public void addUserNote(NewEvent event) {
		System.out.println("Add Note: " + event.getTaskId() + ", " + event.getComment() + ", "+ EventType.NOTE);
	}

	@POST
	@Path(ResourcePaths.SUBTASK_PATH)
	public void addSubtask(NewEvent event) {
		System.out.println("Add Subtask: " + event.getTaskId() + ", " + event.getComment() + ", "+ EventType.SUBTASK);
	}

	//Developers have been creating "note types" manually using [Subtask] and [Prediction] as prefixes in their comments.
	//Subtask events in particular I'm using to derive a "Subtask band" and collapse all the details of events/bands
	// that happen within a subtask, so you can "drill in" on one subtask at a time ford a complex IFM.

}
