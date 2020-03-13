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

package net.senmori.project.spigot.util;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import lombok.Getter;
import net.senmori.Console;
import net.senmori.project.minecraft.VersionManifest;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.storage.Directory;
import net.senmori.util.LogHandler;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FilenameUtils;

/**
 * Controls all functionality related to the building of Spigot jars.
 */
public class BuildTools {


    @Getter
    private final Directory workingDirectory;
    @Getter
    private final BuildToolsSettings settings;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final BooleanProperty initialized = new SimpleBooleanProperty( this, "initialized", false );
    private final BooleanProperty runBuildTools = new SimpleBooleanProperty( this, "runBuildTools", false );
    private final BooleanProperty reimportVersions = new SimpleBooleanProperty( this, "reimportVersions", false );

    private final BooleanProperty running = new SimpleBooleanProperty( this, "running", false );
    private final BooleanProperty disableCertificateCheck = new SimpleBooleanProperty( this, "disableCertificateCheck", false );
    private final BooleanProperty dontUpdate = new SimpleBooleanProperty( this, "dontUpdate", false );
    private final BooleanProperty skipCompile = new SimpleBooleanProperty( this, "skipCompile", false );
    private final BooleanProperty genSource = new SimpleBooleanProperty( this, "genSource", false );
    private final BooleanProperty genDocumentation = new SimpleBooleanProperty( this, "genDocumentation", false );
    private final BooleanProperty invalidateCache = new SimpleBooleanProperty( this, "invalidateCache", false );

    public final BooleanProperty updateVersions = new SimpleBooleanProperty( this, "updateVersions", false );

    private final StringProperty version;
    private final ObservableSet<String> outputDirectories;

    @Getter
    private final Console console;
    @Getter
    private final VersionManifest versionManifest;


    public BuildTools(Directory workingDirectory, BuildToolsSettings settings, VersionManifest versionManifest, Console console) {
        this.workingDirectory = workingDirectory;
        this.settings = settings;
        this.versionManifest = versionManifest;
        this.console = console;

        version = new SimpleStringProperty( this, "version", settings.getDefaultVersion() );
        version.set( this.settings.getDefaultVersion() );
        outputDirectories = FXCollections.observableSet( workingDirectory.getFile().getAbsolutePath() );

        // must always have at least one output directory
        outputDirectories.addListener( new SetChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> change) {
                if ( change.wasRemoved() && change.getSet().isEmpty() ) {
                    outputDirectories.add( workingDirectory.getFile().getAbsolutePath() );
                }
            }
        } );
        // default to the specified default version
        version.addListener( (observable, oldValue, newValue) -> {
            if ( ( newValue == null ) || newValue.trim().isEmpty() ) {
                version.set( this.settings.getDefaultVersion() );
            }
        } );
    }

    public void invalidateCache() {
        Platform.runLater( () -> {
            this.initialized.set( false );
        } );

    }

    private void delete(File file) {
        if ( file.isDirectory() ) {
            for ( File in : file.listFiles() ) {
                if ( in.isDirectory() ) {
                    delete( in );
                } else {
                    //console.setOptionalText( FilenameUtils.getBaseName( in.getName() ) );
                    try {
                        FileDeleteStrategy.FORCE.delete( in );
                    } catch ( NullPointerException | IOException e ) {
                        LogHandler.error( "** Could not delete " + in + " **" );
                    }
                }
            }
        }
        //console.setOptionalText( "Deleting " + FilenameUtils.getBaseName( file.getName() ) );
        try {
            FileDeleteStrategy.FORCE.delete( file );
        } catch ( NullPointerException | IOException e ) {
            LogHandler.error( "** Could not delete " + file + " **" );
        }
    }


    public BooleanProperty getInitialized() {
        return initialized;
    }

    public void setInitialized(boolean value) {
        initialized.set( value );
    }

    public boolean isInitialized() {
        return initialized.get();
    }

    public BooleanProperty getRunBuildTools() {
        return runBuildTools;
    }

    public void runBuildTools() {
        runBuildTools.set( true );
    }

    public BooleanProperty getReimportVersions() {
        return reimportVersions;
    }

    public void importVersions() {
        reimportVersions.set( true );
    }

    public BooleanProperty getRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running.set( running );
    }

    public boolean isRunning() {
        return running.get();
    }

    public BooleanProperty getDisableCertificateCheckProperty() {
        return disableCertificateCheck;
    }

    public void setDisableCertificateCheck(boolean value) {
        disableCertificateCheck.set( value );
    }

    public boolean isDisableCertificateCheck() {
        return disableCertificateCheck.get();
    }

    public BooleanProperty getDontUpdate() {
        return dontUpdate;
    }

    public void setDontUpdate(boolean value) {
        dontUpdate.setValue( value );
    }

    public boolean isDontUpdate() {
        return dontUpdate.get();
    }

    public BooleanProperty getSkipCompile() {
        return skipCompile;
    }

    public void setSkipCompile(boolean value) {
        skipCompile.set( value );
    }

    public boolean isSkipCompile() {
        return skipCompile.get();
    }

    public BooleanProperty getGenSource() {
        return genSource;
    }

    public void setGenSource(boolean value) {
        genSource.set( value );
    }

    public boolean isGenSource() {
        return genSource.get();
    }

    public BooleanProperty getGenDocumentation() {
        return genDocumentation;
    }

    public void setGenDocumentation(boolean value) {
        genDocumentation.set( value );
    }

    public boolean isGenDocumentation() {
        return genDocumentation.get();
    }

    public BooleanProperty getInvalidateCache() {
        return invalidateCache;
    }

    public void setInvalidateCache(boolean value) {
        invalidateCache.set( value );
    }

    public boolean isInvalidateCache() {
        return invalidateCache.get();
    }

    public BooleanProperty getUpdateVersions() {
        return updateVersions;
    }

    public void setUpdateVersions(boolean value) {
        updateVersions.set( value );
    }

    public boolean isUpdateVersions() {
        return updateVersions.get();
    }

    public StringProperty getVersionProperty() {
        return version;
    }

    public void setVersion(String version) {
        this.version.setValue( version );
    }

    public String getVersion() {
        return version.get();
    }

    public ObservableSet<String> getOutputDirectories() {
        return outputDirectories;
    }

    public boolean addOutputDirectory(String directory) {
        return outputDirectories.add( directory );
    }

    public boolean addOutputDirectory(File directory) {
        return outputDirectories.add( directory.getAbsolutePath() );
    }

    public boolean addOutputDirectory(Directory directory) {
        return outputDirectories.add( directory.getFile().getAbsolutePath() );
    }

    public void clearDirectories() {
        outputDirectories.clear();
    }
}
