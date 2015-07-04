package edu.knox.minecraft.serverturtle;

import net.canarymod.Canary;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.hook.HookHandler;
import net.canarymod.logger.Logman;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.knoxcraft.hooks.KCTUploadHook;
import edu.knoxcraft.http.server.HttpUploadServer;
import edu.knoxcraft.turtle3d.KCTCommand;
import edu.knoxcraft.turtle3d.KCTScript;

//Things need to be overly simple during testing for ease of use

//In time, need to build in string verification for correct input style (ie. All caps, etc)

public class TurtleAPI extends Plugin implements CommandListener, PluginListener {

    //POSITION VARIABLES

    //in world position/direction of player at turtle on --> (0,0,0) for Turtle's relative coord system.
    private Position originPos;
    private Direction originDir;

    //current relative position
    private Position relPos;
    private Direction relDir;

    //true current position in game coords(made by combining relative and real)
    private Position gamePos;
    private Direction gameDir;  //TODO:  I don't think this is ever updated... //true Facts  //do we even need this?

    //MODE TOGGLES
    private boolean tt = false;  //Turtle on/off
    private boolean bp = false;  //Block Place on/off

    //OTHER VARIABLES
    private Turtle turtle = new Turtle();
    private BlockType bt = BlockType.Stone;  //default turtle block type 
    private World world;  //World in which all actions occur
    private HttpUploadServer httpServer;
    public static Logman logger;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor.
     */
    public TurtleAPI() {
        TurtleAPI.logger = getLogman();
    }

    /**
     * Called when plugin is disabled.
     */
    @Override
    public void disable() {
        httpServer.disable();
    }

    /**
     * Called when plugin is enabled. 
     * @return
     */
    @Override
    public boolean enable() {
        try {
            getLogman().info("Registering plugin");
            Canary.hooks().registerListener(this, this);
            httpServer=new HttpUploadServer();
            httpServer.enable();
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
        turtle = new Turtle();

        //GET WORLD
        world = sender.asPlayer().getWorld();

        //Set up positions

        //Get origin Position and Direction
        originPos = sender.asPlayer().getPosition();
        originDir = sender.asPlayer().getCardinalDirection();

        //Make the Relative Position
        relPos = new Position(0,0,0);
        relDir = originDir;  //Faces player direction
        
        //Update game position
        gamePos = new Position();
        //gameDir = new Direction();
        gameDir = relDir;
        updateGamePos();
        updateGameDir();  //Maybe we don't need this...
        
        //Need to build in safety checks
        //Also, better way?
        //If doesn't work-> set to North ONLY, as way to start debugging

        //Turning on Turtle
        tt = true;
        consoleHelper(sender, "Turtle mode on.  Origin position: ");
        consoleHelper(sender, originPos);
        consoleHelper(sender, originDir);
        //consoleHelper(sender, tt);
    }

    /**
     * Turn off turtle mode.
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
        turtle = null;
        //consoleHelper(sender, tt);
        consoleHelper(sender, "Turtle mode off.");

        //TODO:  Do we need to reset position/direction here?  
        //Or maybe not, since ton does, and we can't access those methods with tt false...
    }

    /**
     * Toggle turtle mode on/off.
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
     * Expects args[0] = "c"
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
        sender.message(message); 
    }

    /**
     * Toggle block placement mode on/off.
     * 
     * TODO:  IF placement off -> dont change vs AIr placement
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

        bp = !bp;
        TurtleBlockPlaceStatus(sender, args);  //alert user about change
    }

    /**
     * Checks whether block placement mode is on.
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

        consoleHelper(sender, bp);
    }

    /**
     * Set turtle position (relative coords)
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "srp", "SetPosition" },
            description = "Set Turtle position in relative coords",
            permissions = { "" },
            toolTip = "/srp")
    public void TurtleSetRelPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //Change location to new location based on relative coordinates
        relPos = new Position (Integer.parseInt(args[1]), Integer.parseInt(args[2]),Integer.parseInt(args[3]));
    }

    /**
     * Set turtle direction.  Textbased (North, South, East, West)
     * Number based for simplicity in early tests?
     * 
     * TODO:  I think this can support the diagonals now.
     * 
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "sd", "SetDirection" },
            description = "Set turtle direction",
            permissions = { "" },
            toolTip = "/sd")
    public void TurtleSetDirection(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        relDir = Direction.getFromIntValue(Integer.parseInt(args[1]));
        // 0 = NORTH
        // 2 = EAST
        // 4 = SOUTH
        // 6 = WEST
        // Else = ERROR
    }

    /**
     * Get current position (relative)
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
        consoleHelper(sender, relPos);
    }

    /**
     * Get current position of Turtle in game coords
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
        consoleHelper(sender, gamePos);
        //TODO:  or maybe this is where the exception is?  Update:  yes.
    }

    /**
     * Get position of relative origin (Player's pos at Turtle on) in game coords
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
        consoleHelper(sender, originPos);
    }

    /**
     * Get current direction (relative)
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
        consoleHelper(sender, relDir);
    }

    /**
     * Set block type (int based)
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "sbt", "SetBlockType" },
            description = "Set Turtle block type",
            permissions = { "" },
            toolTip = "/sbt")
    public void TurtleSetBlockType(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (!checkBP(sender))  //don't allow if block placement mode isn't on either
            return;

        BlockType temp;

        //set current BT of turtle	
        if (!(args.length == 3))
        {
            temp = BlockType.fromId(Integer.parseInt(args[1]));
        }else{
            temp = BlockType.fromIdAndData(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        }

        bt = temp;
    }

    /**
     * set Block type (string/BlockType based)
     * @param sender
     * @param args
     */
    //TODO implementation

    /**
     * Get current block type
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
        consoleHelper(sender, bt);
    }

    /**
     * Move (forward/back)
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "m" , "f", "b", "move", "forward", "back"},
            description = "Turtle move forward/back",
            permissions = { "" },
            toolTip = "/m or /f or /b")
    public void TurtleMove(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;
        
        boolean fd = false;  //flipped direction (for moving backward) 
        int x = 1;  //default move distance
        if(args.length>1)  {  //alternate move distance specified
            x = Integer.parseInt(args[1]);  
        }     
        
        //check if distance is negative (going backward)
        if (x < 0 || args[0].equals("/b") || args[0].equals("/back")){  
            //if so, reverse turtle direction
            x = -x;
            flipDir();
            fd = true;
        }

        for (int i = x; i > 0; i--){

            //Place block if block placement mode on
            if (bp) {
                world.setBlockAt(gamePos, bt);                 
                //TODO:  keep track of this block to undo
            }

            //update turtle position
            relPos = turtle.move(relPos, relDir, false, false);
            updateGamePos();
        }

        //if reversed turtle direction, reset to original
        if (fd == true){
            fd = false;
            flipDir();
        }
    }

    /**
     * Moves turtle up/down
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "u", "d", "up", "down" },
            description = "Turtle up/down",
            permissions = { "" },
            toolTip = "/u or /d")
    public void TurtleUpDown(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        boolean up = true;  //default direction is up
        int x = 1;  //default move distance
        if(args.length>1)  {  //alternate move distance specified
            x = Integer.parseInt(args[1]);  
        } 

        //check if distance is negative (going down)
        if (x < 0 || args[0].equals("/d") || args[0].equals("/down")){  
            //if so, reverse turtle direction
            x = -x;
            up = false;
        }

        for (int i = x; i > 0; i--){

            //Place block if block placement mode on
            if (bp) {
                world.setBlockAt(gamePos, bt);    
                //TODO:  keep track of this block to undo
            }

            //update turtle position
            relPos = turtle.move(relPos, relDir, up, !up);  //only moving vertically--> relDir doesn't matter
            updateGamePos();
        }
    }


    /**
     * Turn (Right/Left) (text based)(degrees)
     * @param sender
     * @param args
     */	
    @Command(
            aliases = { "t", "turn" },
            description = "Turtle turn",
            permissions = { "" },
            toolTip = "/t")
    public void TurtleTurn(MessageReceiver sender, String[] args)
    {
        //TODO implementation -> will allow diagonals

        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //Get desired direction from args
        String dir = args[1];
        boolean left = true;  //default dir is left  

        if (dir.equals("right") || dir.equals("RIGHT") || dir.equals("Right"))  {  //going right
            left = false;
        }  else if (!dir.equals("left") && !dir.equals("LEFT") && !dir.equals("Left"))  { //bad input
            consoleHelper(sender, "That is not a valid direction.");
            return;
        }

        //turn turtle (left or right)
        relDir = turtle.turn(relDir,  left, 0);
        updateGameDir();
    }

    @HookHandler
    public void uploadJSON(KCTUploadHook hook) {
        logger.info("Hook called, json is "+hook.getJSON());
        JSONParser parser=new JSONParser();
        try {
            logger.info(hook.getJSON());
            JSONObject json=(JSONObject)parser.parse(hook.getJSON());

            String scriptname=(String)json.get("scriptname");

            KCTScript script=new KCTScript(scriptname);

            logger.info(String.format("%s\n", scriptname));

            JSONArray lang= (JSONArray) json.get("commands");
            for (int i=0; i<lang.size(); i++) {
                JSONObject cmd=(JSONObject)lang.get(i);
                script.addCommand(cmd);
                logger.info(String.format("script %s has command %s", script.getScriptName(), cmd.get(KCTCommand.CMD)));
            }
            // TODO: Put script someplace now that we've created it
        } catch (ParseException e) {
            // TODO: log better? handle better?
            throw new RuntimeException(e);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRIVATE HELPER FUNCTIONS

    /**
     * Updates game position by combining origin position and current relative position.
     */
    private void updateGamePos(){
        //get origin coords
        int xo = originPos.getBlockX();
        int yo = originPos.getBlockY();
        int zo = originPos.getBlockZ();     

        //get relative coords
        int xr = relPos.getBlockX();
        int yr = relPos.getBlockY();
        int zr = relPos.getBlockZ();

        //update game position
        //gamePos = originPos + relPos;
        gamePos.setX(xo+xr);
        gamePos.setY(yo+yr);
        gamePos.setZ(zo+zr);
    }

    private void updateGameDir(){
        //TODO:  implement this
        //maybe it just needs to be the same as relDir?  Still need to think about this.
        gameDir = relDir;
    }

   private void updateRelPos(){
        int xg = gamePos.getBlockX();
        int yg = gamePos.getBlockY();
        int zg = gamePos.getBlockZ();

        int xo = originPos.getBlockX();
        int yo = originPos.getBlockY();
        int zo = originPos.getBlockZ(); 

        relPos.setX(xg-xo);
        relPos.setY(yg-yo);
        relPos.setZ(zg-zo);
    }


    /**
     * Reverses relative direction (turn 180 degrees).  Used when moving backward.
     */
    private void flipDir(){
        //get current direction (N, NE, ... , S --> 0, 1, ... , 7)
        int dirInt = relDir.getIntValue();  

        //calculate new direction
        dirInt = (dirInt + 4) % 8;

        //update relDir
        relDir = Direction.getFromIntValue(dirInt);
    }

    /**
     * Helper methods for for calling TurtleConsole as a method (rather than in game).  
     * Creates args array and submits desired content to console. 
     *
     * @param sender
     */

    /**
     * Boolean version
     * @param b
     */
    private void consoleHelper(MessageReceiver sender, boolean b){
        //set up array 
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = b + "";

        //output message using TurtleConsole
        TurtleConsole(sender, str);
    }

    /**
     * BlockType version
     * @param bt
     */
    private void consoleHelper(MessageReceiver sender, BlockType bt){
        //set up array 
        String []str = new String [2];
        str[0] = "/c";
        str[1] = bt.toString() + "";

        //output message using TurtleConsole
        TurtleConsole(sender, str);
    }

    /**
     * Direction version
     * @param d
     */
    private void consoleHelper(MessageReceiver sender, Direction d){
        //set up array
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = d.toString() + "";

        //output message using TurtleConsole
        TurtleConsole(sender, str);  //getting exception here?
    }

    /**
     * Position version
     * @param p
     */
    private void consoleHelper(MessageReceiver sender, Position p){
        //set up array
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = p.toString() + ""; //TODO:  Need to overload / fix this output

        //output message using TurtleConsole
        TurtleConsole(sender, str);
    }

    /**
     * String version
     * @param msg
     */
    private void consoleHelper(MessageReceiver sender, String msg) {
        //set up array
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = msg + "";

        //output message using TurtleConsole
        TurtleConsole(sender, str);
    }

    /**
     * Checks whether turtle mode is on.  If so, returns true.  If not, alerts user and returns false.
     * @return Status of turtle mode
     */
    private boolean checkTT(MessageReceiver sender)  {
        if (tt)  { //turtle mode is on-- no problems
            return true;
        }  else  {  //turtle mode is off-- need to alert user
            consoleHelper(sender, "Turtle mode is not on.");
            return false;
        }
    }

    /**
     * Checks whether block placement mode is on.  If so, returns true.  If not, alerts user and returns false.
     * @return Status of block placement mode
     */
    private boolean checkBP(MessageReceiver sender)  {
        if (bp)  { //block placement mode is on-- no problems
            return true;
        }  else  {  //block placement mode is off-- need to alert user
            consoleHelper(sender, "Block placement mode is not on.");
            return false;
        }
    }
}