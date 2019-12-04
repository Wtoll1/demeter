package com.wtoll.demeter.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.Utility;
import com.wtoll.demeter.container.ObservationTableContainer;
import net.fabricmc.loader.game.MinecraftGameProvider;
import net.fabricmc.loom.providers.MinecraftProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.container.Property;
import net.minecraft.container.StonecutterContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Set;

public class ObservationTableScreen extends AbstractContainerScreen<ObservationTableContainer> {
    public Identifier TEXTURE = Utility.id("textures/gui/container/observation_table.png");

    private float scrollAmount;
    private boolean withinScroll;

    private int viewportHeight;
    private int viewportWidth;
    private int viewportX;
    private int viewportY;

    private int viewportPadding;

    private int scrollItemHeight;
    private int scrollItemWidth;
    private int scrollAreaX;
    private int scrollAreaY;

    public ObservationTableScreen(int syncId, PlayerEntity player) {
        super(new ObservationTableContainer(syncId, player.inventory), player.inventory, new TranslatableText("container.demeter.observation_table"));
    }

    protected void init() {
        super.init();
        this.viewportHeight = 79;
        this.viewportWidth = 99;
        this.viewportX = 55;
        this.viewportY = 18;

        this.viewportPadding = 4;

        this.scrollItemHeight = 15;
        this.scrollItemWidth = 12;
        this.scrollAreaX = 156;
        this.scrollAreaY = 17;

        this.containerHeight = 200;
        this.containerWidth = 176;
        this.x = (this.width - this.containerWidth) / 2;
        this.y = (this.height - this.containerHeight) / 2;
    }

    public void render(int mouseX, int mouseY, float delta) {
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        this.renderBackground();
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
        this.blit(this.x, this.y, 0, 0, this.containerWidth, this.containerHeight);
        this.blit(this.x + this.scrollAreaX, this.y + this.scrollAreaY + this.getScrollOffset(), this.containerWidth + (this.shouldScroll() ? 0 : this.scrollItemWidth), 0, this.scrollItemWidth, this.scrollItemHeight);
        drawViewportContents();
    }

    protected void drawForeground(int mouseX, int mouseY) {
        this.font.draw(this.title.asFormattedString(), 8.0F, 6.0F, 13875486);
        this.font.draw(this.playerInventory.getDisplayName().asFormattedString(), 8.0F, (float)(this.containerHeight - 93), 4210752);
    }

    private void drawViewportContents() {

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        glScissor(this.x + this.viewportX, this.y + this.viewportY, this.viewportWidth, this.viewportHeight);

        if (!this.getContainer().inventory.isInvEmpty()) {
            ItemStack stack = this.getContainer().inventory.getInvStack(0);

            int yoffset = this.viewportPadding;
            Identifier[] ids = Demeter.CROP_PROPERTIES.getIds().toArray(new Identifier[]{});
            for (int i = 0; i < ids.length; i++) {
                Identifier id = ids[i];
                net.minecraft.state.property.Property property = Demeter.CROP_PROPERTIES.get(id);
                if (property != null) {
                    CompoundTag tag = stack.getSubTag("BlockStateTag");
                    if (tag != null) {
                        Tag property_value = tag.get(Demeter.CROP_PROPERTIES.get(id).getName());
                        if (property_value != null) {
                            Sprite BADGE = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(new Identifier(id.getNamespace(), "gui/property/" + id.getPath()));
                            if (BADGE == MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(new Identifier(""))) {
                                BADGE = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(new Identifier("demeter:gui/property/missing"));
                            }
                            drawSprite(this.x + this.viewportX + this.viewportPadding, this.y + this.viewportY - getViewportOffset() + yoffset, BADGE);
                            this.font.draw(new TranslatableText("property." + id.getNamespace() + "." + id.getPath()).asFormattedString(), this.x + this.viewportX + this.viewportPadding + 16 + this.viewportPadding, this.y + this.viewportY - getViewportOffset() + yoffset + 4, 16777215);
                            rightJustifiedText(parseStrings(property_value.toString()), this.x + this.viewportX + this.viewportWidth - (this.viewportPadding*2), this.y + this.viewportY - getViewportOffset() + yoffset + 4, 13875486);
                            yoffset += 16 + this.viewportPadding;
                        }
                    }
                }
            }

        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    private String parseStrings(String s) {
        String r = s;
        if (r.charAt(0) == '"') {
            r = r.substring(1);
        }
        if (r.charAt(r.length()-1) == '"') {
            r = r.substring(0, r.length()-1);
        }
        return r;
    }

    private void rightJustifiedText(String s, int x, int y, int color) {
        this.font.draw(s, x - this.font.getStringWidth(s), y, color);
    }

    private void glScissor(int x, int y, int width, int height) {
        MinecraftClient client = MinecraftClient.getInstance();
        double scale = client.getWindow().getScaleFactor();
        GL11.glScissor((int)((x-1) * scale), (int)((client.getWindow().getHeight() * 2) - (((y-1) + height) * scale)), (int)(width * scale), (int)(height * scale));
    }

    private void drawSprite(int x, int y, Sprite s) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(x,y + s.getHeight(), 0).texture(s.getMinU(), s.getMaxV()).next();
        buffer.vertex(x + s.getWidth(),y + s.getHeight(), 0).texture(s.getMaxU(), s.getMaxV()).next();
        buffer.vertex(x + s.getWidth(), y,0).texture(s.getMaxU(), s.getMinV()).next();
        buffer.vertex(x,y,0).texture(s.getMinU(), s.getMinV()).next();
        tessellator.draw();
        RenderSystem.disableBlend();
    }

    private int getViewportOffset() {
        int offset = (int) ((viewportContentsHeight() - viewportHeight) * scrollAmount);
        return offset > 0 ? offset : 0;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.withinScroll = false;
        if (mouseInScrollArea(mouseX, mouseY)) {
            this.withinScroll = true;
            updateScrollAmount(mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseInScrollArea(double mouseX, double mouseY) {
        return mouseX > this.x + this.scrollAreaX && mouseX < this.x + this.scrollAreaX + this.scrollItemWidth && mouseY > this.y + this.scrollAreaY && mouseY < this.y + this.scrollAreaY + this.viewportHeight;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.withinScroll) {
            updateScrollAmount(mouseY);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public void updateScrollAmount(double mouseY) {
        double relativeHeight = mouseY - this.y - this.scrollAreaY;
        this.scrollAmount = ((float) relativeHeight / (float) this.viewportHeight);
        this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
    }

    public boolean mouseScrolled(double d, double e, double amount) {
        if (this.shouldScroll()) {
            int i = this.viewportContentsHeight();
            this.scrollAmount = (float)((double)this.scrollAmount - amount / (double)i);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
        }

        return true;
    }

    /**
     * If the height of the content is greater than the height of the viewport than scrolling is necessary
     * @return
     */
    private boolean shouldScroll() {
        return viewportContentsHeight() > this.viewportHeight;
    }

    /**
     * The height of the content to be rendered within the viewport
     * @return
     */
    protected int viewportContentsHeight() {
        if (!this.getContainer().inventory.isInvEmpty()) {
            ItemStack stack = this.getContainer().inventory.getInvStack(0);
            int yoffset = this.viewportPadding;
            Identifier[] ids = Demeter.CROP_PROPERTIES.getIds().toArray(new Identifier[]{});
            for (int i = 0; i < ids.length; i++) {
                Identifier id = ids[i];
                net.minecraft.state.property.Property property = Demeter.CROP_PROPERTIES.get(id);
                if (property != null) {
                    CompoundTag tag = stack.getSubTag("BlockStateTag");
                    if (tag != null) {
                        Tag property_value = tag.get(Demeter.CROP_PROPERTIES.get(id).getName());
                        if (property_value != null) {
                            yoffset += 16 + this.viewportPadding;
                        }
                    }
                }
            }
            return yoffset;
        }

        return 0;
    }

    /**
     * Returns how far down the scroll thingy should render
     * @return
     */
    private int getScrollOffset() {
        return (int) (scrollAmount * (this.viewportHeight - this.scrollItemHeight));
    }
}
