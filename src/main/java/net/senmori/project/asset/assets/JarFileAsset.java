package net.senmori.project.asset.assets;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.senmori.util.ReflectionUtil;

public class JarFileAsset extends FileAsset {

    private final Supplier<ClassLoader> classLoaderSupplier;

    private JarFileAsset(File file, Supplier<ClassLoader> supplier) {
        super(file);
        this.classLoaderSupplier = supplier;
    }

    public ClassLoader getClassLoader() {
        return classLoaderSupplier.get();
    }

    public static JarFileAsset of(String assetName, Class<?> clazz) {
        Objects.requireNonNull(assetName, () -> "Cannot create jar file asset with null name");
        Objects.requireNonNull(clazz, () -> "Cannot create JarFileAsset with null classloader");
        Supplier<ClassLoader> supplier = clazz::getClassLoader;
        File assetFile = getFileFromAssetName(assetName, clazz.getClassLoader());
        return new JarFileAsset(assetFile, supplier);
    }

    public static JarFileAsset of(String assetName, ClassLoader classLoader) {
        Objects.requireNonNull(assetName, () -> "Cannot create jar file asset with null name");
        Objects.requireNonNull(classLoader, () -> "Cannot create JarFileAsset with null classloader");
        File assetFile = getFileFromAssetName(assetName, classLoader);
        return new JarFileAsset(assetFile, () -> classLoader);
    }

    public static JarFileAsset of(String assetName) {
        Objects.requireNonNull(assetName, () -> "Cannot create JarFileAsset with empty asset name");
        Class<?> clazz = ReflectionUtil.getCallingClass();
        File assetFile = getFileFromAssetName(assetName, clazz.getClassLoader());
        return new JarFileAsset(assetFile, clazz::getClassLoader);
    }

    public static JarFileAsset of(File file, ClassLoader classLoader) {
        Objects.requireNonNull(file, () -> "Cannot create JarFileAsset without a source file");
        Objects.requireNonNull(classLoader, "Cannot create JarFileAsset without a class loader");
        return new JarFileAsset(file, () -> classLoader);
    }

    private static File getFileFromAssetName(String assetName, ClassLoader classLoader) {
        Optional<File> fileOptional = Optional.empty();
        URL resourceURL = getURLFromResource(assetName, classLoader);
        try {
            File file = new File(resourceURL.toURI());
            fileOptional = Optional.of(file);
        } catch ( URISyntaxException e ) {
            e.printStackTrace();
        }
        return fileOptional.orElseThrow(IllegalArgumentException::new);
    }

    private static URL getURLFromResource(String assetName, ClassLoader classLoader) {
        Optional<URL> resourceURL = Optional.ofNullable(classLoader.getResource(assetName));
        return resourceURL.orElseThrow(IllegalArgumentException::new);
    }
}
