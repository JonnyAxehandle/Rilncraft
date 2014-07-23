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
class DivorceCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public DivorceCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        Player player = (Player) cs;
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.spouse != null )
        {
            String spouseName;
            Player spousePlayer = Bukkit.getPlayer(playerData.spouse);
            if( spousePlayer != null )
            {
                spouseName = spousePlayer.getDisplayName();
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
            
            RCPlayer spouse = plugin.playerList.get(playerData.spouse);
            
            player.sendMessage(String.format("You are no longer married to %s",spouseName));
            
            if( spousePlayer != null )
            {
                spousePlayer.sendMessage(String.format("%s has divorced you!",player.getDisplayName()));
            }
            
            int newBalance = playerData.getRiln() / 2;
            
            playerData.setBalance( newBalance );
            spouse.setBalance( newBalance );
            
            playerData.spouse = null;
            spouse.spouse = null;
            
        } else {
            player.sendMessage("You are not married");
        }

        return true;
    }
    
}
