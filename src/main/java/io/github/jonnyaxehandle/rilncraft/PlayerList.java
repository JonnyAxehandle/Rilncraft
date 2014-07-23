package io.github.jonnyaxehandle.rilncraft;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class PlayerList {
    
    private final HashMap<UUID,RCPlayer> players;
    private final Rilncraft plugin;
    
    public PlayerList( Rilncraft rc )
    {
        plugin = rc;
        players = new HashMap<>();
    }

    public RCPlayer get( Player p )
    {
        RCPlayer playerData = get( p.getUniqueId() );
        if( playerData == null )
        {
            RCPlayer newP = create( p.getUniqueId() );
            players.put( p.getUniqueId() , newP );
            return newP;
        }
        return playerData;
    }

    public RCPlayer get( UUID id )
    {
        if( players.containsKey( id ) )
        {
            return players.get( id );
        }

        RCPlayer p = load( id );
        if( p == null )
        {
            return null;
        }

        players.put( id , p );
        return p;
    }
	
    RCPlayer load( UUID id )
    {
        File configFile = new File(plugin.getDataFolder(), "players/" + id.toString() + ".yml");
        if( !configFile.canRead() )
        {
            return null;
        }

        return new RCPlayer( plugin ).load( id );
    }

    RCPlayer create( UUID id )
    {
        return new RCPlayer( plugin ).create( id );
    }
    
    public void save()
    {
        for( Map.Entry<UUID,RCPlayer> e : players.entrySet() )
        {
            RCPlayer playerData = e.getValue();
            
            if( playerData.getRequest() != null )
            {
                playerData.getRequest().timeout();
            }
            
            e.getValue().saveConfig();
        }
    }
    
}
