package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Jonny
 */
public class ClaimEventHandler implements Listener {
    private final Rilncraft plugin;

    ClaimEventHandler(Rilncraft rc) {
        plugin = rc;
    }
    
    @EventHandler
    public void playerMovesTheirAssAround( PlayerMoveEvent e )
    {
        ClaimedRegion region = plugin.claimedRegions.getRegion( e.getTo() );
        RCPlayer playerData = plugin.playerList.get( e.getPlayer() );
        if( playerData.currentRegion == region )
        {
            return;
        }
        
        playerData.currentRegion = region;
        
        if( region != null )
        {
            StringBuilder b = new StringBuilder();
            b.append( Prefixes.claims );
            
            switch( region.getType() )
            {
                case USER:
                    if( region.getOwner() != null )
                    {
                        b.append( String.format("%s",region.getOwner().getName()) );
                    } else {
                        b.append("§4~CORRUPTED CLAIM");
                    }
                    if( region.getSharedWithFriends() )
                    {
                        b.append(" §2[§aF§2]§r");
                    }
                    break;
                case SAFEZONE:
                    b.append("§a Safe Zone - No PVP/Mobs");
                    break;
            }
            
            e.getPlayer().sendMessage( b.toString() );
        }
        
        if( region == null )
        {
            e.getPlayer().sendMessage(Prefixes.claims + "Unclaimed land");
        }
    }
    
    @EventHandler
    public void blockDamaged( BlockDamageEvent e )
    {
        Player player = e.getPlayer();
        RCPlayer playerData = plugin.playerList.get( player );
        Location location = e.getBlock().getLocation();
        ClaimedRegion r = plugin.claimedRegions.getRegion( location );
        ItemStack tool = e.getItemInHand();
        
        if( r != null )
        {
            if( !r.editableBy(playerData) )
            {
                e.setCancelled(true);
                return;
            }
        }
        
        if( tool.getType() == playerData.getClaimTool() )
        {
            e.setCancelled(true);
            doClaimTool( playerData , e.getBlock() );
        }
    }
    
    private void doClaimTool( RCPlayer playerData , Block block )
    {
        ClaimedRegion workingClaim = playerData.workingClaim;
        if( workingClaim == null )
        {
            // No working claim
            // Do position 1
            ClaimedRegion newClaim = new ClaimedRegion( plugin );
            newClaim.setWorld( block.getWorld() );
            newClaim.setLocation1(block.getLocation() );
            newClaim.setType(ClaimedRegion.Type.USER);
            newClaim.setOwner( playerData );
            playerData.workingClaim = newClaim;
            playerData.getPlayer().sendMessage("Position 1 set to " + block.getX() +","+ block.getZ());
        }
        else
        {
            // Do position 2
            if( workingClaim.getLocation2() == null )
            {
                if( !workingClaim.getLocation1().getWorld().equals( block.getWorld() ) )
                {
                    playerData.workingClaim = null;
                    doClaimTool( playerData , block );
                    return;
                }
                workingClaim.setLocation2( block.getLocation() );
                playerData.getPlayer().sendMessage("Position 2 set to " + block.getX() +","+ block.getZ());
                playerData.getPlayer().sendMessage( String.format("This region can be claimed for %d Riln",workingClaim.getBlockCount()) );
            }
            else
            {
                playerData.workingClaim = null;
                doClaimTool( playerData , block );
            }
            
        }
    }
    
    @EventHandler
    public void blockPlaced( BlockPlaceEvent e )
    {
        Player player = e.getPlayer();
        RCPlayer playerData = plugin.playerList.get(player);
        Location location = e.getBlock().getLocation();
        ClaimedRegion r = plugin.claimedRegions.getRegion( location );
        if( r == null )
        {
            return;
        }
        
        if( !r.editableBy(playerData) )
        {
            e.setCancelled(true);
        }
    }
    
}
