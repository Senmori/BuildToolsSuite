package net.senmori.project.minecraft.tasks;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import javafx.concurrent.Task;
import net.senmori.Console;
import net.senmori.pool.TaskUtil;
import net.senmori.project.minecraft.MinecraftVersion;
import net.senmori.project.minecraft.ReleaseType;
import net.senmori.pool.TaskPool;
import net.senmori.pool.TaskPools;
import net.senmori.project.spigot.util.BuildTools;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.storage.SettingsFactory;
import net.senmori.util.LogHandler;
import net.senmori.versioning.ComparableVersion;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;

public class ImportMinecraftTask extends Task<Collection<MinecraftVersion>> {
    private final BuildToolsSettings settings;
    private final BuildToolsSettings.Directories dirs;

    private final String versionManifestURL;
    private final BuildTools buildTools;
    private final Console console;
    private final Collection<MinecraftVersion> availableVersions = Lists.newLinkedList();
    private final TaskPool pool = TaskPools.createFixedThreadPool( 4 );

    public ImportMinecraftTask(BuildTools buildTools) {
        this.buildTools = buildTools;
        this.console = buildTools.getConsole();
        this.settings = buildTools.getSettings();
        this.dirs = this.settings.getDirectories();

        this.versionManifestURL = this.settings.getMinecraftVersionManifestURL();
    }

    @Override
    protected Collection<MinecraftVersion> call() throws IOException {
        LogHandler.info( "Importing Minecraft Versions!" );
        File versionsDir = new File( dirs.getVersionsDir().getFile(), "minecraft" );
        versionsDir.mkdirs();
        File manifestFile = new File( versionsDir, "version_manifest.json" );
        if ( !manifestFile.exists() ) {
            // download it
            manifestFile.createNewFile();
            manifestFile = TaskUtil.asyncDownloadFile( versionManifestURL, manifestFile );
            //console.setOptionalText( "" );

            if ( manifestFile == null ) {
                LogHandler.error( "*** Could not download \'version_manifest.json\' file." );
                return availableVersions;
            }
            LogHandler.info( "Downloaded new " + FilenameUtils.getBaseName( manifestFile.getName() ) );
        } else {
            LogHandler.info( "Found \'" + FilenameUtils.getBaseName( manifestFile.getName() ) + '\'' );
        }
        JsonObject json = SettingsFactory.getGson().fromJson( new FileReader( manifestFile ), JsonObject.class );
        if ( json == null ) {
            LogHandler.error( "*** Could not parse json in " + FilenameUtils.getBaseName( manifestFile.getName() ) + '\'' );
            return availableVersions;
        }

        if ( json.has( "versions" ) ) {
            LogHandler.info( "Processing versions..." );
            JsonArray versionsArray = json.getAsJsonArray( "versions" );

            for ( JsonElement element : versionsArray ) {
                JsonObject version = element.getAsJsonObject();

                String id = version.get( "id" ).getAsString();
                String type = version.get( "type" ).getAsString();
                String releaseTime = version.get( "releaseTime" ).getAsString();
                String versionURL = version.get( "url" ).getAsString();

                ReleaseType releaseType = ReleaseType.getByName( type );

                LocalDateTime releaseDate = LocalDateTime.parse( releaseTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME ); // ISO_OFFSET_DATE_TIME

                //console.setOptionalText( "Processing: " + id );

                // download this version's specific json file
                File versionFile = new File( versionsDir, id + ".json" );
                if ( !versionFile.exists() ) {
                    versionFile.createNewFile();
                    versionFile = TaskUtil.asyncDownloadFile( versionURL, versionFile );

                    if ( versionFile == null ) {
                        LogHandler.error( "*** Unable to download \'" + id + "\'\'s version file." );
                        continue;
                    }
                }

                JsonObject versionJson = SettingsFactory.getGson().fromJson( new FileReader( versionFile ), JsonObject.class );
                if ( versionJson == null ) {
                    LogHandler.error( "*** Unable to parse version \"" + id + "\" manifest json." );
                    LogHandler.error( "*** Deleting " + versionFile.getName() + ". Please invalidate the cache in the Minecraft tab if this causes problems." );
                    FileDeleteStrategy.FORCE.delete( versionFile );
                    continue;
                }

                if ( versionJson.has( "downloads" ) ) {
                    JsonObject downloads = versionJson.getAsJsonObject( "downloads" );

                    if ( downloads.has( "server" ) ) {
                        JsonObject server = downloads.getAsJsonObject( "server" );

                        String sha = server.get( "sha1" ).getAsString();
                        String serverDownloadURL = server.get( "url" ).getAsString();

                        MinecraftVersion minecraftVersion = new MinecraftVersion( new ComparableVersion( id ), releaseType, releaseDate, sha, serverDownloadURL );

                        availableVersions.add( minecraftVersion );

                    } else if ( downloads.has( "client" ) ) {
                        // old_alpha or some old_beta
                        JsonObject server = downloads.getAsJsonObject( "client" );

                        String sha = server.get( "sha1" ).getAsString();
                        String serverDownloadURL = server.get( "url" ).getAsString();

                        MinecraftVersion minecraftVersion = new MinecraftVersion( new ComparableVersion( id ), releaseType, releaseDate, sha, serverDownloadURL );

                        availableVersions.add( minecraftVersion );
                    }
                }
            }
            //console.setOptionalText( "" );
        }
        LogHandler.info( "All versions imported!" );
        //console.setOptionalText( "All versions imported!" );
        return availableVersions;
    }
}
