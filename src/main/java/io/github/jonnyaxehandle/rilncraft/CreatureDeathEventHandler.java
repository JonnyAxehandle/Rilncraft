package io.github.jonnyaxehandle.rilncraft;

import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

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
            // plugin.getServer().broadcastMessage(targetName);
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
        
        reward += getArmorBonus( entity );
        
        killer.sendMessage( String.format("§a+%d §eRiln", reward) );
        playerData.addRiln( reward );
    }
    
    int getArmorBonus( LivingEntity entity )
    {
        if( entity instanceof Player )
        {
            return 0;
        }
        
        int reward = 0;
        
        EntityEquipment equipment = entity.getEquipment();
        ItemStack helmet = equipment.getHelmet();
        ItemStack chestplate = equipment.getChestplate();
        ItemStack leggings = equipment.getLeggings();
        ItemStack boots = equipment.getBoots();
        
        switch( helmet.getType() )
        {
            case LEATHER_HELMET:
                reward += 1;
                break;
            case IRON_HELMET:
                reward += getRandom(3,6);
                break;
            case GOLD_HELMET:
                reward += getRandom(2,4);
                break;
            case DIAMOND_HELMET:
                reward += getRandom(6,8);
                break;
        }
        
        switch( chestplate.getType() )
        {
            case LEATHER_CHESTPLATE:
                reward += 1;
                break;
            case IRON_CHESTPLATE:
                reward += getRandom(3,6);
                break;
            case GOLD_CHESTPLATE:
                reward += getRandom(2,4);
                break;
            case DIAMOND_CHESTPLATE:
                reward += getRandom(6,8);
                break;
        }
        
        switch( leggings.getType() )
        {
            case LEATHER_LEGGINGS:
                reward += 1;
                break;
            case IRON_LEGGINGS:
                reward += getRandom(3,6);
                break;
            case GOLD_LEGGINGS:
                reward += getRandom(2,4);
                break;
            case DIAMOND_LEGGINGS:
                reward += getRandom(6,8);
                break;
        }
        
        return reward;
    }
    
    static int getRandom( int min , int max )
    {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
