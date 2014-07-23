package io.github.jonnyaxehandle.rilncraft;

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
public class FriendsCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;
    
    public FriendsCommandExecutor( Rilncraft rc )
    {
        plugin = rc;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        RCPlayer playerData = plugin.playerList.get((Player) sender);
        
        String commandName;
        String targetName;
        try {
            commandName = args[0];
        } catch( ArrayIndexOutOfBoundsException ex ) {
            if( playerData.getFriends().isEmpty() )
            {
                sender.sendMessage( Prefixes.friends + "You have no friends");
            }
            for( UUID id : playerData.getFriends() )
            {
                String friendName;
                Player player = Bukkit.getPlayer(id);
                if( player != null )
                {
                    friendName = player.getDisplayName();
                } else
                {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
                    if( offlinePlayer != null )
                    {
                        friendName = offlinePlayer.getName();
                    }
                    else
                    {
                        continue;
                    }
                }
                sender.sendMessage( String.format("- %s",friendName) );
            }
            return true;
        }
        
        try {
            targetName = args[1];
        } catch( ArrayIndexOutOfBoundsException ex ) {
            return false;
        }
        
        switch( commandName )
        {
            case "add":
                Player targetPlayer = Bukkit.getServer().getPlayer(targetName);
                if( targetPlayer == null )
                {
                    sender.sendMessage( String.format( Prefixes.friends + "%s is not online", targetName) );
                    return true;
                }
                
                RCPlayer target = plugin.playerList.get(targetPlayer);
                
                if( target.getRequest() != null )
                {
                    sender.sendMessage( String.format(Prefixes.friends + "%s already has a pending request", targetName) );
                    return true;
                }
                
                FriendRequest newRq = new FriendRequest( plugin , playerData , target );
                
                targetPlayer.sendMessage( String.format( Prefixes.friends + "%s has sent you a friend request",sender.getName()) );
                targetPlayer.sendMessage("-- Type /accept to accept");
                targetPlayer.sendMessage("-- Type /deny to deny");
                
                // targetPlayer.sendRawMessage("{text:\"[ACCEPT]\",clickEvent:{action:run_command,value:\"/accept\"},color:\"green\"}");
                
                target.setRequest(newRq);
                
                sender.sendMessage( Prefixes.friends + "Friend request sent");
                return true;
            case "remove":
                UUID id = getFriendByName( playerData , targetName );
                RCPlayer friend = plugin.playerList.get(id);
                if( friend != null )
                {
                    playerData.removeFriend( id );
                    friend.removeFriend( playerData.uuid );
                    friend.saveConfig();
                    playerData.saveConfig();
                    sender.sendMessage( Prefixes.friends + "Friend removed");
                }
                else
                {
                    sender.sendMessage( Prefixes.friends + "Critical error: player was null");
                }
                return true;
        }
        
        return false;
    }
    
    UUID getFriendByName( RCPlayer playerData , String targetName )
    {
        for( UUID id : playerData.getFriends() )
        {
            String friendName;
            Player player = Bukkit.getPlayer(id);
            if( player != null )
            {
                friendName = player.getDisplayName();
            } else
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
                if( offlinePlayer != null )
                {
                    friendName = offlinePlayer.getName();
                }
                else
                {
                    continue;
                }
            }
            if( friendName.equalsIgnoreCase(targetName) )
            {
                return id;
            }
        }
        return null;
    }
    
}