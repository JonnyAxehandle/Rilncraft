package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
class ClaimCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public ClaimCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        
        String commandName;
        
        try {
            commandName = args[0];
        } catch( ArrayIndexOutOfBoundsException ex ) {
            purchaseWorkingClaim( (Player) sender );
            return true;
        }
        
        switch( commandName )
        {
            case "sz":
                setSafeZone( (Player) sender );
                return true;
            case "tool":
                setClaimTool( (Player) sender );
                return true;
            case "share":
                try {
                    String with = args[1];
                    return shareClaim( (Player) sender , with );
                } catch( ArrayIndexOutOfBoundsException ex ) {
                    return false;
                }
        }
        
        return false;
    }

    private void purchaseWorkingClaim(Player sender) {
        RCPlayer playerData = plugin.playerList.get( sender );
        
        if( playerData.workingClaim == null )
        {
            sender.sendMessage(Prefixes.claims + "No region selected");
            return;
        }
        
        if( playerData.workingClaim.getLocation1() == null || playerData.workingClaim.getLocation2() == null )
        {
            sender.sendMessage(Prefixes.claims + "No region selected");
            return;
        }
        
        int blockCount = playerData.workingClaim.getBlockCount();
        
        if( !playerData.chargeRiln(blockCount) )
        {
            sender.sendMessage(Prefixes.claims + "You cannot afford to claim this region");
            return;
        }
        
        sender.sendMessage(Prefixes.claims + "Region was claimed");
        playerData.sendBalance();
        
        plugin.claimedRegions.add( playerData.workingClaim );
        playerData.workingClaim = null;
    }

    private void setClaimTool(Player player) {
        Material newTool = player.getItemInHand().getType();
        if( newTool == Material.AIR )
        {
            player.sendMessage(Prefixes.claims + "Can't use empty hand as your claim tool!");
            return;
        }
        
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.getClaimTool() == newTool )
        {
            playerData.setClaimTool( null );
            player.sendMessage(Prefixes.claims + "Claim tool cleared");
        }
        else
        {
            playerData.setClaimTool(newTool);
            player.sendMessage(Prefixes.claims + String.format("%s is now your claim tool", newTool.toString()));
        }
    }

    private boolean shareClaim(Player player, String with) {
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( !playerData.currentRegion.editableBy(playerData) )
        {
            player.sendMessage(Prefixes.claims + "You cannot edit this claim");
            return true;
        }
        
        switch( with )
        {
            case "friends":
                playerData.currentRegion.setSharedWithFriends( true );
                return true;
        }
        
        return false;
    }

    private void setSafeZone(Player player) {
        RCPlayer playerData = plugin.playerList.get( player );
        
        if( playerData.workingClaim == null )
        {
            player.sendMessage(Prefixes.claims + "No region selected");
            return;
        }
        
        if( playerData.workingClaim.getLocation1() == null || playerData.workingClaim.getLocation2() == null )
        {
            player.sendMessage(Prefixes.claims + "No region selected");
            return;
        }
        
        playerData.workingClaim.setOwner(null);
        playerData.workingClaim.setType(ClaimedRegion.Type.SAFEZONE);
        
        plugin.claimedRegions.add( playerData.workingClaim );
        playerData.workingClaim = null;
    }
    
}
