package com.neoassist.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.neoassist.module.impl.misc.FakeLag;
import com.neoassist.util.LagHelper;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(
            method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;Z)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0)
    private void neoassist$fakeLag(Packet<?> packet, PacketSendListener listener, boolean flush, CallbackInfo ci) {
        if (LagHelper.flushing || !FakeLag.active) {
            return;
        }
        if (packet instanceof ServerboundMovePlayerPacket) {
            Connection self = (Connection) (Object) this;
            LagHelper.queue(() -> self.send(packet, listener, flush));
            ci.cancel();
        }
    }
}
