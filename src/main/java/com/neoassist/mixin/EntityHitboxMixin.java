package com.neoassist.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.neoassist.module.impl.combat.Hitbox;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityHitboxMixin {
    @Inject(method = "getPickRadius", at = @At("RETURN"), cancellable = true, require = 0)
    private void neoassist$hitbox(CallbackInfoReturnable<Float> cir) {
        float extra = Hitbox.expand;
        if (extra > 0.0F) {
            cir.setReturnValue(cir.getReturnValueF() + extra);
        }
    }
}
