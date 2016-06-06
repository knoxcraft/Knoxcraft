package net.canarymod.database;

/**
 * Position assistant DataAccess object
 *
 * @author Jason Jones (darkdiplomat)
 */
public abstract class PositionDataAccess extends DataAccess {

    public PositionDataAccess(String tableName) {
        super(tableName);
    }

    public PositionDataAccess(String tableName, String tableSuffix) {
        super(tableName, tableSuffix);
    }

    // Absolute out of world defaults used
    @Column(columnName = "posX", notNull = true, dataType = Column.DataType.DOUBLE)
    public double posX = 0;

    @Column(columnName = "posY", notNull = true, dataType = Column.DataType.DOUBLE)
    public double posY = 0;

    @Column(columnName = "posZ", notNull = true, dataType = Column.DataType.DOUBLE)
    public double posZ = 0;
}
