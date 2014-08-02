/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Jonny
 */
public class ChatEventHandler implements Listener {
    private final Rilncraft plugin;

    ChatEventHandler(Rilncraft rc) {
        plugin = rc;
    }
    
    @EventHandler
    public void onPlayerChat( AsyncPlayerChatEvent e )
    {
        Player player = e.getPlayer();
        RCPlayer playerData = plugin.playerList.get(player);
        String message = e.getMessage();
        
        if( playerData.getChatChannel() != null )
        {
            playerData.getChatChannel().sendMessage(player, message);
            e.setCancelled(true);
            return;
        }
        
        for( Player recipient : e.getRecipients() )
        {
            RCPlayer recipientData = plugin.playerList.get(recipient);
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
            
            if( recipientData.getChatChannel() != null )
            {
                // Player is pub, recip is not
                recipient.sendMessage(String.format("%s%s§8: %s",player.getDisplayName(),tags,message));
            }
            else
            {
                // Both pub
                recipient.sendMessage(String.format("%s%s: %s",player.getDisplayName(),tags,message));
            }
        }
        
        e.setCancelled(true);
    }
    
}
