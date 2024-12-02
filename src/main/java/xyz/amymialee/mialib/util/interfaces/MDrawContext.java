package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public @SuppressWarnings("unused") interface MDrawContext {
    default void mialib$drawTexture(Identifier sprite, float x, float y, float width, float height, float textureWidth, float textureHeight) {
        this.mialib$drawTexture(RenderLayer::getGuiTextured, sprite, x, x + width, y, y + height, 0, width / textureWidth, 0, height / textureHeight, 0xFFFFFFFF);
    }

    default void mialib$drawTexture(Identifier sprite, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        this.mialib$drawTexture(RenderLayer::getGuiTextured, sprite, x, x + width, y, y + height, u / textureWidth, (u + width) / textureWidth, v / textureHeight, (v + height) / textureHeight, 0xFFFFFFFF);
    }

    default void mialib$drawTexture(Identifier sprite, float x, float y, float z, float width, float height, float textureWidth, float textureHeight) {
        this.mialib$drawTexture(RenderLayer::getGuiTextured, sprite, x, x + width, y, y + height, 0, width / textureWidth, 0, height / textureHeight, 0xFFFFFFFF);
    }

    default void mialib$drawTexture(Identifier sprite, float x, float y, float z, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        this.mialib$drawTexture(RenderLayer::getGuiTextured, sprite, x, x + width, y, y + height, u / textureWidth, (u + width) / textureWidth, v / textureHeight, (v + height) / textureHeight, 0xFFFFFFFF);
    }
    
    default void mialib$drawTexture(Identifier sprite, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2, int color) {
        this.mialib$drawTexture(RenderLayer::getGuiTextured, sprite, x1, x2, y1, y2, u1, u2, v1, v2, color);
    }

    default void mialib$drawTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2, int color) {
        this.mialib$drawTexture(renderLayers, sprite, x1, x2, y1, y2, 0, u1, u2, v1, v2, color);
    }

    default void mialib$drawTexture(Function<Identifier, RenderLayer> renderLayers, Identifier sprite, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2, int color) {}

    default void mialib$fill(float x1, float y1, float x2, float y2, int color) {
        this.mialib$fill(x1, y1, x2, y2, 0, color);
    }

    default void mialib$fill(float x1, float y1, float x2, float y2, float z, int color) {
        this.mialib$fill(RenderLayer.getGui(), x1, y1, x2, y2, z, color);
    }

    default void mialib$fill(RenderLayer layer, float x1, float y1, float x2, float y2, int color) {
        this.mialib$fill(layer, x1, y1, x2, y2, 0, color);
    }

    default void mialib$fill(RenderLayer layer, float x1, float y1, float x2, float y2, float z, int color) {}
}