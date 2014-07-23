package io.github.jonnyaxehandle.rilncraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Jonny
 */
public class ClaimedRegionList {
    
    ArrayList<ClaimedRegion> claimedRegions;
    private final Rilncraft plugin;
    
    ClaimedRegionList( Rilncraft rc )
    {
        plugin = rc;
    }
    
    public void load()
    {
        claimedRegions = new ArrayList<>();
        
        // Make sure claims file exists
        File claimsJson = new File(plugin.getDataFolder(), "claims.json");
        if( !claimsJson.canRead() )
        {
            try {
                claimsJson.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Rilncraft.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(plugin.getDataFolder() + "/claims.json"));
            String jsonString = new String(encoded, StandardCharsets.UTF_8);
            
            if( jsonString.isEmpty() )
            {
                return;
            }
            
            JSONArray jsonClaims = new JSONArray( jsonString );
            for( int i = 0; i < jsonClaims.length(); i++ )
            {
                JSONObject jsonClaim = jsonClaims.getJSONObject(i);
                ClaimedRegion r = new ClaimedRegion( plugin ).fromJson( jsonClaim );
                if( r != null )
                {
                    claimedRegions.add(r);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Rilncraft.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save()
    {
        JSONArray jsonClaims = new JSONArray("[]");
        for( ClaimedRegion r : claimedRegions )
        {
            jsonClaims.put( r.toJson() );
        }
        try {
            String jsonString = jsonClaims.toString();
            String filename = plugin.getDataFolder() + "/claims.json";
            
            BufferedWriter writer = new BufferedWriter(new FileWriter( filename ));
            writer.write( jsonString );
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rilncraft.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClaimedRegionList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    ClaimedRegion getRegion( Location l )
    {
        for( ClaimedRegion r : claimedRegions )
        {
            if( r.isLocationInside( l ) )
            {
                return r;
            }
        }
        return null;
    }

    void add(ClaimedRegion workingClaim) {
        claimedRegions.add(workingClaim);
        save();
    }

    void remove(ClaimedRegion region) {
        claimedRegions.remove(region);
        save();
    }
    
}
