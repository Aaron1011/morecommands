package com.mrnobody.morecommands.command.server;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@Command(
		name = "duplicate",
		description = "command.duplicate.description",
		example = "command.duplicate.example",
		syntax = "command.duplicate.syntax",
		videoURL = "command.duplicate.videoURL"
		)
public class CommandDuplicate extends StandardCommand implements ServerCommandProperties {

	@Override
	public String getCommandName() {
		return "duplicate";
	}

	@Override
	public String getCommandUsage() {
		return "command.duplicate.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayerMP player = getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class);
		
		if (params.length > 0 && params[0].equalsIgnoreCase("all")) {
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				if (player.inventory.getStackInSlot(i) == null) continue;
				
				ItemStack item = player.inventory.getStackInSlot(i);
				ItemStack duplicate = new ItemStack(item.getItem(), item.stackSize, item.getItemDamage());
				if (item.getTagCompound() != null) duplicate.setTagCompound((NBTTagCompound) item.getTagCompound().copy());
				
				EntityItem itemEntity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, duplicate);
				player.worldObj.spawnEntityInWorld(itemEntity);
			}
		}
		else {
			if (player.getHeldItemMainhand() == null)
				throw new CommandException("command.duplicate.notSelected", sender);
			ItemStack item = player.getHeldItemMainhand();
			ItemStack duplicate = new ItemStack(item.getItem(), item.stackSize, item.getItemDamage());
			if (item.getTagCompound() != null) duplicate.setTagCompound((NBTTagCompound) item.getTagCompound().copy());
			EntityItem itemEntity = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, duplicate);
			player.worldObj.spawnEntityInWorld(itemEntity);
		}
		
		sender.sendLangfileMessage("command.duplicate.duplicated");
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
