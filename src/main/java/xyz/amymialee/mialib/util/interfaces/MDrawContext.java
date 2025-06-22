package xyz.amymialee.mialib.util.interfaces;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public @SuppressWarnings("unused") interface MDrawContext {
    default void mialib$fill(RenderPipeline pipeline, TextureSetup textureSetup, float x1, float y1, float x2, float y2, int color, @Nullable Integer color2) {}
    default void mialib$drawTextWithBackground(TextRenderer textRenderer, Text text, float x, float y, float width, int color) {}
    default void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float width, float height, int color) {}
    default void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float textureWidth, float textureHeight, float u, float v, float x, float y, float width, float height, int color) {}
    default void mialib$drawTexturedQuad(RenderPipeline pipeline, Identifier sprite, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2, int color) {}
    default void mialib$drawTexturedQuad(RenderPipeline pipeline, GpuTextureView texture, float x1, float y1, float x2, float y2, float u1, float u2, float v1, float v2, int color) {}

    default void mialib$drawHorizontalLine(float x1, float x2, float y, int color) {
        if (x2 < x1) {
            var i = x1;
            x1 = x2;
            x2 = i;
        }
        this.mialib$fill(x1, y, x2 + 1, y + 1, color);
    }

    default void mialib$drawVerticalLine(float x, float y1, float y2, int color) {
        if (y2 < y1) {
            var i = y1;
            y1 = y2;
            y2 = i;
        }
        this.mialib$fill(x, y1 + 1, x + 1, y2, color);
    }

    default void mialib$fill(float x1, float y1, float x2, float y2, int color) {
        this.mialib$fill(RenderPipelines.GUI, x1, y1, x2, y2, color);
    }

    default void mialib$fill(RenderPipeline pipeline, float x1, float y1, float x2, float y2, int color) {
        if (x1 < x2) {
            var i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            var i = y1;
            y1 = y2;
            y2 = i;
        }
        this.mialib$fill(pipeline, TextureSetup.empty(), x1, y1, x2, y2, color, null);
    }

    default void mialib$fillGradient(float startX, float startY, float endX, float endY, int colorStart, int colorEnd) {
        this.mialib$fill(RenderPipelines.GUI, TextureSetup.empty(), startX, startY, endX, endY, colorStart, colorEnd);
    }

    default void mialib$fill(RenderPipeline pipeline, TextureSetup textureSetup, float x1, float y1, float x2, float y2) {
        this.mialib$fill(pipeline, textureSetup, x1, y1, x2, y2, -1, null);
    }

    default void mialib$drawBorder(float x, float y, float width, float height, int color) {
        this.mialib$fill(x, y, x + width, y + 1, color);
        this.mialib$fill(x, y + height - 1, x + width, y + height, color);
        this.mialib$fill(x, y + 1, x + 1, y + height - 1, color);
        this.mialib$fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    default void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float width, float height) {
        this.mialib$drawGuiTexture(pipeline, sprite, x, y, width, height, -1);
    }

    default void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float width, float height, float alpha) {
        this.mialib$drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.withAlpha(alpha, Colors.WHITE));
    }

    default void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float textureWidth, float textureHeight, float u, float v, float x, float y, float width, float height) {
        this.mialib$drawGuiTexture(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, -1);
    }

    default void mialib$drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, float x, float y, float width, float height) {
        this.mialib$drawSpriteStretched(pipeline, sprite, x, y, width, height, -1);
    }

    default void mialib$drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, float x, float y, float width, float height, int color) {
        if (width != 0 && height != 0) this.mialib$drawTexturedQuad(pipeline, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), color);
    }

    default void mialib$drawSpriteRegion(RenderPipeline pipeline, Sprite sprite, float textureWidth, float textureHeight, float u, float v, float x, float y, float width, float height, int color) {
        if (width != 0 && height != 0) this.mialib$drawTexturedQuad(pipeline, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getFrameU((float)u / textureWidth), sprite.getFrameU((float)(u + width) / textureWidth), sprite.getFrameV((float)v / textureHeight), sprite.getFrameV((float)(v + height) / textureHeight), color);
    }

    default void mialib$drawSpriteNineSliced(RenderPipeline pipeline, Sprite sprite, Scaling.@NotNull NineSlice nineSlice, float x, float y, float width, float height, int color) {
        var border = nineSlice.border();
        var i = Math.min(border.left(), width / 2);
        var j = Math.min(border.right(), width / 2);
        var k = Math.min(border.top(), height / 2);
        var l = Math.min(border.bottom(), height / 2);
        if (width == nineSlice.width() && height == nineSlice.height()) {
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, height, color);
        } else if (height == nineSlice.height()) {
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, i, height, color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x + i, y, width - j - i, height, i, 0, nineSlice.width() - j - i, nineSlice.height(), nineSlice.width(), nineSlice.height(), color);
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, 0, x + width - j, y, j, height, color);
        } else if (width == nineSlice.width()) {
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, k, color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x, y + k, width, height - l - k, 0, k, nineSlice.width(), nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - l, x, y + height - l, width, l, color);
        } else {
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, i, k, color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x + i, y, width - j - i, k, i, 0, nineSlice.width() - j - i, k, nineSlice.width(), nineSlice.height(), color);
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, 0, x + width - j, y, j, k, color);
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - l, x, y + height - l, i, l, color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x + i, y + height - l, width - j - i, l, i, nineSlice.height() - l, nineSlice.width() - j - i, l, nineSlice.width(), nineSlice.height(), color);
            this.mialib$drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, nineSlice.height() - l, x + width - j, y + height - l, j, l, color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x, y + k, i, height - l - k, 0, k, i, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x + i, y + k, width - j - i, height - l - k, i, k, nineSlice.width() - j - i, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
            this.mialib$drawInnerSprite(pipeline, nineSlice, sprite, x + width - j, y + k, j, height - l - k, nineSlice.width() - j, k, j, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
        }
    }

    default void mialib$drawInnerSprite(RenderPipeline pipeline, Scaling.NineSlice nineSlice, Sprite sprite, float x, float y, float width, float height, float u, float v, float tileWidth, float tileHeight, float textureWidth, float textureHeight, int color) {
        if (width <= 0 || height <= 0) return;
        if (nineSlice.stretchInner()) {
            this.mialib$drawTexturedQuad(pipeline, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getFrameU((float)u / textureWidth), sprite.getFrameU((float)(u + tileWidth) / textureWidth), sprite.getFrameV((float)v / textureHeight), sprite.getFrameV((float)(v + tileHeight) / textureHeight), color);
        } else this.mialib$drawSpriteTiled(pipeline, sprite, x, y, width, height, u, v, tileWidth, tileHeight, textureWidth, textureHeight, color);
    }

    default void mialib$drawSpriteTiled(RenderPipeline pipeline, Sprite sprite, float x, float y, float width, float height, float u, float v, float tileWidth, float tileHeight, float textureWidth, float textureHeight, int color) {
        if (width <= 0 || height <= 0) return;
        if (tileWidth > 0 && tileHeight > 0) {
            for (var i = 0; i < width; i += tileWidth) {
                var j = Math.min(tileWidth, width - i);
                for (var k = 0; k < height; k += tileHeight) {
                    var l = Math.min(tileHeight, height - k);
                    this.mialib$drawSpriteRegion(pipeline, sprite, textureWidth, textureHeight, u, v, x + i, y + k, j, l, color);
                }
            }
        } else throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight);
    }

    default void mialib$drawTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight, int color) {
        this.mialib$drawTexture(pipeline, sprite, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color);
    }

    default void mialib$drawTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        this.mialib$drawTexture(pipeline, sprite, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }

    default void mialib$drawTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float u, float v, float width, float height, float regionWidth, float regionHeight, float textureWidth, float textureHeight) {
        this.mialib$drawTexture(pipeline, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, -1);
    }

    default void mialib$drawTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float u, float v, float width, float height, float regionWidth, float regionHeight, float textureWidth, float textureHeight, int color) {
        this.mialib$drawTexturedQuad(pipeline, sprite, x, x + width, y, y + height, (u + 0.0F) / textureWidth, (u + regionWidth) / textureWidth, (v + 0.0F) / textureHeight, (v + regionHeight) / textureHeight, color);
    }

    default void mialib$drawTexturedQuad(Identifier sprite, float x1, float y1, float x2, float y2, float u1, float u2, float v1, float v2) {
        this.mialib$drawTexturedQuad(RenderPipelines.GUI_TEXTURED, sprite, x1, x2, y1, y2, u1, u2, v1, v2, -1);
    }
}