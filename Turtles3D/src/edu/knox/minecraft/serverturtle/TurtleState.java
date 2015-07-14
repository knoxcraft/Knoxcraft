package edu.knox.minecraft.serverturtle;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;
import net.canarymod.chat.MessageReceiver;

public class TurtleState {
    //TODO:  should this just have a sender variable too?  Then wouldn't need to pass it all the time in TurtleAPI...
    
    //POSITION VARIABLES
    
    //in world position of player at turtle on --> (0,0,0) for Turtle's relative coord system.
    private Position originPos;
    
    //current relative position
    private Position relPos;
    
    //true current position/direction in game coords(made by combining relative and real)
    private Position gamePos;
    private Direction dir;

    //OTHER VARIABLES
    private boolean bp = false;  //Block Place on/off
    private BlockType bt = BlockType.Stone;  //default turtle block type 
    private World world;  //World in which all actions occur
    
    ///////////////////////////////////////////////////////////////////////////////
    
    /**
     * Initialize the turtle.
     * @param sender
     */
    public void turtleInit(MessageReceiver sender)  {
        //GET WORLD
        world = sender.asPlayer().getWorld();

        //Set up positions

        //Get origin Position and Direction
        originPos = sender.asPlayer().getPosition();
        dir = sender.asPlayer().getCardinalDirection();

        //Make the Relative Position
        relPos = new Position(0,0,0);

        //Update game position
        gamePos = new Position();
 
        updateGamePos();
    }   
    
    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return the originPos
     */
    public Position getOriginPos() {
        return originPos;
    }

    /**
     * @param originPos the originPos to set
     */
    public void setOriginPos(Position originPos) {
        this.originPos = originPos;
    }

    /**
     * @return the relPos
     */
    public Position getRelPos() {
        return relPos;
    }

    /**
     * @param relPos the relPos to set
     */
    public void setRelPos(Position relPos) {
        this.relPos = relPos;
    }

    /**
     * @return the gamePos
     */
    public Position getGamePos() {
        return gamePos;
    }

    /**
     * Update game pos
     */
    public void updateGamePos() {
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

    /**
     * @return the dir
     */
    public Direction getDir() {
        return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(Direction dir) {
        this.dir = dir;
    }

    /**
     * @return the bp
     */
    public boolean getBp() {
        return bp;
    }

    /**
     * @param Toggle block placement mode
     */
    public void toggleBp() {
        bp = !bp;
    }

    /**
     * @return the bt
     */
    public BlockType getBt() {
        return bt;
    }

    /**
     * @param bt the bt to set
     */
    public void setBt(BlockType bt) {
        this.bt = bt;
    } 
}
