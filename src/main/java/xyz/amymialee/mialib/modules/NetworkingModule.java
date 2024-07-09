package xyz.amymialee.mialib.modules;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import xyz.amymialee.mialib.networking.AttackingPayload;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.networking.MValuePayload;
import xyz.amymialee.mialib.networking.UsingPayload;

public interface NetworkingModule {
    static void init() {
        PayloadTypeRegistry.playC2S().register(AttackingPayload.ID, AttackingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(UsingPayload.ID, UsingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MValuePayload.ID, MValuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FloatyPayload.ID, FloatyPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MValuePayload.ID, MValuePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AttackingPayload.ID, new AttackingPayload.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(UsingPayload.ID, new UsingPayload.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(MValuePayload.ID, new MValuePayload.ServerReceiver());
    }
}