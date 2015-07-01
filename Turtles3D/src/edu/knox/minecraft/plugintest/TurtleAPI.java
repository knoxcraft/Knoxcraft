package edu.knox.minecraft.plugintest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import edu.knoxcraft.hooks.UploadJSONHook;
import edu.knoxcraft.http.server.HttpUploadServer;

// If tt is not true-> no other command works??
//Or we could keep rel pos/dir because they get set up intially (or should be)

//Things need to be overly simple during testing for ease of use

//In time, need to build in string verification for correct input style (ie. All caps, etc)


public class TurtleAPI extends Plugin implements CommandListener, PluginListener {

    Turtle turtle;
    BlockType bt = BlockType.Stone;
    //World in which all actions occur
    private World world;
    //in world position of player -> (0,0,0) for Turtle. This should probably be called originPos.
    private Position truePos;
    private Direction trueDir;
    //relative position
    private Position relPos;
    private Direction relDir;
    //current position (made by combining relative and real) -> this gets sent to the game.
    //May want to call this truePos (instead of the other truePos) or gamePos.
    private Position curPos;
    private Direction curDir;

    //Turtle on/off
    private boolean tt = false;
    //Block Place on/off
    private boolean bp = false;
    //flipped direction
    private boolean fd = false;
    private HttpUploadServer httpServer;
    public static Logman logger;
    
    public TurtleAPI() {
        TurtleAPI.logger = getLogman();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //HELPER FUNCTIONS

    private void updateCurPos(){
        int xt = truePos.getBlockX();
        int yt = truePos.getBlockY();
        int zt = truePos.getBlockZ();		

        int xr = relPos.getBlockX();
        int yr = relPos.getBlockY();
        int zr = relPos.getBlockZ();

        //curPos = truePos + relPos;

        curPos.setX(xt+xr);
        curPos.setY(yt+yr);
        curPos.setZ(zt+zr);
    }

    private void updateCurDir(){
    }

    //should this be called updateRelPos?  It doesn't return/print anything...
    private void getRelPos(){
        int xc = curPos.getBlockX();
        int yc = curPos.getBlockY();
        int zc = curPos.getBlockZ();

        int xt = truePos.getBlockX();
        int yt = truePos.getBlockY();
        int zt = truePos.getBlockZ();	

        relPos.setX(xc-xt);
        relPos.setY(yc-yt);
        relPos.setZ(zc-zt);

    }

    private void flipDir(){
        int y = relDir.getIntValue();
        switch(y){
        case(0):

            break;
        case(1):

            break;
        case(2):

            break;
        case(3):

            break;
        case(4):

            break;
        case(5):

            break;
        case(6):

            break;
        case(7):

            break;		
        }				
    }

    private void getString(MessageReceiver sender, boolean b){
        //Get the Boolean value 
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = b + "";

        //return status of BP using TurtleConsole
        TurtleConsole(sender, str);
    }

    private void getString(MessageReceiver sender, BlockType b){
        //Get the Boolean value 
        String []str = new String [2];
        str[0] = "/c";
        str[1] = b.toString() + "";

        //return status of BP using TurtleConsole
        TurtleConsole(sender, str);
    }

    private void getString(MessageReceiver sender, Direction b){
        //Get the Boolean value 
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = b.toString() + "";

        //return status of BP using TurtleConsole
        TurtleConsole(sender, str);
    }

    private void getString(MessageReceiver sender, Position b){
        //Get the Boolean value 
        String [] str = new String [2];
        str[0] = "/c";
        str[1] = b.toString() + ""; //Need to overload / fix this output

        //return status of BP using TurtleConsole
        TurtleConsole(sender, str);
    }

    
    
    /**
     * Checks whether block placement mode is on.  If so, returns true.  If not, alerts user and returns false.
     * @return Status of block placement mode
     */
    private boolean checkBP(MessageReceiver sender)  {
        if (bp)  { //block placement mode is on-- no problems
            return true;
        }  else  {  //block placement mode is off-- need to alert user
            String [] str = new String [2];
            str[0] = "/c";
            str[1] = "Block placement mode is not on.";
            TurtleConsole(sender, str);
            return false;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Called when plugin is disabled.  Currently does nothing.
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
        
        //Relative pos stuff

        //Get True Position and Direction
        truePos = sender.asPlayer().getPosition();
        trueDir = sender.asPlayer().getCardinalDirection();

        //Make the Relative Position
        relPos = new Position(0,0,0);
        relDir = Direction.getFromIntValue(0);
        //updateCurPos();
        //updateCurDir();
        //Faces player direction
        //Need to build in safety checks
        //Also, better way?
        //If doesn't work-> set to North ONLY, as way to start debugging
        relDir = trueDir; //??

        //Turning on Turtle
        tt = true;
        getString(sender, truePos);
        getString(sender, trueDir);
        getString(sender, tt);
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
        getString(sender, tt);
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
     * Checks whether turtle mode is on.  If so, returns true.  If not, alerts user and returns false.
     * @return Status of turtle mode
     */
    @Command(
            aliases = { "tt?", "TurtleToggle" },
            description = "Turtle ",
            permissions = { "" },
            toolTip = "/tt?")
    private boolean checkTT(MessageReceiver sender)  {
        if (tt)  { //turtle mode is on-- no problems
            return true;
        }  else  {  //turtle mode is off-- need to alert user
            String [] str = new String [2];
            str[0] = "/c";
            str[1] = "Turtle mode is not on.";
            TurtleConsole(sender, str);
            return false;
        }
    }

    //Other Commands*******************************************************************************

    /**
     * Output a message to the player console.
     * Expects args[0] = "c"   
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
        TurtleBlockPlaceStatus(sender, args);
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

        getString(sender, bp);
    }

    /**
     * Set turtle position (relative coords)
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "sp", "SetPosition" },
            description = "Set Turtle position",
            permissions = { "" },
            toolTip = "/sp")
    public void TurtleSetPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //Change location to new location based on relative coordinates
        relPos = new Position (Integer.parseInt(args[1]), Integer.parseInt(args[2]),Integer.parseInt(args[3]));
    }

    /**
     * Set turtle direction.  Textbased (North, South, East, West)
     * Number based for simplicity in early tests?
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
     * Return current position (relative)
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "rp", "ReturnPosition" },
            description = "Return Turtle position",
            permissions = { "" },
            toolTip = "/rp")
    public void TurtleReturnPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //return position of turtle (relative position)

        //   getRelPos();
        getString(sender, relPos);
    }
    
    /**
     * Return current position of Turtle in game coords
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "rgp", "ReturnGamePosition" },
            description = "Return Turtle position in game coords",
            permissions = { "" },
            toolTip = "/rgp")
    public void TurtleReturnGamePosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //return position of turtle (game coord position)
        getString(sender, curPos);
    }
    
    /**
     * Return position of relative origin (Player's pos at Turtle on) in game coords
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "rop", "ReturnOriginPosition" },
            description = "Return origin position in game coords",
            permissions = { "" },
            toolTip = "/rop")
    public void TurtleReturnOriginPosition(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //return position of origin (game coord position)
        getString(sender, truePos);
    }

    /**
     * Return current direction (relative)
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "rd", "ReturnDirection" },
            description = "Return Turtle direction",
            permissions = { "" },
            toolTip = "/rd")
    public void TurtleReturnDirection(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //return position of turtle	
        getString(sender, relDir);
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
     * Return current block type
     * @param sender
     * @param args
     */
    @Command(
            aliases = { "rb", "ReturnBlockType" },
            description = "Return Turtle block type",
            permissions = { "" },
            toolTip = "/rb")
    public void TurtleReturnBlockType(MessageReceiver sender, String[] args)
    {
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        if (!checkBP(sender))  //don't allow if block placement mode isn't on either
            return;
        
        //return current BT of turtle	
        getString(sender, bt);
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

        //move turtle	
        // Move should act in a loop to go 1 -> just happens. To go 10, loop 10 times
        //Allows easier pos/ bp coding
        int x = Integer.parseInt(args[1]);
        //check if negative
        if (x < 0){
            x = -x;
            flipDir();
            fd = true;
        }
        for (int i = x; i > 0; i--){
            //If block place True
            if (bp) {

                world.setBlockAt(relPos, bt);
            }else {
                //Place nothing
            }

            relPos = turtle.move(relPos, relDir, false, false);
        }
        if (fd = true){
            fd = false;
            flipDir();
        }
    }

    /**
     * turn (number based) (degrees)
     * @param sender
     * @param args
     */
    //TODO implementation -> will allow diagonals

    /**
     * Turn (Right/Left) (text based)
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
        if (!checkTT(sender))  //Don't allow if turtle mode is not on
            return;

        //turn turtle (left or right)

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
        
        //move up or down turtle (left or right)
        //move turtle	
        // Move should act in a loop to go 1 -> just happens. To go 10, loop 10 times
        //Allows easier pos/ bp coding
        int x = Integer.parseInt(args[1]);
        boolean dir = true;
        //check if negative
        if (x < 0){
            x = -x;
            dir = !dir;
        }
        for (int i = x; i > 0; i--){
            //If block place True
            if (bp) {

                world.setBlockAt(relPos, bt);
            }else {
                //Place nothing
            }

            relPos = turtle.move(relPos, relDir, dir, !dir);//RelDir dont matter
        }				
    }
    @HookHandler
    public void uploadJSON(UploadJSONHook hook) {
        // TODO: Write classes to represent commands
        // TODO: Convert JSON into a list of commands
        logger.info("Hook called");
        JSONParser parser=new JSONParser();
        try {
            logger.info(hook.getJSON());
            JSONObject json=(JSONObject)parser.parse(hook.getJSON());
            
            String scriptname=(String)json.get("scriptname");
            logger.info(String.format("%s\n", scriptname));
            
            JSONArray lang= (JSONArray) json.get("commands");
            for (int i=0; i<lang.size(); i++) {
                JSONObject cmd=(JSONObject)lang.get(i);
                String commandName=(String)cmd.get("cmd");
                JSONObject args=(JSONObject)cmd.get("args");
                if (commandName.equals("forward")) {
                    //int distance=getInt(args, "dist");
                    long distance=(long)args.get("dist");
                    // Move forward by the appropriate distance
                    logger.info(String.format("Move forward by %d\n", distance));
                } else if (commandName.equals("turn")) {
                    String dir=(String)args.get("dir");
                    //int degrees=getInt(args, "degrees");
                    long degrees=(long)args.get("degrees");
                    logger.info(String.format("turn %s %d degrees\n", dir, degrees));
                }
            }
        } catch (ParseException e) {
            // TODO: log better? handle better?
            throw new RuntimeException(e);
        }
    }
}
