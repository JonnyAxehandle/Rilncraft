package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class RankUpCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public RankUpCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] strings) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        RCPlayer playerData = plugin.playerList.get((Player) sender);
        int nextRankCost = playerData.rank.nextRankCost();
        if( !playerData.chargeRiln(nextRankCost) )
        {
            sender.sendMessage("Not enough Riln!");
            return true;
        }
        
        playerData.rank.rankUp();
        plugin.getServer().broadcastMessage( String.format("%s is now Rank %d!", sender.getName(), playerData.rank.getRank() ) );
        sender.sendMessage( String.format("-- Cost to Rank Up: %d",playerData.rank.nextRankCost()) );
        
        return true;
    }
    
}
