package xyz.amymialee.mialib.config;

import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.MDir;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Mialib Properties are read from %APPDATA%/.mialib/mialib.yaml
 * These properties are used to store user preferences and other settings across instances, and are typically very optional.
 * They will never be written to by the mod and can only be changed manually by the user.
 */
public class MialibProperties {
    private static final List<MProperty<?>> properties = new ArrayList<>();
    private static boolean loaded = false;
    public static MBooleanProperty eulaAccepted = new MBooleanProperty("eula_accepted", false);
    public static MBooleanProperty skipNarrator = new MBooleanProperty("skip_narrator", false);

    /**
     * Load the mialib properties from the mialib.yaml file in the %APPDATA%/.mialib/ directory.
     */
    public static void loadConfig() {
        loaded = true;
        var mialibFile = MDir.getMialibFile("mialib.yaml");
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
                MiaLib.LOGGER.warn("Failed to load mialib boolean property {} from value {}", this.key, value, e);
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
            if (!loaded) loadConfig();
            return this.value;
        }

        protected void internalOnlySet(T value) {
            this.value = value;
        }

        protected abstract void load(String value);
    }
}