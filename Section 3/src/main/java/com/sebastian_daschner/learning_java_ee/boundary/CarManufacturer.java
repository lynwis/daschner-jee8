package com.sebastian_daschner.learning_java_ee.boundary;

import com.sebastian_daschner.learning_java_ee.control.CarFactory;
import com.sebastian_daschner.learning_java_ee.entity.Car;
import com.sebastian_daschner.learning_java_ee.entity.Specification;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CarManufacturer {

    @Inject
    CarFactory carFactory;

    @PersistenceContext
    EntityManager entityManager;

//    NB ABOUT ROLLBACKS !!
//    if the exception thrown uring the execution of the transactional method
//    is an unchecked exception (RuntimeException), the TX is automatically rolled back
//    if it's an application level exception, or checked exception (it's in the signature of the method)
//    then the TX is not rolled back, as it's seen as an "expected" exception
//    to change this behaviour, you can specify the @ApplicationException annotation with rollback=true
    
//    NB as i'm in an ejb, this method is wrapped in a transaction
//    when the method ends, the transaction is committed
    public Car manufactureCar(Specification specification) {
        Car car = carFactory.createCar(specification);
        entityManager.persist(car);
        return car;
    }

//    getting all cars with a named query
    public List<Car> retrieveCars() {
        return entityManager.createNamedQuery(Car.FIND_ALL, Car.class).getResultList();
    }

}
