package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class BalanceCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public BalanceCommandExecutor( Rilncraft rc )
    {
        plugin = rc;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] strings) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        RCPlayer playerData = plugin.playerList.get( ( Player ) sender );
        
        playerData.sendBalance();
        
        return true;
    }
    
}
