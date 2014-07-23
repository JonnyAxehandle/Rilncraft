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
class TPRCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public TPRCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        String targetName;
        try {
            targetName = args[0];
        } catch( ArrayIndexOutOfBoundsException ex ) {
            return false;
        }
        
        RCPlayer playerData = plugin.playerList.get((Player) sender);
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if( targetPlayer == null )
        {
            sender.sendMessage( String.format(Prefixes.teleport + "%s is not online", targetName) );
            return true;
        }
        
        RCPlayer target = plugin.playerList.get( targetPlayer );
        
        if( playerData.getRelationTo(target) == PlayerRelation.SPOUSE )
        {
            ( (Player) sender ).teleport( targetPlayer.getLocation() );
            return true;
        }
        
        if( target.getRequest() != null )
        {
            sender.sendMessage( String.format(Prefixes.teleport + "%s already has a pending request", targetName) );
            return true;
        }
        
        int cost = 25;
        
        if( playerData.getRelationTo(target) == PlayerRelation.FRIEND )
        {
            cost = 10;
        }
        
        if( !playerData.chargeRiln( cost ) )
        {
            sender.sendMessage( String.format( Prefixes.teleport + "%d Riln is required to teleport" , cost ) );
            return true;
        }
        
        TPRequest newRq = new TPRequest( plugin , playerData , target );
        
        newRq.setRefund(cost);
        
        targetPlayer.sendMessage( String.format(Prefixes.teleport + "%s has requested to teleport to you",sender.getName()) );
        targetPlayer.sendMessage("-- Type /accept to accept teleport");
        targetPlayer.sendMessage("-- Type /deny to deny teleport");
        
        target.setRequest(newRq);
        
        sender.sendMessage(Prefixes.teleport + "Request was sent at the cost of "+cost+" Riln.");
        playerData.sendBalance();
        
        return false;
    }
    
}
