package xyz.amymialee.mialib.config;

import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Mialib Properties are read from %APPDATA%/.mialib/mialib.yaml
 * These properties are used to store user preferences and other settings across instances, and are typically very optional.
 */
public class MiaLibProperties {
    private static final List<MProperty<?>> properties = new ArrayList<>();
    public static MBooleanProperty eulaAccepted = new MBooleanProperty("eula_accepted", false);

    public static void loadConfig() {
        var mialibFile = getMialibFile("mialib.yaml");
        try (var reader = new BufferedReader(new FileReader(mialibFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (var property : properties) {
                    if (line.startsWith(property.key + ":")) {
                        property.load(line.split(":")[1].trim());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            MiaLib.LOGGER.info("Failed to find mialib properties file {}", mialibFile, e);
        } catch (Exception e) {
            MiaLib.LOGGER.warn("Failed to load mialib properties file {}", mialibFile, e);
        }
    }

    public static @NotNull Path getMialibPath(String fileName) {
        return getMialibFile(fileName).toPath();
    }

    public static @NotNull File getMialibFile(String fileName) {
        String workingDirectory;
        if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
            workingDirectory = System.getenv("AppData");
        } else {
            workingDirectory = System.getProperty("user.home") + "/Library/Application Support";
        }
        return new File(workingDirectory, ".mialib/" + fileName);
    }

    public static class MBooleanProperty extends MProperty<Boolean> {
        public MBooleanProperty(String key, Boolean defaultValue) {
            super(key, defaultValue);
            properties.add(this);
        }

        @Override
        protected void load(String value) {
            try {
                this.internalOnlySet(Boolean.parseBoolean(value));
            } catch (Exception e) {
                MiaLib.LOGGER.warn("Failed to load mialib property {} from value {}", this.key, value, e);
            }
        }
    }

    private static abstract class MProperty<T> {
        protected final String key;
        private T value;

        public MProperty(String key, T defaultValue) {
            this.key = key;
            this.value = defaultValue;
        }

        public T get() {
            return this.value;
        }

        protected void internalOnlySet(T value) {
            this.value = value;
        }

        protected abstract void load(String value);
    }
}