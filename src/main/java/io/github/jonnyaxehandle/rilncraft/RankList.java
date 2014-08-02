package io.github.jonnyaxehandle.rilncraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Jonny
 */
public class RankList {
    private final Rilncraft plugin;
    private ArrayList<Rank> ranks;
    
    public RankList( Rilncraft rc )
    {
        plugin = rc;
    }
    
    public void load( File rankConfigFile )
    {
        YamlConfiguration rankConfig = YamlConfiguration.loadConfiguration(rankConfigFile);
        if( rankConfig.isList("ranks") )
        {
            List< Map<?,?> > list = rankConfig.getMapList("ranks");
            for( Map<?,?> item : list )
            {
                String name = (String) item.get("name");
                int cost = Integer.parseInt( (String) item.get("cost") );
            }
        }
    }
    
}
