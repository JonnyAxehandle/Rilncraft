package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Jonny
 */
public class PlayerEvents implements Listener {
    private final Rilncraft plugin;
    
    public PlayerEvents( Rilncraft rc )
    {
        plugin = rc;
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        RCPlayer data = plugin.playerList.get( player );
        //player.sendMessage( String.format("Welcome, %s!",player.getName()) );
        player.sendMessage( Prefixes.rc );
        player.sendMessage( String.format("-- Riln: %d",data.getRiln()) );
        player.sendMessage( String.format("-- Current Rank: %d",data.rank.getRank()) );
        player.sendMessage( String.format("-- Cost to Rank Up: %d",data.rank.nextRankCost()) );
        player.sendMessage( "-- Type /rchelp for more info!" );
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        RCPlayer playerData = plugin.playerList.get( e.getPlayer() );
        
        if( playerData.getRequest() != null )
        {
            playerData.getRequest().timeout();
        }
        
        playerData.saveConfig();
    }
    
}
