package xyz.amymialee.mialib.config;

import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.MDir;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Default Options Overrides are read from %APPDATA%/.mialib/defaultoptions.yaml
 * These will override unset options, allowing for skipping basic setup steps.
 * They will never be written to by the mod and can only be changed manually by the user.
 */
public class DefaultOptionsOverride {
    private static final Map<String, String> defaultOverrides = new HashMap<>();

    /**
     * Load the default options from the defaultoptions.yaml file in the %APPDATA%/.mialib/ directory.
     */
    public static void loadConfig() {
        var mialibFile = MDir.getMialibFile("defaultoptions.yaml");
        try (var reader = new BufferedReader(new FileReader(mialibFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                var split = line.split(":");
                if (split.length < 2) {
                    Mialib.LOGGER.warn("Failed to parse default options line {}, too few :", line);
                } else if (split.length == 2) {
                    defaultOverrides.put(split[0].trim(), split[1].trim());
                } else {
                    Mialib.LOGGER.warn("Failed to parse default options line {}, too many :", line);
                }
            }
        } catch (FileNotFoundException ignored) {
            Mialib.LOGGER.info("Failed to find default options file {}", mialibFile);
        } catch (Exception ignored) {
            Mialib.LOGGER.warn("Failed to load default options file {}", mialibFile);
        }
    }

    public static boolean hasOverride(String key) {
        return defaultOverrides.containsKey(key);
    }

    public static String getOverride(String key) {
        return defaultOverrides.get(key);
    }
}