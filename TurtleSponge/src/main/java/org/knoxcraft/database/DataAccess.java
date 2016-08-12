// Copyright (c) 2012 - 2015, CanaryMod Team
// Under the management of PlayBlack and Visual Illusions Entertainment
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of the CanaryMod Team nor the
//       names of its contributors may be used to endorse or promote products
//       derived from this software without specific prior written permission.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL CANARYMOD TEAM OR ITS CONTRIBUTORS BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// 
// Any source code from the Minecraft Server is not owned by CanaryMod Team, PlayBlack,
// Visual Illusions Entertainment, or its contributors and is not covered by above license.
// Usage of source code from the Minecraft Server is subject to the Minecraft End User License Agreement as set forth by Mojang AB.
// The Minecraft EULA can be viewed at https://account.mojang.com/documents/minecraft_eula
// CanaryMod Team, PlayBlack, Visual Illusions Entertainment, CanaryLib, CanaryMod, and its contributors
// are NOT affiliated with, endorsed, or sponsored by Mojang AB, makers of Minecraft.
// "Minecraft" is a trademark of Notch Development AB
// "CanaryMod" name is used with permission from FallenMoonNetwork.

package org.knoxcraft.database;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knoxcraft.database.exceptions.DatabaseAccessException;
import org.knoxcraft.database.exceptions.DatabaseTableInconsistencyException;
import org.knoxcraft.database.exceptions.DatabaseWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle the layout and creation of tables
 *
 * @author Chris (damagefilter)
 */
public abstract class DataAccess {

    private String tableName;
    private boolean isInconsistent = false;
    private boolean isLoaded = false;
    private boolean hasData = false;
    private Logger log=LoggerFactory.getLogger(DataAccess.class);
    private static final Set<Class<? extends DataAccess>> schemaUpdated = new HashSet<Class<? extends DataAccess>>();

    @Column(columnName = "id", dataType = Column.DataType.INTEGER, autoIncrement = true, columnType = Column.ColumnType.PRIMARY)
    public Integer id;

    /**
     * Construct a new DataAccess object that represents a table
     * in the database with the given name. This AccessObject is empty after it has been created.
     * You need to get data from Database and load it. CanaryMod will do this for you.
     * For this simply call Canary.db().load(yourDataAccess, String[]lookupFields, Object[]whereData);
     * This will fill your AccessObject.
     *
     * @param tableName
     *         The table name
     */
    public DataAccess(String tableName) {
        this.tableName = tableName;
        createTable();
    }

    public DataAccess(String tableName, String tableSuffix) {
        if (tableSuffix != null) {
            this.tableName = tableName + "_" + tableSuffix;
        }
        else {
            this.tableName = tableName;
        }
        createTable();
    }

    /**
     * Load a Data set into this DataAccess object
     *
     * @param dataSet
     *         the data set to be loaded
     *
     * @throws DatabaseAccessException
     */
    public final void load(HashMap<String, Object> dataSet) throws DatabaseAccessException {
        try {
            applyDataSet(dataSet);
        }
        catch (IllegalAccessException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new DatabaseAccessException(e.getMessage());
        }
        if (dataSet.size() > 0) {
            hasData = true;
        }
        isLoaded = true;
    }
    
    /**
     * Merge 2 arrays. This will remove duplicates.
     *
     * @param first
     *         the first array to be merged
     * @param second
     *         the second array to be merged
     * @param template
     *         the array to use as a template for merging the arrays into one
     *
     * @return array containing all elements of the given 2 arrays, minus duplicate entries
     */
    public static <T> T[] safeArrayMerge(T[] first, T[] second, T[] template) {
        HashSet<T> res = new HashSet<T>();

        Collections.addAll(res, first);
        Collections.addAll(res, second);
        return res.toArray(template);
    }

    /**
     * Creates a HashMap containing all relevant fields for the database, which will then
     * be saved into the database along with their values
     *
     * @return HashMap that maps the Column meta data to the data present in database.
     *
     * @throws DatabaseTableInconsistencyException
     */
    public final HashMap<Column, Object> toDatabaseEntryList() throws DatabaseTableInconsistencyException {
        List<Field> fields = Arrays.asList(safeArrayMerge(getClass().getFields(), getClass().getDeclaredFields(), new Field[1]));
        HashMap<Column, Object> fieldMap = new HashMap<Column, Object>();
        for (Field field : fields) {
            Column colInfo = field.getAnnotation(Column.class);

            if (colInfo == null) {
                // Not what we're looking for
                continue;
            }
            if (fieldMap.containsKey(colInfo)) {
                // Seriously ...
                isInconsistent = true;
                throw new DatabaseTableInconsistencyException("Found duplicate column field: " + colInfo.columnName());
            }
            try {
                fieldMap.put(colInfo, field.get(this));
            }
            catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
            }
            catch (IllegalAccessException e) {
                isInconsistent = true;
                throw new DatabaseTableInconsistencyException("Could not access an annotated column field: " + field.getName());
            }
        }
        return fieldMap;
    }

    public final void applyDataSet(HashMap<String, Object> dataSet) throws DatabaseAccessException, IllegalArgumentException, IllegalAccessException {
        Field[] fields = safeArrayMerge(getClass().getFields(), getClass().getDeclaredFields(), new Field[1]);
        int columnFields = 0;

        for (Field field : fields) {
            Column col = field.getAnnotation(Column.class);

            if (col == null) {
                continue;
            }
            if (!dataSet.containsKey(col.columnName())) {
                isInconsistent = true;
                throw new DatabaseAccessException("Cannot apply data to " + getClass().getSimpleName() + ". Column name mismatches! (" + col.columnName() + " does not exist) - " + dataSet.keySet().toString());
            }
            field.set(this, dataSet.get(col.columnName())); // TODO: Fix null value to use field default
            columnFields++;
        }
        // If the columnFields is not the size of the dataSet,
        // There is either excess data or data that has not been put in the AccessObject.
        // This causes inconsistency and therefore must throw an exception
        if (columnFields != dataSet.size()) {
            isInconsistent = true;
            throw new DatabaseAccessException("Supplied Data set cannot be applied to this DataAccess(" + getClass().getSimpleName() + "). Column count mismatches!");
        }
    }

    /**
     * Gets the table layout. That is: all column annotations in this class that make up the table
     *
     * @return a HashSet containing all Columns as defined in this {@link DataAccess} object
     *
     * @throws DatabaseTableInconsistencyException
     */
    public final HashSet<Column> getTableLayout() throws DatabaseTableInconsistencyException {
        Field[] fields = safeArrayMerge(getClass().getFields(), getClass().getDeclaredFields(), new Field[1]);
        HashSet<Column> layout = new HashSet<Column>(fields.length);

        for (Field field : fields) {
            if (field == null) {
                throw new DatabaseTableInconsistencyException("A field of " + getClass().getSimpleName() + " is not initialized, check your DataAccess!");
            }
            Column colInfo = field.getAnnotation(Column.class);

            if (colInfo == null) {
                // Not what we're looking for
                continue;
            }
            if (layout.contains(colInfo)) {
                // Dude!
                isInconsistent = true;
                throw new DatabaseTableInconsistencyException("Found duplicate column field: " + colInfo.columnName());
            }
            layout.add(colInfo);
        }
        return layout;
    }

    /**
     * This shall return the name of the Table this DataAccess belongs to
     *
     * @return the table name
     */
    public final String getName() {
        return tableName;
    }

    /**
     * Returns true if this DataAccess object has been marked as inconsistent.
     * Inconsistent DataAccess objects will not be saved into the database.
     * This is probably rarely going to be true but it's an extra security measure
     * to keep the data safe and consistent
     *
     * @return true if this object is inconsistent to the database schema, false otherwise
     */
    public final boolean isInconsistent() {
        return isInconsistent;
    }

    /**
     * Check if this DataAccess has been loaded properly
     *
     * @return true if a load has been attempted for this {@link DataAccess}
     */
    public final boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Check if there is data in this DataAccess object.
     * This will also return false if there was an exception while
     * the data has been loaded
     *
     * @return true if this {@link DataAccess} contains data
     */
    public final boolean hasData() {
        return hasData;
    }

    /**
     * Checks if this {@link DataAccess} has a Column with the given name.
     *
     * @param name
     *         the name to check for
     *
     * @return true if DataAccess has this column, false otherwise
     */
    public final boolean hasColumn(String name) {
        try {
            for (Column col : getTableLayout()) {
                if (col.columnName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
        catch (DatabaseTableInconsistencyException e) {
            log.error("Could not finish column name lookup in database for " + tableName, e);
            return false;
        }
    }

    /**
     * Retrieves a Column with the given name from this DataAccess.
     *
     * @param name
     *         the column name
     *
     * @return a column or null if there is no column with the name given
     */
    public final Column getColumnForName(String name) {
        try {
            for (Column col : getTableLayout()) {
                if (col.columnName().equals(name)) {
                    return col;
                }
            }
            return null;
        }
        catch (DatabaseTableInconsistencyException e) {
            log.error("Could not finish column name lookup in database for " + tableName, e);
            return null;
        }
    }

    /**
     * Makes sure the database file for this DataAccess exists before anything starts to use it
     */
    private void createTable() {
        if (!schemaUpdated.contains(getClass())) {
            try {
                Database.get().updateSchema(this);
            }
            catch (DatabaseWriteException e) {
                log.error(e.getMessage(), e);
            }
            schemaUpdated.add(getClass());
        }
    }

    /**
     * Converts this DataAccess object into a string representation.<br>
     * Format: Table : tableName { [`columnName`,'fieldName'] }
     *
     * @return string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<Column, Object> columns = null;
        try {
            columns = this.toDatabaseEntryList();
        }
        catch (DatabaseTableInconsistencyException dtie) {

        }
        if (columns != null) {
            for (Column column : columns.keySet()) {
                sb.append("[`").append(column.columnName()).append("`, '").append(columns.get(column)).append("'] ");
            }
        }
        return "Table : " + this.tableName + " { " + sb.toString() + "}";
    }

    /**
     * Returns an empty instance of this {@link DataAccess} object
     *
     * @return instance
     */
    public abstract DataAccess getInstance();
}
