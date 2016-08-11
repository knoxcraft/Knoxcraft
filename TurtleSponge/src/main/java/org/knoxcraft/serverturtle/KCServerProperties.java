package org.knoxcraft.serverturtle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class to change the server.properties file to tell the Minecraft server how to format the world.
 * @author kakoijohn
 *
 */
public class KCServerProperties {
    
    /**
     * Constructor
     */
    public KCServerProperties () {
        
    }
    
    /**
     * Loads the current server.properties file.
     * @return Returns -1 if the file does not exist.
     *         Returns 0 if the file exists but the world file name is incorrect.
     *         Returns 1 if the file exists and the proper world file name is there.
     */
    public int loadServerProperties() {
        File file = new File("server.properties");
        if (!file.exists())
            return -1;
            //if the file does not exist return -1 to notify
            //plugin to create new file.
        else {
            Path worldPath = Paths.get("KnoxcraftFlatWorld");
            if (Files.notExists(worldPath))
                return 0;
                //return 0 if the properties file needs to be changed.
            else
                return 1;
                //if everything is set properly the file does not need to be changed.
        }
    }
    
    /**
     * Creates a server.properties file with the default configurations of a Knoxcraft flat world. 
     */
    public void createPropertiesFile() {
        ArrayList<String> properties = new ArrayList<String>();
        properties.add("#Minecraft server properties");
        properties.add("#Date");
        properties.add("spawn-protection=16");
        properties.add("max-tick-time=60000");
        properties.add("generator-settings=");
        properties.add("force-gamemode=false");
        properties.add("allow-nether=true");
        properties.add("gamemode=2");
        properties.add("broadcast-console-to-ops=true");
        properties.add("enable-query=false");
        properties.add("player-idle-timeout=0");
        properties.add("difficulty=0");
        properties.add("spawn-monsters=false");
        properties.add("op-permission-level=4");
        properties.add("resource-pack-hash=");
        properties.add("announce-player-achievements=false");
        properties.add("pvp=false");
        properties.add("snooper-enabled=true");
        properties.add("level-type=FLAT");
        properties.add("hardcore=false");
        properties.add("enable-command-block=false");
        properties.add("max-players=20");
        properties.add("network-compression-threshold=256");
        properties.add("max-world-size=29999984");
        properties.add("server-port=25565");
        properties.add("server-ip=");
        properties.add("spawn-npcs=false");
        properties.add("allow-flight=true");
        properties.add("level-name=KnoxcraftFlatWorld");
        properties.add("view-distance=10");
        properties.add("resource-pack=");
        properties.add("spawn-animals=false");
        properties.add("white-list=false");
        properties.add("generate-structures=false");
        properties.add("online-mode=true");
        properties.add("max-build-height=256");
        properties.add("level-seed=");
        properties.add("enable-rcon=false");
        properties.add("motd=A Knoxcraft Server");
        
        Path path = Paths.get("server.properties");
        try {
            Files.write(path, properties, Charset.forName("UTF-8"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
