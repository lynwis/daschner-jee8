package com.sebastian_daschner.learning_java_ee.control;

import com.sebastian_daschner.learning_java_ee.entity.CarCreated;

import javax.enterprise.event.ObservesAsync;
import java.util.concurrent.locks.LockSupport;

// NB prior to JavaEE 8, there wasn't support for async events, so to handle events in
// an async way you had to use an @Asynchronous EJB, defining a listener method with
// the @Observes annotation on its input event
// with the ejb, or the method itself, defined as async, the event is handled in an async way

// with javaEE 8, you can use the @ObservesAsync annotation, without the need of an ejb

public class CarCreationListener {

    public void onCarCreation(@ObservesAsync CarCreated carCreated) {
        LockSupport.parkNanos(2_000_000_000L);
        System.out.println("new car created with id " + carCreated.getIdentifier());
    }

}
