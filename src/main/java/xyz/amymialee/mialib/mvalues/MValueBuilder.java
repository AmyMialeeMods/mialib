package xyz.amymialee.mialib.mvalues;

import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.world.entity.player.Player;

public class MValueBuilder<T> {
    private final Identifier id;
    private final MValueType<T> type;
    private String translationKey;
    private PermissionCheck permissionCheck = Commands.LEVEL_GAMEMASTERS;
    private Predicate<Player> canChange = (_) -> true;
    private Consumer<MValue<T>> onChange = (_) -> {};

    protected MValueBuilder(@NonNull Identifier id, MValueType<T> type) {
        this.id = id;
        this.type = type;
        this.translationKey = "mvalue.%s.%s".formatted(id.getNamespace(), id.getPath());
    }

    public MValueBuilder<T> translationKey(String translationKey) {
        this.translationKey = translationKey;
        return this;
    }

    public MValueBuilder<T> permissionLevel(PermissionCheck permissionLevel) {
        this.permissionCheck = permissionLevel;
        return this;
    }

    public MValueBuilder<T> predicate(Predicate<Player> predicate) {
        this.canChange = predicate;
        return this;
    }

    public MValueBuilder<T> onChange(Consumer<MValue<T>> onChange) {
        this.onChange = onChange;
        return this;
    }

    public MValue<T> build() {
        var value = new MValue<>(this.id, this.translationKey, this.type, this.permissionCheck, this.canChange, this.onChange);
        MVServerManager.register(value);
        value.type.registerCommand(value);
        return value;
    }
}