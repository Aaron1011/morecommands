package com.mrnobody.morecommands.command.server;

import java.text.DecimalFormat;
import java.util.Iterator;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.util.ServerPlayerSettings;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Command(
		name = "waypoint",
		description = "command.waypoint.description",
		example = "command.waypoint.example",
		syntax = "command.waypoint.syntax",
		videoURL = "command.waypoint.videoURL"
		)
public class CommandWaypoint extends StandardCommand implements ServerCommandProperties {
	private class NotFoundException extends Exception {}
	
	@Override
	public String getCommandName() {
		return "waypoint";
	}

	@Override
	public String getCommandUsage() {
		return "command.waypoint.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		ServerPlayerSettings settings = getPlayerSettings(getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class));
		
		if (params.length > 1) {
			Player player = new Player(getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class));
			
			if (params[0].equalsIgnoreCase("set")) {
				double x = player.getPosition().getX();
				double y = player.getPosition().getY();
				double z = player.getPosition().getZ();
					
				if (params.length > 4) {
					try {
						x = Double.parseDouble(params[2]);
						y = Double.parseDouble(params[3]);
						z = Double.parseDouble(params[4]);
					}
					catch (NumberFormatException nfe) {throw new CommandException("command.waypoint.NAN", sender);}
				}
				String name = params[1];
				double[] data = new double[] {x, y, z, (double) player.getYaw(), (double) player.getPitch()};
				this.setWaypoint(settings, name, data, sender.getMinecraftISender());
				
				DecimalFormat f = new DecimalFormat("#.##");
					
				sender.sendStringMessage("Waypoint '" + name + "' successfully set at: "
						+ " X = " + f.format(x)
						+ "; Y = " + f.format(y)
						+ "; Z = " + f.format(z));
			}
			else if (params[0].equalsIgnoreCase("rem") || params[0].equalsIgnoreCase("remove") || params[0].equalsIgnoreCase("del") || params[0].equalsIgnoreCase("delete")) {
				try {this.deleteWaypoint(settings, params[1], sender.getMinecraftISender()); sender.sendLangfileMessage("command.waypoint.removed", params[1]);}
				catch (NotFoundException nfe) {throw new CommandException("command.waypoint.notFound", sender, params[1]);}
			}
			else if (params[0].equalsIgnoreCase("goto")) {
				double[] data;
				try {data = this.getWaypoint(settings, params[1], sender.getMinecraftISender());}
				catch (NotFoundException nfe) {throw new CommandException("command.waypoint.notFound", sender, params[1]);}
				
				player.setPosition(new BlockPos(data[0], data[1], data[2]));
				player.setYaw((float) data[3]);
				player.setPitch((float) data[4]);
				
				sender.sendLangfileMessage("command.waypoint.teleported", params[1]);
			}
			else throw new CommandException("command.waypoint.invalidArgs", sender);
		}
		else if (params.length > 0 && params[0].equalsIgnoreCase("list")) {
			if (settings.waypoints == null) return;
			
			Iterator<String> names = settings.waypoints.keySet().iterator();
			DecimalFormat f = new DecimalFormat("#.##");
			
			while (names.hasNext()) {
				String name = names.next();
				double[] data = settings.waypoints.get(name);
				
				sender.sendStringMessage("- '" + name + "' (X = " + f.format(data[0]) + "; Y = " + f.format(data[1]) + "; Z = " + f.format(data[2]) + ")");
			}
		}
		else throw new CommandException("command.waypoint.invalidArgs", sender);
	}

	private void setWaypoint(ServerPlayerSettings settings, String name, double[] data, ICommandSender sender) {
		settings.waypoints = settings.putAndUpdate("waypoints", name, data, double[].class, true);
		
		if (settings.hasModifiedCompassTarget && name.equals(settings.waypointCompassTarget) && sender instanceof EntityPlayerMP)
			MoreCommands.INSTANCE.getPacketDispatcher().sendS13SetCompassTarget(getSenderAsEntity(sender, EntityPlayerMP.class), MathHelper.floor_double(data[0]), MathHelper.floor_double(data[2]));
	}
	
	private void deleteWaypoint(ServerPlayerSettings settings, String name, ICommandSender sender) throws NotFoundException {
		if (!settings.waypoints.containsKey(name)) throw new NotFoundException();
		settings.waypoints = settings.removeAndUpdate("waypoints", name, double[].class, true);
		
		if (settings.hasModifiedCompassTarget && name.equals(settings.waypointCompassTarget) && sender instanceof EntityPlayerMP) {
			MoreCommands.INSTANCE.getPacketDispatcher().sendS13ResetCompassTarget(getSenderAsEntity(sender, EntityPlayerMP.class));
			settings.hasModifiedCompassTarget = false;
			settings.waypointCompassTarget = null;
		}
	}
	
	private double[] getWaypoint(ServerPlayerSettings settings, String name, ICommandSender sender) throws NotFoundException {
		if (!settings.waypoints.containsKey(name)) throw new NotFoundException();
		return settings.waypoints.get(name);
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
		return isSenderOfEntityType(sender, EntityPlayerMP.class);
	}
}
