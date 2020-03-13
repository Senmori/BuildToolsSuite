package net.senmori.project.spigot.tasks;

import com.google.common.collect.Maps;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import net.senmori.Console;
import net.senmori.pool.TaskUtil;
import net.senmori.project.spigot.util.BuildInfo;
import net.senmori.project.spigot.util.BuildTools;
import net.senmori.project.spigot.util.SpigotVersion;
import net.senmori.storage.BuildToolsSettings;
import net.senmori.storage.SettingsFactory;
import net.senmori.util.LogHandler;
import net.senmori.versioning.ComparableVersion;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImportSpigotTask extends Task<Map<SpigotVersion, BuildInfo>> {
    private final Pattern JSON_PATTERN = Pattern.compile( ".json" );
    private final Pattern EXCLUSION_PATTERN = Pattern.compile( "(\\d{2,4})[-_][a-zA-Z]*" );
    private final Pattern NUMBERS_ONLY_EXCLUSION = Pattern.compile( "\\d{3,4}" );
    private final BuildToolsSettings settings;
    private final BuildToolsSettings.Directories dirs;

    private final String url;

    private final BuildTools buildTools;
    private final Console console;

    public ImportSpigotTask(BuildTools buildTools) {
        this.buildTools = buildTools;
        this.console = buildTools.getConsole();

        this.settings = buildTools.getSettings();
        this.dirs = buildTools.getSettings().getDirectories();
        this.url = this.settings.getVersionLink();
    }

    @Override
    public Map<SpigotVersion, BuildInfo> call() throws Exception {
        File spigotVersionsDir = new File( dirs.getVersionsDir().getFile(), "spigot" );
        spigotVersionsDir.mkdirs();
        File versionFile = new File( spigotVersionsDir, "versions.html" );
        if ( !versionFile.exists() ) {
            versionFile.createNewFile();
            versionFile = TaskUtil.asyncDownloadFile(url,versionFile);
            LogHandler.info( " Downloaded " + FilenameUtils.getBaseName( versionFile.getName() ) );
        } else {
            LogHandler.info( versionFile.getName() + " file already exists!" );
        }

        Elements links = Jsoup.parse(versionFile, StandardCharsets.UTF_8.name()).getElementsByTag("a");
        Map<SpigotVersion, BuildInfo> map = Maps.newHashMap();

        LogHandler.info( "Start processing versions file (" + links.size() + ')' );
        for ( Element element : links ) {
            if ( element.wholeText().startsWith("..") ) // ignore non-version links
                continue;
            String text = element.wholeText(); // 1.12.2.json
            String versionText = JSON_PATTERN.matcher(text).replaceAll(""); // 1.12.2
            if ( ( versionText.split( "\\." ).length == 0 ) || NUMBERS_ONLY_EXCLUSION.matcher( versionText ).find() ) {
                continue;
            }

            SpigotVersion version = new SpigotVersion( new ComparableVersion( versionText ) );
            String versionUrl = url + text; // ../work/versions/1.12.2.json
            File verFile = new File( spigotVersionsDir, text );
            if ( !verFile.exists() ) {
                verFile.createNewFile();
                verFile = TaskUtil.asyncDownloadFile( versionUrl, verFile );
            }
            //console.setOptionalText( "Imported version: " + FilenameUtils.getBaseName( verFile.getName() ) );
            JsonReader reader = new JsonReader( new FileReader( verFile ) );
            BuildInfo buildInfo = SettingsFactory.getGson().fromJson( reader, BuildInfo.class );
            map.put( version, buildInfo );
        }
        LogHandler.info( "Processed " + map.size() + " spigot version(s)." );
        return map;
    }
}
