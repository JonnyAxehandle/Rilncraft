package io.github.jonnyaxehandle.rilncraft.ChatChannels;

import io.github.jonnyaxehandle.rilncraft.PlayerRelation;
import io.github.jonnyaxehandle.rilncraft.RCPlayer;
import io.github.jonnyaxehandle.rilncraft.Rilncraft;
import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class ChatChannel
{
    private final Rilncraft plugin;
    private final ArrayList<Player> players;
    
    public ChatChannel( Rilncraft rc )
    {
        players = new ArrayList<>();
        plugin = rc;
    }
    
    public void addPlayer( Player player )
    {
        players.add( player );
        sendMessage(String.format("%s joined the channel", player.getDisplayName()));
    }
    
    public void removePlayer( Player player )
    {
        players.remove( player );
        sendMessage(String.format("%s left the channel", player.getDisplayName()));
    }
    
    public void sendMessage( Player player , String message )
    {
        RCPlayer playerData = plugin.getPlayerList().get(player);
        
        for( Player recipient : players )
        {
            RCPlayer recipientData = plugin.getPlayerList().get(recipient);
            if( recipientData == null )
            {
                return;
            }
            StringBuilder tagList = new StringBuilder();
            
            if( recipientData.hasRelationTo(playerData, PlayerRelation.SPOUSE) )
            {
                tagList.append("§dM");
            }
            
            if( recipientData.hasRelationTo(playerData, PlayerRelation.FRIEND) )
            {
                tagList.append("§aF");
            }
            
            if( recipientData.hasRelationTo(playerData, PlayerRelation.TEAMMATE) )
            {
                tagList.append("§9T");
            }
            
            String tags = ( tagList.length() > 0 ) ? " §8["+tagList.toString()+"§8]§r" : "";
            recipient.sendMessage(String.format("%s%s: %s",player.getDisplayName(),tags,message));
        }
    }
    
    public void sendMessage( String message )
    {
        for( Player recipient : players )
        {
            recipient.sendMessage(message);
        }
    }
}
