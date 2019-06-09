package com.sebastian_daschner.learning_java_ee.cars.boundary;

import com.sebastian_daschner.learning_java_ee.cars.entity.CarCreated;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.ArrayList;
import java.util.List;

/**
 * @see SseClientTest in the test scope to see an example of a client
 * @author it42874
 *
 */

@Path("car-created-events")
@Singleton
public class CarCreatedEventsResource {

	@Context
	Sse sse;

//	the broadcaster is used to send events to ALL the clients that registered to this instance of the resource
//	each client is associated to an sseEventSink, which is registered with the broadcaster
//	when i send an event with the broadcaster, every subscriber receives it through its sink
	private SseBroadcaster sseBroadcaster;
	
//	list of generated events
	private final List<CarCreated> createdCars = new ArrayList<>();

	@PostConstruct
	private void initSseBroadcaster() {
		sseBroadcaster = sse.newBroadcaster();
	}

	@GET
	@Produces(MediaType.SERVER_SENT_EVENTS)
	@Lock(LockType.READ) // read lock - only one client at a time can register and get the missed events
	public void streamCreatedCars(
//			this context is injected by JAXRS
//			the "sink" is linked to the specific client calling this endpoint, and can later be used to send events
			@Context SseEventSink sseEventSink,
			@HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER) @DefaultValue("-1") int lastEventId) {
		sseBroadcaster.register(sseEventSink);

//		I also enable the feature of resending eventually missed events to the clients (they might have been disconnected)
		if (lastEventId >= 0) {
			resentMissingEvents(sseEventSink, lastEventId);
		}
	}

	private void resentMissingEvents(SseEventSink sseEventSink, int lastEventId) {
		for (int i = lastEventId; i < createdCars.size(); i++) {
			OutboundSseEvent event = createEvent(createdCars.get(i), i + 1);
			sseEventSink.send(event);
		}
	}

//	I listen to the CarCreated event, and broadcast it
	@Lock // write lock - different calls (threads) have to wait in order to modify the event list!
	public void onCreatedCar(@Observes CarCreated carCreated) {
		sseBroadcaster.broadcast(createEvent(carCreated, createdCars.size() + 1));
		createdCars.add(carCreated);
	}

	private OutboundSseEvent createEvent(CarCreated carCreated, int eventId) {
		return sse.newEventBuilder().id(String.valueOf(eventId)).data(carCreated.getIdentifier()).build();
	}

}
