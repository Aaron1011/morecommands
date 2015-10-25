package com.mrnobody.morecommands.command.server;

import java.util.Iterator;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.ServerCommand;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;
import com.mrnobody.morecommands.wrapper.Coordinate;

@Command(
		name = "defuse",
		description = "command.defuse.description",
		example = "command.defuse.example",
		syntax = "command.defuse.syntax",
		videoURL = "command.defuse.videoURL"
		)
public class CommandDefuse extends ServerCommand {

	@Override
	public String getCommandName() {
		return "defuse";
	}

	@Override
	public String getUsage() {
		return "command.defuse.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		double radius = 16;
		
		if (params.length > 0) {
			try {radius = Double.parseDouble(params[0]);}
			catch (NumberFormatException nfe) {throw new CommandException("command.defuse.invalidArg", sender);}
		}
		
		World world = sender.getWorld().getMinecraftWorld();
		Coordinate pos = sender.getPosition();
		
		Iterator<Entity> tntPrimedIterator = world.getEntitiesWithinAABB(EntityTNTPrimed.class, AxisAlignedBB.getBoundingBox(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius)).iterator();
	
		while (tntPrimedIterator.hasNext()) {
			Entity tntPrimed = tntPrimedIterator.next();
			tntPrimed.setDead();
			
			EntityItem tnt = new EntityItem(world, tntPrimed.posX, tntPrimed.posY, tntPrimed.posZ, new ItemStack((Item) Item.itemRegistry.getObject("tnt"), 1));
			world.spawnEntityInWorld(tnt);
		}
		
		sender.sendLangfileMessage("command.defuse.defused");
	}
	
	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}

	@Override
	public ServerType getAllowedServerType() {
		return ServerType.ALL;
	}
	
	@Override
	public int getPermissionLevel() {
		return 2;
	}
	
	@Override
	public boolean canSenderUse(ICommandSender sender) {
		return true;
	}
}
