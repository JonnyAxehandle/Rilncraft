package io.github.jonnyaxehandle.rilncraft;

import io.github.jonnyaxehandle.rilncraft.ChatChannels.ChatChannel;
import io.github.jonnyaxehandle.rilncraft.ChatChannels.MarriageChatChannel;
import java.util.UUID;
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

        String targetName,subCommand;
        try {
            subCommand = args[0];
            
            switch( subCommand )
            {
                case "propose":
                    try
                    {
                        targetName = args[1];
                        return sendMarriageRequest( ( Player ) cs , targetName );
                    } catch( ArrayIndexOutOfBoundsException ex ) {
                        return false;
                    }
                case "bed":
                    return goToBed( ( Player ) cs );
                case "chat":
                    return goToChat( ( Player ) cs );
            }
        } catch( ArrayIndexOutOfBoundsException ex ) {
            return displayMarriageInfo( ( Player ) cs );
        }
        
        return false;
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

    private boolean goToBed(Player player) {
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.getSpouse() == null )
        {
            player.sendMessage(Prefixes.marry + "You are not married");
            return true;
        }
        
        if( playerData.getMarriageBed() == null )
        {
            player.sendMessage(Prefixes.marry + "Marriage bed is not set!");
            return true;
        }
        
        player.teleport( playerData.getMarriageBed() );
        
        return true;
    }

    private boolean goToChat(Player player) {
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.getSpouse() == null )
        {
            player.sendMessage(Prefixes.marry + "You are not married");
            return true;
        }
        
        if( playerData.getChatChannel() != null )
        {
            // Leave your current channel
            ChatChannel currentChatChannel = playerData.getChatChannel();
            currentChatChannel.removePlayer(player);
            playerData.setChatChannel( null );
            if( currentChatChannel instanceof MarriageChatChannel )
            {
                // Intent was to leave marriage chat
                return true;
            }
        }
        
        MarriageChatChannel channelToJoin;
        
        Player spousePlayer = getOnlineSpouse( player );
        RCPlayer spouseData;
        if( spousePlayer != null )
        {
            spouseData = plugin.getPlayerList().get(spousePlayer);
            if( spouseData.getChatChannel() != null && spouseData.getChatChannel() instanceof MarriageChatChannel )
            {
                channelToJoin = (MarriageChatChannel) spouseData.getChatChannel();
            }
            else
            {
                channelToJoin = new MarriageChatChannel( plugin );
                spousePlayer.sendMessage( Prefixes.marry + player.getDisplayName() + " joined marriage chat");
                spousePlayer.sendMessage( "-- To join type: /marry chat");
            }
        }
        else
        {
            channelToJoin = new MarriageChatChannel( plugin );
        }
        
        channelToJoin.addPlayer(player);
        playerData.setChatChannel(channelToJoin);
        
        return true;
    }
    
    private Player getOnlineSpouse( Player player )
    {
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.getSpouse() == null )
        {
            return null;
        }
        
        UUID spouseID = playerData.getSpouse();
        Player spousePlayer = Bukkit.getPlayer(spouseID);
        if( spousePlayer == null )
        {
            return null;
        }
        
        return spousePlayer;
    }
    
}
