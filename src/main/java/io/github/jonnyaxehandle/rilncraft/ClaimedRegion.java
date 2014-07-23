package io.github.jonnyaxehandle.rilncraft;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jonny
 */
public class ClaimedRegion {
    private boolean sharedWithFriends = false;
    private final Rilncraft plugin;

    public static enum Type {
        USER,TEAM,SAFEZONE,WARZONE
    }
    
    private static boolean isPosInside( int p1 , int p2 , int pa )
    {
        if( p1 < p2 )
        {
            return ( pa >= p1 && pa <= p2 );
        }
        else
        {
            return ( pa <= p1 && pa >= p2 );
        }
    }
    
    private Location loc1;
    private Location loc2;
    private RCPlayer owner;
    private World world;
    private Type type;
    
    public ClaimedRegion( Rilncraft rc )
    {
        plugin = rc;
    }
    
    public void setLocation1( Location l )
    {
        loc1 = l;
    }
    
    public void setLocation2( Location l )
    {
        loc2 = l;
    }
    
    public boolean isLocationInside( Location l )
    {
        if( l.getWorld() != world )
        {
            return false;
        }
        
        int x1 = loc1.getBlockX();
        int z1 = loc1.getBlockZ();
        int x2 = loc2.getBlockX();
        int z2 = loc2.getBlockZ();
        int xa = l.getBlockX();
        int za = l.getBlockZ();
        
        return ( isPosInside( x1 , x2 , xa ) && isPosInside( z1 , z2 , za ) );
    }
    
    public void setOwner( RCPlayer p )
    {
        owner = p;
    }
    
    public RCPlayer getOwner()
    {
        return owner;
    }

    void setWorld(World w) {
        world = w;
    }

    public World getWorld()
    {
        return world;
    }
    
    Location getLocation2() {
        return loc2;
    }

    Location getLocation1() {
        return loc1;
    }
    
    int getBlockCount()
    {
        int w = Math.abs( loc1.getBlockX() - loc2.getBlockX() ) + 1;
        int h = Math.abs( loc1.getBlockZ() - loc2.getBlockZ() ) + 1;
        return w*h;
    }
    
    public boolean editableBy( RCPlayer player )
    {
        if( type == Type.USER )
        {
            return editableByUser( player );
        }
        
        return false;
    }
    
    boolean editableByUser( RCPlayer player )
    {
        if( player.equals( owner ) )
        {
            return true;
        }
        
        PlayerRelation relationTo = getOwner().getRelationTo( player );
        if( relationTo == PlayerRelation.SPOUSE )
        {
            return true;
        }
        
        if( relationTo == PlayerRelation.FRIEND && sharedWithFriends )
        {
            return true;
        }
        
        return false;
    }
    
    void setSharedWithFriends(boolean b) {
        sharedWithFriends = b;
    }
    
    boolean getSharedWithFriends()
    {
        return sharedWithFriends;
    }
    
    public void setType( Type t )
    {
        type = t;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public JSONObject toJson()
    {
        JSONObject jsonClaim = new JSONObject("{}");
        jsonClaim.put("world", getWorld().getName());

        JSONArray pos1 = new JSONArray("[]");
        JSONArray pos2 = new JSONArray("[]");

        pos1.put( getLocation1().getX() );
        pos1.put( getLocation1().getZ() );

        pos2.put( getLocation2().getX() );
        pos2.put( getLocation2().getZ() );

        jsonClaim.put("point1", pos1);
        jsonClaim.put("point2", pos2);
        
        switch( type )
        {
            case USER:
                jsonClaim.put("type", "user");
                jsonClaim.put("owner", getOwner().uuid.toString());
                int isSharedWithFriends = sharedWithFriends ? 1 : 0;
                jsonClaim.put("isSharedWithFriends", isSharedWithFriends);
                break;
            case SAFEZONE:
                jsonClaim.put("type", "safezone");
                break;
        }
        
        return jsonClaim;
    }
    
    public ClaimedRegion fromJson( JSONObject jsonClaim )
    {
        World claimWorld = plugin.getServer().getWorld( jsonClaim.getString("world") );
        setWorld( claimWorld );
        JSONArray pos1 = jsonClaim.getJSONArray("point1");
        JSONArray pos2 = jsonClaim.getJSONArray("point2");
        setLocation1( new Location( claimWorld , pos1.getDouble(0) , 0 , pos1.getDouble(1) ) );
        setLocation2( new Location( claimWorld , pos2.getDouble(0) , 0 , pos2.getDouble(1) ) );
        if( "user".equals(jsonClaim.getString("type")) )
        {
            setType(ClaimedRegion.Type.USER);
            owner = plugin.playerList.get( UUID.fromString( jsonClaim.getString("owner") ) );
            if( owner == null )
            {
                return null;
            }
            try {
                if( jsonClaim.getInt("isSharedWithFriends") == 1 )
                {
                    setSharedWithFriends(true);
                }
            } catch ( JSONException e ) {
                setSharedWithFriends(false);
            }
        }
        if( "safezone".equals(jsonClaim.getString("type")) )
        {
            setType(ClaimedRegion.Type.SAFEZONE);
        }
        return this;
    }
    
}
