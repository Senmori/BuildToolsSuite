package net.senmori.project;

import net.senmori.storage.Directory;

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
     * Get the {@link ClassLoader} that will be used to load project specifc
     * files without having to specify the direct path names.
     *
     * @return the classloader
     */
    ClassLoader getProjectClassLoader();

    Directory getWorkingDirectory();

    /**
     * Initialize project settings.
     *
     * @return the appropriate {@link Result}
     */
    Result initSettings(ApplicationDetails rootProject);

    /**
     * Initialize the javafx scenes, stages, and components
     *
     * @return the appropriate {@link Result}
     */
    Result initStage(ApplicationDetails rootProject);

    /**
     * Populate the scenes with the appropriate information
     *
     * @return the appropriate {@link Result}
     */
    Result populateView(ApplicationDetails rootProject);
}
