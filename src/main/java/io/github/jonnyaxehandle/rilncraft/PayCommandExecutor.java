package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class PayCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public PayCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        int amount;
        String targetName;
        try {
            targetName = args[0];
            amount = Integer.parseInt( args[1] );
        } catch( NumberFormatException ex ) {
            sender.sendMessage("Amount must be a number");
            return true;
        } catch( ArrayIndexOutOfBoundsException ex ) {
            return false;
        }
        
        RCPlayer playerData = plugin.playerList.get((Player) sender);
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if( targetPlayer == null )
        {
            sender.sendMessage( String.format("%s is not online", targetName) );
            return true;
        }
        
        RCPlayer target = plugin.playerList.get( targetPlayer );
        
        if( !playerData.chargeRiln(amount) )
        {
            sender.sendMessage("Not enough Riln");
            return true;
        }
        
        target.addRiln(amount);
        
        sender.sendMessage( String.format("You paid %s %d Riln",targetName,amount) );
        targetPlayer.sendMessage( String.format("%s gave you %d Riln",sender.getName(),amount) );
        
        playerData.sendBalance();
        target.sendBalance();
        
        playerData.saveConfig();
        target.saveConfig();
        
        return true;
    }
    
}
