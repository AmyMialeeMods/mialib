package xyz.amymialee.mialib.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

/**
 * Get a file or path in the mialib appdata directory.
 */
public interface MDir {
    static @NotNull Path getMialibPath(String fileName) {
        return getMialibFile(fileName).toPath();
    }

    static @NotNull File getMialibFile(String fileName) {
        if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
            return new File(System.getenv("AppData"), ".mialib/" + fileName);
        }
        return new File(System.getProperty("user.home") + "/.local/share", "mialib/" + fileName);
    }
}
