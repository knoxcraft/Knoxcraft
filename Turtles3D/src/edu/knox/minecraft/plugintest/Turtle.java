package edu.knox.minecraft.plugintest;

import net.canarymod.api.world.position.Direction;
import net.canarymod.api.world.position.Position;

public class Turtle {

    //Make a Turtle

    //Constructor

    //Move (adjust relPos)
    public Position move (Position p, Direction d, boolean up, boolean down){ 
        //Boolean is for vertical changes-> ignore position and direction if veritcal
        //unless canry handles vertical?? -> need to find out-> would simplify things
        int dn = d.getIntValue();
        if (up || down ){
            if (up){
                //add y +1
                p.setY(p.getBlockY() + 1);

            }else if(down){
                //subtract y -1
                p.setY(p.getBlockY() - 1);

            }else{
                //BAD THINGS
            }
        }else{
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
            }



        }


        return null;
    }


}
