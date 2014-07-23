package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class MarriageRequest extends Request {

    public MarriageRequest(Rilncraft rc, RCPlayer s, RCPlayer t) {
        super(rc, s, t);
    }

    @Override
    public void accept() {
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();
        
        sender.spouse = target.uuid;
        target.spouse = sender.uuid;
        
        Bukkit.getServer().broadcastMessage( Prefixes.marry + String.format("%s and %s are now married!",
                senderPlayer.getDisplayName(),
                targetPlayer.getDisplayName()) );
    }

    @Override
    public void deny() {
        Player senderPlayer = sender.getPlayer();
        senderPlayer.sendMessage( Prefixes.marry + "Marriage request was denied" );
    }

    @Override
    public void timeout() {
        Player senderPlayer = sender.getPlayer();
        senderPlayer.sendMessage( Prefixes.marry + "Marriage request timed out" );
    }
    
}
