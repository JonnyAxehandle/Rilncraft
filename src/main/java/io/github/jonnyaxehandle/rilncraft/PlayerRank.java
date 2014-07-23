package io.github.jonnyaxehandle.rilncraft;

/**
 *
 * @author Jonny
 */
public class PlayerRank {
    
    static int rankCost( int r )
    {
        if( r <= 0 )
        {
            return 0;
        }
        if( r == 1 )
        {
            return 500;
        }
        return ( r * rankCost( 1 ) ) + rankCost( r - 1 );
    }
    
    private int rank = 0;
    private final Rilncraft plugin;

    PlayerRank(Rilncraft rc) {
        plugin = rc;
    }
    
    public void setRank( int r )
    {
        rank = r;
    }
    
    public int getRank()
    {
        return rank;
    }
    
    public void rankUp()
    {
        rank += 1;
    }
    
    public int nextRankCost()
    {
        return rankCost( rank + 1 );
    }
    
}
