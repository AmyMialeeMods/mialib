package xyz.amymialee.mialib.util;

import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import xyz.amymialee.mialib.Mialib;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Get a file or path in the mialib appdata directory.
 */
public interface MDir {
    static final @Nullable String MIALIB_DIR_OVERRIDE = System.getProperty("mialib.mdir");

    static @NotNull Path getMialibPath(Path relative) {
        return MDirImpl.INSTANCE.path.resolve(relative);
    }

    static @NotNull Path getMialibPath(String fileName) {
        return MDirImpl.INSTANCE.path.resolve(fileName);
    }

    static @NotNull Path getMialibPath(String first, String... more) {
        return MDirImpl.INSTANCE.path.resolve(Path.of(first, more));
    }

    static @NotNull File getMialibFile(String fileName) {
        return getMialibPath(fileName).toFile();
    }

    static class MDirImpl {
        private static final MDirImpl INSTANCE = new MDirImpl();
        private final Path path;

        public MDirImpl() {
            if (MIALIB_DIR_OVERRIDE != null) {
                Mialib.LOGGER.info("mialib directory overridden, setting to {}", MIALIB_DIR_OVERRIDE);
                this.path = Paths.get(MIALIB_DIR_OVERRIDE);
                return;
            }
            var userHome = System.getProperty("user.home");
            var fallback = Path.of(userHome, ".mialib");
            this.path = switch (Util.getOperatingSystem()) {
                case WINDOWS -> {
                    String appData = System.getenv("AppData");
                    if (appData == null) {
                        Mialib.LOGGER.warn("No value for AppData env var even though we're on windows, falling back to {}", fallback);
                        yield fallback;
                    }
                    yield Path.of(appData, ".mialib");
                }
                case OSX -> Path.of(userHome, "Library", "Application Support", "mialib");
                case LINUX -> {
                    String xdgData = System.getenv("XDG_DATA_HOME");
                    var localShare = Path.of(userHome, ".local", "share", "mialib");
                    if (xdgData == null) {
                        Mialib.LOGGER.warn("No value for XDG_DATA_HOME, falling back to {}", localShare);
                        yield localShare;
                    }
                    yield Path.of(xdgData, "mialib");
                }
                case UNKNOWN, SOLARIS -> fallback;
            };
            Mialib.LOGGER.info("Set mialib directory to {}", this.path);
        }
    }
}
