package net.kore.utils;

import net.kore.Kore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.Vec3;

public class EntityUtils
{
    private static boolean isOnTeam(final EntityPlayer player) {
        for (final Score score : Kore.mc.thePlayer.getWorldScoreboard().getScores()) {
            if (score.getObjective().getName().equals("health") && score.getPlayerName().contains(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTeam(final EntityLivingBase e2) {
        if (!(e2 instanceof EntityPlayer) || e2.getDisplayName().getUnformattedText().length() < 4) {
            return false;
        }
        if (SkyblockUtils.isOnSkyBlock()) {
            return isOnTeam((EntityPlayer)e2);
        }
        return Kore.mc.thePlayer.getDisplayName().getFormattedText().charAt(2) == Kore.fancy && e2.getDisplayName().getFormattedText().charAt(2) == '?' && Kore.mc.thePlayer.getDisplayName().getFormattedText().charAt(3) == e2.getDisplayName().getFormattedText().charAt(3);
    }

    public static Vec3 getInterpolatedPos(Entity entity, float partialTicks)
    {
        return new Vec3(interpolate(entity.prevPosX, entity.posX, partialTicks), interpolate(entity.prevPosY, entity.posY, partialTicks), interpolate(entity.prevPosZ, entity.posZ, partialTicks));
    }

    public static double interpolate(final double prev, final double newPos, final float partialTicks) {
        return prev + (newPos - prev) * partialTicks;
    }
}
