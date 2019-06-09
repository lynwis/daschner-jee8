package com.sebastian_daschner.learning_java_ee.boundary;

import com.sebastian_daschner.learning_java_ee.entity.Car;
import com.sebastian_daschner.learning_java_ee.entity.Color;
import com.sebastian_daschner.learning_java_ee.entity.EngineType;
import com.sebastian_daschner.learning_java_ee.entity.Specification;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonCollectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("cars")
@Produces(MediaType.APPLICATION_JSON)
public class CarsResource {

    @Inject
    CarManufacturer carManufacturer;

//    special "resource" that comes with jax-rs
    @Context
    UriInfo uriInfo;
    
    @GET
    public List<Car> retrieveCars() {
    	return carManufacturer.retrieveCars();
    }

//  using Json-P to customize output
    @GET
    public JsonArray retrieveCars(@NotNull @QueryParam("filter") EngineType engineType) {
        return carManufacturer.retrieveCars(engineType)
                .stream()
                .map(car -> Json.createObjectBuilder()
                        .add("identifier", car.getIdentifier())
                        .add("color", car.getColor().name())
                        .add("engine", car.getEngineType().name())
                        .build())
                .collect(JsonCollectors.toJsonArray());
    }

//	post with json-p
    @POST
    public void createCar(JsonObject jsonObject) {
    	Color color = Color.valueOf(jsonObject.getString("color"));
    	EngineType engine = EngineType.valueOf(jsonObject.getString("engine"));
    	carManufacturer.manufactureCar(new Specification(color, engine));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    using bean validation on the input
    public Response createCar(@Valid @NotNull Specification specification) {
        Car car = carManufacturer.manufactureCar(specification);
 
        URI uri = uriInfo.getBaseUriBuilder()
//        		jax-rs resolves this path at runtime!
                .path(CarsResource.class)
//                with this i'm including the uri to retrieve the object i just created
//                again, jax-rs resolves this at runtime, from the method name i'm passing as a string, and the class containing it
                .path(CarsResource.class, "retrieveCar")
//                the resource path to get a specific car contains a uri parameter: with this method i'm telling jax-rs to replace the parameter with the value i'm passing it
                .build(car.getIdentifier());
//        so, i'll get a response in HATEOAS style, indicating the uri to retrieve all cars and the uri to retrieve the one i just created

        return Response.created(uri).build();
    }

    @GET
    @Path("{id}")
    public Car retrieveCar(@PathParam("id") String identifier) {
        return carManufacturer.retrieveCar(identifier);
    }

}
