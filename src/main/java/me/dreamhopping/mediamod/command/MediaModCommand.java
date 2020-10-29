package me.dreamhopping.mediamod.command;

import me.dreamhopping.mediamod.gui.GuiMediaModSettings;
import me.dreamhopping.mediamod.util.TickScheduler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.util.Arrays;
import java.util.List;

/**
 * The client-side command to open the MediaMod GUI
 *
 * @see net.minecraft.command.ICommand
 */
public class MediaModCommand extends CommandBase {

    @Override
    public String getName() {
        return "mediamod";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mediamod";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("media", "mm");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            TickScheduler.INSTANCE.schedule(1, () -> FMLClientHandler.instance().getClient().displayGuiScreen(new GuiMediaModSettings()));
        } else {
            String subbcommand = args[0];
            if (subbcommand.equals("debug")) {
                String className = args[1];
                try {
                    GuiScreen screen = (GuiScreen) Class.forName(className).newInstance();
                    TickScheduler.INSTANCE.schedule(1, () -> FMLClientHandler.instance().getClient().displayGuiScreen(screen));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
