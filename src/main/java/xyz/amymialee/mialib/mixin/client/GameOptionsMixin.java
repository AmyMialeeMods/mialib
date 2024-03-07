package xyz.amymialee.mialib.mixin.client;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.config.DefaultOptionsOverride;

import java.io.File;
import java.io.StringReader;
import java.util.function.Function;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow protected abstract void accept(GameOptions.Visitor visitor);

    @WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Ljava/io/File;exists()Z"))
    private boolean mialib$loadDefaultOptions(File instance, @NotNull Operation<Boolean> original) {
        var exists = original.call(instance);
        if (!exists) {
            DefaultOptionsOverride.loadConfig();
            this.accept(new GameOptions.Visitor() {
                @Nullable
                private String find(String key) {
                    return DefaultOptionsOverride.hasOverride(key) ? DefaultOptionsOverride.getOverride(key) : null;
                }

                @Override
                public <T> void accept(String key, SimpleOption<T> option) {
                    var string = this.find(key);
                    if (string != null) {
                        var jsonReader = new JsonReader(new StringReader(string.isEmpty() ? "\"\"" : string));
                        var dataResult = option.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseReader(jsonReader));
                        dataResult.error().ifPresent(partialResult -> MiaLib.LOGGER.error("Error parsing default option value " + string + " for option " + option + ": " + partialResult.message()));
                        dataResult.result().ifPresent(option::setValue);
                    }
                }

                @Override
                public int visitInt(String key, int current) {
                    var string = this.find(key);
                    if (string != null) {
                        try {
                            return Integer.parseInt(string);
                        } catch (NumberFormatException e) {
                            MiaLib.LOGGER.warn("Invalid integer value for default option {} = {}", key, string, e);
                        }
                    }
                    return current;
                }

                @Override
                public boolean visitBoolean(String key, boolean current) {
                    var string = this.find(key);
                    return string != null ? GameOptions.isTrue(string) : current;
                }

                @Override
                public String visitString(String key, String current) {
                    var string = this.find(key);
                    return string != null ? string : current;
                }

                @Override
                public float visitFloat(String key, float current) {
                    var string = this.find(key);
                    if (string == null) {
                        return current;
                    } else if (GameOptions.isTrue(string)) {
                        return 1.0F;
                    } else if (GameOptions.isFalse(string)) {
                        return 0.0F;
                    } else {
                        try {
                            return Float.parseFloat(string);
                        } catch (NumberFormatException e) {
                            MiaLib.LOGGER.warn("Invalid floating point value for default option {} = {}", key, string, e);
                            return current;
                        }
                    }
                }

                @Override
                public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                    var string = this.find(key);
                    return string == null ? current : decoder.apply(string);
                }
            });
        }
        return exists;
    }
}