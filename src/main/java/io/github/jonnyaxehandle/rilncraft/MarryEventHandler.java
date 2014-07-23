package io.github.jonnyaxehandle.rilncraft;

import java.util.Date;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Jonny
 */
public class MarryEventHandler implements Listener {
    private final Rilncraft plugin;
    
    public MarryEventHandler( Rilncraft rc )
    {
        plugin = rc;
    }
    
    @EventHandler
    public void onPlayerInteract( PlayerInteractEntityEvent e )
    {
        Player player = e.getPlayer();
        RCPlayer playerData = plugin.playerList.get(player);
        Entity rightClicked = e.getRightClicked();
        
        if( rightClicked instanceof Player )
        {
            Player targetPlayer = ( Player ) rightClicked;
            RCPlayer targetData = plugin.playerList.get( targetPlayer );
            
            if( playerData.getRelationTo(targetData) != PlayerRelation.SPOUSE )
            {
                return;
            }
            
            if( player.isSneaking() )
            {
                if( !canPlayerKiss( playerData ) )
                {
                    return;
                }
                
                double healthMissing = 20 - targetPlayer.getHealth();
                double healthToHeal = ( healthMissing < 5 ) ? healthMissing : 5;
                
                targetPlayer.setHealth( targetPlayer.getHealth() + healthToHeal );
                
                player.sendMessage(Prefixes.marry + "You gave "+targetPlayer.getDisplayName()+" a kiss!");
                targetPlayer.sendMessage(Prefixes.marry + player.getDisplayName()+" gave you a kiss!");
                
            } else {
                PlayerInventory otherIV = targetPlayer.getInventory();
                player.openInventory(otherIV);
                return;
            }
            
        }
        
        if( rightClicked instanceof Wolf || rightClicked instanceof Ocelot )
        {
            Tameable pet = (Tameable) rightClicked;
            if( pet.isTamed() )
            {
                Player owner;
                AnimalTamer tamer = pet.getOwner();
                if( tamer == null )
                {
                    return;
                }
                
                if( tamer instanceof Player )
                {
                    owner = (Player) tamer;
                    if( playerData.getRelationTo( plugin.playerList.get(owner) ) != PlayerRelation.SPOUSE )
                    {
                        return;
                    }
                } else {
                    owner = ( (OfflinePlayer) tamer ).getPlayer();
                    if( playerData.getRelationTo( plugin.playerList.get(owner.getUniqueId()) ) != PlayerRelation.SPOUSE )
                    {
                        return;
                    }
                }
                
                pet.setOwner( player );
                player.sendMessage(Prefixes.marry + "This pet is now following you");
            }
        }
    }
    
    private boolean canPlayerKiss( RCPlayer playerData )
    {
        Date now = new Date();
        if( playerData.lastKiss == null )
        {
            playerData.lastKiss = now;
            return true;
        }
        
        if( now.getTime() - playerData.lastKiss.getTime() >= 5000 )
        {
            playerData.lastKiss = now;
            return true;
        }
        
        return false;
    }
    
}