package net.senmori.project.spigot.config;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.senmori.project.config.ProjectConfig;
import net.senmori.versioning.ComparableVersion;

@Getter
public class SpigotSettings {

    @Getter(AccessLevel.NONE)
    private final ProjectConfig config;

    private String spigotVersionsURL = "https://hub.spigotmc.org/versions/";
    private String gitInstallerLink = "https://static.spigotmc.org/git/PortableGit-{0}-{1}-bit.7z.exe";
    private String mavenInstallerLink = "https://static.spigotmc.org/maven/apache-maven-{0}-bin.zip";
    private String stashRepoLink = "https://hub.spigotmc.org/stash/scm/spigot/";
    private String minecraftManifestLink = "https://launchermeta.mojang.com/mc/game/version_manifest.json";

    private ComparableVersion spigotVersion = new ComparableVersion("1.15.2");
    private ComparableVersion mavenVersion = new ComparableVersion("3.5.0");
    private ComparableVersion portableGitVersion = new ComparableVersion("2.15.0");

    private List<String> outputDirectories = new ArrayList<>();

    public SpigotSettings(@NonNull ProjectConfig config) {
        this.config = config;
    }

    public void setSpigotVersion(@NonNull ComparableVersion version) {
        this.spigotVersion = version;
    }

    public void setMavenVersion(@NonNull ComparableVersion version) {
        this.mavenVersion = version;
    }

    public void setPortableGitVersion(@NonNull ComparableVersion version) {
        this.portableGitVersion = version;
    }
}
