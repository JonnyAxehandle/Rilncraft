package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class FriendRequest extends Request {

    public FriendRequest(Rilncraft rc, RCPlayer s, RCPlayer t) {
        super(rc, s, t);
    }

    @Override
    public void accept() {
        sender.addFriend( target.uuid );
        target.addFriend( sender.uuid );
        
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();
        
        senderPlayer.sendMessage( String.format( Prefixes.friends + "%s added to friends list", targetPlayer.getName()) );
        targetPlayer.sendMessage( String.format( Prefixes.friends + "%s added to friends list", senderPlayer.getName()) );
    }

    @Override
    public void deny() {
        Player senderPlayer = sender.getPlayer();
        senderPlayer.sendMessage( Prefixes.friends + "Friend request was denied" );
    }

    @Override
    public void timeout() {
        Player senderPlayer = sender.getPlayer();
        senderPlayer.sendMessage( Prefixes.friends + "Friend request timed out" );
    }
    
}
