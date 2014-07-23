package io.github.jonnyaxehandle.rilncraft;

import java.util.List;
import java.util.Random;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

/**
 *
 * @author Jonny
 */
public class CreatureDeathEventHandler implements Listener {
    private final Rilncraft plugin;
    
    public CreatureDeathEventHandler( Rilncraft rc )
    {
        plugin = rc;
    }
    
    @EventHandler
    public void creatureKilledEvent(EntityDeathEvent e)
    {
        // Gather data!
        LivingEntity entity = e.getEntity();
        Player killer = entity.getKiller();
        String targetName;
        
        // Make sure it's a player kill
        if( killer == null )
        {
            return;
        }
        
        // Ensure player data exists
        RCPlayer playerData = plugin.playerList.get( killer );
        if( playerData == null )
        {
            return;
        }
        
        // Make sure it's not a spawner-made mob
        Metadatable metaData = (Metadatable) entity;
        List<MetadataValue> metadata = metaData.getMetadata("isSpawnerMob");
        for( MetadataValue m : metadata )
        {
            if( m.getOwningPlugin() == plugin )
            {
                return;
            }
        }
        
        int reward,min,max;
        if( entity instanceof Player )
        {
            Player player = (Player) entity;
            RCPlayer target = plugin.playerList.get( player );
            
            targetName = player.getName();
            
            min = (int) Math.ceil( target.getRiln() * 0.1 );
            max = (int) Math.ceil( target.getRiln() * 0.3 );
            reward = Math.abs( getRandom( min , max ) );
        }
        else
        {
            targetName = e.getEntityType().getEntityClass().getName();
            targetName = targetName.replace("org.bukkit.entity.", "");
            if( !plugin.config.isConfigurationSection("mob_reward") )
            {
                killer.sendMessage("Error: mob_reward is not present in config!");
                return;
            }
            ConfigurationSection mob_reward = plugin.config.getConfigurationSection("mob_reward");
            if( mob_reward.isInt(targetName) )
            {
                reward = mob_reward.getInt(targetName);
            } else {
                if( mob_reward.isConfigurationSection(targetName) )
                {
                    ConfigurationSection mob = mob_reward.getConfigurationSection(targetName);
                    if( !mob.isInt("min") || !mob.isInt("max") )
                    {
                        return;
                    }
                    reward = Math.abs( getRandom( mob.getInt("min") , mob.getInt("max") ) );
                }
                else
                {
                    return;
                }
            }
        }
        
        killer.sendMessage( String.format("You gain %d Riln for killing %s", reward, targetName) );
        playerData.addRiln( reward );
    }
    
    static int getRandom( int min , int max )
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
