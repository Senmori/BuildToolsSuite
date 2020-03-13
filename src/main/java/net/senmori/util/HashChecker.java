package net.senmori.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import lombok.NonNull;
import net.senmori.project.spigot.util.VersionInfo;

public class HashChecker {
    private final File jar;
    private final VersionInfo info;
    private final HashFunction hashChecker;
    private final HashCode hash;

    public HashChecker(@NonNull HashFunction hashChecker, @NonNull File jar, @NonNull VersionInfo info) {
        this.jar = jar;
        this.info = info;
        this.hashChecker = hashChecker;
        HashCode hashCode = null;
        try {
            hashCode = hashChecker.newHasher().putBytes(Files.toByteArray(jar)).hash();
            if ( hashCode == null ) {
                hashCode = Files.asByteSource(jar).hash(hashChecker);
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        this.hash = hashCode;
    }

    public boolean checkHash() {
        return this.hash != null && this.hash.toString().equals(info.getMinecraftHash());
    }
}
