package io.github.jonnyaxehandle.rilncraft;

/**
 *
 * @author Jonny
 */
public abstract class Request {
    protected final Rilncraft plugin;
    protected final RCPlayer sender;
    protected final RCPlayer target;
    public Request( Rilncraft rc , RCPlayer s , RCPlayer t )
    {
        plugin = rc;
        sender = s;
        target = t;
    }
    
    public abstract void accept();
    public abstract void deny();
    public abstract void timeout();
}
