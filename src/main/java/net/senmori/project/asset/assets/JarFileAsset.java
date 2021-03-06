package net.senmori.project.asset.assets;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.senmori.project.Project;

public class JarFileAsset extends FileAsset {

    private final Supplier<ClassLoader> classLoaderSupplier;

    private JarFileAsset(File file, Supplier<ClassLoader> supplier) {
        super(file);
        this.classLoaderSupplier = supplier;
    }

    public ClassLoader getClassLoader() {
        return classLoaderSupplier.get();
    }

    public static JarFileAsset of(Project project, String assetName) {
        Objects.requireNonNull(assetName, () -> "Cannot create jar file asset with null asset name");
        Objects.requireNonNull(project, () -> "Cannot create JarFileAsset with no project");
        Supplier<ClassLoader> supplier = project::getProjectClassLoader;
        File assetFile = getFileFromAssetName(assetName, project.getClass());
        return new JarFileAsset(assetFile, supplier);
    }

    private static File getFileFromAssetName(String assetName, Class<?> clazz) {
        Optional<File> fileOptional = Optional.empty();
        URL resourceURL = getURLFromResource(assetName, clazz);
        try {
            File file = new File(resourceURL.toURI());
            fileOptional = Optional.of(file);
        } catch ( URISyntaxException e ) {
            e.printStackTrace();
        }
        return fileOptional.orElseThrow(IllegalArgumentException::new);
    }

    private static URL getURLFromResource(String assetName, Class<?> clazz) {
        Optional<URL> resourceURL = Optional.ofNullable(clazz.getResource(assetName));
        Supplier<String> errorMessageSupplier = () -> "Could not find asset with name " + assetName + " using " + clazz.getName() + "'s classloader";
        return resourceURL.orElseThrow(() -> new IllegalArgumentException(errorMessageSupplier.get()));
    }
}
