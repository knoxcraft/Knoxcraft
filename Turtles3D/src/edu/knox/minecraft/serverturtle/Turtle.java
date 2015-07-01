package edu.knox.minecraft.serverturtle;

import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;

public class Turtle {

    /**
     * Constructor
     */
    public Turtle(){
        //Nothing to a turtle
    }

    /**
     * Move.  Returns new relative position of Turtle.
     * 
     * @param p Initial relative position of turtle
     * @param d forward direction of turtle
     * @param up Is this move going up?
     * @param down Is this move going down?
     * @return New relative position of turtle.
     */
    public Position move (Position p, Direction d, boolean up, boolean down){ 

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
     *  Turn.  Returns new relative direction of Turtle.
     *  
     *  TODO:  for the degree version of this, it may be worth just taking the 
     *  number of "notches"/eighth turns instead of real degrees.  Might be easier for users. 
     *  
     * @param d  Initial relative direction of turtle.
     * @param left Is this turn going left?  (False -> turning right)
     * @param deg  number of degrees to turn in specified direction
     * @return New relative direction of turtle.
     */
    public Direction turn(Direction d, boolean left, int deg)  {

        //get current direction (N, NE, ... , S --> 0, 1, ... , 7)
        int dirInt = d.getIntValue();  

        //calculate new direction    
        if (deg != 0)  {  //using basic string-based version w/o degrees
            if (left)  {  //turning left
                dirInt -= 2;
            }  else  {  //turning right
                dirInt += 2;
            }
            
        }  else {  //turning by degrees
            //This currently only works for 45 deg intervals.  It may be okay to leave it that way.
            
            int turns = deg/45;  //desired number of eighth turns
            
            if (left)  {  //turning left
                dirInt -= turns;
            }  else  {  //turning right
                dirInt += turns;
            }
        }
        dirInt = (dirInt + 4) % 8;
        
        //update direction and return
        d = Direction.getFromIntValue(dirInt);
        return d;
    }
}