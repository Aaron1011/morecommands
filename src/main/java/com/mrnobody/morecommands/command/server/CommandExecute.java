package com.mrnobody.morecommands.command.server;

import java.util.Arrays;
import java.util.List;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.util.TargetSelector;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

@Command(
		description = "command.execute.description",
		example = "command.execute.example",
		name = "command.execute.name",
		syntax = "command.execute.syntax",
		videoURL = "command.execute.videoURL"
		)
public class CommandExecute extends StandardCommand implements ServerCommandProperties {
	@Override
	public String getCommandName() {
		return "execute";
	}

	@Override
	public String getCommandUsage() {
		return "command.execute.syntax";
	}

	@Override
	public void execute(final CommandSender sender, String[] params) throws CommandException {
		params = reparseParamsWithNBTData(params); BlockPos relCoord = null;
		
		if (params.length <= 1)
			throw new CommandException("command.generic.invalidUsage", sender, this.getCommandName());
		
		if (params[1].equalsIgnoreCase("rel") || params[1].equalsIgnoreCase("relative")) {
			if (params.length <= 5) throw new CommandException("command.generic.invalidUsage", sender, this.getCommandName());
			
			try {relCoord = getCoordFromParams(sender.getMinecraftISender(), params, 2);}
			catch (NumberFormatException nfe) {}
		}
		
		String target = params[0]; params = Arrays.copyOfRange(params, relCoord == null ? 1 : 5, params.length);
		String command = rejoinParams(params);
		
		if (isTargetSelector(target) && target.startsWith("@b"))
			throw new CommandException("command.execute.invalidTarget", sender);
		else if (!isTargetSelector(target) && getPlayer(sender.getServer(), target) == null)
			throw new CommandException("command.execute.playerNotFound", sender);
		
		final BlockPos relCoord_f = relCoord;
		List<? extends Entity> entities = isTargetSelector(target) ? 
				TargetSelector.EntitySelector.matchEntites(sender.getMinecraftISender(), target, Entity.class) :
				Arrays.asList(getPlayer(sender.getServer(), target));
		
		for (final Entity entity : entities)
			sender.getServer().getCommandManager().executeCommand(new ICommandSender() {
				public String getName() {return entity.getName();}
				public ITextComponent getDisplayName() {return entity.getDisplayName();}
				public void addChatMessage(ITextComponent message) {sender.getMinecraftISender().addChatMessage(message);}
				public boolean canCommandSenderUseCommand(int permLevel, String commandName) {return sender.canUseCommand(permLevel, commandName);}
				public BlockPos getPosition(){return relCoord_f == null ? this.getCommandSenderEntity().getPosition() : relCoord_f;}
				public Vec3d getPositionVector() {return new Vec3d(this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ());}
				public World getEntityWorld() {return this.getCommandSenderEntity().worldObj;}
				public Entity getCommandSenderEntity() {return entity;}
				public boolean sendCommandFeedback() {return sender.getServer() == null || sender.getServer().worldServers[0].getGameRules().getBoolean("commandBlockOutput");}
				public void setCommandStat(CommandResultStats.Type type, int amount) {entity.setCommandStat(type, amount);}
				public MinecraftServer getServer() {return sender.getServer();}
            }, command);
	}

	@Override
	public CommandRequirement[] getRequirements() {
		return new CommandRequirement[0];
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
		return true;
	}
}
