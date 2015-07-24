package org.knoxcraft.database;

import net.canarymod.database.Column;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;

public class KCTScriptAccess extends DataAccess
{
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
}
