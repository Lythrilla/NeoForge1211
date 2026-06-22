package com.neoassist.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.neoassist.module.impl.render.NameTags;

import net.minecraft.client.renderer.entity.EntityRenderer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @ModifyConstant(method = "renderNameTag", constant = {
            @Constant(floatValue = 0.025F),
            @Constant(floatValue = -0.025F)
    }, require = 0)
    private float neoassist$scaleNameTag(float original) {
        return NameTags.active ? original * NameTags.scale : original;
    }
}
