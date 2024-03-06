package xyz.amymialee.mialib.config;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public interface MialibDir {
    static @NotNull Path getMialibPath(String fileName) {
        return getMialibFile(fileName).toPath();
    }

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