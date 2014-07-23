package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.entity.Player;

/**
 *
 * @author Jonny
 */
public class TeamInvite extends Request {

    public TeamInvite(Rilncraft rc, RCPlayer s, RCPlayer t) {
        super(rc, s, t);
    }

    @Override
    public void accept() {
        if( sender.currentTeam == null )
        {
            target.getPlayer().sendMessage(Prefixes.team + "Error: Team is null!");
            return;
        }
        target.currentTeam = sender.currentTeam;
        target.currentTeam.addMember(target.uuid);
        plugin.getServer().broadcastMessage( String.format( Prefixes.team + "%s has joined %s" , target.getName() , sender.currentTeam.getName() ) );
    }

    @Override
    public void deny() {
        Player senderPlayer = sender.getPlayer();
        senderPlayer.sendMessage( Prefixes.team + "Team invite was denied" );
    }

    @Override
    public void timeout() {
        Player senderPlayer = sender.getPlayer();
        senderPlayer.sendMessage( Prefixes.team + "Team invite timed out" );
    }
    
}
