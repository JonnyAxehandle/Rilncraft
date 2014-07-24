package io.github.jonnyaxehandle.rilncraft;

/*import com.lenis0012.bukkit.npc.NPC;
import com.lenis0012.bukkit.npc.NPCFactory;
import com.lenis0012.bukkit.npc.NPCProfile;*/
import java.io.File;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;*/

/**
 *
 * @author Jonny
 */
public final class Rilncraft extends JavaPlugin implements Listener {
    
    FileConfiguration config;
    ClaimedRegionList claimedRegions;
    //EffectManager effectManager;
    
    PlayerList playerList;
    TeamList teamList;
    
    @Override
    public void onEnable()
    {
        // Load the configs
        File configFile = new File(getDataFolder(), "config.yml");
        if( !configFile.canRead() )
        {
            saveDefaultConfig();
        }
        else
        {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        config = getConfig();
        
        // Teams
        File teamDir = new File(getDataFolder(), "teams/");
        if( !teamDir.isDirectory() )
        {
            if( teamDir.mkdir() )
            {
                getLogger().log(Level.CONFIG, "RilnCraft: Team directory created");
            }
            else
            {
                getLogger().log(Level.SEVERE, "RilnCraft: Could not create the team directory!");
            }
        }
        
        teamList = new TeamList( this ).load( teamDir );
        
        // Ensure player directory exists
        File playerDir = new File(getDataFolder(), "players/");
        if( !playerDir.isDirectory() )
        {
            if( playerDir.mkdir() )
            {
                getLogger().log(Level.CONFIG, "RilnCraft: Player directory created");
            }
            else
            {
                getLogger().log(Level.SEVERE, "RilnCraft: Could not create the player directory!");
            }
        }
        
        // Load existing players
        playerList = new PlayerList( this );
        for( Player player : Bukkit.getServer().getOnlinePlayers() ) {
            playerList.get(player);
        }
        
        // Command executors
        getCommand("rchelp").setExecutor(new HelpCommandExecutor(this));
        getCommand("riln").setExecutor(new RilnCommandExecutor(this));
        
        getCommand("tpr").setExecutor(new TPRCommandExecutor(this));
        getCommand("friends").setExecutor(new FriendsCommandExecutor(this));
        
        getCommand("accept").setExecutor(new AcceptCommandExecutor(this));
        getCommand("deny").setExecutor(new DenyCommandExecutor(this));
        
        getCommand("claim").setExecutor(new ClaimCommandExecutor(this));
        getCommand("unclaim").setExecutor(new UnclaimCommandExecutor(this));
        
        getCommand("rankup").setExecutor(new RankUpCommandExecutor(this));
        
        getCommand("marry").setExecutor(new MarryCommandExecutor(this));
        getCommand("divorce").setExecutor(new DivorceCommandExecutor(this));
        
        getCommand("team").setExecutor(new TeamCommandExecutor(this));
        
        // Events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        getServer().getPluginManager().registerEvents(new CreatureDeathEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new ClaimEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new MarryEventHandler(this), this);
        
        // Particles
        //EffectLib lib = EffectLib.instance();
        //effectManager = new EffectManager(lib);
        
        // Claims
        claimedRegions = new ClaimedRegionList( this );
        claimedRegions.load();
        
    }
    
    @Override
    public void onDisable()
    {
        saveConfig();
        claimedRegions.save();
        playerList.save();
        teamList.save();
    }
    
    @EventHandler
    public void creatureSpawned(CreatureSpawnEvent e)
    {
        LivingEntity entity = e.getEntity();
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();
        Metadatable metaData = (Metadatable) entity;
        
        if( spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER )
        {
            metaData.setMetadata("isSpawnerMob",new FixedMetadataValue(this,true));
        }
    }
    
    @EventHandler
    public void playerRespawn(PlayerRespawnEvent e)
    {
        RCPlayer playerData = playerList.get( e.getPlayer() );
        if( playerData == null )
        {
            return;
        }
        
        int deduction = (int) Math.ceil( playerData.getRiln() * 0.3 );
        playerData.addRiln(deduction*-1);
        e.getPlayer().sendMessage(String.format("You lost %d Riln",deduction));
        playerData.sendBalance();
    }
    
    @EventHandler
    public void onPlayerChat( AsyncPlayerChatEvent e )
    {
        Player player = e.getPlayer();
        RCPlayer playerData = playerList.get(player);
        String message = e.getMessage();
        
        for( Player recipient : e.getRecipients() )
        {
            RCPlayer recipientData = playerList.get(recipient);
            if( recipientData == null )
            {
                return;
            }
            
            StringBuilder tagList = new StringBuilder();
            
            if( recipientData.hasRelationTo(playerData, PlayerRelation.SPOUSE) )
            {
                tagList.append("§5[§dM§5]§r");
            }
            
            if( recipientData.hasRelationTo(playerData, PlayerRelation.FRIEND) )
            {
                tagList.append("§2[§aF§2]§r");
            }
            
            if( recipientData.hasRelationTo(playerData, PlayerRelation.TEAMMATE) )
            {
                tagList.append("§1[§9T§1]§r");
            }
            
            String tags = ( tagList.length() > 0 ) ? " "+tagList.toString() : "";
            
            recipient.sendMessage(String.format("<%s%s> %s",player.getDisplayName(),tags,message));
        }
        
        e.setCancelled(true);
    }
    
}
