package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class AcceptCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public AcceptCommandExecutor(Rilncraft rc) {
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
        
        playerData.getRequest().accept();
        playerData.setRequest(null);
        
        return true;
    }
    
}
