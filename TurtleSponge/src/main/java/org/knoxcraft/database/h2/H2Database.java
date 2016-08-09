package org.knoxcraft.database.h2;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Database;
import org.knoxcraft.database.exceptions.DatabaseReadException;
import org.knoxcraft.database.exceptions.DatabaseWriteException;

public class H2Database extends Database
{

    private H2Database() {
        File path = new File("db/");

        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new ExceptionInInitializerError("Unable to create database directories... Please check your read/write permissions and try again");
            }
        }
    }

    private static H2Database instance;

    public static H2Database getInstance() {
        if (instance == null) {
            instance = new H2Database();
        }
        return instance;
    }

    @Override
    public void insert(DataAccess data) throws DatabaseWriteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertAll(List<DataAccess> data) throws DatabaseWriteException {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(DataAccess data,Map<String, Object> filters)
    throws DatabaseWriteException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAll(DataAccess template,Map<DataAccess, Map<String, Object>> data)
    throws DatabaseWriteException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(DataAccess da,Map<String, Object> filters)
    throws DatabaseWriteException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeAll(DataAccess da,Map<String, Object> filters)
    throws DatabaseWriteException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void load(DataAccess dataset,Map<String, Object> filters)
    throws DatabaseReadException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadAll(DataAccess typeTemplate, List<DataAccess> datasets,
        Map<String, Object> filters)
    throws DatabaseReadException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateSchema(DataAccess schemaTemplate)
    throws DatabaseWriteException
    {
        // TODO Auto-generated method stub

    }

}
