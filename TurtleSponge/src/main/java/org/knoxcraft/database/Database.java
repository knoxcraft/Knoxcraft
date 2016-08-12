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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knoxcraft.database.exceptions.DatabaseException;
import org.knoxcraft.database.exceptions.DatabaseReadException;
import org.knoxcraft.database.exceptions.DatabaseWriteException;
import org.knoxcraft.database.h2.H2Database;
import org.knoxcraft.database.mysql.MySQLDatabase;
import org.knoxcraft.database.sqlite.SQLiteDatabase;
import org.knoxcraft.database.xml.XmlDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;


/**
 * A database representation, used to store any kind of data
 *
 * @author Chris (damagefilter)
 */
public abstract class Database {
    // FIXME: figure out the way to read configuration information in Sponge
    
    public static final String MYSQL="mysql";
    public static final String XML="xml";
    public static final String SQLITE="sqlite";
    public static final String H2="h2";
    
    private static Logger log=LoggerFactory.getLogger(Database.class);
    
    private static Database instance;
    private static DatabaseConfiguration dbConfig;
    
    /**
     * Configure the database. The parameter should contain configuration
     * settings; if not this method will add a set of defaults.
     * 
     * This method should only be called once to configure database support.
     * Subsequent calls are ignored.
     * 
     * @param config
     */
    public static void configure(CommentedConfigurationNode config) {
        if (instance!=null) {
            // only configure the DB once
            return;
        }
        dbConfig=new DatabaseConfiguration(config);
        String dbType=dbConfig.getDataSourceType();
        if (dbType.equals(MYSQL)){
            instance=MySQLDatabase.getInstance();
        } else if (dbType.equals(XML)){
            instance=XmlDatabase.getInstance();
        } else if (dbType.equals(SQLITE) || dbType.equals(H2)){
            log.warn("Support for SQLite and H2 doesn't work. Defaulting to XML database");
            instance=XmlDatabase.getInstance();
        }
    }
    
    /**
     * Return the DatabaseConfiguration, which is a wrapper over the CommentedConfigurationNode
     * that keeps the code compatible with the DB layer from Canarymod that we are using.
     * 
     * {@link #configure(CommentedConfigurationNode)} must be called before this method.
     * 
     * In the future, the {@link DatabaseConfiguration} may be phased out in favor
     * of a single configuration object, or direct use of the {@link CommentedConfigurationNode}.
     * 
     * In the very distant future, this entire layer should be replaced with Hibernate.
     * 
     * @return
     */
    public static DatabaseConfiguration getDbConfig() {
        if (dbConfig==null){
            throw new IllegalStateException("Database has not been configured! Call Database.configure() with a ConfigurationNode before calling this method");
        }
        return dbConfig;
    }
    
    /**
     * Get a reference to the database layer. This method returns a subclass.
     * 
     * {@link #configure(CommentedConfigurationNode)} should be called before this method.
     * 
     * 
     * @return
     */
    public static Database get() {
        if (instance==null) {
            throw new IllegalStateException("Cannot get() the database before calling configure().");
        }
        return instance;
    }

    /**
     * Insert the given DataAccess object as new set of data into database
     *
     * @param data
     *         the data to insert
     *
     * @throws DatabaseWriteException
     *         when something went wrong during the write operation
     */
    public abstract void insert(DataAccess data) throws DatabaseWriteException;

    /**
     * Insert a range of DataAccess objects at once.
     *
     * @param data
     *         the list of data to insert
     *
     * @throws DatabaseWriteException
     *         when something went wrong during the write operation
     */
    public abstract void insertAll(List<DataAccess> data) throws DatabaseWriteException;

    /**
     * Updates the record in the database that fits to your fields and values given.
     * Those are NOT the values and fields to update. Those are values and fields to identify
     * the correct entry in the database to update. The updated data must be provided in the DataAccess
     *
     * @param data
     *         the data to be updated. Additionally this acts as information about the table schema
     * @param filters
     *         FieldName->Value map to filter which rows should be updated
     *
     * @throws DatabaseWriteException
     */
    public abstract void update(DataAccess data, Map<String, Object> filters) throws DatabaseWriteException;

    /**
     * Updates the records in the database that fits to your fields and values given.
     * Those are NOT the values and fields to update. Those are values and fields to identify
     * the correct entry in the database to update. The updated data must be provided in the DataAccess
     *
     * @param template
     *         the template data access used for verification.
     * @param data
     *         a map of data access objects to insert and the filters to apply them with
     *
     * @throws DatabaseWriteException
     */
    public abstract void updateAll(DataAccess template, Map<DataAccess, Map<String, Object>> data) throws DatabaseWriteException;

    /**
     * Removes the data set from the given table that suits the given field names and values.
     *
     * @param da
     *         the DataAccess object that specifies the data that should be removed
     * @param filters
     *         FieldName->Value map to filter which rows should be deleted
     *
     * @throws DatabaseWriteException
     */
    public abstract void remove(DataAccess da, Map<String, Object> filters) throws DatabaseWriteException;

    /**
     * Removes the data set from the given table that suits the given field names and values.
     *
     * @param da
     *         the DataAccess object that specifies the data that should be removed
     * @param filters
     *         FieldName->Value map to filter which rows should be deleted
     *
     * @throws DatabaseWriteException
     */
    public abstract void removeAll(DataAccess da, Map<String, Object> filters) throws DatabaseWriteException;

    /**
     * This method will fill your DataAccess object with the first data set from database,
     * that matches the given values in the given fields.<br>
     * For instance if you pass String[] {"score", "name"}<br>
     * with respective values Object[] {130, "damagefilter"},<br>
     * Canary will look in the database for records where score=130 and name=damagefilter.<br>
     * Canary will only look in the table with the name you have set in your AccessObject<br>
     *
     * @param dataset
     *         The class of your DataAccess object
     * @param filters
     *         FieldName->Value map to filter which row should be loaded
     *
     * @throws DatabaseReadException
     */
    public abstract void load(DataAccess dataset, Map<String, Object> filters) throws DatabaseReadException;

    /**
     * Loads all results that match the field - values given into a DataAccess list.
     *
     * @param typeTemplate
     *         The type template (an instance of the dataaccess type you want to load)
     * @param datasets
     *         DataAccess set - you can savely cast those to the type of typeTemplate
     * @param filters
     *         FieldName->Value map to filter which rows should be loaded
     *
     * @throws DatabaseReadException
     */
    public abstract void loadAll(DataAccess typeTemplate, List<DataAccess> datasets, Map<String, Object> filters) throws DatabaseReadException;

    /**
     * Updates the database table fields for the given DataAccess object.
     * This method will remove fields that aren't there anymore and add new ones if applicable.
     *
     * @param schemaTemplate
     *         the new schema
     */
    public abstract void updateSchema(DataAccess schemaTemplate) throws DatabaseWriteException;
}
