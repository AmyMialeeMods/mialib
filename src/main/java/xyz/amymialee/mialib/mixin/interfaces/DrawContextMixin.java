package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.amymialee.mialib.util.interfaces.MDrawContext;

import java.util.function.*;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements MDrawContext {
    @Shadow @Final private MatrixStack matrices;
    @Shadow @Final private VertexConsumerProvider.Immediate vertexConsumers;

    @Override
    public void mialib$drawTexture(@NotNull Function<Identifier, RenderLayer> renderLayers, Identifier sprite, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2, int color) {
	    var renderLayer = renderLayers.apply(sprite);
	    var matrix4f = this.matrices.peek().getPositionMatrix();
	    var vertexConsumer = this.vertexConsumers.getBuffer(renderLayer);
        vertexConsumer.vertex(matrix4f, x1, y1, 0.0F).texture(u1, v1).color(color);
        vertexConsumer.vertex(matrix4f, x1, y2, 0.0F).texture(u1, v2).color(color);
        vertexConsumer.vertex(matrix4f, x2, y2, 0.0F).texture(u2, v2).color(color);
        vertexConsumer.vertex(matrix4f, x2, y1, 0.0F).texture(u2, v1).color(color);
    }

    @Override
    public void mialib$fill(RenderLayer layer, float x1, float y1, float x2, float y2, float z, int color) {
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
    }
}