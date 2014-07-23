package io.github.jonnyaxehandle.rilncraft;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class RCPlayer {
    private int riln;
    private final ArrayList<UUID> friends = new ArrayList<>();
    UUID spouse;
    Rilncraft plugin;
    ClaimedRegion workingClaim;
    ClaimedRegion currentRegion;
    PlayerRank rank;
    private File customConfigFile;
    private YamlConfiguration customConfig;
    private Request request;
    private Material claimTool;
    UUID uuid;
    Date lastKiss;
    Team currentTeam;
    
    @Override
    public boolean equals( Object o )
    {
        if( o == null )
        {
            return false;
        }
        
        if( getClass() != o.getClass() )
        {
            return false;
        }
        RCPlayer other = ( RCPlayer ) o;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.uuid);
        return hash;
    }
    
    public RCPlayer( Rilncraft rc )
    {
        plugin = rc;
        rank = new PlayerRank( rc );
    }
    
    /**
     * This function is called whenever a player logs in or if the server 
     * reloads with the player already online.
     * 
     * @param id
     * @return RCPlayer
     */
    public RCPlayer load( UUID id )
    {
        uuid = id;
        customConfigFile = new File(plugin.getDataFolder(), "players/" + uuid.toString() + ".yml");
        
        if( !customConfigFile.canRead() )
        {
            return this;
        }
        
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        // Get Riln
        if( customConfig.isInt("riln") )
        {
            riln = customConfig.getInt("riln");
        }
        else
        {
            riln = plugin.config.getInt("starting_balance",100);
        }

        // Get Rank
        if( customConfig.isInt("rank") )
        {
            rank.setRank( customConfig.getInt("rank") );
        }

        // Get Friends
        if( customConfig.isString("friends") )
        {
            String[] split = customConfig.getString("friends").split(",");
            List<String> list = Arrays.asList(split);
            for( String n : list )
            {
                if( !"".equals(n) )
                {
                    addFriend( UUID.fromString(n) );
                }
            }
        }
        
        // Get Spouse
        if( customConfig.isString("spouse") )
        {
            if( !"null".equals(customConfig.getString("spouse")) )
            {
                spouse = UUID.fromString( customConfig.getString("spouse") );
            }
        }
        
        // Get Team
        if( customConfig.isString("team") )
        {
            String teamName = customConfig.getString("team");
            if( plugin.teamList.teamExists(teamName) )
            {
                currentTeam = plugin.teamList.get(teamName);
            }
        }
        
        return this;
    }
    
    public RCPlayer create( UUID id )
    {
        uuid = id;
        customConfigFile = new File(plugin.getDataFolder(), "players/" + uuid.toString() + ".yml");
        
        // Defaults
        customConfig = new YamlConfiguration();
        riln = plugin.config.getInt("starting_balance",100);

        customConfig.set("riln", riln);
        saveConfig();
        
        return this;
    }
    
    public int getRiln()
    {
        int amount = riln;
        if( spouse != null )
        {
            RCPlayer spousePlayer = plugin.playerList.get(spouse);
            if( spousePlayer != null )
            {
                amount += spousePlayer.riln;
            }
        }
        return amount;
    }
    
    public boolean chargeRiln( int amount )
    {
        if( getRiln() >= amount )
        {
            riln -= amount;
            saveConfig();
            return true;
        }
        return false;
    }
    
    public void saveConfig()
    {
        try {
            if( getPlayer() != null )
            {
                customConfig.set("name", getPlayer().getName());
            }
            customConfig.set("riln", riln);
            customConfig.set("rank", rank.getRank());
            if( spouse == null )
            {
                customConfig.set("spouse", "null");
            }
            else
            {
                customConfig.set("spouse", spouse.toString());
            }
            
            String friendsConcat = "";
            for( UUID id : friends )
            {
                friendsConcat += id.toString() + ",";
            }
            customConfig.set("friends", friendsConcat);
            
            if( currentTeam != null )
            {
                customConfig.set("team", currentTeam.getName());
            }
            else
            {
                customConfig.set("team", " ");
            }
            
            customConfig.save(customConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(RCPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendBalance()
    {
        Player player1 = getPlayer();
        if( player1 != null )
        {
            player1.sendMessage( String.format("-- Current balance: %d Riln",getRiln()) );
        }
    }
        
    public void setRequest( Request r )
    {
        request = r;
    }
    
    public Request getRequest()
    {
        return request;
    }
    
    public Material getClaimTool()
    {
        return claimTool;
    }
    
    public void setClaimTool( Material newTool )
    {
        claimTool = newTool;
    }
    
    public void addFriend( UUID id )
    {
        if( uuid.equals(id) )
        {
            return;
        }
        
        if( friends.contains(id) )
        {
            return;
        }
        
        friends.add(id);
    }
    
    public void addRiln( int amount )
    {
        // Is the player online?
        Player player;
        player = Bukkit.getPlayer( uuid );
        if( player != null )
        {
            int nextRankCost = rank.nextRankCost();
            if( riln < nextRankCost && ( riln + amount ) >= nextRankCost )
            {
                player.sendMessage( String.format("Type /rankup to purchase Rank %d for %d Riln!",rank.getRank()+1,nextRankCost) );
            }
        }
        riln += amount;
    }
    
    public Player getPlayer()
    {
        Player player;
        
        player = Bukkit.getPlayer( uuid );
        if( player == null )
        {
            return Bukkit.getOfflinePlayer(uuid).getPlayer();
        }
        
        return player;
    }
    
    public String getName()
    {
        Player player;
        player = Bukkit.getPlayer( uuid );
        if( player == null )
        {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
        
        return player.getDisplayName();
    }
    
    public PlayerRelation getRelationTo( RCPlayer other )
    {
        if( other == null )
        {
            return PlayerRelation.NONE;
        }
        
        if( other.uuid.equals( spouse ) )
        {
            return PlayerRelation.SPOUSE;
        }
        
        if( friends.contains(other.uuid) )
        {
            return PlayerRelation.FRIEND;
        }
        
        if( currentTeam != null )
        {
            if( currentTeam == other.currentTeam )
            {
                return PlayerRelation.TEAMMATE;
            }
        }
        
        return PlayerRelation.NONE;
    }

    ArrayList<UUID> getFriends() {
        return friends;
    }

    UUID getSpouse()
    {
        return spouse;
    }
    
    void removeFriend(UUID id) {
        friends.remove(id);
    }
    
    public boolean hasRelationTo( RCPlayer other , PlayerRelation relation )
    {
        if( other == null )
        {
            return false;
        }
        switch( relation )
        {
            case SPOUSE:
                if( spouse == null )
                {
                    return false;
                }
                return spouse.equals( other.uuid );
            case FRIEND:
                return friends.contains(other.uuid);
            case TEAMMATE:
                if( currentTeam == null )
                {
                    return false;
                }
                return ( currentTeam == other.currentTeam );
            case NONE:
                return getRelationTo( other ) == PlayerRelation.NONE;
        }
        return false;
    }

    void setBalance(int newBalance) {
        riln = newBalance;
    }
    
}
