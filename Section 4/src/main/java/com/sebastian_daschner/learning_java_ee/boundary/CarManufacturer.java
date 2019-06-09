package com.sebastian_daschner.learning_java_ee.boundary;

import java.util.List;
import java.util.function.Consumer;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.sebastian_daschner.learning_java_ee.control.CarCache;
import com.sebastian_daschner.learning_java_ee.control.CarFactory;
import com.sebastian_daschner.learning_java_ee.control.ProcessTracker;
import com.sebastian_daschner.learning_java_ee.control.Tracked;
import com.sebastian_daschner.learning_java_ee.entity.Car;
import com.sebastian_daschner.learning_java_ee.entity.Specification;

@Stateless
public class CarManufacturer {

    @Inject
    CarFactory carFactory;

//    i'm not using the entity manager anymore, i'm passing through the cache
    @Inject
    CarCache carCache;

    @Inject
    Consumer<Throwable> fatalLogger;
    
//    @Inject
//    FatalLogger logger;

    @Tracked(ProcessTracker.Category.MANUFACTURER)
    public Car manufactureCar(Specification specification) {
        Car car = carFactory.createCar(specification);
        carCache.cache(car);

        try {
            // do something that can fail
        } catch (Exception e) {
            fatalLogger.accept(e);
        }

        return car;
    }

//    TODO define an interceptor that automatically caches the results the first time
//    the method is invoked, and then returns the cached results in subsequent calls
//    see: Jcache api
    public List<Car> retrieveCars() {
        return carCache.retrieveCars();
    }

}
