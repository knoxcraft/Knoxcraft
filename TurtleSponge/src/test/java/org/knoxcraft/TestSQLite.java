package org.knoxcraft;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Database;
import org.knoxcraft.database.tables.KCTScriptAccess;

public class TestSQLite
{

    /**
     * Connect to a sample database
     *
     * @param fileName the database file name
     */
    public static void createNewDatabase(String fileName) {
         String url = "jdbc:sqlite:build/" + fileName;
 
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
 
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
   
    @Test
    public void testCreateBasic() {
        createNewDatabase("dbtest.db");
    }
    
    @Test
    public void testKCTDataAccess() throws Exception {
        KCTScriptAccess data=new KCTScriptAccess();
        List<DataAccess> results=new LinkedList<DataAccess>();
        Map<String,KCTScriptAccess> mostRecentScripts=new HashMap<String,KCTScriptAccess>();

        Map<String,Object> filters=new HashMap<String,Object>();
        Database.get().loadAll(data, results, filters);
        for (DataAccess d : results) {
            KCTScriptAccess scriptAccess=(KCTScriptAccess)d;
            // Figure out the most recent script for each player-scriptname combo
            String key=scriptAccess.playerName+"-"+scriptAccess.scriptName;
            if (!mostRecentScripts.containsKey(key)) {
                mostRecentScripts.put(key, scriptAccess);
            } else {
                if (scriptAccess.timestamp > mostRecentScripts.get(key).timestamp) {
                    mostRecentScripts.put(key,scriptAccess);
                }
            }
            System.out.println(String.format("from DB: player %s has script %s at time %d%n",
                    scriptAccess.playerName, scriptAccess.scriptName,
                    scriptAccess.timestamp));
        }
    }
    
    @Test
    public void testInsert() throws Exception {
        KCTScriptAccess data=new KCTScriptAccess();
        data.json=FileUtils.readFileToString(new File("src/test/resources/testscript.json"));
        data.source=FileUtils.readFileToString(new File("src/test/resources/TestScript.java"));
        data.playerName="spacdog";
        data.scriptName="testscript";
        data.language="java";
        Database.get().insert(data);
    }
}
