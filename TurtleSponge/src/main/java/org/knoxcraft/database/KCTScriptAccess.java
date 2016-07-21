package org.knoxcraft.database;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.knoxcraft.serverturtle.TurtlePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.canarymod.database.Column;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;
import net.canarymod.database.Database;
import net.canarymod.database.exceptions.DatabaseReadException;

public class KCTScriptAccess extends DataAccess
{
    private Logger log=LoggerFactory.getLogger(TurtlePlugin.ID);
    
    // XXX should we read KCTSCRIPT_TABLE_NAME out of a configuration file?
    public static final String KCTSCRIPT_TABLE_NAME="kctscript";
    
    public KCTScriptAccess() {
        super(KCTSCRIPT_TABLE_NAME);
        timestamp=System.currentTimeMillis();
    }

    @Column(columnName="playerName",
            dataType=DataType.STRING,
            notNull=true)
    public String playerName;
    
    @Column(columnName="language",
            dataType=DataType.STRING)
    public String language;
    
    @Column(columnName="json",
            dataType=DataType.STRING,
            notNull=true)
    public String json;

    @Column(columnName="source",
            dataType=DataType.STRING)
    public String source;

    @Column(columnName="timestamp",
            dataType=DataType.LONG)
    public Long timestamp;
    
    @Column(columnName="scriptName",
            dataType=DataType.STRING)
    public String scriptName;
    
    @Override
    public DataAccess getInstance() {
        return new KCTScriptAccess();
    }
    
    public static Map<String,KCTScriptAccess> getMostRecentScripts() throws DatabaseReadException {
        Map<String,KCTScriptAccess> mostRecentScripts=new HashMap<String,KCTScriptAccess>();
        KCTScriptAccess data=new KCTScriptAccess();
        List<DataAccess> results=new LinkedList<>();
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

        }
        return mostRecentScripts;

    }
}
