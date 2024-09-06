package xyz.amymialee.mialib.mixin.interfaces;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.amymialee.mialib.util.interfaces.MDrawContext;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements MDrawContext {
    @Shadow @Final private MatrixStack matrices;
    @Shadow @Final private VertexConsumerProvider.Immediate vertexConsumers;
    @Shadow @Deprecated protected abstract void tryDraw();

    @Override
    public void mialib$drawTexturedQuadFloat(Identifier texture, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        var matrix4f = this.matrices.peek().getPositionMatrix();
        var bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture(u2, v1);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    @Override
    public void mialib$fillFloat(RenderLayer layer, float x1, float y1, float x2, float y2, float z, int color) {
        var matrix4f = this.matrices.peek().getPositionMatrix();
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
        var vertexConsumer = this.vertexConsumers.getBuffer(layer);
        vertexConsumer.vertex(matrix4f, x1, y1, z).color(color);
        vertexConsumer.vertex(matrix4f, x1, y2, z).color(color);
        vertexConsumer.vertex(matrix4f, x2, y2, z).color(color);
        vertexConsumer.vertex(matrix4f, x2, y1, z).color(color);
        this.tryDraw();
    }
}