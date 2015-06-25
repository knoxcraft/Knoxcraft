package edu.knox.minecraft.plugintest;

import net.canarymod.Canary;
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

	Turtle turtle;
	BlockType bt = BlockType.Stone;
	//World in which all actions occur
	private World world;
	//in world position
	private Position truePos;
	private Direction trueDir;
	//relative position
	private Position relPos;
	private Direction relDir;
	//current position (made by combining relative and real) -> this gets sent to the game 
	private Position curPos;
	private Direction curDir;

	//Turtle on/off
	private boolean tt = false;
	//Block Place on/off
	private boolean bp = false;
	//flipped direction
	private boolean fd = false;	

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
	 * Checks whether turtle mode is on.  If so, returns true.  If not, alerts user and returns false.
	 * @return
	 */
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
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Called when plugin is disabled.  Currently does nothing.
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when plugin is enabled. 
	 * @return
	 */
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

	/**
	 * Turn on turtle mode.  Sets up relative position. 
	 * (0,0,0) is player position.  Forward is player direction.
	 * @param sender
	 * @param args
	 */
	@Command(
			aliases = { "ton", "TurtleOn" },
			description = "Turtle On",
			permissions = { "" },
			toolTip = "/ton")
	public void TurtleOn(MessageReceiver sender, String[] args)
	{
		
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
			description = "Turtle Off",
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

	/**
	 * Output a message to the player console.
	 * Expects args[0] = "c"   
	 * @param sender
	 * @param args
	 */
	@Command(
			aliases = { "c", "TurtleConsole" },
			description = "Turtle Console",
			permissions = { "" },
			toolTip = "/c + 'String'")
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
			description = "Turtle Block Placement",
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
			description = "Turtle Block Placement",
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
			description = "Turtle Setpos",
			permissions = { "" },
			toolTip = "/sp'")
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
			description = "Turtle Setdir",
			permissions = { "" },
			toolTip = "/sd'")
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
			aliases = { "rp", "ReturnDirection" },
			description = "Turtle retpos",
			permissions = { "" },
			toolTip = "/rp'")
	public void TurtleReturnPosition(MessageReceiver sender, String[] args)
	{
		if (!checkTT(sender))  //Don't allow if turtle mode is not on
			return;

		//return position of turtle (relative position)

		//   getRelPos();
		getString(sender, relPos);
	}

	/**
	 * Return current direction (relative)
	 * @param sender
	 * @param args
	 */
	@Command(
			aliases = { "rd", "ReturnDirection" },
			description = "Turtle retdir",
			permissions = { "" },
			toolTip = "/rd'")
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
			aliases = { "sb", "SetBlockType" },
			description = "Turtle set bt",
			permissions = { "" },
			toolTip = "/sb'")
	public void TurtleSetBlockType(MessageReceiver sender, String[] args)
	{
		if (!checkTT(sender))  //Don't allow if turtle mode is not on
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
			description = "Turtle ret bt",
			permissions = { "" },
			toolTip = "/rb'")
	public void TurtleReturnBlockType(MessageReceiver sender, String[] args)
	{
		if (!checkTT(sender))  //Don't allow if turtle mode is not on
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
			description = "Turtle move",
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
			toolTip = "/t'")
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
			toolTip = "/u or /d'")
	public void TurtleUpDown(MessageReceiver sender, String[] args)
	{
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
}
