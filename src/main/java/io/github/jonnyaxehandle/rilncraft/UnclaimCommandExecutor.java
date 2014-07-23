package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class UnclaimCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public UnclaimCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        Location location = ( (Player) sender ).getLocation();
        ClaimedRegion region = plugin.claimedRegions.getRegion(location);
        RCPlayer playerData = plugin.playerList.get((Player) sender);
        
        if( region == null )
        {
            sender.sendMessage(Prefixes.claims + "You are not inside a claimed region");
            return true;
        }
        
        if( !region.getOwner().equals( playerData ) )
        {
            sender.sendMessage(Prefixes.claims + "You do not own this region");
            return true;
        }
        
        int blockCount = region.getBlockCount();
        playerData.addRiln(blockCount);
        
        plugin.claimedRegions.remove( region );
        sender.sendMessage(Prefixes.claims + "Region was unclaimed");
        playerData.sendBalance();
        
        return true;
    }
    
}
