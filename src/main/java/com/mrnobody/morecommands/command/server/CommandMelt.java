package com.mrnobody.morecommands.command.server;

import com.mrnobody.morecommands.command.Command;
import com.mrnobody.morecommands.command.CommandRequirement;
import com.mrnobody.morecommands.command.ServerCommandProperties;
import com.mrnobody.morecommands.command.StandardCommand;
import com.mrnobody.morecommands.core.MoreCommands.ServerType;
import com.mrnobody.morecommands.wrapper.CommandException;
import com.mrnobody.morecommands.wrapper.CommandSender;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

@Command(
		name = "melt",
		description = "command.melt.description",
		example = "command.melt.example",
		syntax = "command.melt.syntax",
		videoURL = "command.melt.videoURL"
		)
public class CommandMelt extends StandardCommand implements ServerCommandProperties {

	@Override
	public String getCommandName() {
		return "melt";
	}

	@Override
	public String getCommandUsage() {
		return "command.melt.syntax";
	}

	@Override
	public void execute(CommandSender sender, String[] params)throws CommandException {
		boolean all = false;
		if (params.length > 0 && params[0].equalsIgnoreCase("all")) all = true;
		
		EntityPlayerMP player = getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class);
		ItemStack result;
		int smelt = 0;
		
		if (!all) {
			if (player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == null) return;
			result = FurnaceRecipes.instance().getSmeltingResult(player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND));
			if (result != null) player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(result.getItem(),
								player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).stackSize, result.getItemDamage()));
		}
		else {
			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
				if (player.inventory.getStackInSlot(i) == null) continue;
				result = FurnaceRecipes.instance().getSmeltingResult(player.inventory.getStackInSlot(i));
				if (result != null) player.inventory.setInventorySlotContents(i, new ItemStack(result.getItem(),
									player.inventory.getStackInSlot(i).stackSize, result.getItemDamage()));
				if (result != null) smelt++;
			}
		}
		
		sender.sendLangfileMessage("command.melt.molten", smelt);
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
