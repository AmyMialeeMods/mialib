package xyz.amymialee.mialib.mixin.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.MDir;

import java.nio.file.Path;
import java.util.Properties;

@Mixin(ServerPropertiesHandler.class)
public class ServerPropertiesHandlerMixin {
    @WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/ServerPropertiesHandler;loadProperties(Ljava/nio/file/Path;)Ljava/util/Properties;"))
    private static Properties mialib$defaultProperties(@NotNull Path path, @NotNull Operation<Properties> original) {
        if (path.toFile().exists()) return original.call(path);
        Mialib.LOGGER.info("No server properties file found at {}, loading default properties.", path);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            Mialib.LOGGER.info("Development environment detected, using default dev properties.");
            var defaultPath = MDir.getMialibPath("devserver.properties");
            if (defaultPath.toFile().exists()) return original.call(defaultPath);
            Mialib.LOGGER.info("No default dev properties file found at {}, trying default properties.", defaultPath);
        }
        var defaultPath = MDir.getMialibPath("defaultserver.properties");
        if (defaultPath.toFile().exists()) return original.call(defaultPath);
        Mialib.LOGGER.info("No default server properties file found at {}, skipping.", defaultPath);
        return original.call(path);
    }
}