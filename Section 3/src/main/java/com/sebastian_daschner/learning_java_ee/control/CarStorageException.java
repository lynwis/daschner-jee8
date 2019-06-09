package com.sebastian_daschner.learning_java_ee.control;

import javax.ejb.ApplicationException;

// this annotation marks the exception as an application level exception (for ejbs)
// this means:
//	it will not be wrapped in an EJBException if propagated through the ejb
//	it will not roll back the transaction
//		NB using rollback=true changes this behaviour!
@ApplicationException(rollback = true)
public class CarStorageException extends Exception {

	private static final long serialVersionUID = 1650842619671152423L;

	public CarStorageException(String message) {
        super(message);
    }

}
