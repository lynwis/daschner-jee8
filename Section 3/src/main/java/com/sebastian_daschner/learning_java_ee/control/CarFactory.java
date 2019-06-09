package com.sebastian_daschner.learning_java_ee.control;

import com.sebastian_daschner.learning_java_ee.entity.Car;
import com.sebastian_daschner.learning_java_ee.entity.Specification;

import javax.transaction.Transactional;
import java.util.UUID;

public class CarFactory {

//	for EJB transactions you can define rollback behaviour in the applicationException annotation
//	this won't work for the CDI transactions
//	for those, rollbackOn must be used, specifying the exceptions that cause the tx to rollback
    @Transactional(rollbackOn = CarStorageException.class)
    public Car createCar(Specification specification) {
        Car car = new Car();
        car.setIdentifier(UUID.randomUUID().toString());
        car.setColor(specification.getColor());
        car.setEngineType(specification.getEngineType());
        return car;
    }

}