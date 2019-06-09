package com.sebastian_daschner.learning_java_ee.control;

import javax.ejb.ApplicationException;

// this annotation prevents the wrapping of this exception in an EJB exception
// when it arrives to the ejb and gets propagated through it
@ApplicationException
public class CarCreationException extends RuntimeException {

	private static final long serialVersionUID = -8381538580340629503L;

	public CarCreationException(String message) {
        super(message);
    }

}
