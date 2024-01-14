package net.kore.modules.combat;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.EntityUtils;
import net.kore.utils.MathUtils;
import net.kore.utils.rotation.Rotation;
import net.kore.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.List;

public class AimAssist extends Module
{
    public NumberSetting fov;
    public NumberSetting speed;
    public NumberSetting minSpeed;
    public NumberSetting range;
    public BooleanSetting vertical;
    public BooleanSetting players;
    public BooleanSetting mobs;
    public BooleanSetting invisibles;
    public BooleanSetting teams;

    public AimAssist() {
        super("Aim Assist", Category.COMBAT);
        this.fov = new NumberSetting("Fov", 60.0, 30.0, 180.0, 1.0);
        this.speed = new NumberSetting("Max speed", 30.0, 1.0, 40.0, 0.1) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (value < minSpeed.getValue()) {
                    this.setValue(minSpeed.getValue());
                }
            }
        };
        this.minSpeed = new NumberSetting("Min speed", 15.0, 1.0, 40.0, 0.1) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (this.getValue() > speed.getValue()) {
                    this.setValue(speed.getValue());
                }
            }
        };
        this.range = new NumberSetting("Range", 5.0, 0.0, 6.0, 0.1);
        this.vertical = new BooleanSetting("Vertical", true);
        this.players = new BooleanSetting("Players", true);
        this.mobs = new BooleanSetting("Mobs", false);
        this.invisibles = new BooleanSetting("Invisibles", false);
        this.teams = new BooleanSetting("Teams", true);
        this.addSettings(this.fov, this.range, this.minSpeed, this.speed, this.players, this.mobs, this.teams, this.invisibles, this.vertical);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.aimAssist = this;
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (this.isToggled()) {
            final Entity target = this.getTarget();
            if (target != null && Kore.mc.objectMouseOver != null && Kore.mc.objectMouseOver.entityHit != target) {
                final Rotation rotation = this.getRotation(target);
                final float yaw = Kore.mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(rotation.getYaw() - Kore.mc.thePlayer.rotationYaw);
                final float pitch = Kore.mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(rotation.getPitch() - Kore.mc.thePlayer.rotationPitch);
                final float diffY = (float)((yaw - Kore.mc.thePlayer.rotationYaw) / MathUtils.getRandomInRange(this.speed.getValue(), this.minSpeed.getValue()));
                final float diffP = (float)((pitch - Kore.mc.thePlayer.rotationPitch) / MathUtils.getRandomInRange(this.speed.getValue(), this.minSpeed.getValue()));
                Kore.mc.thePlayer.rotationYaw += diffY;
                if (this.vertical.isEnabled()) {
                    Kore.mc.thePlayer.rotationPitch += diffP;
                }
            }
        }
    }

    public Entity getTarget() {
        final List<Entity> validEntities = (List<Entity>)Kore.mc.theWorld.getEntities((Class) EntityLivingBase.class, entity -> this.isValid((EntityLivingBase)entity));
        validEntities.sort(Comparator.comparingDouble(entity -> Kore.mc.thePlayer.getDistanceToEntity(entity)));
        if (!validEntities.isEmpty()) {
            return validEntities.get(0);
        }
        return null;
    }

    private boolean isValid(final EntityLivingBase entity) {
        return entity != Kore.mc.thePlayer && AntiBot.isValidEntity((Entity)entity) && (this.invisibles.isEnabled() || !entity.isInvisible()) && !(entity instanceof EntityArmorStand) && Kore.mc.thePlayer.canEntityBeSeen((Entity)entity) && entity.getHealth() > 0.0f && entity.getDistanceToEntity((Entity)Kore.mc.thePlayer) <= this.range.getValue() && Math.abs(MathHelper.wrapAngleTo180_float(Kore.mc.thePlayer.rotationYaw) - MathHelper.wrapAngleTo180_float(this.getRotation((Entity)entity).getYaw())) <= this.fov.getValue() && ((!(entity instanceof EntityMob) && !(entity instanceof EntityAmbientCreature) && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntitySlime)) || this.mobs.isEnabled()) && (!(entity instanceof EntityPlayer) || ((!EntityUtils.isTeam(entity) || !this.teams.isEnabled()) && this.players.isEnabled())) && !(entity instanceof EntityVillager);
    }

    private Rotation getRotation(final Entity entity) {
        if (entity != null) {
            final Vec3 vec3 = Kore.mc.thePlayer.getPositionEyes(1.0f);
            final Vec3 vec4 = net.kore.utils.PlayerUtils.getVectorForRotation(Kore.mc.thePlayer.rotationYaw, Kore.mc.thePlayer.rotationPitch);
            final Vec3 vec5 = vec3.addVector(vec4.xCoord, vec4.yCoord, vec4.zCoord);
            return RotationUtils.getRotations(RotationUtils.getClosestPointInAABB(vec5, entity.getEntityBoundingBox()));
        }
        return null;
    }
}