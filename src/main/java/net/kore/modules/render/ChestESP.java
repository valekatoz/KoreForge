package net.kore.modules.render;

import net.kore.Kore;
import net.kore.events.RenderChestEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.render.MobRenderUtils;
import net.kore.utils.render.RenderUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.tileentity.TileEntity;

import java.awt.*;
import java.util.stream.Collectors;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ChestESP extends Module
{
    public BooleanSetting tracer;
    public NumberSetting opacity;
    private boolean hasRendered;

    public ChestESP() {
        super("Chest ESP", Category.RENDER);
        this.opacity = new NumberSetting("Chams Opacity", 255.0, 0.0, 255.0, 1.0);
        this.tracer = new BooleanSetting("Tracers", true);
        this.addSettings(this.opacity, this.tracer);
    }

    @Override
    public void assign()
    {
        Kore.chestEsp = this;
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (!this.isToggled() || !this.tracer.isEnabled()) {
            return;
        }
        for (final TileEntity tileEntityChest : Kore.mc.theWorld.loadedTileEntityList.stream().filter(tileEntity -> tileEntity instanceof TileEntityChest).collect(Collectors.toList())) {
            RenderUtils.tracerLine(tileEntityChest.getPos().getX() + 0.5, tileEntityChest.getPos().getY() + 0.5, tileEntityChest.getPos().getZ() + 0.5, Kore.clickGui.getColor());
        }
    }

    @SubscribeEvent
    public void onRenderChest(final RenderChestEvent event) {
        if (this.isToggled()) {
            if (event.isPre() && event.getChest() == Kore.mc.theWorld.getTileEntity(event.getChest().getPos())) {
                final Color color = RenderUtils.applyOpacity(Kore.clickGui.getColor(), (int)this.opacity.getValue());
                RenderUtils.enableChams();
                MobRenderUtils.setColor(color);
                this.hasRendered = true;
            }
            else if (this.hasRendered) {
                RenderUtils.disableChams();
                MobRenderUtils.unsetColor();
                this.hasRendered = false;
            }
        }
    }
}
