package xyz.amymialee.mialib.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class CodecParticleType<T extends ParticleEffect> extends ParticleType<T> {
    private final MapCodec<T> codec;
    private final PacketCodec<? super RegistryByteBuf, T> packetCodec;

    public CodecParticleType(boolean alwaysShow, MapCodec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        super(alwaysShow);
        this.codec = codec;
        this.packetCodec = packetCodec;
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