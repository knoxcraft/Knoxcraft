package edu.knox.minecraft.plugintest;

import net.canarymod.api.world.World;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.position.Vector3D;

public class Turtle
{
/*
 * Workflow ideas:
 * 
 * TODO cool name for scripts: MineScripts? Minelets? Progines? Mein Skript? Proglets?
 * 
 * From Minecraft: multiple ways to activate a proggie
 * - When you activate the plugin, you end up with a special item in your inventory
 * kind of like an ipad or a magic turtle or something
 * - Get the current (x,y,z) location using a command-line command
 * - May need some way to activate item to select the program to run on the next right-click
 * could potentially set up a different key for activating things. try to avoid the need
 * for a new GUI element like in Ars Magica 2.
 * 
 * 
 * Web interface for managing Mein Skript plugins
 * - simple servlets, full crazy GWT, or JHipster? Could we use JMX? 
 *      We want this to be for localhost, after all
 * - simplest possible way to list programs
 * - some kind of server-side dynamic classloading of dynamic binding
 * - probably upload the source code and then compile it. Will need a way to 
 * figure out the language. Probably should be an upload parameter.
 * - Will probably use HTTP POST with a post parameter for the source language
 * - Need to give HTTP response codes and good error messages
 * - server side, each minelet should keep track of the blocks it places and allow us to undo
 * - may want to parameterize functions from inside Minecraft
 * 
 * 
 * Directly upload a plugin from BlueJ
 * - sort of scripting this to do the HTTP POST operation for us
 * 
 * From BlueJ or from Python:
 * - Name the minelet somehow; required method call
 * - Could probably use annotations for Java and decorators for Python to decide method to launch
 * - Need some kind of API to compile against, using a Turtle
 *  > maybe you'll automatically have access to a Turtle by extending something 
 *  and over-riding the right method. Not sure how to do that in Python... Could also
 *  make it a parameter.
 * 
 * 
 * 
 */
    
    private World world;
    private int x;
    private int y;
    private int z;
    
    public Turtle(World world, int x, int y, int z) {
        this.world=world;
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public void forward(Vector3D forDir) {
    	x += forDir.getBlockX();
    	y += forDir.getBlockY();
    	z += forDir.getBlockZ();
        world.setBlockAt(x, y, z, BlockType.OakPlanks);
    }
}
