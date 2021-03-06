package com.phonecompany.exception.service_layer;

/**
 * Gets thrown if the given type is not supported by the method that
 * executes upon the object of this type.
 */
public class TypeNotSupportedException extends UnsupportedOperationException {

    public TypeNotSupportedException(Class<?> unsupportedType) {
        this("The only supported classes are: instances of Number, String and Date. " +
                "But you provided: " + unsupportedType.getSimpleName());
    }

    public TypeNotSupportedException(String message) {
        super(message);
    }
}
