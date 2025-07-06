package xyz.amymialee.mialib.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.mvalues.MValueColorWidget;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class ColorPickerOverlayScreen extends Screen
{
	final Screen parent;
	final MValueColorWidget source;
	final int defaultValue;
	ColorPickerWidget colorPicker;
	
	public ColorPickerOverlayScreen(Screen parent, MValueColorWidget source, int defaultValue)
	{
		super(source.value.getText());
		this.parent = parent;
		this.source = source;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public void clearAndInit()
	{
		super.clearAndInit();
	}
	
	@Override
	protected void init()
	{
		super.init();
		colorPicker = addDrawableChild(new ColorPickerWidget(width / 2 - 50, height / 2 - 50, getTitle(), colorPicker == null ? source.value.get() : colorPicker.value));
		parent.init(client, width, height);
		addDrawableChild(ButtonWidget.builder(Text.translatable("mialib.screen.mvalues.color_picker.confirm"), i -> close())
								 .dimensions(width / 2 - 95, height / 2 + 54, 90, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.translatable("mialib.screen.mvalues.color_picker.reset"), i -> colorPicker.setValue(defaultValue))
								 .dimensions(width / 2 + 5, height / 2 + 54, 90, 20).build());
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks)
	{
		super.render(context, mouseX, mouseY, deltaTicks);
	}
	
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks)
	{
		if(parent != null)
			parent.render(context, 0, 0, deltaTicks);
		if(client != null && client.world != null)
		{
			renderDarkening(context);
			renderDarkening(context);
		}
		super.renderBackground(context, mouseX, mouseY, deltaTicks);
	}
	
	@Override
	public void close()
	{
		if(client == null)
			return;
		client.setScreen(parent);
		source.value.send(colorPicker.value);
	}
	
	static class ColorPickerWidget extends ClickableWidget
	{
		static final Identifier TEXTURE = Mialib.id("textures/gui/mvalue/color_picker.png");
		
		final List<ColorChannelSlider> channels = new ArrayList<>();
		final TextFieldWidget hexInputField;
		ColorChannelSlider red, green, blue;
		int value;
		
		public ColorPickerWidget(int x, int y, Text title, int startColor)
		{
			super(x, y, 100, 100, title);
			value = startColor;
			channels.add(red = new ColorChannelSlider(x + 2, y + 36, width - 4, 20, ColorHelper.getRedFloat(startColor),
					new Vector3f(1, 0, 0), this::onColorChanged));
			channels.add(green = new ColorChannelSlider(x + 2, y + 57, width - 4, 20, ColorHelper.getGreenFloat(startColor),
					new Vector3f(0, 1, 0), this::onColorChanged));
			channels.add(blue = new ColorChannelSlider(x + 2, y + 78, width - 4, 20, ColorHelper.getBlueFloat(startColor),
					new Vector3f(0, 0, 1), this::onColorChanged));
			hexInputField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 60, 20, Text.of("Hex Field"));
			hexInputField.setMaxLength(6);
			hexInputField.setTextPredicate(i -> {
				for(char c : i.toCharArray())
					if(!HexFormat.isHexDigit(c))
						return false;
				return true;
			});
			hexInputField.setPosition(x + 3, y + 15);
			hexInputField.setChangedListener(this::setColorHex);
			onColorChanged();
		}
		
		@Override
		public void setPosition(int x, int y)
		{
			super.setPosition(x, y);
			red.setPosition(x + 2, y + 36);
			green.setPosition(x + 2, y + 57);
			blue.setPosition(x + 2, y + 78);
			hexInputField.setPosition(x + 3, y + 15);
		}
		
		@Override
		protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
		{
			var matrices = context.getMatrices();
			matrices.pushMatrix();
			int x = getX(), y = getY();
			context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x - 3, y - 3, 0, 0, 106, 104, 106, 104);
			TextRenderer tRender = MinecraftClient.getInstance().textRenderer;
			context.drawText(tRender, getMessage(), x + (width - tRender.getWidth(getMessage())) / 2, y - 14, 0xffffffff, true);
			channels.forEach(i -> i.render(context, mouseX, mouseY, delta));
			hexInputField.render(context, mouseX, mouseY, delta);
			context.fill(x + width - 35, y + 3, x + width - 3, y + 3 + 32, ColorHelper.withAlpha(255, value));
			matrices.popMatrix();
		}
		
		@Override
		public void onClick(double mouseX, double mouseY)
		{
			super.onClick(mouseX, mouseY);
			setFocused(true);
			channels.forEach(i -> i.onClick(mouseX, mouseY));
			hexInputField.setFocused(hexInputField.isHovered());
			hexInputField.onClick(mouseX, mouseY);
		}
		
		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers)
		{
			return hexInputField.keyPressed(keyCode, scanCode, modifiers);
		}
		
		@Override
		public boolean charTyped(char chr, int modifiers)
		{
			return hexInputField.charTyped(chr, modifiers);
		}
		
		@Override
		protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY)
		{
			super.onDrag(mouseX, mouseY, deltaX, deltaY);
			channels.forEach(i -> i.onDrag(mouseX, mouseY, deltaX, deltaY));
		}
		
		@Override
		public void onRelease(double mouseX, double mouseY)
		{
			super.onRelease(mouseX, mouseY);
			channels.forEach(i -> i.onRelease(mouseX, mouseY));
			hexInputField.onRelease(mouseX, mouseY);
		}
		
		public void setValue(int value)
		{
			int full = ColorHelper.withAlpha(1f, value);
			red.setFullColor(full);
			green.setFullColor(full);
			blue.setFullColor(full);
			hexInputField.setText(Integer.toHexString(ColorHelper.withAlpha(0f, value)));
			this.value = value;
		}
		
		void onColorChanged()
		{
			int fullColor = ColorHelper.fromFloats(1f, red.getValue(), green.getValue(), blue.getValue());
			red.setFullColor(fullColor);
			green.setFullColor(fullColor);
			blue.setFullColor(fullColor);
			hexInputField.setText(Integer.toHexString(fullColor).substring(2));
			
			value = ColorHelper.withAlpha(0, fullColor);
		}
		
		void setColorHex(String v)
		{
			if(v.length() != 6)
				return;
			int color = HexFormat.fromHexDigits(v);
			
			red.setValue(ColorHelper.getRedFloat(color));
			green.setValue(ColorHelper.getGreenFloat(color));
			blue.setValue(ColorHelper.getBlueFloat(color));
			red.setFullColor(color);
			green.setFullColor(color);
			blue.setFullColor(color);
			
			value = color;
		}
		
		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder)
		{
		
		}
		
		static class ColorChannelSlider extends SliderWidget
		{
			private static final Identifier HANDLE = Identifier.of("widget/slider_handle");
			private static final Identifier HANDLE_HIGHLIGHTED = Identifier.of("widget/slider_handle_highlighted");
			final Vector3f channel;
			final Runnable onChangeValue;
			int fullColor;
			
			public ColorChannelSlider(int x, int y, int width, int height, double value, Vector3f channel, Runnable onChangeValue)
			{
				super(x, y, width, height, Text.empty(), value);
				this.channel = channel;
				this.onChangeValue = onChangeValue;
			}
			
			@Override
			protected void updateMessage()
			{
			
			}
			
			@Override
			protected void applyValue()
			{
				onChangeValue.run();
			}
			
			@Override
			public void onClick(double mouseX, double mouseY)
			{
				if(isHovered())
					super.onClick(mouseX, mouseY);
				setFocused(isHovered());
			}
			
			@Override
			public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY)
			{
				if(isFocused())
					super.onDrag(mouseX, mouseY, deltaX, deltaY);
			}
			
			@Override
			public void onRelease(double mouseX, double mouseY)
			{
				super.onRelease(mouseX, mouseY);
				if(isFocused())
					setFocused(false);
			}
			
			@Override
			public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
			{
				MinecraftClient client = MinecraftClient.getInstance();
				var matrices = context.getMatrices();
				matrices.pushMatrix();
				matrices.pushMatrix();
				matrices.rotate((float)Math.toRadians(90));
				int start = getPreviewColor(true);
				int end = getPreviewColor(false);
				context.fillGradient(getY() + 3, -getX() - width + 1, getY() + height - 3, -getX() - 1,
						ColorHelper.withAlpha(alpha, start), ColorHelper.withAlpha(alpha, end));
				matrices.popMatrix();
				context.drawBorder(getX() + 1, getY() + 3, width - 2, height - 6, hovered ? 0xffffffff : 0xff000000);
				context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, !hovered && !isFocused() ? HANDLE : HANDLE_HIGHLIGHTED,
						getX() + (int)(value * (double)(width - 8)), getY(), 8, getHeight(), ColorHelper.getWhite(alpha));
				int c = active ? 16777215 : 10526880;
				drawScrollableText(context, client.textRenderer, 2, c | MathHelper.ceil(alpha * 255f) << 24);
				matrices.popMatrix();
			}
			
			int getPreviewColor(boolean full)
			{
				float red = full ? Math.max(channel.x, ColorHelper.getRedFloat(fullColor)) : Math.min(1f - channel.x, ColorHelper.getRedFloat(fullColor));
				float green = full ? Math.max(channel.y, ColorHelper.getGreenFloat(fullColor)) : Math.min(1f - channel.y, ColorHelper.getGreenFloat(fullColor));
				float blue = full ? Math.max(channel.z, ColorHelper.getBlueFloat(fullColor)) : Math.min(1f - channel.z, ColorHelper.getBlueFloat(fullColor));
				return ColorHelper.fromFloats(1f, red, green, blue);
			}
			
			public void setFullColor(int v)
			{
				fullColor = v;
			}
			
			public void setValue(float value)
			{
				this.value = value;
			}
			
			public float getValue()
			{
				return (float)value;
			}
		}
	}
}
