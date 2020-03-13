package net.senmori.project;

/**
 * Represents a system that can be interacted with in order to
 * build a given product.
 *
 * It is up to the implementation to determine the resources that need to
 * be loaded, how to load them, and how the system should be interacted with.
 */
public interface Project {

    /**
     * The name of the project
     *
     * @return the name of the project
     */
    String getName();

    /**
     *
     *
     * @return
     */
    Result loadSettings();
}
