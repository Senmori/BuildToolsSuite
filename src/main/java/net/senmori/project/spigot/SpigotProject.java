package net.senmori.project.spigot;

import net.senmori.project.ApplicationDetails;
import net.senmori.project.Project;
import net.senmori.project.Result;
import net.senmori.project.asset.assets.JarFileAsset;
import net.senmori.project.asset.assets.LocalFileAsset;
import net.senmori.project.config.ConfigBuilder;
import net.senmori.project.config.ProjectConfig;
import net.senmori.storage.Directory;

public class SpigotProject implements Project {

    private LocalFileAsset configFileAsset;
    private JarFileAsset sourceFileAsset;
    private Directory workingDirectory;
    private ProjectConfig config;

    public SpigotProject() {}

    @Override
    public String getName() {
        return "Spigot";
    }

    @Override
    public ClassLoader getProjectClassLoader() {
        return getClass().getClassLoader();
    }

    @Override
    public Directory getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public Result initSettings(ApplicationDetails rootProject) {
        workingDirectory = new Directory(rootProject.getWorkingDirectory(), "Spigot");
        workingDirectory.getFile().mkdirs();
        configFileAsset = LocalFileAsset.of(this, "spigot_settings.toml");
        sourceFileAsset = JarFileAsset.of(this, "spigot_settings.toml");
        config = ConfigBuilder.newBuilder(configFileAsset)
                              .sourceFile(sourceFileAsset)
                              .copySourceFileOnLoad()
                              .build();
        return Result.SUCCESS;
    }

    @Override
    public Result initStage(ApplicationDetails rootProject) {
        return Result.SUCCESS;
    }

    @Override
    public Result populateView(ApplicationDetails rootProject) {
        return Result.SUCCESS;
    }

    public ProjectConfig getConfig() {
        return config;
    }
}
