package xyz.amymialee.mialib.mvalues;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.client.ColorPickerOverlayScreen;
import xyz.amymialee.mialib.util.runnables.Consumer3;

import java.util.HexFormat;

public class MValueColorWidget extends MValueWidget<Integer>
{
	public MValueColorWidget(int x, int y, @NotNull MValue<Integer> value)
	{
		super(x, y, value);
	}
	
	@Override
	public void onClick(double mouseX, double mouseY)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (mouseX < getX() + 18)
			value.send(value.type.defaultValue);
		else
			client.setScreen(new ColorPickerOverlayScreen(client.currentScreen, this, value.type.defaultValue));
	}
	
	@Override
	protected void renderWidget(@NotNull DrawContext context, int mouseX, int mouseY, float delta)
	{
		var client = MinecraftClient.getInstance();
		if (client == null || client.world == null) return;
		var matrices = context.getMatrices();
		var scroll = this.scroll + this.velocity * delta;
		this.scissorContains = context.scissorContains(mouseX, mouseY);
		this.hovered = this.scissorContains
							   && mouseX >= this.getX()
							   && mouseY >= this.getY() - scroll
							   && mouseX < this.getX() + this.width
							   && mouseY < this.getY() + this.height - scroll;
		var textures = this.value.clientSide ? CLIENT_BUTTON_TEXTURES : BUTTON_TEXTURES;
		context.mialib$drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures.get(true, this.hovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
		context.mialib$drawGuiTexture(RenderPipelines.GUI_TEXTURED, textures.get(true, this.hovered), this.getX() + 18, this.getY(), this.width - 18, this.height, 0xFFFFFFFF);
		var valueText = Text.literal("#" + StringUtils.leftPad(this.value.type.getValueAsString(this.value), 6, '0'));
		context.fill(getX() + width - client.textRenderer.getWidth(valueText) + 6, getY() + 1, getX() + width - 1, getY() + height - 1, ColorHelper.withAlpha(255, HexFormat.fromHexDigits(value.getValueAsString())));
		context.drawItem(this.value.getStack(), this.getX() + 1, this.getY() + 1);
		final Consumer3<Float, Float, Float> moveAndScale = (x, y, s) -> {
			matrices.pushMatrix();
			matrices.translate(x, y);
			matrices.scale(s);
		};
		var scale = 0.7f;
		moveAndScale.accept(this.getX() + this.getWidth() - 3f, this.getY() + this.getHeight() / 2f, scale);
		int color = HexFormat.fromHexDigits(value.getValueAsString());
		float brightness = (ColorHelper.getRedFloat(color) + ColorHelper.getGreenFloat(color) + ColorHelper.getBlueFloat(color)) / 3f;
		int valueX = -client.textRenderer.getWidth(valueText), valueY = -client.textRenderer.fontHeight / 2;
		if(1f - brightness > 0.4) //outline
		{
			int outlineColor = 1f - brightness > 0.5f ? 0xFF000000 : 0xFFFFFFFF;
			context.drawText(client.textRenderer, valueText, valueX - 1, valueY, outlineColor, false);
			context.drawText(client.textRenderer, valueText, valueX + 1, valueY, outlineColor, false);
			context.drawText(client.textRenderer, valueText, valueX, valueY - 1, outlineColor, false);
			context.drawText(client.textRenderer, valueText, valueX, valueY + 1, outlineColor, false);
		}
		context.drawText(client.textRenderer, valueText, valueX, valueY, 0xFFFFFFFF - color, false);
		matrices.popMatrix();
		moveAndScale.accept(this.getX() + 21f, this.getY() + this.getHeight() / 2f, scale);
		var wrapLines = client.textRenderer.wrapLines(this.getMessage(), 165 - client.textRenderer.getWidth(valueText));
		for (var i = 0; i < wrapLines.size(); i++) {
			var text = wrapLines.get(i);
			var y = -(wrapLines.size()) * client.textRenderer.fontHeight * .5 + client.textRenderer.fontHeight * i;
			context.drawText(client.textRenderer, text, 0, (int) y, 0xFFFFFFFF, true);
		}
		matrices.popMatrix();
	}
}
