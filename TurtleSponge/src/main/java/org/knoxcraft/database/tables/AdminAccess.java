package org.knoxcraft.database.tables;

import org.knoxcraft.database.Column;
import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Column.DataType;

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
        return new AdminAccess();
    }

}
