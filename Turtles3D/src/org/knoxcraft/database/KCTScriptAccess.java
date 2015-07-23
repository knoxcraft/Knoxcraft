package org.knoxcraft.database;

import net.canarymod.database.Column;
import net.canarymod.database.Column.ColumnType;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;

public class KCTScriptAccess extends DataAccess
{
    // XXX shoudl we read KCTSCRIPT_TABLE_NAME out of a configuration file?
    public static final String KCTSCRIPT_TABLE_NAME="kctscript";
    
    // TODO Can we also load schema from XML file?
    
    public KCTScriptAccess() {
        super(KCTSCRIPT_TABLE_NAME);
        timestamp=System.currentTimeMillis();
    }

    @Column(columnName="id",
            dataType=DataType.INTEGER,
            columnType=ColumnType.PRIMARY,
            autoIncrement=true, 
            notNull=true)
    public int id;

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
    
    @Override
    public DataAccess getInstance() {
        return new KCTScriptAccess();
    }
}
