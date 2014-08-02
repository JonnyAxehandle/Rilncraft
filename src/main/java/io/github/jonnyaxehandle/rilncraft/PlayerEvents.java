package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

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
        player.sendMessage( Prefixes.rc );
        player.sendMessage( String.format("-- Riln: %d",data.getRiln()) );
        player.sendMessage( "-- Type /rchelp for more info!" );
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("riln", "dummy");
        //Setting where to display the scoreboard/objective (either SIDEBAR, PLAYER_LIST or BELOW_NAME)
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        //Setting the display name of the scoreboard/objective
        objective.setDisplayName("Rilncraft");
        Score score = objective.getScore( Bukkit.getOfflinePlayer("§eRiln") );
        score.setScore( data.getRiln() );
        
        data.myBoard = board;
        player.setScoreboard(board);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        RCPlayer playerData = plugin.playerList.get( e.getPlayer() );
        
        if( playerData.getRequest() != null )
        {
            playerData.getRequest().timeout();
        }
        
        if( playerData.getChatChannel() != null )
        {
            playerData.getChatChannel().removePlayer(e.getPlayer());
            playerData.setChatChannel(null);
        }
        
        playerData.saveConfig();
    }
    
}
