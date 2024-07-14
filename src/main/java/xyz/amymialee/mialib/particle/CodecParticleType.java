package xyz.amymialee.mialib.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CodecParticleType<T extends ParticleEffect> extends ParticleType<T> {
    private final MapCodec<T> codec;
    private final PacketCodec<? super RegistryByteBuf, T> packetCodec;

    public CodecParticleType(boolean alwaysShow, MapCodec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        super(alwaysShow);
        this.codec = codec;
        this.packetCodec = packetCodec;
    }

    public CodecParticleType(boolean alwaysShow, @NotNull Function<ParticleType<T>, MapCodec<T>> codecGetter, @NotNull Function<ParticleType<T>, PacketCodec<? super RegistryByteBuf, T>> packetCodecGetter) {
        super(alwaysShow);
        this.codec = codecGetter.apply(this);
        this.packetCodec = packetCodecGetter.apply(this);
    }

    @Override
    public MapCodec<T> getCodec() {
        return this.codec;
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
        return this.packetCodec;
    }
}