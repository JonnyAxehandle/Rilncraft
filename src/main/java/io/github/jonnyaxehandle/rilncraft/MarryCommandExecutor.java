package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class MarryCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public MarryCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

        String targetName;
        try {
            targetName = args[0];
            return sendMarriageRequest( ( Player ) cs , targetName );
        } catch( ArrayIndexOutOfBoundsException ex ) {
            return displayMarriageInfo( ( Player ) cs );
        }
    }

    private boolean displayMarriageInfo( Player player ) {
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.spouse != null )
        {
            String spouseName;
            Player spouse = Bukkit.getPlayer(playerData.spouse);
            if( spouse != null )
            {
                spouseName = spouse.getDisplayName();
            } else
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerData.spouse);
                if( offlinePlayer != null )
                {
                    spouseName = offlinePlayer.getName();
                }
                else
                {
                    return true;
                }
            }
            player.sendMessage(String.format(Prefixes.marry + "You are married to %s",spouseName));
        } else {
            player.sendMessage(Prefixes.marry + "You are not married");
        }

        return true;
    }

    private boolean sendMarriageRequest(Player player , String targetName) {
        RCPlayer playerData = plugin.playerList.get( player );
        Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
        if( targetPlayer == null )
        {
            player.sendMessage( String.format( Prefixes.marry + "%s is not online", targetName) );
            return true;
        }
        
        RCPlayer target = plugin.playerList.get( targetPlayer );
        if( target.spouse != null )
        {
            player.sendMessage( String.format( Prefixes.marry + "%s is already married", targetName) );
            return true;
        }
        
        if( target.getRequest() != null )
        {
            player.sendMessage( String.format(Prefixes.marry + "%s already has a pending request", targetName) );
            return true;
        }
        
        MarriageRequest newRq = new MarriageRequest( plugin , playerData , target );
        
        target.setRequest(newRq);
        targetPlayer.sendMessage( String.format( Prefixes.marry + "%s has proposed to you",player.getDisplayName()) );
        targetPlayer.sendMessage("-- Type /accept to accept");
        targetPlayer.sendMessage("-- Type /deny to deny");
        
        return true;
    }
    
}
