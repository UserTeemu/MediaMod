package org.mediamod.mediamod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerMessenger {

    public static final PlayerMessenger INSTANCE = new PlayerMessenger();
    private static final ConcurrentLinkedQueue<String> queuedMessages = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ITextComponent> messages = new ConcurrentLinkedQueue<>();

    private PlayerMessenger() {
        TickScheduler.INSTANCE.schedule(0, this::check);
    }

    public static void sendMessage(ITextComponent message) {
        if (Minecraft.getMinecraft().player == null) return;
        if (message == null) message = new TextComponentString("");
        messages.add(message);
    }

    public static void sendMessage(ITextComponent message, boolean header) {
        if (Minecraft.getMinecraft().player == null) return;
        if (message == null) message = new TextComponentString("");

        if (header) {
            messages.add(new TextComponentString(ChatColor.translateAlternateColorCodes('&',
                    "&c[&fMediaMod&c]&r ")).appendSibling(message));
        } else {
            messages.add(message);
        }
    }

    public static void sendMessage(String message) {
        if (message == null || Minecraft.getMinecraft().player == null) return;
        sendMessage(ChatColor.translateAlternateColorCodes('&', message), true);
    }

    public static void sendMessage(String message, boolean header) {
        if (message == null || Minecraft.getMinecraft().player == null) return;

        if (header) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ChatColor.translateAlternateColorCodes('&',
                    "&c[&fMediaMod&c]&r " + message)));
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }

    private void check() {
        if (!queuedMessages.isEmpty()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null && queuedMessages.peek() != null) {
                String poll = queuedMessages.poll();
                sendMessage(poll);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().player == null) {
            return;
        }

        while (!messages.isEmpty()) {
            Minecraft.getMinecraft().player.sendMessage(messages.poll());
        }

        while (!queuedMessages.isEmpty()) {
            sendMessage(queuedMessages.poll());
        }
    }
}
