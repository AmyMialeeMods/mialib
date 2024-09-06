package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public interface MDrawContext {
    default void mialib$drawTextureFloat(Identifier texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
        this.mialib$drawTextureFloat(texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    default void mialib$drawTextureFloat(Identifier texture, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        this.mialib$drawTextureFloat(texture, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
    }

    default void mialib$drawTextureFloat(Identifier texture, float x1, float x2, float y1, float y2, float z, float regionWidth, float regionHeight, float u, float v, float textureWidth, float textureHeight) {
        this.mialib$drawTexturedQuadFloat(texture, x1, x2, y1, y2, z, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight);
    }

    default void mialib$drawTexturedQuadFloat(Identifier texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {}

    default void mialib$fillFloat(float x1, float y1, float x2, float y2, int color) {
        this.mialib$fillFloat(x1, y1, x2, y2, 0, color);
    }

    default void mialib$fillFloat(float x1, float y1, float x2, float y2, float z, int color) {
        this.mialib$fillFloat(RenderLayer.getGui(), x1, y1, x2, y2, z, color);
    }

    default void mialib$fillFloat(RenderLayer layer, float x1, float y1, float x2, float y2, int color) {
        this.mialib$fillFloat(layer, x1, y1, x2, y2, 0, color);
    }

    default void mialib$fillFloat(RenderLayer layer, float x1, float y1, float x2, float y2, float z, int color) {}
}