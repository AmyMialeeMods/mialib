package xyz.amymialee.mialib.client;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import xyz.amymialee.mialib.Mialib;

import java.util.Arrays;
import java.util.List;

public class MialibServerWidget extends MultiplayerServerListWidget.Entry {
    private final MultiplayerServerListWidget widget;
    private final MultiplayerScreen screen;
    private final MinecraftClient client;
    private final ServerInfo server;
    private final WorldIcon icon;
    private byte @Nullable [] favicon;
    private long time;
    private @Nullable List<Text> playerListSummary;
    private @Nullable Identifier statusIconTexture;
    private @Nullable Text statusTooltipText;

    public MialibServerWidget(MultiplayerServerListWidget widget, final MultiplayerScreen screen, final @NotNull ServerInfo server) {
        this.widget = widget;
        this.screen = screen;
        this.server = server;
        this.client = MinecraftClient.getInstance();
        this.icon = WorldIcon.forServer(this.client.getTextureManager(), server.address);
        this.update();
    }

    @Override
    protected boolean isOfSameType(MultiplayerServerListWidget.Entry entry) {
        return entry instanceof MialibServerWidget;
    }

    @Override
    public void connect() {
        this.screen.connect(this.server);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        var index = this.screen.getServerList().mialib$getMialibServers().indexOf(this.server);
        if (this.server.getStatus() == ServerInfo.Status.INITIAL) {
            this.server.setStatus(ServerInfo.Status.PINGING);
            this.server.label = ScreenTexts.EMPTY;
            this.server.playerCountLabel = ScreenTexts.EMPTY;
            MultiplayerServerListWidget.SERVER_PINGER_THREAD_POOL.submit(() -> {
                try {
//                    this.screen.getServerListPinger().add(this.server, () -> this.client.execute(() -> this.screen.getServerList().saveFile()), () -> {
//                        this.server.setStatus(this.server.protocolVersion == SharedConstants.getGameVersion().protocolVersion() ? ServerInfo.Status.SUCCESSFUL : ServerInfo.Status.INCOMPATIBLE);
//                        this.client.execute(this::update);
//                    });
//                } catch (UnknownHostException var2) {
//                    this.server.setStatus(ServerInfo.Status.UNREACHABLE);
//                    this.server.label = MultiplayerServerListWidget.CANNOT_RESOLVE_TEXT;
//                    this.client.execute(this::update);
                } catch (Exception var3) {
                    this.server.setStatus(ServerInfo.Status.UNREACHABLE);
                    this.server.label = MultiplayerServerListWidget.CANNOT_CONNECT_TEXT;
                    this.client.execute(this::update);
                }
            });
        }
        context.drawTextWithShadow(this.client.textRenderer, this.server.name, this.getX() + 32 + 3, this.getY() + 1, Colors.WHITE);
        var list = this.client.textRenderer.wrapLines(this.server.label, this.getWidth() - 32 - 2);
        for (var i = 0; i < Math.min(list.size(), 2); i++) context.drawTextWithShadow(this.client.textRenderer, list.get(i), this.getX() + 32 + 3, this.getY() + 12 + 9 * i, -8355712);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.icon.getTextureId(), this.getX(), this.getY(), 0.0F, 0.0F, 32, 32, 32, 32);
        if (this.server.getStatus() == ServerInfo.Status.PINGING) {
            var i = (int) (Util.getMeasuringTimeMs() / 100L + (index * 2L) & 7L);
            if (i > 4) i = 8 - i;
            this.statusIconTexture = switch (i) {
                case 1 -> MultiplayerServerListWidget.PINGING_2_TEXTURE;
                case 2 -> MultiplayerServerListWidget.PINGING_3_TEXTURE;
                case 3 -> MultiplayerServerListWidget.PINGING_4_TEXTURE;
                case 4 -> MultiplayerServerListWidget.PINGING_5_TEXTURE;
                default -> MultiplayerServerListWidget.PINGING_1_TEXTURE;
            };
        }
        var i = this.getX() + this.getWidth() - 10 - 5;
        if (this.statusIconTexture != null) context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.statusIconTexture, i, this.getY(), 10, 8);
        var bs = this.server.getFavicon();
        if (!Arrays.equals(bs, this.favicon)) {
            if (this.uploadFavicon(bs)) {
                this.favicon = bs;
            } else {
                this.server.setFavicon(null);
                this.screen.getServerList().saveFile();
            }
        }
        var text = this.server.getStatus() == ServerInfo.Status.INCOMPATIBLE ? this.server.version.copy().formatted(Formatting.RED) : this.server.playerCountLabel;
        var j = this.client.textRenderer.getWidth(text);
        var k = i - j - 5;
        context.drawTextWithShadow(this.client.textRenderer, text, k, this.getY() + 1, Colors.GRAY);
        if (this.statusTooltipText != null && mouseX >= i && mouseX <= i + 10 && mouseY >= this.getY() && mouseY <= this.getY() + 8) {
            context.drawTooltip(this.statusTooltipText, mouseX, mouseY);
        } else if (this.playerListSummary != null && mouseX >= k && mouseX <= k + j && mouseY >= this.getY() && mouseY <= this.getY() - 1 + 9) {
            context.drawTooltip(Lists.transform(this.playerListSummary, Text::asOrderedText), mouseX, mouseY);
        }
        if (this.client.options.getTouchscreen().getValue() || hovered) {
            context.fill(this.getX(), this.getY(), this.getX() + 32, this.getY() + 32, -1601138544);
            var l = mouseX - this.getX();
            var m = mouseY - this.getY();
            if (l < 32 && l > 16) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.JOIN_HIGHLIGHTED_TEXTURE, this.getX(), this.getY(), 32, 32);
            } else {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.JOIN_TEXTURE, this.getX(), this.getY(), 32, 32);
            }
            if (index > 0) {
                if (l < 16 && m < 16) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_UP_HIGHLIGHTED_TEXTURE, this.getX(), this.getY(), 32, 32);
                } else {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_UP_TEXTURE, this.getX(), this.getY(), 32, 32);
                }
            }
            if (index < this.screen.getServerList().mialib$getMialibServers().size() - 1) {
                if (l < 16 && m > 16) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_DOWN_HIGHLIGHTED_TEXTURE, this.getX(), this.getY(), 32, 32);
                } else {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, MultiplayerServerListWidget.MOVE_DOWN_TEXTURE, this.getX(), this.getY(), 32, 32);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.client.isShiftPressed()) {
            var i = this.screen.getServerList().mialib$getMialibServers().indexOf(this.server);
            if (i == -1) return true;
            if (input.getKeycode() == GLFW.GLFW_KEY_DOWN && i < this.screen.getServerList().mialib$getMialibServers().size() - 1 || input.getKeycode() == GLFW.GLFW_KEY_UP && i > 0) {
                this.swapEntries(i, input.getKeycode() == GLFW.GLFW_KEY_DOWN ? i + 1 : i - 1);
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        var d = click.x() - (double) this.widget.getRowLeft();
        var e = click.y() - (double) this.widget.getRowTop(this.widget.children().indexOf(this));
        if (d <= 32.0) {
            if (d < 32.0 && d > 16.0) {
                this.screen.setFocused(this);
                this.screen.connect(this.server);
                return true;
            }
            var i = this.screen.getServerList().mialib$getMialibServers().indexOf(this.server);
            if (d < 16.0 && e < 16.0 && i > 0) {
                this.swapEntries(i, i - 1);
                return true;
            }
            if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().mialib$getMialibServers().size() - 1) {
                this.swapEntries(i, i + 1);
                return true;
            }
        }
        this.screen.setFocused(this);
        if (Util.getMeasuringTimeMs() - this.time < 250L) this.screen.connect(this.server);
        this.time = Util.getMeasuringTimeMs();
        return super.mouseClicked(click, doubled);
    }

    public ServerInfo getServer() {
        return this.server;
    }

    private void update() {
        this.playerListSummary = null;
        switch (this.server.getStatus()) {
            case INITIAL, PINGING -> {
                this.statusIconTexture = MultiplayerServerListWidget.PING_1_TEXTURE;
                this.statusTooltipText = MultiplayerServerListWidget.PINGING_TEXT;
            }
            case INCOMPATIBLE -> {
                this.statusIconTexture = MultiplayerServerListWidget.INCOMPATIBLE_TEXTURE;
                this.statusTooltipText = MultiplayerServerListWidget.INCOMPATIBLE_TEXT;
                this.playerListSummary = this.server.playerListSummary;
            }
            case UNREACHABLE -> {
                this.statusIconTexture = MultiplayerServerListWidget.UNREACHABLE_TEXTURE;
                this.statusTooltipText = MultiplayerServerListWidget.NO_CONNECTION_TEXT;
            }
            case SUCCESSFUL -> {
                if (this.server.ping < 150L) {
                    this.statusIconTexture = MultiplayerServerListWidget.PING_5_TEXTURE;
                } else if (this.server.ping < 300L) {
                    this.statusIconTexture = MultiplayerServerListWidget.PING_4_TEXTURE;
                } else if (this.server.ping < 600L) {
                    this.statusIconTexture = MultiplayerServerListWidget.PING_3_TEXTURE;
                } else if (this.server.ping < 1000L) {
                    this.statusIconTexture = MultiplayerServerListWidget.PING_2_TEXTURE;
                } else {
                    this.statusIconTexture = MultiplayerServerListWidget.PING_1_TEXTURE;
                }
                this.statusTooltipText = Text.translatable("multiplayer.status.ping", this.server.ping);
                this.playerListSummary = this.server.playerListSummary;
            }
        }
    }

    private void swapEntries(int i, int j) {
        var serverInfo = this.screen.getServerList().mialib$getMialibServers().get(i);
        this.screen.getServerList().mialib$getMialibServers().set(i, this.screen.getServerList().mialib$getMialibServers().get(j));
        this.screen.getServerList().mialib$getMialibServers().set(j, serverInfo);
        this.screen.getServerList().saveFile();
        this.screen.serverListWidget.setServers(this.screen.getServerList());
        var entry = this.screen.serverListWidget.children().stream().filter((e) -> e instanceof MialibServerWidget widget && widget.server == serverInfo).findFirst().orElse(null);
        if (entry == null) return;
        this.screen.serverListWidget.setSelected(entry);
    }

    private boolean uploadFavicon(byte @Nullable [] bytes) {
        if (bytes == null) {
            this.icon.destroy();
        } else {
            try {
                this.icon.load(NativeImage.read(bytes));
            } catch (Throwable var3) {
                Mialib.LOGGER.error("Invalid icon for server {} ({})", this.server.name, this.server.address, var3);
                return false;
            }
        }
        return true;
    }

    public @Override Text getNarration() {
        var mutableText = Text.empty();
        mutableText.append(Text.translatable("narrator.select", this.server.name));
        mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
        switch (this.server.getStatus()) {
            case PINGING -> mutableText.append(MultiplayerServerListWidget.PINGING_TEXT);
            case INCOMPATIBLE -> {
                mutableText.append(MultiplayerServerListWidget.INCOMPATIBLE_TEXT);
                mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                mutableText.append(Text.translatable("multiplayer.status.version.narration", this.server.version));
                mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
            }
            case UNREACHABLE -> mutableText.append(MultiplayerServerListWidget.NO_CONNECTION_TEXT);
            default -> {
                mutableText.append(MultiplayerServerListWidget.ONLINE_TEXT);
                mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                mutableText.append(Text.translatable("multiplayer.status.ping.narration", this.server.ping));
                mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                mutableText.append(Text.translatable("multiplayer.status.motd.narration", this.server.label));
                if (this.server.players != null) {
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Text.translatable("multiplayer.status.player_count.narration", this.server.players.online(), this.server.players.max()));
                    mutableText.append(ScreenTexts.SENTENCE_SEPARATOR);
                    mutableText.append(Texts.join(this.server.playerListSummary, Text.literal(", ")));
                }
            }
        }
        return mutableText;
    }

    public @Override void close() {
        this.icon.close();
    }
}