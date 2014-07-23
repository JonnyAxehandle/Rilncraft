package io.github.jonnyaxehandle.rilncraft;

/**
 *
 * @author Jonny
 */
public class TPRequest extends Request {
    private int refund;

    public TPRequest(Rilncraft rc, RCPlayer s, RCPlayer t) {
        super(rc, s, t);
    }

    @Override
    public void accept() {
        sender.getPlayer().teleport( target.getPlayer().getLocation() );
    }

    @Override
    public void deny() {
        sender.addRiln(refund);
        sender.getPlayer().sendMessage(Prefixes.teleport + "Teleport request was denied, Riln refunded");
        sender.sendBalance();
    }

    @Override
    public void timeout() {
        sender.addRiln(refund);
        sender.getPlayer().sendMessage(Prefixes.teleport + "Teleport timed out, Riln refunded");
        sender.sendBalance();
        
        target.getPlayer().sendMessage(Prefixes.teleport + "Teleport timed out");
    }
    
    public void setRefund( int r )
    {
        refund = r;
    }
    
}
