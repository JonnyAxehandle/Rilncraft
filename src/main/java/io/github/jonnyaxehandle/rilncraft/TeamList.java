package io.github.jonnyaxehandle.rilncraft;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jonny
 */
public class TeamList {
    private final Rilncraft plugin;
    private final HashMap<String,Team> teams;
    
    public TeamList( Rilncraft rc )
    {
        this.teams = new HashMap<>();
        plugin = rc;
    }
    
    public boolean teamExists( String teamName )
    {
        return teams.containsKey(teamName);
    }

    public Team get( String name )
    {
        return teams.get(name);
    }
    
    public TeamList load(File teamDir) {
        
        if( !teamDir.isDirectory() || !teamDir.canRead() )
        {
            return this;
        }
        File[] listFiles = teamDir.listFiles();
        for (File f : listFiles) {
            plugin.getServer().broadcastMessage("Loading:"+f.getAbsolutePath());
            Team newTeam = new Team( plugin ).load( f );
            teams.put(newTeam.getName(), newTeam);
        }
        
        return this;
    }

    public void add(Team newTeam) {
        teams.put(newTeam.getName(), newTeam);
    }
    
    public void save()
    {
        for( Map.Entry<String,Team> t : teams.entrySet() )
        {
            t.getValue().save();
        }
    }
    
    public HashMap<String,Team> getTeams()
    {
        return teams;
    }

    void remove(Team t) {
        t.delete();
        teams.remove(t.getName());
    }
    
}