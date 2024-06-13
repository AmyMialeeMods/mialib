package xyz.amymialee.mialib.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public interface MDir {
    static @NotNull Path getMialibPath(String fileName) {
        return getMialibFile(fileName).toPath();
    }

    /**
     * Get a file in the mialib appdata directory.
     */
    static @NotNull File getMialibFile(String fileName) {
        String workingDirectory;
        if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
            workingDirectory = System.getenv("AppData");
        } else {
            workingDirectory = System.getProperty("user.home") + "/Library/Application Support";
        }
        return new File(workingDirectory, ".mialib/" + fileName);
    }
}