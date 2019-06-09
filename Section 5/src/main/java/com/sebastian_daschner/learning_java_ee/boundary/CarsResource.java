package com.sebastian_daschner.learning_java_ee.boundary;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

import com.sebastian_daschner.learning_java_ee.entity.Car;
import com.sebastian_daschner.learning_java_ee.entity.Specification;

@Path("cars")
public class CarsResource {

	@Inject
	CarManufacturer carManufacturer;

	@Resource
	ManagedExecutorService mes;

	@GET
	public List<Car> getCars() {
		return carManufacturer.retrieveCars();
	}

	@POST
	@Path("timeout")
	//
//	using an AsyncResponse annotated with @Suspended means that everything in this method
//    is NOT executed in the http request thread, but asynchronously in a separate thread
//    this thread comes from the managedExecutorService
//    the http thread is suspended, waiting for the other to finish
//    once this happens, "resume" is called on the response
	//
	public void createCarAsyncTimeout(Specification specification, @Suspended AsyncResponse asyncResponse) {
		asyncResponse.setTimeout(10, TimeUnit.SECONDS);
		asyncResponse.setTimeoutHandler(
				response -> response.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build()));

		mes.execute(() -> asyncResponse.resume(createCar(specification)));

	}

//  i can also use a completable future in the response, communicating back the stage
	@POST
	public CompletionStage<Response> createCarAsync(Specification specification) {
		return CompletableFuture.supplyAsync(() -> createCar(specification), mes);
	}
//	another version of the above where i also set the timeout and its handler
	public CompletionStage<Response> createCarAsyncTimeout_CompletableFuture(Specification specification, @Suspended AsyncResponse asyncResponse) {
		asyncResponse.setTimeout(10, TimeUnit.SECONDS);
		asyncResponse.setTimeoutHandler(
				response -> response.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build()));

		return CompletableFuture.supplyAsync(() -> createCar(specification), mes);


	}

//    if this class was an EJB, then i wouldn't need the executor, just the async response
//    however, not beiing an ejb is a more flexible solution
//    @Asynchronous
//    public void createCarAsyncTimeout(Specification specification,
//            @Suspended AsyncResponse asyncResponse) {
//    	asyncResponse.resume(createCar(specification));
//    }

	private Response createCar(Specification specification) {
		carManufacturer.manufactureCar(specification);
		return Response.status(Response.Status.NO_CONTENT).build();
	}

}
