package org.knoxcraft.database;

import net.canarymod.database.Column;
import net.canarymod.database.Column.DataType;
import net.canarymod.database.DataAccess;

public class AdminAccess extends DataAccess
{

    public static final String ADMIN_TABLE_NAME="admin";
    
    public AdminAccess() {
        super(ADMIN_TABLE_NAME);
    }

    @Column(columnName="playerName",
            dataType=DataType.STRING,
            notNull=true)
    public String playerName;
    
    @Column(columnName="password",
            dataType=DataType.STRING,
            notNull=true)
    public String password;

    @Override
    public DataAccess getInstance() {
        // TODO Auto-generated method stub
        return new AdminAccess();
    }

}
