package net.kore.utils;
import net.kore.Kore;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PacketUtils {
    public static void sendPacket(Packet<?> packet)
    {
        Kore.mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void logPacket(Packet<?> packet)
    {
        Field[] fields = packet.getClass().getDeclaredFields();

        System.out.println(packet.getClass().getTypeName());

        for (Field field : fields)
        {
            try
            {
                field.setAccessible(true);

                System.out.println(field.getName() + " -> " + field.get(packet));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public static ArrayList<Packet<?>> noEvent;

    public static void sendPacketNoEvent(final Packet<?> packet) {
        PacketUtils.noEvent.add(packet);
        Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)packet);
    }

    public static C03PacketPlayer.C06PacketPlayerPosLook getResponse(final S08PacketPlayerPosLook packet) {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();
        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
            x += Kore.mc.thePlayer.posX;
        }
        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
            y += Kore.mc.thePlayer.posY;
        }
        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
            z += Kore.mc.thePlayer.posZ;
        }
        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += Kore.mc.thePlayer.rotationPitch;
        }
        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += Kore.mc.thePlayer.rotationYaw;
        }
        return new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, yaw % 360.0f, pitch % 360.0f, false);
    }

    public static String packetToString(final Packet<?> packet) {
        final StringBuilder postfix = new StringBuilder();
        boolean first = true;
        for (final Field field : packet.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                postfix.append(first ? "" : ", ").append(field.get(packet));
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            first = false;
        }
        return packet.getClass().getSimpleName() + String.format("{%s}", postfix);
    }

    static {
        PacketUtils.noEvent = new ArrayList<Packet<?>>();
    }
}