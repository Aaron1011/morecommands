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
import net.minecraft.item.ItemStack;

@Command(
		name = "stackcombine",
		description = "command.stackcombine.description",
		example = "command.stackcombine.example",
		syntax = "command.stackcombine.syntax",
		videoURL = "command.stackcombine.videoURL"
		)
public class CommandStackcombine extends StandardCommand implements ServerCommandProperties {
	public String getCommandName() {
		return "stackcombine";
	}
    
	public String getCommandUsage() {
		return "command.stackcombine.syntax";
	}
    
	public void execute(CommandSender sender, String[] params) throws CommandException {
		EntityPlayerMP player = getSenderAsEntity(sender.getMinecraftISender(), EntityPlayerMP.class);
		
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack sloti = player.inventory.getStackInSlot(i);
			if (sloti == null) continue;
			
            for (int j = i + 1; j < player.inventory.getSizeInventory(); j++) {
            	ItemStack slotj = player.inventory.getStackInSlot(i);
            	if (slotj == null) continue;
            	
            	if (sloti.isItemEqual(slotj)) {
            		if (sloti.stackSize + slotj.stackSize > sloti.getMaxStackSize()) {
            			int noItems = sloti.stackSize + slotj.stackSize;
            			sloti.stackSize = sloti.getMaxStackSize(); noItems -= sloti.getMaxStackSize();
            			slotj.stackSize = noItems > sloti.getMaxStackSize() ? sloti.getMaxStackSize() : noItems;
            			noItems -= sloti.getMaxStackSize();
            		}
            		else {
            			sloti.stackSize += slotj.stackSize;
            			player.inventory.setInventorySlotContents(j, null);
            		}
            	}
            }
		}
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
		return 0;
	}
	
	@Override
	public boolean canSenderUse(String commandName, ICommandSender sender, String[] params) {
		return isSenderOfEntityType(sender, EntityPlayerMP.class);
	}
}
