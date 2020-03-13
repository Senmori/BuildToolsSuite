/*
 * Copyright (c) 2018, Senmori. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * The name of the author may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package net.senmori.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.senmori.project.spigot.util.VersionInfo;
import net.senmori.util.FileUtil;

/**
 * The settings files contains the required links to tools required by Build
 * Tools.
 * As well, it will store the location of directories used by BTS.
 */
@Getter
public final class BuildToolsSettings {
    private static BuildToolsSettings INSTANCE = null;

    private BuildToolsSettings.Directories directories;

    /**
     * The url to where the archive of spigot versions is.
     * <p>
     * Current value: https://hub.spigotmc.org/versions/
     */
    private final String versionLink;


    /**
     * The url to where Spigot hosts the latest working version valueOf
     * PortableGit.
     * <br>
     * Current value: https://static.spigotmc.org/git/
     */
    private final String gitInstallerLink;

    /**
     * The url to where Spigot hosts a maven installation folder.
     * <br>
     * Current value: https://static.spigotmc.org/maven/apache-maven-{0}-bin.zip
     */
    private final String mvnInstallerLink;

    /**
     * The url to the Spigot Stash.
     * <br>
     * Current value: https://hub.spigotmc.org/stash/scm/spigot/
     */
    private final String stashRepoLink; // append with 'bukkit.git', 'craftbukkit.git', etc.

    /**
     * The url of where to download Minecraft server jars.
     * <br>
     * Current value: https://s3.amazonaws.com/Minecraft.Download/versions/%1$s/minecraft_server.%1$s.jar
     */
    private final String s3DownloadLink;

    /**
     * The url to the updated method of downloading server jars.
     * <br>
     * Current value: https://launchermeta.mojang.com/mc/game/version_manifest.json
     */
    private final String minecraftVersionManifestURL; // TODO: download new jars from here

    /**
     * The PortableGit version
     * <br>
     * Current value: 2.15.0
     */
    private final String gitVersion;

    /**
     * The default version of Spigot.
     * <br>
     * Current value: 1.12.2
     */
    private final String defaultVersion;

    /**
     * The default version of maven that BuildToolsSuite uses.
     * <br>
     * Current value: 3.5.0
     */
    private final String mavenVersion;

    /**
     * The most recently used output directories.<br>
     * The output will automatically be put in working_dir if no other options
     * exist.
     */
    private final List<String> recentOutputDirectories;

    public BuildToolsSettings() {
        this(Maps.newHashMap(), Lists.newLinkedList());
    }

    public BuildToolsSettings(Map<String, String> map, List<String> recentOutputDirs) {

        this.directories = new Directories();

        // versions
        this.defaultVersion = map.getOrDefault("defaultVersion", "1.15.2");
        this.mavenVersion = map.getOrDefault("mvnVersion", "3.5.0");
        // git
        this.gitVersion = map.getOrDefault("gitVersion", "2.15.0");
        this.recentOutputDirectories = Lists.newArrayList(recentOutputDirs);

        // urls
        String formatMvn = map.getOrDefault("mavenInstallerLink", "https://static.spigotmc.org/maven/apache-maven-{0}-bin.zip");
        this.mvnInstallerLink = MessageFormat.format(formatMvn, this.mavenVersion);

        String formatGit = map.getOrDefault("gitInstallerLink", "https://static.spigotmc.org/git/PortableGit-{0}-{1}-bit.7z.exe");
        String arch = ( System.getProperty("os.arch").endsWith("64") ? "64" : "32" );
        this.gitInstallerLink = MessageFormat.format(formatGit, gitVersion, arch);

        this.versionLink = map.getOrDefault("spigotVersionLink", "https://hub.spigotmc.org/versions/");
        this.stashRepoLink = map.getOrDefault("stashRepoLink", "https://hub.spigotmc.org/stash/scm/spigot/");
        this.s3DownloadLink = map.getOrDefault("s3DownloadLink", "https://s3.amazonaws.com/Minecraft.Download/versions/%1$s/minecraft_server.%1$s.jar");
        this.minecraftVersionManifestURL = map.getOrDefault("mcJarDownloadLink", "https://launchermeta.mojang.com/mc/game/version_manifest.json");
    }


    /**
     * Get the default {@link VersionInfo} if none were found elsewhere.
     * This will not set the decompile command.
     *
     * @return a new instance valueOf {@link VersionInfo}
     */
    public VersionInfo getBukkitVersionInfo(String version) {
        String bukkitAT = "bukkit-" + version + ".at";
        String bukkitCL = "bukkit-" + version + "-cl.csrg";
        String bukkitPKG = "bukkit-" + version + "-members.csrg";
        String mcHash = " package.srg";
        String decompCmd = null;
        return new VersionInfo(version, bukkitAT, bukkitCL, bukkitPKG, mcHash, decompCmd);
    }

    /**
     * Inner class that holds all directories related to the BuildToolsSuite
     * program.
     */
    @Getter
    public static class Directories {
        /**
         * The directory where BuildToolsSuite is working from.
         * Cannot be changed.
         */
        @Setter(AccessLevel.NONE)
        private final Directory workingDir = new Directory(System.getProperty("user.dir"), "");

        /**
         * The location where most valueOf the work done by BuildToolsSuite is
         * performed.
         */
        private final Directory workDir = new Directory(workingDir, "work");

        /**
         * Temp working directory that gets deleted after the program closes.
         */
        private final Directory tmpDir = new Directory(workingDir, "tmp");


        /**
         * The location all version files will be downloaded to for caching.
         */
        private final Directory versionsDir = new Directory(workDir, "versions");

        /**
         * The location all jar files will be downloaded to for caching.
         */
        private final Directory jarDir = new Directory(workingDir, "jars");

        //
        //  All directories below here can be set to a different value, although I don't recommend it ;)
        //

        /**
         * The location where Maven is installed.
         */
        @Setter
        private Directory mvnDir = new Directory(workingDir, "maven");

        /**
         * The location where Git, or PortableGit, is installed.
         */
        @Setter
        private Directory portableGitDir = new Directory(workingDir, "PortableGit");

        public void init() {
            workingDir.getFile().mkdir(); // Required & immutable.

            workDir.getFile().mkdir();

            if ( tmpDir.getFile().exists() ) {
                FileUtil.deleteDirectory(tmpDir.getFile());
            }
            tmpDir.getFile().mkdir();
            tmpDir.getFile().deleteOnExit();

            versionsDir.getFile().mkdir();
            jarDir.getFile().mkdir();
        }
    }
}
