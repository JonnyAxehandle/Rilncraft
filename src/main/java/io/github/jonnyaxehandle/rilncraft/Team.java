package io.github.jonnyaxehandle.rilncraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Jonny
 */
public class Team {
    
    private UUID owner;
    private final ArrayList<UUID> officers;
    private final ArrayList<UUID> members;
    private final Rilncraft plugin;
    private String name;
    private File customConfigFile;
    private YamlConfiguration customConfig;
    
    public Team( Rilncraft rc )
    {
        this.members = new ArrayList<>();
        this.officers = new ArrayList<>();
        plugin = rc;
    }
    
    public Team create( String n , UUID o )
    {
        name = n;
        owner = o;
        customConfigFile = new File(plugin.getDataFolder(), "teams/" + n + ".yml");
        customConfig = new YamlConfiguration();
        save();
        return this;
    }
    
    public Team load( File f )
    {
        customConfigFile = f;
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
        
        name = customConfig.getString("name");
        owner = UUID.fromString( customConfig.getString("owner") );
        
        if( customConfig.isString("officers") )
        {
            String[] split = customConfig.getString("officers").split(",");
            for( String n : split )
            {
                if( !"".equals(n) )
                {
                    officers.add( UUID.fromString(n) );
                }
            }
        }
        
        if( customConfig.isString("members") )
        {
            String[] split = customConfig.getString("members").split(",");
            for( String n : split )
            {
                if( !"".equals(n) )
                {
                    members.add( UUID.fromString(n) );
                }
            }
        }
        
        return this;
    }
    
    public void save() {
        try {
            customConfig.set("name", name);
            customConfig.set("owner", owner.toString());
            
            String offConcat = "";
            for( UUID id : officers )
            {
                offConcat += id.toString() + ",";
            }
            customConfig.set("officers", offConcat);
            
            String memConcat = "";
            for( UUID id : members )
            {
                memConcat += id.toString() + ",";
            }
            customConfig.set("members", memConcat);
            
            customConfig.save(customConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(Team.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<UUID> getPlayers()
    {
        ArrayList<UUID> result = new ArrayList<>();
        
        result.add(owner);
        result.addAll(officers);
        result.addAll(members);
        
        return result;
    }
    
    public String getName()
    {
        return name;
    }
    
    public ArrayList<UUID> getOfficers()
    {
        return officers;
    }
    
    public UUID getOwner()
    {
        return owner;
    }
    
    public void addOfficer( UUID off )
    {
        if( members.contains(off) )
        {
            members.remove(off);
        }
        officers.add(off);
        save();
    }
    
    public void addMember( UUID mem )
    {
        if( officers.contains(mem) )
        {
            officers.remove(mem);
        }
        members.add(mem);
        save();
    }
    
}