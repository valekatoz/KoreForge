package net.kore.modules.render;


import net.kore.Kore;
import net.kore.events.RenderLayersEvent;
import net.kore.modules.Module;
import net.kore.modules.combat.AntiBot;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.render.MobRenderUtils;
import net.kore.utils.OutlineUtils;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class PlayerESP extends Module
{
    public ModeSetting mode;
    public NumberSetting opacity;
    private EntityPlayer lastRendered;

    public PlayerESP() {
        super("Player ESP", Category.RENDER);
        this.mode = new ModeSetting("Mode", "2D", new String[] { "Outline", "2D", "Chams", "Box", "Tracers" });
        this.opacity = new NumberSetting("Opacity", 255.0, 0.0, 255.0, 1.0) {
            @Override
            public boolean isHidden() {
                return !PlayerESP.this.mode.is("Chams");
            }
        };
        this.addSettings(this.mode, this.opacity);
    }

    @Override
    public void assign()
    {
        Kore.playerEsp = this;
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (!this.isToggled() || (!this.mode.getSelected().equals("2D") && !this.mode.getSelected().equals("Box") && !this.mode.getSelected().equals("Tracers"))) {
            return;
        }

        final Color color = Kore.clickGui.getColor();

        for (final EntityPlayer entityPlayer : Kore.mc.theWorld.playerEntities) {
            if (this.isValidEntity(entityPlayer) && entityPlayer != Kore.mc.thePlayer) {
                final String selected = this.mode.getSelected();
                switch (selected) {
                    case "2D": {
                        RenderUtils.draw2D((Entity)entityPlayer, event.partialTicks, 1.0f, color);
                        continue;
                    }
                    case "Box": {
                        RenderUtils.entityESPBox((Entity)entityPlayer, event.partialTicks, color);
                        continue;
                    }
                    case "Tracers": {
                        RenderUtils.tracerLine((Entity)entityPlayer, event.partialTicks, 1.0f, color);
                        continue;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(final RenderLayersEvent event) {
        final Color color = Kore.clickGui.getColor();
        if (this.isToggled() && event.entity instanceof EntityPlayer && this.isValidEntity((EntityPlayer)event.entity) && event.entity != Kore.mc.thePlayer && this.mode.getSelected().equals("Outline")) {
            OutlineUtils.outlineESP(event, color);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderLiving(final RenderLivingEvent.Pre event) {
        if (this.lastRendered != null) {
            this.lastRendered = null;
            RenderUtils.disableChams();
            MobRenderUtils.unsetColor();
        }
        if (!(event.entity instanceof EntityOtherPlayerMP) || !this.mode.getSelected().equals("Chams") || !this.isToggled()) {
            return;
        }
        final Color color = RenderUtils.applyOpacity(Kore.clickGui.getColor(), (int)this.opacity.getValue());
        RenderUtils.enableChams();
        MobRenderUtils.setColor(color);
        this.lastRendered = (EntityPlayer)event.entity;
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onRenderLivingPost(final RenderLivingEvent.Specials.Pre event) {
        if (event.entity == this.lastRendered) {
            this.lastRendered = null;
            RenderUtils.disableChams();
            MobRenderUtils.unsetColor();
        }
    }

    private boolean isValidEntity(final EntityPlayer player) {
        return AntiBot.isValidEntity((Entity)player) && player.getHealth() > 0.0f && !player.isDead;
    }
}