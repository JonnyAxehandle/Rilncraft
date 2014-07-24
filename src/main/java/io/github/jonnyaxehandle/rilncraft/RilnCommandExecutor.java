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
class RilnCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public RilnCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        Player player = ( Player ) sender;
        
        String subCommand;
        try
        {
            subCommand = args[0];
        } catch( ArrayIndexOutOfBoundsException e )
        {
            return showBalance( player );
        }
        
        switch( subCommand )
        {
            case "pay":
                
                int amount;
                String targetName;
                try {
                    targetName = args[1];
                    amount = Integer.parseInt( args[2] );
                } catch( NumberFormatException ex ) {
                    sender.sendMessage("Amount must be a number");
                    return true;
                } catch( ArrayIndexOutOfBoundsException ex ) {
                    return false;
                }
                return pay( player , targetName , amount );
        }
        
        return false;
    }

    private boolean showBalance(Player player) {
        RCPlayer playerData = plugin.playerList.get( player );
        playerData.sendBalance();
        return true;
    }

    private boolean pay(Player player, String targetName, int amount) {
        RCPlayer playerData = plugin.playerList.get(player);
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if( targetPlayer == null )
        {
            player.sendMessage( String.format("%s is not online", targetName) );
            return true;
        }
        
        RCPlayer target = plugin.playerList.get( targetPlayer );
        
        if( !playerData.chargeRiln(amount) )
        {
            player.sendMessage("Not enough Riln");
            return true;
        }
        
        target.addRiln(amount);
        
        player.sendMessage( String.format( Prefixes.rc + "You paid %s %d Riln",targetName,amount) );
        targetPlayer.sendMessage( String.format( Prefixes.rc + "%s gave you %d Riln",player.getName(),amount) );
        
        playerData.sendBalance();
        target.sendBalance();
        
        playerData.saveConfig();
        target.saveConfig();
        
        return true;
    }
    
}
