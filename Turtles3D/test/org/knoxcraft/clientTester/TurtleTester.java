package org.knoxcraft.clientTester;

import org.knoxcraft.netty.server.HttpUploadServer;
import org.knoxcraft.serverturtle.Turtle;

import net.canarymod.Canary;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

public class TurtleTester extends Plugin implements CommandListener, PluginListener {

    private boolean tt = false;  //Turtle on/off
    private Turtle turtle;  //this should probably be a map of sender->turtle.  Maybe okay for now.

    private HttpUploadServer httpServer;
    public static Logman logger;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor.
     */
    public TurtleTester() {
        TurtleTester.logger = getLogman();
    }

    /**
     * Called when plugin is disabled.
     */
    @Override
    public void disable() {
        logger.warn("TurtleTester.disable() invoked");
        httpServer.disable();
    }

    /**
     * Called when plugin is enabled. 
     * 
     * @return
     */
    @Override
    public boolean enable() {
        try {
            getLogman().info("Registering plugin");
            Canary.hooks().registerListener(this, this);
            httpServer=new HttpUploadServer();
            httpServer.enable(logger);
            //getName() returns the class name, in this case TurtleAPI
            getLogman().info("Enabling "+getName() + " Version " + getVersion()); 
            getLogman().info("Authored by "+getAuthor());
            Canary.commands().registerCommands(this, this, false);
            return true;
        } catch (CommandDependencyException e){
            throw new RuntimeException(e);
        }
    }

    //API TIME

    /**
     * Turn on turtle mode.  Sets up relative position. 
     * (0,0,0) is player position.  Forward is player direction.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "ton", "TurtleOn" },
            description = "Turtle Mode On",
            permissions = { "" },
            toolTip = "/ton")
    public void TurtleOn(MessageReceiver sender, String[] args)
    {
        //Make Turtle
        turtle = new Turtle(logger);
        turtle.turtleInit(sender);

        //Turning on Turtle
        tt = true;
        turtle.turtleConsole("Turtle mode on.  Origin position: ");
        turtle.turtleReportOriginPosition();
        turtle.turtleReportDirection();
    }

    /**
     * Turn off turtle mode.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "toff", "TurtleOff" },
            description = "Turtle Mode Off",
            permissions = { "" },
            toolTip = "/toff")
    public void TurtleOff(MessageReceiver sender, String[] args)
    {
        //Turning off Turtle
        tt = false;
        turtle.turtleConsole("Turtle mode off.");
        turtle = null;
    }

    /**
     * Toggle turtle mode on/off.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "tt", "TurtleToggle" },
            description = "Toggle Turtle Mode",
            permissions = { "" },
            toolTip = "/tt")
    public void TurtleToggle(MessageReceiver sender, String[] args)
    {
        if (tt)
        { //if On, Turning off Turtle
            TurtleOff(sender, args);
        }
        else
        { //if Off, Turning on Turtle
            TurtleOn(sender, args);
        }
    }

    /**
     * Output a message to the player console.
     * Expects args[0] = "/c"
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "c", "TurtleConsole" },
            description = "Displays a message on the turtle console",
            permissions = { "" },
            toolTip = "/c <message>")
    public void TurtleConsole(MessageReceiver sender, String[] args)
    {
        //Display string in console
        String message = "";
        for (int i=1; i<args.length; i++) {  //skip the command, just send the message
            message = message + args[i]+ " ";
        }
        turtle.turtleConsole(message);
    }

    /**
     * Toggle block placement mode on/off.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "bp", "BlockPlacement" },
            description = "Toggle Turtle block placement mode",
            permissions = { "" },
            toolTip = "/bp")
    public void TurtleBlockPlace(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (turtle.getBP())  {
            turtle.turtleSetBlockPlace(false);
        }  else {
            turtle.turtleSetBlockPlace(true);
        }
        turtle.turtleBlockPlaceStatus();  //alert user about change
    }

    /**
     * Checks whether block placement mode is on and alerts player.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "bp?", "CheckBlockPlacement" },
            description = "Checks Turtle block placement mode",
            permissions = { "" },
            toolTip = "/bp?")
    public void TurtleBlockPlaceStatus(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        turtle.turtleBlockPlaceStatus();
    }

    /**
     * Set turtle position (relative coords)
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "sp", "SetPosition" },
            description = "Set Turtle position in relative coords",
            permissions = { "" },
            toolTip = "/sp <x> <y> <z>")
    public void TurtleSetRelPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //Change location to new location based on relative coordinates
        if (args.length == 4)  { //are there enough arguments?
            try  {
                turtle.turtleSetRelPosition(Integer.parseInt(args[1]), Integer.parseInt(args[2]),Integer.parseInt(args[3]));
            }  catch (Exception e)  {  //args weren't really numbers
                turtle.turtleConsole("Not a valid position.");
            }
        }  else  {
            turtle.turtleConsole("Not enough arguments.");
        }
    }

    /**
     * Set turtle direction.  Number based.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "sd", "SetDirection" },
            description = "Set turtle direction",
            permissions = { "" },
            toolTip = "/sd <dir>")
    public void TurtleSetDirection(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (args.length<2)  {  //not enough arguments
            turtle.turtleConsole("Not enough arguments.");
            return;
        }

        //update direction
        // 0 = NORTH
        // 1 = NORTHEAST
        // 2 = EAST
        // 3 = SOUTHEAST
        // 4 = SOUTH
        // 5 = SOUTHWEST
        // 6 = WEST
        // 7 = NORTHWEST
        // Else = ERROR 
        try  {
            turtle.turtleSetDirection(Integer.parseInt(args[1]));
        }  catch (Exception e)  {  //arg wasn't a number or was invalid
            turtle.turtleConsole("Not a valid direction.");
        }        
    }

    /**
     * Get current position (relative)
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "gp", "GetPosition" },
            description = "Get Turtle position in relative coords",
            permissions = { "" },
            toolTip = "/gp")
    public void TurtleGetPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //report position of turtle (relative position)
        turtle.turtleReportPosition();
    }

    /**
     * Get current position of Turtle in game coords
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "ggp", "GetGamePosition" },
            description = "Get Turtle position in game coords",
            permissions = { "" },
            toolTip = "/ggp")
    public void TurtleGetGamePosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //report position of turtle (game coord position)
        turtle.turtleReportGamePosition();
    }

    /**
     * Get position of relative origin (Player's pos at Turtle on) in game coords
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "gop", "GetOriginPosition" },
            description = "Get origin position in game coords",
            permissions = { "" },
            toolTip = "/gop")
    public void TurtleGetOriginPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //report position of origin (game coord position)
        turtle.turtleReportOriginPosition();
    }

    /**
     * Get current direction
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "gd", "GetDirection" },
            description = "Get Turtle direction",
            permissions = { "" },
            toolTip = "/gd")
    public void TurtleGetDirection(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //report position of turtle	
        turtle.turtleReportDirection();
    }

    /**
     * Set block type (int based)
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "sbt", "SetBlockType" },
            description = "Set Turtle block type",
            permissions = { "" },
            toolTip = "/sbt <type>")
    public void TurtleSetBlockType(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (!checkBP(sender))  //don't allow if block placement mode isn't on either
            return;


        if (args.length<2)  {  //not enough arguments
            turtle.turtleConsole("Not enough arguments.");
            return;
        }

        //set current BT of turtle	
        try  {
            if (!(args.length == 3))  {
                turtle.turtleSetBlockType(Integer.parseInt(args[1]));
            }
        }  catch (Exception e)  {  //bad arguments
            turtle.turtleConsole("Not a valid block type.");
        }
    }

    /**
     * set Block type (string/BlockType based)
     * 
     * @param sender
     * @param args
     */
    //TODO implementation-- maybe we don't need this version for TurtleTester?

    /**
     * Get current block type
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "gbt", "GetBlockType" },
            description = "Get Turtle block type",
            permissions = { "" },
            toolTip = "/gbt")
    public void TurtleGetBlockType(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (!checkBP(sender))  //don't allow if block placement mode isn't on either
            return;

        //report current BT of turtle	
        turtle.turtleReportBlockType();
    }

    /**
     * Move (forward/back)
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "m" , "f", "b", "move", "forward", "back"},
            description = "Turtle move forward/back",
            permissions = { "" },
            toolTip = "/m [dist] or /f [dist] or /b [dist]")
    public void TurtleMove(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        int x = 1;  //default move distance
        if(args.length>1)  {  //alternate move distance specified
            try  {
                x = Integer.parseInt(args[1]);  
            }  catch (Exception e)  {  //arg not a number
                turtle.turtleConsole("Not a valid distance.");
            }
        }   

        //check if going backward
        if (args[0].equals("/b") || args[0].equals("/back")){  
            //if so, make distance negative
            x = -x;
        }

        turtle.turtleMove(x);
    }

    /**
     * Move (up/down)
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "u", "d", "up", "down" },
            description = "Turtle up/down",
            permissions = { "" },
            toolTip = "/u [dist] or /d [dist]")
    public void TurtleUpDown(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        int x = 1;  //default move distance
        if(args.length>1)  {  //alternate move distance specified
            try  {
                x = Integer.parseInt(args[1]);  
            }  catch (Exception e)  {  //arg not a number
                turtle.turtleConsole("Not a valid distance.");
            }
        } 

        //check if going down
        if (args[0].equals("/d") || args[0].equals("/down")){  
            //if so, make distance negative
            x = -x;
        }

        turtle.turtleUpDown(x);
    }

    /**
     * Turn right/left.  Can also take arg for desired degrees-- default is 90.
     * 
     * @param sender
     * @param args
     */	
    @Command(
            aliases = { "t", "turn" },
            description = "Turtle turn",
            permissions = { "" },
            toolTip = "/t <right/left> [degrees]")
    public void TurtleTurn(MessageReceiver sender, String[] args)
    {

        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (args.length<2)  {  //not enough arguments
            turtle.turtleConsole("Not enough arguments.");
            return;
        }

        //Get desired direction from args
        String dir = args[1];
        boolean left = true;  //default dir is left  

        if (dir.equals("right") || dir.equals("RIGHT") || dir.equals("Right"))  {  //going right
            left = false;
        }  else if (!dir.equals("left") && !dir.equals("LEFT") && !dir.equals("Left"))  { //bad input
            turtle.turtleConsole("That is not a valid direction.");
            return;
        }

        //Get desired turn amount
        int deg = 90;  //default turn amount
        if (args.length == 3)  {  //different amount specified
            try  {
                deg = Integer.parseInt(args[2]);
            }  catch (Exception e)  {  //arg not a number
                turtle.turtleConsole("Not a valid turn amount.");
            }
        }        

        //turn turtle
        turtle.turtleTurn(left, deg);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRIVATE HELPER FUNCTIONS

    /**
     * Checks whether turtle mode is on.  If so, returns true.  If not, alerts user and returns false.
     * 
     * @return Status of turtle mode
     */
    private boolean checkTT(MessageReceiver sender)  {
        if (tt)  { //turtle mode is on-- no problems
            return true;
        }  else  {  //turtle mode is off-- need to alert user
            turtle.turtleConsole("Turtle mode is not on.");
            return false;
        }
    }

    /**
     * Checks whether block placement mode is on.  If so, returns true.  If not, alerts user and returns false.
     * 
     * @return Status of block placement mode
     */
    private boolean checkBP(MessageReceiver sender)  {
        if (turtle.getBP())  { //block placement mode is on-- no problems
            return true;
        }  else  {  //block placement mode is off-- need to alert user
            turtle.turtleConsole("Block placement mode is not on.");
            return false;
        }
    }
}