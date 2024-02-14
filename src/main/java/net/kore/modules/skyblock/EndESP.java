package net.kore.modules.skyblock;

import net.kore.Kore;
import net.kore.events.PacketReceivedEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.utils.objects.BlockPosition;
import net.kore.utils.render.RenderUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class EndESP extends Module
{
    public BooleanSetting nodes;
    public BooleanSetting nodesTracer;

    public EndESP() {
        super("End ESP", Category.SKYBLOCK);
        this.nodes = new BooleanSetting("Ender Nodes", true);
        this.nodesTracer = new BooleanSetting("(Nodes) Tracer", true, a -> !nodes.isEnabled());
        this.addSettings(this.nodes, this.nodesTracer);
    }

    @Override
    public void assign()
    {
        Kore.endESP = this;
    }

    public static final ConcurrentHashMap<BlockPosition, Long> enderNodes = new ConcurrentHashMap<>();

    @SubscribeEvent(receiveCanceled = true)
    public void onParticle(PacketReceivedEvent event) {
        if (!this.isToggled() || !this.nodes.isEnabled()) return;
        if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            if (packet.getParticleType().equals(EnumParticleTypes.PORTAL)) {
                double x = packet.getXCoordinate();
                double y = packet.getYCoordinate();
                double z = packet.getZCoordinate();

                if (x % 1 == 0.25) {
                    x += 1;
                } else if (x % 1 == 0.75) {
                    x -= 1;
                }

                if (y % 1 == 0.25) {
                    y -= 1;
                } else if (y % 1 == 0.75) {
                    y += 1;
                }

                if (z % 1 == 0.25) {
                    z += 1;
                } else if (z % 1 == 0.75) {
                    z -= 1;
                }

                BlockPosition blockPosition = new BlockPosition((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

                IBlockState blockState = Kore.mc.theWorld.getBlockState(blockPosition.toBlockPos());
                if (blockState.getBlock() != Blocks.air && blockState.getBlock() != Blocks.bedrock) {
                    enderNodes.put(blockPosition, System.currentTimeMillis());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!this.isToggled() || Kore.mc.thePlayer == null) return;

        HashSet<BlockPosition> toRemove = new HashSet<>();

        enderNodes.forEach((blockPosition, timestamp) -> {
            if (System.currentTimeMillis() - timestamp > 2000) {
                toRemove.add(blockPosition);
            }
        });

        toRemove.forEach(enderNodes::remove);

        for (BlockPosition blockPosition : enderNodes.keySet()) {
            RenderUtils.renderEspBox(blockPosition.toBlockPos(), event.partialTicks, Kore.themeManager.getSecondaryColor().getRGB());
            if(this.nodesTracer.isEnabled()) {
                RenderUtils.tracerLine(blockPosition.x + 0.5, blockPosition.y + 0.5, blockPosition.z + 0.5, Kore.themeManager.getSecondaryColor());
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        enderNodes.clear();
    }
}