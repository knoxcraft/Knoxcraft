package edu.knox.minecraft.serverturtle;

import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;

//TODO:  maybe don't need these anymore. See below.
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.knoxcraft.hooks.KCTUploadHook;
import edu.knoxcraft.http.server.HttpUploadServer;
import edu.knoxcraft.turtle3d.KCTCommand;
import edu.knoxcraft.turtle3d.KCTScript;

public class TurtleAPI {
    //TODO:  should just make state field and eliminate args?  Or else make class static.

    /**
     * Output a message to the player console.
     * 
     * @param sender
     * @param args
     */
    public void TurtleConsole(MessageReceiver sender, String msg)
    {
        //Display string in console
        sender.message(msg); 
    }  

    /**
     * Toggle block placement mode on/off.
     * 
     * TODO:  IF placement off -> dont change vs AIr placement
     * @param sender
     * @param args
     */
    public void TurtleBlockPlace(TurtleState state, MessageReceiver sender)
    {
        state.toggleBp();
        TurtleBlockPlaceStatus(state, sender);  //alert user about change
    }

    /**
     * Checks whether block placement mode is on.
     * @param sender
     * @param args
     */
    public void TurtleBlockPlaceStatus(TurtleState state, MessageReceiver sender)
    {
        if(state.getBp())  {
            TurtleConsole(sender, "Block placement mode on.");
        }  else {
            TurtleConsole(sender, "Block placement mode off.");
        }
    }

    /**
     * Set turtle position (relative coords)
     * @param sender
     * @param args
     */
    public void TurtleSetRelPosition(TurtleState state, int x, int y, int z)
    {     
        state.setRelPos(new Position(x, y, z));
    }

    /**
     * Set turtle direction.  Number based.
     * 
     * @param sender
     * @param args
     */
    public void TurtleSetDirection(TurtleState state, int dir)
    {
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
        state.setDir(Direction.getFromIntValue(dir));
    }

    /**
     * Get current position (relative)
     * @param sender
     * @param args
     */
    public void TurtleGetPosition(TurtleState state, MessageReceiver sender)
    {
        TurtleConsole(sender, "" + state.getRelPos());
    }

    /**
     * Get current position of Turtle in game coords
     * @param sender
     * @param args
     */
    public void TurtleGetGamePosition(TurtleState state, MessageReceiver sender)
    {
        TurtleConsole(sender, "" + state.getGamePos());
    }

    /**
     * Get position of relative origin (Player's pos at Turtle on) in game coords
     * @param sender
     * @param args
     */
    public void TurtleGetOriginPosition(TurtleState state, MessageReceiver sender)
    {
        TurtleConsole(sender, "" + state.getOriginPos());
    }

    /**
     * Get current direction (relative)
     * @param sender
     * @param args
     */
    public void TurtleGetDirection(TurtleState state, MessageReceiver sender)
    {
        TurtleConsole(sender, "" + state.getDir());
    }

    /**
     * Set block type (int based)
     * @param sender
     * @param args
     */
    public void TurtleSetBlockType(TurtleState state, int blockType)
    {
        if (!state.getBp())  //don't allow if block placement mode isn't on
            return;

        state.setBt(BlockType.fromId(blockType));      
    }

    /**
     * set Block type (string/BlockType based)
     * @param sender
     * @param args
     */
    //TODO implementation-- maybe we don't need this version?

    /**
     * Get current block type
     * @param sender
     * @param args
     */
    public void TurtleGetBlockType(TurtleState state, MessageReceiver sender)
    {
        if (!state.getBp())  //don't allow if block placement mode isn't on
            return;

        //report current BT of turtle	
        TurtleConsole(sender, "" + state.getBt());
    }

    /**
     * Move (forward/back)
     * 
     * @param sender
     * @param args
     */
    public void TurtleMove(TurtleState state, int dist)
    {
        boolean fd = false;  //flipped direction (for moving backward) 

        //check if distance is negative (going backward)
        if (dist < 0){  
            //if so, reverse turtle direction
            dist = Math.abs(dist);
            flipDir(state);
            fd = true;
        }

        for (int i = dist; i > 0; i--){

            //update turtle position
            state.setRelPos(calculateMove(state.getRelPos(), state.getDir(), false, false));
            state.updateGamePos();

            //Place block if block placement mode on
            if (state.getBp()) {
                state.getWorld().setBlockAt(state.getGamePos(), state.getBt());                 
                //TODO:  keep track of this block to undo
            }
        }

        //if reversed turtle direction, reset to original
        if (fd == true){
            flipDir(state);
        }
    }

    /**
     * Moves turtle up/down
     * @param sender
     * @param args
     */
    public void TurtleUpDown(TurtleState state, int dist)
    {
        boolean up = true;  //default direction is up

        //check if distance is negative (going down)
        if (dist < 0 ){  
            //if so, reverse turtle direction
            dist = Math.abs(dist);
            up = false;
        }

        for (int i = dist; i > 0; i--){

            //update turtle position
            state.setRelPos(calculateMove(state.getRelPos(), state.getDir(), up, !up)); 
            state.updateGamePos();

            //Place block if block placement mode on
            if (state.getBp()) {
                state.getWorld().setBlockAt(state.getGamePos(), state.getBt());    
                //TODO:  keep track of this block to undo
            }
        }
    }

    /**
     * Turn right/left..
     * 
     * @param sender
     * @param args
     */	
    public void TurtleTurn(TurtleState state, boolean left, int deg)
    {
        //turn turtle
        state.setDir(calculateTurn(state.getDir(), left, deg));
    }

    //TODO:  Not sure if this still belongs in this class, or in the new plugin we'll need/TurtleTester
    /* @HookHandler
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
    }  */

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PRIVATE HELPER FUNCTIONS

    /**
     * Reverses relative direction (turn 180 degrees).  Used when moving backward.
     */
    private void flipDir(TurtleState state){
        //get current direction (N, NE, ... , S --> 0, 1, ... , 7)
        int dirInt = state.getDir().getIntValue();  

        //calculate new direction
        dirInt = (dirInt + 4) % 8;

        //update relDir
        state.setDir(Direction.getFromIntValue(dirInt));
    }

    /**
     * Calculate move.  Returns new relative position of Turtle.
     * 
     * @param p Initial relative position of turtle
     * @param d forward direction of turtle
     * @param up Is this move going up?
     * @param down Is this move going down?
     * @return New relative position of turtle.
     */
    private Position calculateMove (Position p, Direction d, boolean up, boolean down){ 

        int dn = d.getIntValue();  //get direction number

        //check if vertical motion
        if (up || down ){
            if (up)  {  //moving up
                //add y +1
                p.setY(p.getBlockY() + 1);

            }else  {  //otherwise moving down
                //subtract y -1
                p.setY(p.getBlockY() - 1);
            }

        }  else  {  //2D motion
            if(dn == 0){ //NORTH
                //subtract z -1
                p.setZ(p.getBlockZ() - 1);

            }else if(dn == 1){//NORTHEAST
                //subtract z -1
                //add x +1
                p.setZ(p.getBlockZ() - 1);
                p.setX(p.getBlockX() + 1);

            }else if(dn == 2){//EAST
                //add x +1
                p.setX(p.getBlockX() + 1);

            }else if(dn == 3){//SOUTHEAST
                //add z +1
                //add x +1
                p.setZ(p.getBlockZ() + 1);
                p.setX(p.getBlockX() + 1);

            }else if(dn == 4){//SOUTH
                //add z +1
                p.setZ(p.getBlockZ() + 1);

            }else if(dn == 5){//SOUTHWEST
                //add z +1
                //subtract x -1
                p.setZ(p.getBlockZ() + 1);
                p.setX(p.getBlockX() - 1);

            }else if(dn == 6){//WEST
                //subtract x -1
                p.setX(p.getBlockX() - 1);

            }else if(dn == 7){//NORTHWEST
                //subtract z -1
                //subtract x -1
                p.setZ(p.getBlockZ() - 1);
                p.setX(p.getBlockX() - 1);

            }else {
                //BAD STUFF
                //Not one of the 8 main directions.  
                //Will require more math, but maybe we don't want to worry about this case.
            }
        }
        return p;  //return updated position
    }

    /**
     *  Calculate turn.  Returns new relative direction of Turtle.
     *  
     * @param d  Initial relative direction of turtle.
     * @param left Is this turn going left?  (False -> turning right)
     * @param deg  number of degrees to turn in specified direction
     * @return New relative direction of turtle.
     */
    private Direction calculateTurn(Direction d, boolean left, int deg)  {

        //get current direction (N, NE, ... , S --> 0, 1, ... , 7)
        int dirInt = d.getIntValue();  

        //calculate new direction            
        //This currently only works correctly for 45 deg intervals.  It may be okay to leave it that way.

        int turns = deg/45;  //desired number of eighth turns

        if (left)  {  //turning left
            dirInt -= turns;
        }  else  {  //turning right
            dirInt += turns;
        }

        dirInt = dirInt % 8;

        //update direction and return
        d = Direction.getFromIntValue(dirInt);
        return d;
    }
}