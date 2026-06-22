package com.neoassist.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.neoassist.module.impl.misc.TimerModule;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.DeltaTracker;

@Mixin(DeltaTracker.Timer.class)
public class TimerMixin {
    @Redirect(
            method = "advanceGameTime",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/floats/FloatUnaryOperator;apply(F)F"),
            require = 0)
    private float neoassist$timer(FloatUnaryOperator provider, float value) {
        float base = provider.apply(value);
        if (TimerModule.active && TimerModule.multiplier > 0) {
            return base / TimerModule.multiplier;
        }
        return base;
    }
}
