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

package net.senmori.project.spigot.tasks;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import difflib.DiffUtils;
import difflib.Patch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import javafx.concurrent.Task;
import net.senmori.Console;
import net.senmori.command.CommandHandler;
import net.senmori.command.CommandIssuer;
import net.senmori.pool.TaskPool;
import net.senmori.pool.TaskPools;
import net.senmori.pool.TaskUtil;
import net.senmori.project.minecraft.MinecraftVersion;
import net.senmori.project.minecraft.VersionManifest;
import net.senmori.project.spigot.util.BuildInfo;
import net.senmori.project.spigot.util.BuildTools;
import net.senmori.project.spigot.util.VersionInfo;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.storage.SettingsFactory;
import net.senmori.task.ExtractFilesTask;
import net.senmori.task.FileDownloadTask;
import net.senmori.task.git.GitCloneTask;
import net.senmori.task.git.GitPullTask;
import net.senmori.util.FileUtil;
import net.senmori.util.HashChecker;
import net.senmori.util.LogHandler;
import net.senmori.util.builders.MavenCommandBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

public final class BuildToolsTask extends Task<Long> {
    private String applyPatchesShell = "sh";

    private final BuildTools options;
    private final BuildToolsSettings settings;
    private final BuildToolsSettings.Directories dirs;
    private final Console console;

    private final TaskPool projectPool = TaskPools.createCachedTaskPool();

    public BuildToolsTask(BuildTools options) {
        this.options = options;
        this.settings = options.getSettings();
        this.dirs = settings.getDirectories();
        this.console = options.getConsole();
    }

    @Override
    protected Long call() throws Exception {
        Stopwatch watch = Stopwatch.createStarted();
        File work = dirs.getWorkDir().getFile();
        printOptions( options );

        if ( options.isInvalidateCache() ) {
            InvalidateCacheTask task = new InvalidateCacheTask( options, options.getVersionManifest() );
            task.messageProperty().addListener( ( (observable, oldValue, newValue) -> {
                //console.setOptionalText( newValue );
            } ) );
            projectPool.submit( task );
            task.get();
        }
        console.setOptionalText( "" );


        LogHandler.info( "Cloning Bukkit..." );
        File bukkit = new File( dirs.getWorkingDir().getFile(), "Bukkit" );
        if ( !bukkit.exists() ) {
            String repo = settings.getStashRepoLink() + "bukkit.git";
            GitCloneTask task = new GitCloneTask( repo, bukkit );
            task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( task );
            task.get();
        } else {
            LogHandler.info( "Found Bukkit repository at " + bukkit );
        }
        console.setOptionalText( "" );

        LogHandler.info( "Cloning CraftBukkit..." );
        File craftBukkit = new File( dirs.getWorkingDir().getFile(), "CraftBukkit" );
        if ( !craftBukkit.exists() ) {
            String repo = settings.getStashRepoLink() + "craftbukkit.git";
            GitCloneTask task = new GitCloneTask( repo, craftBukkit );
            task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( task );
            task.get();
        } else {
            LogHandler.info( "Found CraftBukkit repository at " + craftBukkit );
        }
        console.setOptionalText( "" );

        LogHandler.info( "Cloning Spigot..." );
        File spigot = new File( dirs.getWorkingDir().getFile(), "Spigot" );
        if ( !spigot.exists() ) {
            String repo = settings.getStashRepoLink() + "spigot.git";
            GitCloneTask task = new GitCloneTask( repo, spigot );
            task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( task );
            task.get();
        } else {
            LogHandler.info( "Found Spigot repository at " + spigot );
        }
        console.setOptionalText( "" );

        LogHandler.info( "Cloning BuildData..." );
        File buildData = new File( dirs.getWorkingDir().getFile(), "BuildData" );
        if ( !buildData.exists() ) {
            String repo = settings.getStashRepoLink() + "builddata.git";
            GitCloneTask task = new GitCloneTask( repo, buildData );
            task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( task );
            task.get();
        } else {
            LogHandler.info( "Found BuildData repository at " + buildData );
        }
        console.setOptionalText( "" );

        dirs.getMvnDir().getFile().mkdirs();
        String mvn = new File( dirs.getMvnDir().getFile(), "/bin/mvn" ).getAbsolutePath();

        // Close these when done using them; they could keep file references open
        Git bukkitGit = Git.open( bukkit );
        Git craftBukkitGit = Git.open( craftBukkit );
        Git spigotGit = Git.open( spigot );
        Git buildGit = Git.open( buildData );

        BuildInfo buildInfo = BuildInfo.getDefaultImpl();

        if ( !options.isDontUpdate() ) {
            dirs.getVersionsDir().getFile().mkdirs();
            final String askedVersion = options.getVersion();
            LogHandler.info( "Attempting to build version: '" + askedVersion + "' use --rev <version> to override" );

            String text = askedVersion + ".json";
            File versionsDir = new File( dirs.getVersionsDir().getFile(), "spigot" );
            versionsDir.mkdirs();
            File verInfo = new File( versionsDir, text );
            if ( !verInfo.exists() ) {
                // download file
                String url = settings.getVersionLink() + askedVersion + ".json";
                verInfo = TaskUtil.asyncDownloadFile( url, verInfo );

                LogHandler.info( "Downloaded " + askedVersion + "\'s version file." );
            } else {
                LogHandler.info( "Found version " + askedVersion );
            }
            buildInfo = SettingsFactory.getGson().fromJson( new FileReader( verInfo ), BuildInfo.class );
            console.setOptionalText( "" );

            // BuildData
            LogHandler.info( "Pulling updates from BuildData remote..." );
            GitPullTask buildDataTask = new GitPullTask( buildGit, buildInfo.getRefs().getBuildData(), console );
            buildDataTask.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( buildDataTask );
            buildDataTask.get();
            console.setOptionalText( "" );

            // Bukkit
            LogHandler.info( "Pulling updates from Bukkit remote..." );
            GitPullTask bukkitTask = new GitPullTask( bukkitGit, buildInfo.getRefs().getBukkit(), console );
            bukkitTask.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( bukkitTask );
            bukkitTask.get();
            console.setOptionalText( "" );

            // CraftBukkit
            LogHandler.info( "Pulling updates from CraftBukkit remote..." );
            GitPullTask craftBukkitTask = new GitPullTask( craftBukkitGit, buildInfo.getRefs().getCraftBukkit(), console );
            craftBukkitTask.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( craftBukkitTask );
            craftBukkitTask.get();
            console.setOptionalText( "" );

            // Spigot
            LogHandler.info( "Pulling updates from BuildData remote..." );
            GitPullTask spigotTask = new GitPullTask( spigotGit, buildInfo.getRefs().getSpigot(), console );
            spigotTask.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( spigotTask );
            spigotTask.get();
        }
        console.setOptionalText( "" );

        File infoFile = new File( dirs.getWorkingDir().getFile(), "BuildData/info.json" );
        infoFile.mkdirs();
        VersionInfo versionInfo = SettingsFactory.getGson().fromJson( new FileReader( infoFile ), VersionInfo.class );
        // Default to latest version
        if ( versionInfo == null ) {
            LogHandler.info( "Could not generate version. Using default ( " + settings.getDefaultVersion() + " )." );
            versionInfo = settings.getBukkitVersionInfo( settings.getDefaultVersion() );
        }
        LogHandler.info( "Attempting to build Minecraft with details: " + versionInfo );

        String jarName = "minecraft_server." + versionInfo.getMinecraftVersion() + ".jar";
        dirs.getJarDir().getFile().mkdirs();
        File jarDir = new File( dirs.getJarDir().getFile(), versionInfo.getMinecraftVersion() );
        if ( !jarDir.exists() ) {
            jarDir.mkdir();
        }
        File vanillaJar = new File( jarDir, jarName );
        if ( !vanillaJar.exists() || !new HashChecker(Hashing.md5(), vanillaJar, versionInfo).checkHash() ) {
            LogHandler.info( "Downloading Minecraft Server Version \'" + versionInfo.getMinecraftVersion() + "\' jar." );
            VersionManifest manifest = options.getVersionManifest();
            MinecraftVersion minecraftVersion = manifest.getVersion( versionInfo.getMinecraftVersion() );
            String url = minecraftVersion.getServerDownloadURL();
            vanillaJar = new FileDownloadTask( url, vanillaJar ).call();
        }

        if ( !new HashChecker(Hashing.md5(), vanillaJar, versionInfo ).checkHash() ) {
            LogHandler.error( "**** Could not download clean Minecraft jar, giving up. ****" );
            options.setRunning( false );
            cancel( true );
            return -1L;
        }

        Iterable<RevCommit> mappings = buildGit.log()
                                               .addPath( "mappings/" + versionInfo.getAccessTransforms() )
                                               .addPath( "mappings/" + versionInfo.getClassMappings() )
                                               .addPath( "mappings/" + versionInfo.getMemberMappings() )
                                               .addPath( "mappings/" + versionInfo.getPackageMappings() )
                                               .setMaxCount( 1 ).call();

        Hasher mappingsHash = Hashing.md5().newHasher();
        for ( RevCommit rev : mappings ) {
            mappingsHash.putString( rev.getName(), Charsets.UTF_8 );
        }
        String mappingsVersion = mappingsHash.hash().toString().substring( 24 ); // Last 8 chars

        LogHandler.info( "Attempting to create mapped jar." );
        File finalMappedJar = new File( jarDir, "mapped." + mappingsVersion + ".jar" );
        if ( !finalMappedJar.exists() ) {
            LogHandler.info( "Final mapped jar: " + finalMappedJar + " does not exist, creating!" );

            File clMappedJar = new File( finalMappedJar + "-cl" );
            File mMappedJar = new File( finalMappedJar + "-m" );

            File fernflower = new File( buildData, "bin/fernflower.jar" );
            File specialSource = new File( buildData, "bin/SpecialSource.jar" );
            File specialSource_2 = new File( buildData, "bin/SpecialSource-2.jar" );

            LogHandler.info( "Applying first mappings..." );
            CommandHandler.getCommandIssuer().executeCommand( buildData, "java", "-jar", specialSource_2.getAbsolutePath(), "map", "-i", vanillaJar.getAbsolutePath(), "-m", "mappings/" + versionInfo.getClassMappings(), "-o", clMappedJar.getAbsolutePath() );
            LogHandler.info( clMappedJar.getName() + " created!" );

            LogHandler.info( "Applying second mappings..." );
            CommandHandler.getCommandIssuer().executeCommand( buildData, "java", "-jar", specialSource_2.getAbsolutePath(), "map", "-i", clMappedJar.getAbsolutePath(),
                    "-m", "mappings/" + versionInfo.getMemberMappings(), "-o", mMappedJar.getAbsolutePath() );
            LogHandler.info( mMappedJar.getName() + " created!" );

            LogHandler.info( "Applying final mappings..." );
            CommandHandler.getCommandIssuer().executeCommand( buildData, "java", "-jar", specialSource.getAbsolutePath(), "--kill-lvt", "-i", mMappedJar.getAbsolutePath(), "--access-transformer", "mappings/" + versionInfo.getAccessTransforms(),
                    "-m", "mappings/" + versionInfo.getPackageMappings(), "-o", finalMappedJar.getAbsolutePath() );
        } else {
            LogHandler.info( "Mapped jar for mappings version \'" + mappingsVersion + "\' already exists!" );
        }
        console.setOptionalText( "" );

        LogHandler.info( "Installing \'" + finalMappedJar.getName() + "\' to local repository" );

        String[] goals = new String[] { "sh", mvn,
                "install:install-file", "-Dfile=" + finalMappedJar.getAbsolutePath(),
                "-Dpackaging=jar", "-DgroupId=org.spigotmc",
                "-DartifactId=minecraft-server", "-Dversion=" + versionInfo.getMinecraftVersion() + "-SNAPSHOT"
        };
        CommandIssuer command = CommandHandler.getCommandIssuer();
        command.executeCommand( dirs.getWorkingDir().getFile(), goals );

        LogHandler.info( "Decompiling: " );
        File decompileDir = new File( dirs.getWorkDir().getFile(), "decompile-" + mappingsVersion );
        if ( !decompileDir.exists() ) {
            decompileDir.mkdir();

            LogHandler.info( "Extracting " + finalMappedJar.getName() + " into " + decompileDir.getAbsolutePath() );
            File clazzDir = new File( decompileDir, "classes" );
            clazzDir.mkdir();
            LogHandler.debug( "Unzipping " + finalMappedJar.getName() + " into " + clazzDir );

            ExtractFilesTask task = new ExtractFilesTask( finalMappedJar, clazzDir, console, (str) -> str.startsWith( "net/minecraft/server/" ) );
            task.get();
            console.setOptionalText( "" );

            if ( versionInfo.getDecompileCommand() == null ) {
                LogHandler.info( "Set decompile command for VersionInfo ( " + versionInfo.getMinecraftVersion() + " )" );
                versionInfo.setDecompileCommand( "java -jar BuildData/bin/fernflower.jar -dgs=1 -hdc=0 -rbr=0 -asc=1 -udv=0 {0} {1}" );
            }

            // call fernflower - all this is so we can capture the output and redirect it accordingly
            File fernJar = new File( dirs.getWorkingDir().getFile(), "BuildData/bin/fernflower.jar" );
            LogHandler.info( "Running decompile command..." );
            CommandIssuer handler = CommandHandler.getCommandIssuer();
            Process process = handler.issueCommand( dirs.getWorkingDir().getFile(), MessageFormat.format( versionInfo.getDecompileCommand(), clazzDir.getAbsolutePath(), decompileDir.getAbsolutePath() ).split( " " ) );
            BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
            String line = "";
            while ( ( line = reader.readLine() ) != null ) {
                if ( line.startsWith( ".." ) ) {
                    // it's a ...done message, ignore it
                }
                String[] split = line.split( ":" );
                if ( split.length > 1 ) {
                    console.setOptionalText( split[ 1 ] );
                } else {
                    console.setOptionalText( line );
                }
            }

            LogHandler.info( "Finished decompiling " + FilenameUtils.getBaseName( clazzDir.getName() ) );
        } else {
            // check to see if the directory has net/minecraft/server
            if ( decompileDir.isDirectory() && decompileDir.listFiles().length > 0 ) {
                LogHandler.info( "Decompile directory " + decompileDir + " already exists!" );
            } else {
                LogHandler.error( "*** Decompile directory is not valid. Please delete it, or try invalidating your cache! ***" );
                options.setRunning( false );
                cancel( true );
                return -1L;
            }
        }
        // end decompileDir check
        console.setOptionalText( "" );

        LogHandler.info( "Applying CraftBukkit Patches" );
        File nmsDir = new File( craftBukkit, "src/main/java/net/" );
        nmsDir.mkdir();
        if ( nmsDir.exists() ) {
            dirs.getTmpDir().getFile().mkdirs();
            LogHandler.info( "Backing up NMS dir" );
            FileUtils.moveDirectory( nmsDir, new File( dirs.getTmpDir().getFile(), "nms.old." + System.currentTimeMillis() ) );
        }

        LogHandler.info( "Starting NMS patches..." );
        File patchDir = new File( craftBukkit, "nms-patches" );
        File[] files = patchDir.listFiles();
        for ( File file : files ) {
            if ( !file.getName().endsWith( ".patch" ) ) {
                continue;
            }

            String targetFile = "net/minecraft/server/" + file.getName().replaceAll( ".patch", ".java" );


            File clean = new File( decompileDir, targetFile );
            File t = new File( nmsDir.getParentFile(), targetFile );
            t.getParentFile().mkdirs();

            console.setOptionalText( "Patching: " + FilenameUtils.getBaseName( file.getName() ) );

            List<String> readFile = Files.readLines( file, Charsets.UTF_8 );

            // Manually append prelude if it is not found in the first few lines.
            boolean preludeFound = false;
            for ( int i = 0; i < Math.min( 3, readFile.size() ); i++ ) {
                if ( readFile.get( i ).startsWith( "+++" ) ) {
                    preludeFound = true;
                    break;
                }
            }
            if ( !preludeFound ) {
                readFile.add( 0, "+++" );
            }

            Patch parsedPatch = DiffUtils.parseUnifiedDiff( readFile );
            List<?> modifiedLines = DiffUtils.patch( Files.readLines( clean, Charsets.UTF_8 ), parsedPatch );

            BufferedWriter bw = new BufferedWriter( new FileWriter( t ) );
            for ( String line : ( List<String> ) modifiedLines ) {
                bw.write( line );
                bw.newLine();
            }
            bw.close();
        }
        console.setOptionalText( "" );

        LogHandler.info( "Patching complete!" );
        File tmpNms = new File( craftBukkit, "tmp-nms" );
        FileUtils.copyDirectory( nmsDir, tmpNms );

        LogHandler.info( "Preparing CraftBukkit repository for cloning and patching..." );
        LogHandler.info( "Deleting \'patched\' branch..." );
        craftBukkitGit.branchDelete()
                      .setBranchNames( "patched" )
                      .setForce( true )
                      .call();

        LogHandler.info( "Create new \'patched\' branch..." );
        craftBukkitGit.checkout()
                      .setCreateBranch( true )
                      .setForce( true )
                      .setName( "patched" )
                      .call();

        LogHandler.info( "Add all files that match \'src/main/java/net\' pattern..." );
        craftBukkitGit.add()
                      .addFilepattern( "src/main/java/net/" )
                      .call();

        LogHandler.info( "Setting commit message." );
        craftBukkitGit.commit().setMessage( "CraftBukkit $ " + new Date() ).call();

        LogHandler.info( "Checking out " + buildInfo.getRefs().getCraftBukkit() );
        craftBukkitGit.checkout().setName( buildInfo.getRefs().getCraftBukkit() ).call();

        LogHandler.info( "Moving " + tmpNms.getName() + " to " + nmsDir.getAbsolutePath() );
        FileUtils.moveDirectory( tmpNms, nmsDir );

        LogHandler.info( "Cloning SpigotAPI" );
        File spigotApi = new File( spigot, "Bukkit" );
        if ( !spigotApi.exists() ) {
            String url = "file://" + bukkit.getAbsolutePath();
            GitCloneTask task = new GitCloneTask( url, spigotApi );
            task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( task );
            task.get();
        }
        console.setOptionalText( "" );

        LogHandler.info( "Cloning SpigotServer" );
        File spigotServer = new File( spigot, "CraftBukkit" );
        if ( !spigotServer.exists() ) {
            String url = "file://" + craftBukkit.getAbsolutePath();
            GitCloneTask task = new GitCloneTask( url, spigotServer );
            task.messageProperty().addListener( (observable, oldValue, newValue) -> {
                console.setOptionalText( newValue );
            } );
            projectPool.submit( task );
            task.get();
        }
        console.setOptionalText( "" );

        if ( !options.isSkipCompile() ) {
            MavenCommandBuilder mvnBuild = MavenCommandBuilder.builder().setInteractiveMode( false );
            LogHandler.info( "### Compiling Bukkit ###" );
            mvnBuild.setBaseDirectory( bukkit ).setGoals( Arrays.asList( "install" ) ).execute();
            if ( options.isGenDocumentation() ) {
                mvnBuild.setBaseDirectory( bukkit ).setGoals( Arrays.asList( "javadoc:jar" ) ).execute();
            }
            if ( options.isGenSource() ) {
                mvnBuild.setBaseDirectory( bukkit ).setGoals( Arrays.asList( "source:jar" ) ).execute();
            }
            LogHandler.info( "### Compiling CraftBukkit ###" );
            mvnBuild.setBaseDirectory( craftBukkit ).setGoals( Arrays.asList( "install" ) ).execute();
        }
        console.setOptionalText( "" );

        try {
            LogHandler.info( "Starting applyPatches for Spigot" );
            File spigotDir = new File( dirs.getWorkingDir().getFile(), "Spigot" );
            CommandHandler.getCommandIssuer().executeCommand( spigotDir, applyPatchesShell, "applyPatches.sh" );
            LogHandler.info( "*** Spigot patches applied!" );

            if ( !options.isSkipCompile() ) {
                LogHandler.info( "### Compiling Spigot & Spigot-API ###" );
                MavenCommandBuilder spigotBuilder = MavenCommandBuilder.builder()
                                                                       .setBaseDirectory( new File( dirs.getWorkingDir().getFile(), "Spigot" ) )
                                                                       .setInteractiveMode( false )
                                                                       .setGoals( Arrays.asList( "clean", "install" ) );
                spigotBuilder.execute().getExitCode();
            }
        } catch ( Exception ex ) {
            LogHandler.error( "Error compiling Spigot. Please check the wiki for FAQs." );
            LogHandler.error( "If this does not resolve your issue then please pastebin the entire console when seeking support." );
            ex.printStackTrace();
            options.setRunning( false );
            cancel( true );
            return -1L;
        }

        for ( int i = 0; i < 5; i++ ) {
            LogHandler.info( " " );
        }

        final String version = versionInfo.getMinecraftVersion();
        if ( !options.isSkipCompile() ) {
            projectPool.submit( () -> {
                LogHandler.info( "Success! Everything compiled successfully. Copying final .jar files now." );
                LogHandler.debug( "OutputDirectories: " + options.getOutputDirectories() );
                final File bukkitSourceDir = new File( dirs.getWorkingDir().getFile(), "Bukkit/target" );
                final File craftSourceDir = new File( dirs.getWorkingDir().getFile(), "CraftBukkit/target" );
                final File spigotSourceDir = new File( dirs.getWorkingDir().getFile(), "Spigot/Spigot-Server/target" );
                final Predicate<String> isValidJar = new Predicate<String>() {
                    @Override
                    public boolean test(String str) {
                        return str.contains( version ) &&
                                       !str.startsWith( "original" ) && !str.contains( "-shaded" ) &&
                                       !str.contains( "-remapped" ) && str.endsWith( ".jar" );
                    }
                };
                for ( String outputDir : options.getOutputDirectories() ) {
                    try {
                        FileUtil.copyJar( craftSourceDir, new File( outputDir ), "craftbukkit-" + version + ".jar", isValidJar );
                        FileUtil.copyJar( spigotSourceDir, new File( outputDir ), "spigot-" + version + ".jar", isValidJar );

                        if ( options.isGenSource() ) {
                            FileUtil.copyJar( bukkitSourceDir, new File( outputDir ), "bukkit-" + version + "-sources.jar", (str) -> isValidJar.test( str ) && str.contains( "-sources" ) );
                        }
                        if ( options.isGenDocumentation() ) {
                            FileUtil.copyJar( bukkitSourceDir, new File( outputDir ), "bukkit-" + version + "-javadoc.jar", (str) -> isValidJar.test( str ) && str.contains( "-javadoc" ) );
                        }
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        break;
                    }
                }
                FileUtil.deleteFilesInDirectory( craftSourceDir, (str) -> str.endsWith( ".jar" ) );
                FileUtil.deleteFilesInDirectory( spigotSourceDir, (str) -> str.endsWith( ".jar" ) );
                if ( options.isGenSource() ) {
                    FileUtil.deleteFilesInDirectory( bukkitSourceDir, (str) -> isValidJar.test( str ) && str.contains( "-sources" ) );
                }
                if ( options.isGenDocumentation() ) {
                    FileUtil.deleteFilesInDirectory( bukkitSourceDir, (str) -> isValidJar.test( str ) && str.contains( "-javadoc" ) );
                }
                return true;
            } ).get();
        }
        console.setOptionalText( "" );

        watch = watch.stop();
        long seconds = watch.elapsed( TimeUnit.SECONDS );
        projectPool.getService().shutdown();
        return seconds;
    }

    private void printOptions(BuildTools options) {
        LogHandler.info( "BuildToolsSuite Options: " );
        LogHandler.info( "Disable Certificate Check: " + options.isDisableCertificateCheck() );
        LogHandler.info( "Don't Update: " + options.isDontUpdate() );
        LogHandler.info( "Skip Compile: " + options.isSkipCompile() );
        LogHandler.info( "Generate Sources: " + options.isGenSource() );
        LogHandler.info( "Generate Docs: " + options.isGenDocumentation() );
        LogHandler.info( "Version: " + options.getVersion() );
        for ( String dir : options.getOutputDirectories() ) {
            LogHandler.info( "Output Directory: " + dir );
        }
    }
}
