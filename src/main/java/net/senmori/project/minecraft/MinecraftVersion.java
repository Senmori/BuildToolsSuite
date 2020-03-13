package net.senmori.project.minecraft;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.senmori.versioning.ComparableVersion;

@Getter
@AllArgsConstructor
public class MinecraftVersion {
    private final ComparableVersion version;
    private final ReleaseType releaseType;
    private final LocalDateTime releaseDate;
    private final String SHA_1;
    private final String serverDownloadURL;

    @Override
    public String toString() {
        return version.toString();
    }
}
