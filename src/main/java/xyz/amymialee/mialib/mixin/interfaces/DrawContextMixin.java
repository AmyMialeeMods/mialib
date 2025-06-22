package xyz.amymialee.mialib.mixin.interfaces;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.amymialee.mialib.client.FloatingColoredQuadGuiElementRenderState;
import xyz.amymialee.mialib.client.FloatingTexturedQuadGuiElementRenderState;
import xyz.amymialee.mialib.util.interfaces.MDrawContext;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements MDrawContext {
    @Shadow @Final public GuiRenderState state;
    @Shadow @Final private Matrix3x2fStack matrices;
    @Shadow @Final public DrawContext.ScissorStack scissorStack;
    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract void enableScissor(int x1, int y1, int x2, int y2);
    @Shadow public abstract void disableScissor();
    @Shadow @Final private GuiAtlasManager guiAtlasManager;
    @Shadow public abstract void drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow);

    @Override
    public void mialib$fill(RenderPipeline pipeline, TextureSetup textureSetup, float x1, float y1, float x2, float y2, int color, @Nullable Integer color2) {
        this.state.addSimpleElement(new FloatingColoredQuadGuiElementRenderState(pipeline, textureSetup, new Matrix3x2f(this.matrices), x1, y1, x2, y2, color, color2 != null ? color2 : color, this.scissorStack.peekLast()));
    }

    @Override
    public void mialib$drawTextWithBackground(TextRenderer textRenderer, Text text, float x, float y, float width, int color) {
        var i = this.client.options.getTextBackgroundColor(0.0F);
        if (i != 0) this.mialib$fill(x - 2, y - 2, x + width + 2, y + 9 + 2, ColorHelper.mix(i, color));
        this.matrices.pushMatrix();
        this.matrices.translate(x, y);
        this.drawText(textRenderer, text, 0, 0, color, true);
        this.matrices.popMatrix();
    }

    @Override
    public void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float x, float y, float width, float height, int color) {
        var sprite2 = this.guiAtlasManager.getSprite(sprite);
        var scaling = this.guiAtlasManager.getScaling(sprite2);
        if (scaling instanceof Scaling.Stretch) {
            this.mialib$drawSpriteStretched(pipeline, sprite2, x, y, width, height, color);
        } else if (scaling instanceof Scaling.Tile(var width1, var height1)) {
            this.mialib$drawSpriteTiled(pipeline, sprite2, x, y, width, height, 0, 0, width1, height1, width1, height1, color);
        } else if (scaling instanceof Scaling.NineSlice nineSlice) {
            this.mialib$drawSpriteNineSliced(pipeline, sprite2, nineSlice, x, y, width, height, color);
        }
    }

    @Override
    public void mialib$drawGuiTexture(RenderPipeline pipeline, Identifier sprite, float textureWidth, float textureHeight, float u, float v, float x, float y, float width, float height, int color) {
        var sprite2 = this.guiAtlasManager.getSprite(sprite);
        var scaling = this.guiAtlasManager.getScaling(sprite2);
        if (scaling instanceof Scaling.Stretch) {
            this.mialib$drawSpriteRegion(pipeline, sprite2, textureWidth, textureHeight, u, v, x, y, width, height, color);
        } else {
            this.enableScissor((int) x, (int) y, (int) (x + width), (int) (y + height));
            this.mialib$drawGuiTexture(pipeline, sprite, x - u, y - v, textureWidth, textureHeight, color);
            this.disableScissor();
        }
    }

    @Override
    public void mialib$drawTexturedQuad(RenderPipeline pipeline, Identifier sprite, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2, int color) {
        var gpuTextureView = this.client.getTextureManager().getTexture(sprite).getGlTextureView();
        this.mialib$drawTexturedQuad(pipeline, gpuTextureView, x1, y1, x2, y2, u1, u2, v1, v2, color);
    }

    @Override
    public void mialib$drawTexturedQuad(RenderPipeline pipeline, GpuTextureView texture, float x1, float y1, float x2, float y2, float u1, float u2, float v1, float v2, int color) {
        this.state.addSimpleElement(new FloatingTexturedQuadGuiElementRenderState(pipeline, TextureSetup.withoutGlTexture(texture), new Matrix3x2f(this.matrices), x1, y1, x2, y2, u1, u2, v1, v2, color, this.scissorStack.peekLast()));
    }
}