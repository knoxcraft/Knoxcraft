package net.canarymod.database;

/**
 * Location assistant DataAccess object
 *
 * @author Jason Jones (darkdiplomat)
 */
public abstract class LocationDataAccess extends PositionDataAccess {

    public LocationDataAccess(String tableName) {
        super(tableName);
    }

    public LocationDataAccess(String tableName, String tableSuffix) {
        super(tableName, tableSuffix);
    }

    // X Y Z from PositionDataAccess
    @Column(columnName = "rotation", notNull = true, dataType = Column.DataType.FLOAT)
    public float rotation = 0;

    @Column(columnName = "pitch", notNull = true, dataType = Column.DataType.FLOAT)
    public float pitch = 0;

    @Column(columnName = "world", notNull = true, dataType = Column.DataType.STRING)
    public String world = "default";

    @Column(columnName = "dimension", notNull = true, dataType = Column.DataType.STRING)
    public String dimension = "NORMAL";
}
