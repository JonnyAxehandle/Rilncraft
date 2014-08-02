package io.github.jonnyaxehandle.rilncraft;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Jonny
 */
public class MarryEventHandler implements Listener {
    private final Rilncraft plugin;
    private final HashMap<Player,Location> bedCache;
    
    public MarryEventHandler( Rilncraft rc )
    {
        plugin = rc;
        bedCache = new HashMap<>();
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
            
            boolean kissAttempt = false;
            
            if( player.isSneaking() || player.isInsideVehicle() )
            {
                kissAttempt = true;
            }
            
            if( kissAttempt )
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
    
    @EventHandler
    public void onEnterBed( PlayerBedEnterEvent e )
    {
        // Gather player data
        Player player = e.getPlayer();
        RCPlayer playerData = plugin.playerList.get( player );
        
        // Cache bed location
        Block bed = e.getBed();
        bedCache.put(player, bed.getLocation());
        
        // See if spouse is currently online
        Player spousePlayer = getOnlineSpouse( player );
        if( spousePlayer == null )
        {
            return;
        }
        
        RCPlayer spouseData = plugin.playerList.get( spousePlayer );
        
        // See if they are in bed
        if( !spousePlayer.isSleeping() )
        {
            return;
        }
        
        Location spouseBed = bedCache.get(spousePlayer);
        
        // See if in same world
        if( !spouseBed.getWorld().equals( bed.getWorld() ) )
        {
            return;
        }
        
        // Check range
        double distance = spouseBed.distance( bed.getLocation() );
        if( distance > 5 )
        {
            return;
        }
        
        // Send messages
        playerData.setMarriageBed(player.getLocation());
        spouseData.setMarriageBed(spousePlayer.getLocation());
        
        player.sendMessage(Prefixes.marry + "You got in bed with "+spousePlayer.getDisplayName());
        spousePlayer.sendMessage(Prefixes.marry + player.getDisplayName() + "Got in bed with you");
        
        player.sendMessage(Prefixes.marry + "Marriage bed set. Use /marry bed to TP here!");
        spousePlayer.sendMessage(Prefixes.marry + "Marriage bed set. Use /marry bed to TP here!");
    }
    
    @EventHandler
    public void onLeaveBed( PlayerBedLeaveEvent e )
    {
        Player player = e.getPlayer();
        bedCache.remove(player);
        plugin.getLogger().log(Level.INFO, "RCDebug: {0} left bed", player.getDisplayName());
    }
    
    private boolean canPlayerKiss( RCPlayer playerData )
    {
        Date now = new Date();
        if( playerData.lastKiss == null )
        {
            playerData.lastKiss = now;
            return true;
        }
        
        if( now.getTime() - playerData.lastKiss.getTime() >= 2500 )
        {
            playerData.lastKiss = now;
            return true;
        }
        
        return false;
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