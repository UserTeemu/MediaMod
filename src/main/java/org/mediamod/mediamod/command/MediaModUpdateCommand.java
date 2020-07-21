package org.mediamod.mediamod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.util.*;

import java.io.File;
import java.net.URL;
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
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
