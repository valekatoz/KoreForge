package net.kore.mixins.gui;


import net.kore.Kore;
import net.kore.utils.MathUtils;
import net.kore.utils.StencilUtils;
import net.kore.utils.TimerUtils;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;
import net.kore.utils.render.shader.BlurUtils;

import com.google.common.collect.Lists;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import java.awt.Color;
import net.minecraft.util.MathHelper;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.ChatLine;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { GuiNewChat.class }, priority = 1)
public abstract class MixinGuiNewChat extends MixinGui
{
    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    @Final
    private List<ChatLine> drawnChatLines;
    @Shadow
    private int scrollPos;
    @Shadow
    private boolean isScrolled;

    @Shadow
    public abstract int getLineCount();

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract float getChatScale();

    @Shadow
    public abstract int getChatWidth();

    @Inject(method = { "drawChat" }, at = { @At("HEAD") }, cancellable = true)
    private void drawChat(final int updateCounter, final CallbackInfo ci) {
        if (Kore.interfaces.customChat.isEnabled() && Kore.interfaces.isToggled()) {
            if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
                final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.translate(0.0f, (float)(scaledresolution.getScaledHeight() - 60), 0.0f);
                final int maxLineCount = this.getLineCount();
                boolean isChatOpen = false;
                int j = 0;
                final int lineCount = this.drawnChatLines.size();
                int fontHeight = Kore.interfaces.customChatFont.isEnabled() ? (Fonts.getPrimary().getHeight() + 3) : this.mc.fontRendererObj.FONT_HEIGHT;
                if (lineCount > 0) {
                    if (this.getChatOpen()) {
                        isChatOpen = true;
                    }
                    final float scale = this.getChatScale();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(2.0f, 20.0f, 0.0f);
                    GlStateManager.scale(scale, scale, 1.0f);
                    final int scaledWidth = MathHelper.ceiling_float_int(this.getChatWidth() / scale);
                    final float x = 0.0f;
                    float y = 0.0f;
                    boolean render = false;
                    for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < maxLineCount; ++i) {
                        final ChatLine chatline = this.drawnChatLines.get(i + this.scrollPos);
                        if (chatline != null && (updateCounter - chatline.getUpdatedCounter() < 200 || isChatOpen)) {
                            render = true;
                            if (!isChatOpen && updateCounter - chatline.getUpdatedCounter() > 195) {
                                float percent = 1.0f - (updateCounter - chatline.getUpdatedCounter() + TimerUtils.getTimer().renderPartialTicks - 195.0f) / 5.0f;
                                percent = MathUtils.clamp(percent, 0.0f, 1.0f);
                                y -= fontHeight * percent;
                            }
                            else {
                                y -= fontHeight;
                            }
                        }
                    }
                    if (render) {
                        int blur = 0;
                        final String selected = Kore.interfaces.blurStrength.getSelected();
                        switch (selected) {
                            case "Low": {
                                blur = 7;
                                break;
                            }
                            case "High": {
                                blur = 25;
                                break;
                            }
                        }
                        if (blur > 0) {
                            for (float k = 0.5f; k < 3.0f; k += 0.5f) {
                                RenderUtils.drawRoundedRect(x + k - 2.0f, y + k, x + scaledWidth + 4.0f + k, 1.0f + k, 5.0, new Color(20, 20, 20, 40).getRGB());
                            }
                        }
                        StencilUtils.initStencil();
                        StencilUtils.bindWriteStencilBuffer();
                        RenderUtils.drawRoundedRect(x - 2.0f, y, x + scaledWidth + 4.0f, 1.0, 5.0, Color.white.getRGB());
                        GL11.glPopMatrix();
                        GL11.glPopMatrix();
                        StencilUtils.bindReadStencilBuffer(1);
                        BlurUtils.renderBlurredBackground((float)blur, (float)scaledresolution.getScaledWidth(), (float)scaledresolution.getScaledHeight(), 0.0f, 0.0f, (float)scaledresolution.getScaledWidth(), (float)scaledresolution.getScaledHeight());
                        GL11.glPushMatrix();
                        GlStateManager.translate(0.0f, (float)(scaledresolution.getScaledHeight() - 60), 0.0f);
                        GL11.glPushMatrix();
                        GlStateManager.translate(2.0f, 20.0f, 0.0f);
                        GlStateManager.scale(scale, scale, 1.0f);
                        RenderUtils.drawRoundedRect(x - 2.0f, y, x + scaledWidth + 4.0f, 1.0, 5.0, new Color(20, 20, 20, 60).getRGB());
                    }
                    for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < maxLineCount; ++i) {
                        final ChatLine chatline = this.drawnChatLines.get(i + this.scrollPos);
                        if (chatline != null) {
                            final int j2 = updateCounter - chatline.getUpdatedCounter();
                            if (j2 < 200 || isChatOpen) {
                                ++j;
                                final int left = 0;
                                final int top = -i * fontHeight;
                                final String text = chatline.getChatComponent().getFormattedText();
                                GlStateManager.enableBlend();
                                if (Kore.interfaces.customChatFont.isEnabled()) {
                                    Fonts.getPrimary().drawSmoothStringWithShadow(text, (float)left, (float)(top - (fontHeight - 2.3)), Color.white.getRGB());
                                }
                                else {
                                    this.mc.fontRendererObj.drawStringWithShadow(text, (float)left, (float)(top - (fontHeight - 1)), 16777215);
                                }
                                GlStateManager.disableAlpha();
                                GlStateManager.disableBlend();
                            }
                        }
                    }
                    if (render) {
                        StencilUtils.uninitStencil();
                    }
                    if (isChatOpen) {
                        GlStateManager.translate(-3.0f, 0.0f, 0.0f);
                        fontHeight = this.mc.fontRendererObj.FONT_HEIGHT;
                        final int l2 = lineCount * fontHeight + lineCount;
                        final int i2 = j * fontHeight + j;
                        final int j3 = this.scrollPos * i2 / lineCount;
                        final int k2 = i2 * i2 / l2;
                        if (l2 != i2) {
                            final int opacity = (j3 > 0) ? 170 : 96;
                            final int l3 = this.isScrolled ? 13382451 : 3355562;
                            drawRect(0, -j3, 2, -j3 - k2, l3 + (opacity << 24));
                            drawRect(2, -j3, 1, -j3 - k2, 13421772 + (opacity << 24));
                        }
                    }
                    GlStateManager.popMatrix();
                }
            }
            ci.cancel();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public IChatComponent getChatComponent(final int p_146236_1_, final int p_146236_2_) {
        if (!this.getChatOpen()) {
            return null;
        }
        final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        final int i = scaledresolution.getScaleFactor();
        final float f = this.getChatScale();
        int j = p_146236_1_ / i - 3;
        int k = p_146236_2_ / i - 27;
        if (Kore.interfaces.isToggled() && Kore.interfaces.customChat.isEnabled()) {
            k -= 12;
        }
        j = MathHelper.floor_float(j / f);
        k = MathHelper.floor_float(k / f);
        if (j < 0 || k < 0) {
            return null;
        }
        final int l = Math.min(this.getLineCount(), this.drawnChatLines.size());
        if (j <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && k < this.getHeight() * l + l) {
            final int i2 = k / this.getHeight() + this.scrollPos;
            if (i2 >= 0 && i2 < this.drawnChatLines.size()) {
                final ChatLine chatline = this.drawnChatLines.get(i2);
                int j2 = 0;
                for (final IChatComponent ichatcomponent : chatline.getChatComponent()) {
                    if (ichatcomponent instanceof ChatComponentText) {
                        j2 += (int)((Kore.interfaces.customChatFont.isEnabled() && Kore.interfaces.isToggled() && Kore.interfaces.customChat.isEnabled()) ? Fonts.getPrimary().getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false)) : this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false)));
                        if (j2 > j) {
                            return ichatcomponent;
                        }
                        continue;
                    }
                }
            }
            return null;
        }
        return null;
    }

    private int getHeight() {
        return (Kore.interfaces.customChatFont.isEnabled() && Kore.interfaces.customChat.isEnabled() && Kore.interfaces.isToggled()) ? (Fonts.getPrimary().getHeight() + 3) : this.mc.fontRendererObj.FONT_HEIGHT;
    }

    @Redirect(method = { "setChatLine" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiUtilRenderComponents;splitText(Lnet/minecraft/util/IChatComponent;ILnet/minecraft/client/gui/FontRenderer;ZZ)Ljava/util/List;"))
    private List<IChatComponent> onFunc(final IChatComponent k, final int s1, final FontRenderer chatcomponenttext, final boolean l, final boolean chatcomponenttext2) {
        return (Kore.interfaces.customChatFont.isEnabled() && Kore.interfaces.isToggled() && Kore.interfaces.customChat.isEnabled()) ? this.wrapToLen(k, s1, chatcomponenttext) : GuiUtilRenderComponents.splitText(k, s1, chatcomponenttext, l, chatcomponenttext2);
    }

    private List<IChatComponent> wrapToLen(final IChatComponent p_178908_0_, final int p_178908_1_, final FontRenderer p_178908_2_) {
        int i = 0;
        IChatComponent ichatcomponent = (IChatComponent)new ChatComponentText("");
        final List<IChatComponent> list = Lists.newArrayList();
        final List<IChatComponent> list2 = (List<IChatComponent>)Lists.newArrayList((Iterable)p_178908_0_);
        for (int j = 0; j < list2.size(); ++j) {
            final IChatComponent ichatcomponent2 = list2.get(j);
            String s = ichatcomponent2.getUnformattedTextForChat();
            boolean flag = false;
            if (s.contains("\n")) {
                final int k = s.indexOf(10);
                final String s2 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                final ChatComponentText chatcomponenttext = new ChatComponentText(s2);
                chatcomponenttext.setChatStyle(ichatcomponent2.getChatStyle().createShallowCopy());
                list2.add(j + 1, (IChatComponent)chatcomponenttext);
                flag = true;
            }
            final String s3 = GuiUtilRenderComponents.func_178909_a(ichatcomponent2.getChatStyle().getFormattingCode() + s, false);
            final String s4 = s3.endsWith("\n") ? s3.substring(0, s3.length() - 1) : s3;
            double i2 = Fonts.getPrimary().getStringWidth(s4);
            ChatComponentText chatcomponenttext2 = new ChatComponentText(s4);
            chatcomponenttext2.setChatStyle(ichatcomponent2.getChatStyle().createShallowCopy());
            if (i + i2 > p_178908_1_) {
                String s5 = Fonts.getPrimary().trimStringToWidth(s3, p_178908_1_ - i, false);
                String s6 = (s5.length() < s3.length()) ? s3.substring(s5.length()) : null;
                if (s6 != null && s6.length() > 0) {
                    final int l = s5.lastIndexOf(" ");
                    if (l >= 0 && Fonts.getPrimary().getStringWidth(s3.substring(0, l)) > 0.0) {
                        s5 = s3.substring(0, l);
                        s6 = s3.substring(l);
                    }
                    else if (i > 0 && !s3.contains(" ")) {
                        s5 = "";
                        s6 = s3;
                    }
                    s6 = FontRenderer.getFormatFromString(s5) + s6;
                    final ChatComponentText chatcomponenttext3 = new ChatComponentText(s6);
                    chatcomponenttext3.setChatStyle(ichatcomponent2.getChatStyle().createShallowCopy());
                    list2.add(j + 1, (IChatComponent)chatcomponenttext3);
                }
                i2 = Fonts.getPrimary().getStringWidth(s5);
                chatcomponenttext2 = new ChatComponentText(s5);
                chatcomponenttext2.setChatStyle(ichatcomponent2.getChatStyle().createShallowCopy());
                flag = true;
            }
            if (i + i2 <= p_178908_1_) {
                i += (int)i2;
                ichatcomponent.appendSibling((IChatComponent)chatcomponenttext2);
            }
            else {
                flag = true;
            }
            if (flag) {
                list.add(ichatcomponent);
                i = 0;
                ichatcomponent = (IChatComponent)new ChatComponentText("");
            }
        }
        list.add(ichatcomponent);
        return list;
    }
}