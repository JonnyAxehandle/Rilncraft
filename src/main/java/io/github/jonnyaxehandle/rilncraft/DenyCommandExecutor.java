/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class DenyCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public DenyCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] strings) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        RCPlayer playerData = plugin.playerList.get( (Player) sender );
        if( playerData.getRequest() == null )
        {
            sender.sendMessage("No pending requests");
            return true;
        }
        
        playerData.getRequest().deny();
        playerData.setRequest(null);
        
        return true;
    }
    
}
