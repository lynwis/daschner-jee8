package com.sebastian_daschner.learning_java_ee.boundary;

import com.sebastian_daschner.learning_java_ee.control.CarFactory;
import com.sebastian_daschner.learning_java_ee.control.CarProcessor;
import com.sebastian_daschner.learning_java_ee.control.CarRepository;
import com.sebastian_daschner.learning_java_ee.entity.Car;
import com.sebastian_daschner.learning_java_ee.entity.CarCreated;
import com.sebastian_daschner.learning_java_ee.entity.Specification;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class CarManufacturer {

    @Inject
    CarFactory carFactory;

    @Inject
    CarRepository carRepository;

    @Inject
    CarProcessor carProcessor;

    @Inject
    Event<CarCreated> carCreated;

//  this enables the firing of new threads in a managed way
    @Resource
    ManagedExecutorService mes;

    public Car manufactureCar(Specification specification) {
        Car car = carFactory.createCar(specification);
        carRepository.store(car);
        carProcessor.processNewCarAsync(car);
        
//      in javaEE, you should NOT start your own threads
//      threads should be managed by the container, in order not to risk resource leaks
//      here i'm showcasing managed executor service
//      the method processNewCar is a synchronous one
//      in ejbs I can define async methods
//      let's say I don't want to use an ejb, or for some reason i can't, and I have to use a CDI component
//      CDI components don't have direct support for async invocation
//      so, i have to start a new thread to do the async processing, but I should do it in a managed way, like this:
        mes.execute(() -> carProcessor.processNewCar(car));
        carCreated.fireAsync(new CarCreated(car.getIdentifier()));
        return car;
    }

    public List<Car> retrieveCars() {
        return carRepository.loadCars();
    }

}
