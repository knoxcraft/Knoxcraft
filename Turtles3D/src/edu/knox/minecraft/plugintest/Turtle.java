package edu.knox.minecraft.plugintest;

import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;

public class Turtle {
    
    //TODO:  Will we ever actually make a Turtle, or could this class be static?

    //Make a Turtle

    //Constructor

    /**
     * Move.  Returns new relative position of Turtle.
     * 
     * TODO:  check if canary handles vertical motion in a simpler way (particularly vertical diagonals)
     * Also, does this method need to take a distance, or are we just calling it multiple times?
     * 
     * @param p Initial relative position of turtle
     * @param d forward direction of turtle
     * @param up Is this move going up?
     * @param down Is this move going down?
     * @return New relative position of turtle.
     */
    public Position move (Position p, Direction d, boolean up, boolean down){ 

        int dn = d.getIntValue();  //get direction number

        /*TODO-- do we need to do something to make sure both up and down aren't true?
        As is stands, if up is true we just do that and don't check down, so it may not matter.*/

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
                //Not one of the 8 main directions.  Will require more math.
            }
        }
        return p;  //return updated position
    }
}