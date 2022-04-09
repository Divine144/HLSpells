package com.divinity.hlspells.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TotemActivatedPacket {
    private final UUID player;
    private final ItemStack stack;

    public TotemActivatedPacket(UUID player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public static void encode(TotemActivatedPacket msg, PacketBuffer buffer) {
        buffer.writeUUID(msg.player);
        buffer.writeItemStack(msg.stack, true);
    }

    public static TotemActivatedPacket decode(PacketBuffer buffer) {
        return new TotemActivatedPacket(buffer.readUUID(), buffer.readItem());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                Minecraft mc = Minecraft.getInstance();
                PlayerEntity totemActivatedPlayer = mc.level != null ? mc.level.getPlayerByUUID(player) : null;
                if (totemActivatedPlayer != null) {
                    mc.particleEngine.createTrackingEmitter(totemActivatedPlayer, ParticleTypes.TOTEM_OF_UNDYING, 30);
                    if (mc.player != null && mc.player.getUUID().equals(player)) {
                        Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }

}