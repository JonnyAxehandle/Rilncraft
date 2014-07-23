package io.github.jonnyaxehandle.rilncraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Jonny
 */
class HelpCommandExecutor implements CommandExecutor {
    private final Rilncraft plugin;

    public HelpCommandExecutor(Rilncraft rc) {
        plugin = rc;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        
        cs.sendMessage( Prefixes.rchelp );
        
        String pageName;
        try {
            pageName = args[0];
            
            switch( pageName )
            {
                case "general":
                    cs.sendMessage( "Kill mobs to earn more Riln; you lose some Riln when you die.\n" +
                        "-- To check balance: /balance\n" +
                        "-- To give someone Riln: /pay [player] [amount]" );
                    return true;
                case "teleport":
                    cs.sendMessage("-- To send tp request: /tpr [player]\n" +
                        "Teleports cost 35 Riln, 20 to teammates, 10 to friends");
                    return true;
                case "friends":
                    cs.sendMessage("Adding a friend reduces TP cost and disables PVP.\n" +
"-- To list friends: /friends\n" +
"-- To add a friend: /friends add [player]\n" +
"-- To remove a friend: /friends remove [player]");
                    return true;
                case "claims":
                    cs.sendMessage("Claim land to protect it from grief, at the cost of Riln.\n" +
"-- To make a claim tool: /claim tool\n" +
"Mark opposing corners with the claim tool.\n" +
"-- To purchase marked region: /claim\n" +
"-- To share claim: /claim share friends");
                    return true;
                case "marriage":
                    cs.sendMessage("Married players share Riln and gain other benefits\n" +
"-- To propose to someone: /marry [player]\n" +
"-- To divorce: /divorce\n" +
"Benefits:\n" +
"-- Right click spouse to open their inventory\n" +
"-- Shift+Right click to heal/kiss them\n" +
"-- Instant, free TP with /tpr\n" +
"-- Share pets by right-clicking them");
                    return true;
                case "teams":
                    cs.sendMessage("[Rilncraft Help]\n" +
"Joining teams reduces TP cost and disables PVP.\n" +
"-- To create a team: /team create\n" +
"-- To invite players: /team invite [player]");
                    return true;
            }
        } catch( ArrayIndexOutOfBoundsException ex ) {
            cs.sendMessage( "-- /rchelp general\n" +
                "-- /rchelp teleport\n" +
                "-- /rchelp friends\n" +
                "-- /rchelp claims\n" +
                "-- /rchelp marriage\n" +
                "-- /rchelp teams" );
            return true;
        }
        
        cs.sendMessage( "-- Unknown page" );
        
        return true;
    }
    
}
