package com.neoassist.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class RotationUtil {
    private RotationUtil() {
    }

    /** Returns {yaw, pitch} from an eye position to a target point. */
    public static float[] getRotations(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float pitch = (float) (-(Mth.atan2(dy, dist) * (180.0 / Math.PI)));
        return new float[] { Mth.wrapDegrees(yaw), Mth.clamp(pitch, -90.0F, 90.0F) };
    }

    public static float[] getRotationsToEntity(Vec3 eyes, Entity target) {
        Vec3 center = target.position().add(0, target.getBbHeight() * 0.5, 0);
        return getRotations(eyes, center);
    }
}
