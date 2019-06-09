package com.sebastian_daschner.learning_java_ee.control;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.client.WebTarget;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class WeatherStation {

//	I'm feigning the case where i want to inquire the temperature asking to many external systems
//	so i'm using multiple targets, that i will call in parallel
	private Set<WebTarget> targets;

	@Resource
	ManagedExecutorService mes;

	@PostConstruct
	private void init() {
		// ...
	}

	public double getTemperatureForecast() {
		List<CompletionStage<Double>> completionStages = invokeTemperatureTargets();

		return completionStages.stream()
//		for each completionStage wrapping the Double result, I use the reduce function
//		this function takes the first element from the streams, puts it aside, then gets the next one
//		and calls a binary function on both
//		then the result is put aside in place of the first element, and is combined with the third element
//		using the same function, and so on
//		In our case, the binary function is the Max between the two values
//		so basically, i'm getting the max value out of the stream		
//				.reduce((l, r) -> l.thenCombine(r, (ld, rd) -> Math.max(ld, rd))
				.reduce((l, r) -> l.thenCombine(r, Math::max))
//				in the end, we operate on the final CompletionStage (containing the max Double)
//				we transform it in CompletableFuture, and call "join" on it, meaning we wait for it's completion and get it's actual result
				.map(c -> c.toCompletableFuture().join())
				.orElseThrow(() -> new IllegalStateException("No weather forecast result available"));
	}

//    CompletionStage is a superclass of completableFuture
	private List<CompletionStage<Double>> invokeTemperatureTargets() {
		return targets.stream()
//				for each target, i'm calling its endpoint, then using reactive functionality
//				i'm getting back a CompletionStage for each target
				.map(t -> t.request().rx().get(Double.class))
				.collect(Collectors.toList());
	}

}
