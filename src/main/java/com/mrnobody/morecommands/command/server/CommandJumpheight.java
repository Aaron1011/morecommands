package com.mrnobody.morecommands.command.server;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.patch.EntityPlayerMP;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import net.minecraft.command.ICommandSender;

@Command(
		name = "jumpheight",
		description = "command.jumpheight.description",
		example = "command.jumpheight.example",
		syntax = "command.jumpheight.syntax",
		videoURL = "command.jumpheight.videoURL"
		)
public class CommandJumpheight extends StandardCommand implements ServerCommandProperties {
	@Override
	public String getCommandName() {
		return "jumpheight";
	}

	@Override
	public String getCommandUsage() {
		return "command.jumpheight.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayerMP player = getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class);
		float gravity;
		
		if (params.length > 0) {
			if (params[0].equalsIgnoreCase("reset")) {gravity = 1F; sender.sendLangfileMessage("command.jumpheight.reset");}
			else {
				try {
					gravity = Float.parseFloat(params[0]);
					sender.sendLangfileMessage("command.jumpheight.success");
				}
				catch (NumberFormatException nfe) {throw new CommandException("command.jumpheight.NAN", sender);}
			}
			
			player.setGravity(gravity);
			MoreCommands.INSTANCE.getPacketDispatcher().sendS09Gravity(player, gravity);
		}
		else 
			throw new CommandException("command.generic.invalidUsage", sender, this.getCommandName());
	}

	@Override
	public CommandRequirement[] getRequirements() {
		return new CommandRequirement[] {CommandRequirement.MODDED_CLIENT, CommandRequirement.PATCH_ENTITYPLAYERSP, CommandRequirement.PATCH_ENTITYPLAYERMP};
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}

	@Override
	public int getDefaultPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean canSenderUse(String commandName, ICommandSender sender, String[] params) {
		return isSenderOfEntityType(sender, EntityPlayerMP.class);
	}
}
