/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.github.jonnyaxehandle.rilncraft;

import java.util.Map;
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
class TeamCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public TeamCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if (!(sender instanceof Player)) {
            
            return true;
        }
        
        Player player = (Player) sender;
        
        String subCommand,inputName;
        try
        {
            subCommand = args[0];
        } catch( ArrayIndexOutOfBoundsException e )
        {
            return getTeamInfo( player );
        }
        
        switch( subCommand )
        {
            case "listall":
                return listAllTeams( player );
            case "disband":
                player.sendMessage( Prefixes.team + "Not yet supported" );
                break;    
        }
        
        try
        {
            inputName = args[1];
        } catch( ArrayIndexOutOfBoundsException e )
        {
            return false;
        }
        
        switch( subCommand )
        {
            case "create":
                return createTeam( player , inputName );
            case "invite":
                return invitePlayer( player , inputName );
            case "kick":
            case "promote":
                player.sendMessage( Prefixes.team + "Not yet supported" );
                break;
        }
        
        return false;
    }

    private boolean createTeam(Player player, String newTeamName) {
        RCPlayer playerData = plugin.playerList.get(player);
        
        if( playerData.currentTeam != null )
        {
            player.sendMessage( Prefixes.team + "You cannot create a team while you are already a member of one!" );
            return true;
        }
        
        if( plugin.teamList.teamExists(newTeamName) )
        {
            player.sendMessage( Prefixes.team + "A team with that name already exists!" );
            return true;
        }
        
        if( !newTeamName.matches("[A-Za-z0-9]+") )
        {
            player.sendMessage( Prefixes.team + "Invalid team name" );
            return true;
        }
        
        Team newTeam = new Team(plugin).create(newTeamName, player.getUniqueId());
        plugin.teamList.add( newTeam );
        playerData.currentTeam = newTeam;
        plugin.getServer().broadcastMessage( Prefixes.team + String.format("%s created new team: %s", player.getDisplayName() , newTeamName) );
        
        return true;
    }

    private boolean invitePlayer(Player player, String inputName) {
        RCPlayer playerData = plugin.playerList.get(player);
        
        if( playerData.currentTeam == null )
        {
            player.sendMessage( Prefixes.team + "You are not on a team!" );
            return true;
        }
        
        if( playerData.currentTeam.getOwner() != player.getUniqueId() && !playerData.currentTeam.getOfficers().contains(player.getUniqueId()) )
        {
            player.sendMessage( Prefixes.team + "Only owners/officers can invite" );
            return true;
        }
        
        Player targetPlayer = Bukkit.getServer().getPlayer(inputName);
        if( targetPlayer == null )
        {
            player.sendMessage( String.format( Prefixes.team + "%s is not online", inputName) );
            return true;
        }
        
        TeamInvite inv = new TeamInvite( plugin , playerData , plugin.playerList.get(targetPlayer) );
        
        targetPlayer.sendMessage( String.format( Prefixes.team + "%s has invited you to %s",player.getDisplayName(),playerData.currentTeam.getName()) );
        targetPlayer.sendMessage("-- Type /accept to accept");
        targetPlayer.sendMessage("-- Type /deny to deny");
        
        plugin.playerList.get(targetPlayer).setRequest(inv);
        
        player.sendMessage( Prefixes.team + "Team invite sent");
        
        return true;
    }

    private boolean listAllTeams(Player player) {
        for( Map.Entry<String,Team> t : plugin.teamList.getTeams().entrySet() )
        {
            player.sendMessage( t.getValue().getName() );
        }
        
        return true;
    }

    private boolean getTeamInfo(Player player) {
        Team t = plugin.playerList.get(player).currentTeam;
        
        if( t == null )
        {
            player.sendMessage( Prefixes.team + "You are not on any team" );
            return true;
        }
        
        player.sendMessage( Prefixes.team + t.getName() );
        player.sendMessage( String.format("-- Owner: %s",idToName( t.getOwner() )) );
        
        return true;
    }
    
    private String idToName( UUID id )
    {
        Player player = Bukkit.getPlayer(id);
        if( player != null )
        {
            return player.getDisplayName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
        if( offlinePlayer != null )
        {
            return offlinePlayer.getPlayer().getDisplayName();
        }
        return "NULL";
    }
    
}
