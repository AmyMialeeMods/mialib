package xyz.amymialee.mialib.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record FloatingColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, int col1, int col2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
    public FloatingColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float x0, float y0, float x1, float y1, int col1, int col2, @Nullable ScreenRect scissorArea) {
        this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
    }

    @Override
    public void setupVertices(@NotNull VertexConsumer vertices, float depth) {
        vertices.vertex(this.pose(), this.x0(), this.y0(), depth).color(this.col1());
        vertices.vertex(this.pose(), this.x0(), this.y1(), depth).color(this.col2());
        vertices.vertex(this.pose(), this.x1(), this.y1(), depth).color(this.col2());
        vertices.vertex(this.pose(), this.x1(), this.y0(), depth).color(this.col1());
    }

    @Nullable
    private static ScreenRect createBounds(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        var screenRect = new ScreenRect((int) x0, (int) y0, (int) (x1 - x0), (int) (y1 - y0)).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}