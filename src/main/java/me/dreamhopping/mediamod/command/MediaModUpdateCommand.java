package me.dreamhopping.mediamod.command;

import me.dreamhopping.mediamod.util.ChatColor;
import me.dreamhopping.mediamod.util.PlayerMessenger;
import me.dreamhopping.mediamod.util.UpdaterUtility;
import me.dreamhopping.mediamod.util.VersionChecker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

/**
 * The client-side command to update MediaMod
 *
 * @see net.minecraft.command.ICommand
 */
public class MediaModUpdateCommand extends CommandBase {

    @Override
    public String getName() {
        return "mediamodupdate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mediamodupdate";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("mmupdate");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (VersionChecker.INSTANCE.isLatestVersion) {
            PlayerMessenger.sendMessage(ChatColor.GRAY + "MediaMod is up-to-date!", true);
        } else {
            UpdaterUtility utility = new UpdaterUtility();
            utility.scheduleUpdate();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
