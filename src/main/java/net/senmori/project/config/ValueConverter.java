package net.senmori.project.config;

/**
 * Converts a given value ({@link T}) into a generic object for serialization.
 */
public interface ValueConverter<T, O> {

    /**
     * Convert the object into the appropriate field type.
     *
     * @param object the object to convert
     * @return the appropriate type
     */
    T toField(O object);

    /**
     * Convert the type into the appropriate type in order to be serialized.
     *
     * @param type the type to serialize
     * @return the serializable type
     */
    O fromField(T type);
}
