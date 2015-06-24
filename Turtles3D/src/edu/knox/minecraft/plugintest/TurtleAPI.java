package edu.knox.minecraft.plugintest;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.commandsys.Command;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.canarymod.plugin.Plugin;
import net.canarymod.plugin.PluginListener;


//Things need to be overly simple during testing for ease of use

//In time, need to build in string verification for correct input style (ie. All caps, etc)


public class TurtleAPI  extends Plugin implements CommandListener, PluginListener {

	TurtleAPI turtle;
	//World in which all actions occur
	private World world;
	//in world position
	private Position truePos;
	private Direction trueDir;
	//relative position
	private Position relPos;
	private Direction relDir;
	//current position (made by combining relative and real) -> this gets sent to the game 
	//By subtracting truePos from curPos, you can get relPos -> MaKE helper methodd to handle this!!!!
	private Position curPos;
	private Direction curDir;
	
	//Turtle on/off
	private boolean tt;
	//Block Place on/off
	private boolean bp;
	
	///***************???????????/
	//OLD CODE
//	 @Command(
//	            aliases = { "turtleoff", "toff" },
//	            description = "Turns off turtle mode",
//	            permissions = { "" },
//	            toolTip = "/turtleoff")
//	    public void turtleOffCommand(MessageReceiver sender, String[] args)
//	    {
//	    	turtleMode = false;
//	    	turtle = null;
//	    	
//	    	//alert player
//	    	sender.message("Turtle mode off");
//	    }
//	    
//	    @Command(
//	            aliases = { "forward"},
//	            description = "Move the turtle forward dropping blocks",
//	            permissions = { "" },
//	            toolTip = "/forward [spaces]")
//	    public void turtleForwardCommand(MessageReceiver sender, String[] args)
//	    {
//	    	if (turtleMode)  {
//	    		//did the user specify a number of spaces?
//	    		int spaces = 1;   		
//	    		if (args.length > 1)  {
//	    			spaces = Integer.parseInt(args[1]);
//	    		}
//	    		
//	    		//move forward the desired number of spaces
//	    		Vector3D forDir = sender.asPlayer().getForwardVector();
//	    		for (int i=0; i<spaces; i++)  {
//	    			turtle.forward(forDir);
//	    		}
//	    		
//	    	}  else {
//	    		//alert player
//	        	sender.message("Turtle mode is not on.");
//	    	}
	//END OF OLD CODE
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Constructors
	//Make a Turtle
	
	public TurtleAPI(World world) {
		this.world = world;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//HELPER FUNCTIONS
	private void updateCurPos(){
		//curPos = truePos + relPos;
		
		
	}
	private Position getRelPos(){
		return null;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void disable() {
		// TODO Auto-generated method stub
		//IGNORE :)

	}

	@Override
	public boolean enable() {
		try {
			Canary.hooks().registerListener(this, this);
			getLogman().info("Enabling "+getName() + " Version " + getVersion()); //getName() returns the class name, in this case TurtleAPI
			getLogman().info("Authored by "+getAuthor());
			Canary.commands().registerCommands(this, this, false);
			return true;
		} catch (CommandDependencyException e){
			throw new RuntimeException(e);
		}
	}

	//API TIME


	//Activate commands-> Set up relAative position
	// 0,0,0 is player location -> forward is player direction


	//Turn On
	@Command(
			aliases = { "ton" },
			description = "Turtle On",
			permissions = { "" },
			toolTip = "/ton")
	public void TurtleOn(MessageReceiver sender, String[] args)
	{
		//Relative pos stuff
		
		//Get True Position and Direction
		truePos = sender.asPlayer().getPosition();
		trueDir = sender.asPlayer().getCardinalDirection();
		
		//Make the Relative Position
		relPos = new Position(0,0,0);
		
		updateCurPos();
		//Faces player direction
		//Need to build in safety checks
		//Also, better way?
		//If doesn't work-> set to North ONLY, as way to start debugging
		relDir = trueDir; //??
		
		
		


		//Turning on Turtle
		tt = true;
	}
	//Turn off
	@Command(
			aliases = { "toff" },
			description = "Turtle Off",
			permissions = { "" },
			toolTip = "/toff")
	public void TurtleOff(MessageReceiver sender, String[] args)
	{
		//Turning off Turtle
		tt = false;
		turtle = null;
	}

	//Turn on/off (toggle)
	@Command(
			aliases = { "tt" },
			description = "Turtle Toggle",
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


	//Other Commands*******************************************************************************

	//Console output
	@Command(
			aliases = { "c" },
			description = "Turtle Console",
			permissions = { "" },
			toolTip = "/c + 'String'")
	public void TurtleConsole(MessageReceiver sender, String[] args)
	{
		//Display string in console
        for (String s : args) {
        	//Assumes s is a valid string-> technically true if using Minecraft input, but might not always be true?
            sender.message(s);
        }

	}

	//Place Block on/off -> IF placement off -> dont change vs AIr placement)
	@Command(
			aliases = { "bp" },
			description = "Turtle Block Placement",
			permissions = { "" },
			toolTip = "/bp")
	public void TurtleBlockPlace(MessageReceiver sender, String[] args)
	{
		bp = !bp;
		TurtleBlockPlaceStatus(sender, args);
	}
	
	
	//Placement On? (status)
	@Command(
			aliases = { "bp?" },
			description = "Turtle Block Placement",
			permissions = { "" },
			toolTip = "/bp?")
	public void TurtleBlockPlaceStatus(MessageReceiver sender, String[] args)
	{
		//Get the Boolean value 
		String [] str = new String [5];
		str[0] = "/c";
		str[1] = bp + "";
				
		//return status of BP using TurtleConsole
		TurtleConsole(sender, str);
	}
	
	
	//Set position (based on relative grid)
	@Command(
			aliases = { "sp" },
			description = "Turtle Setpos",
			permissions = { "" },
			toolTip = "/sp'")
	public void TurtleSetPosition(MessageReceiver sender, String[] args)
	{
		//Chnage location to new location based on relative coordinates

	}
	//Set direction (textbased)(North, South, East, West)
	//Number based for simplicity in early tests?

	@Command(
			aliases = { "sd" },
			description = "Turtle Setdir",
			permissions = { "" },
			toolTip = "/sd'")
	public void TurtleSetDirection(MessageReceiver sender, String[] args)
	{
		//Chnage direction
		//CANARY MOD API CODE SECTION :::
		//			switch (value) {
		//			025            case 0:
		//			026                return NORTH;
		//			027
		//			028            case 1:
		//			029                return NORTHEAST;
		//			030
		//			031            case 2:
		//			032                return EAST;
		//			033
		//			034            case 3:
		//			035                return SOUTHEAST;
		//			036
		//			037            case 4:
		//			038                return SOUTH;
		//			039
		//			040            case 5:
		//			041                return SOUTHWEST;
		//			042
		//			043            case 6:
		//			044                return WEST;
		//			045
		//			046            case 7:
		//			047                return NORTHWEST;
		//			048
		//			049            default:
		//			050                return ERROR;
		//			051        }
		//////////////////////////////::::::

	}

	//Return current pos
	@Command(
			aliases = { "rp" },
			description = "Turtle retpos",
			permissions = { "" },
			toolTip = "/rp'")
	public void TurtleReturnPosition(MessageReceiver sender, String[] args)
	{
		//return position of turtle (relative position)
		
		//   getRelPos();

	}
	//return Direction (status)
	@Command(
			aliases = { "rd" },
			description = "Turtle retdir",
			permissions = { "" },
			toolTip = "/rd'")
	public void TurtleReturnDirection(MessageReceiver sender, String[] args)
	{
		//return position of turtle	

	}
	//set Block Type (int based)
	@Command(
			aliases = { "sb" },
			description = "Turtle set bt",
			permissions = { "" },
			toolTip = "/sb'")
	public void TurtleSetBlockType(MessageReceiver sender, String[] args)
	{
		//set current BT of turtle	

	}

	//set Block type (string/BlockType based)
	//TODO implementation

	//return block type
	@Command(
			aliases = { "rb" },
			description = "Turtle ret bt",
			permissions = { "" },
			toolTip = "/rb'")
	public void TurtleReturnBlockType(MessageReceiver sender, String[] args)
	{
		//return current BT of turtle	

	}


	//move (forward/back)
	@Command(
			aliases = { "m" },
			description = "Turtle move",
			permissions = { "" },
			toolTip = "/m'")
	public void TurtleMove(MessageReceiver sender, String[] args)
	{
		//move turtle	
		// Move should act in a loop to go 1 -> just happens. To go 10, loop 10 times
		//Allows easier pos/ bp coding
		
		//If block place True
		if (bp) {
			Position pos = sender.asPlayer().getPosition();
	    	//change
	    	int type = Integer.parseInt(args[1]);
	    	BlockType t = BlockType.fromId(type);
	    	world.setBlockAt(pos, t);
		}else {
			//Place nothing
			
		}
	}

	//turn (number based) (degrees)
	//TODO implementation -> will allow diagonals

	//turn (Right/Left) (text based)
	@Command(
			aliases = { "t" },
			description = "Turtle turn",
			permissions = { "" },
			toolTip = "/t'")
	public void TurtleTurn(MessageReceiver sender, String[] args)
	{
		//turn turtle (left or right)

	}



}
