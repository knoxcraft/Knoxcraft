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

import org.knoxcraft.serverturtle.TurtlePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

/**
 * Database Configuration settings
 *
 * @author Jos Kuijpers
 * @author Jason (darkdiplomat)
 * @author Jaime Spacco
 */
public class DatabaseConfiguration
{
    public static final String DB_TYPE = "knoxcraft.db.type";
    public static final String DB_FILE = "knoxcraft.db.file";
    public static final String DB_FOLDER = "knoxcraft.db.folder";
    public static final String DB_NAME = "knoxcraft.db.name";
    public static final String DB_HOST = "knoxcraft.db.host";
    public static final String DB_PORT= "knoxcraft.db.port";
    public static final String DB_PASSWORD = "knoxcraft.db.password";
    public static final String DB_USERNAME = "knoxcraft.db.username";
    
    // table names (no DB_ in the title)
    public static final String WHITELIST_TABLE_NAME = "knoxcraft.db.whitelist-table-name";
    public static final String WARPS_TABLE_NAME = "knoxcraft.db.warps-table-name";
    public static final String RESERVELIST_TABLE_NAME = "knoxcraft.db.reservelist-table-name";
    public static final String PLAYERS_TABLE_NAME = "knoxcraft.db.players-table-name";
    public static final String PERMISSIONS_TABLE_NAME = "knoxcraft.db.permissions-table-name";
    public static final String OPERATORS_TABLE_NAME = "knoxcraft.db.operators-table-name";
    public static final String KITS_TABLE_NAME = "knoxcraft.db.kits-table-name";
    public static final String GROUPS_TABLE_NAME = "knoxcraft.db.groups-table-name";
    public static final String BANS_TABLE_NAME = "knoxcraft.db.bans-table-name";
    // connection pool configuration information (also not prefixed with DB_)
    public static final String STATEMENT_CACHE_CLOSE_THREADS = "knoxcraft.db.statement-cache-close-threads";
    public static final String MAX_CACHED_STATEMENTS_PER_CONNECTION = "knoxcraft.db.max-cached-statements-per-connection";
    public static final String MAX_CACHED_STATEMENTS = "knoxcraft.db.max-cached-statements";
    public static final String CONNECTION_TEST_FREQUENCY = "knoxcraft.db.connection-test-frequency";
    public static final String RETURN_CONNECTION_TIMEOUT = "knoxcraft.db.return-connection-timeout";
    public static final String NUM_HELPER_THREADS = "knoxcraft.db.num-helper-threads";
    public static final String MIN_CONNECTION_POOL_SIZE = "knoxcraft.db.min-connection-pool-size";
    public static final String MAX_CONNECTION_POOL_SIZE = "knoxcraft.db.max-connection-pool-size";
    public static final String MAX_EXCESS_CONNECTIONS_IDLE_TIME = "knoxcraft.db.max-excess-connections-idle-time";
    public static final String MAX_CONNECTION_IDLE_TIME = "knoxcraft.db.max-connection-idle-time";
    public static final String ACQUIRE_INCREMENT = "knoxcraft.db.acquire-increment";
    public static final String MAX_CONNECTIONS = "knoxcraft.db.maxConnections";
    
    private CommentedConfigurationNode config;
    private Logger log=LoggerFactory.getLogger(TurtlePlugin.ID);
    
    private void addConfigSetting(String path, Object value) {
        addConfigSetting(path, value, null);
    }
    
    private void addConfigSetting(String path, Object value, String comment) {
        // add the given value to the given path, but only if the given path
        // does not already have a value
        CommentedConfigurationNode node=config.getNode(convert(path));
        if (node.isVirtual()) {
            node=node.setValue(value);
            if (comment!=null){
                node.setComment(comment);
            }
        }
    }
    
    /**
     * Ensure that we plug in default settings, if they don't exist.
     * @param config
     */
    private void configureDefaultDatabase(CommentedConfigurationNode config) {
        CommentedConfigurationNode topLevel=config.getNode(convert("knoxcraft.db"));
        if (topLevel.isVirtual() || !topLevel.getComment().isPresent()) {
            topLevel.setComment("NOTE: Currently the configuration file framework doesn't keep these in order\n"+
                "Configuration settings for the database\n"+
                "For more settings explanations see following websites:\n"+
                "http://javatech.org/2007/11/c3p0-connectionpool-configuration-rules-of-thumb\n"+
                "https://community.jboss.org/wiki/HowToConfigureTheC3P0ConnectionPool");
        }
        addConfigSetting(DB_TYPE,"xml", "the type of the DB (xml or mysql)\n"+
                "SQLite and H2 support are coming soon\n"+
                "xml format will store XML files in forge/db");
        addConfigSetting(DB_HOST,"localhost");
        addConfigSetting(DB_NAME,"knoxcraft","Name of the database");
        addConfigSetting(DB_FOLDER,"db","Folder where XML files stored. Path relative to Forge home. Ignored when db.type is mysql.");
        addConfigSetting(DB_FILE,"knoxcraft.db","File where DB is store. Only relevant for SQLite and H2. Path relative to Forge home.");
        addConfigSetting(DB_USERNAME,"root");
        addConfigSetting(DB_PASSWORD,"root");
        addConfigSetting(DB_PORT,8889);
        // everything below here was basically copied from Canarymod's DB layer
        // it's using C3P0 connectino pooling, which I don't really understand
        addConfigSetting(MAX_CONNECTIONS,5);
        addConfigSetting(ACQUIRE_INCREMENT,5,"Determines how many connections at a time c3p0 will try to acquire when pool is exhausted");
        addConfigSetting(MAX_CONNECTION_IDLE_TIME,900,"Determines how long idle connections can stay in the connection pool before removed");
        addConfigSetting(MAX_EXCESS_CONNECTIONS_IDLE_TIME,1800,"Time until the connection pool will be culled down to min-connection-pool-size. Set 0 to not enforce pool shrinking");
        addConfigSetting(MAX_CONNECTION_POOL_SIZE,10,"The maximum allowed number of pooled connections. More for larger servers");
        addConfigSetting(MIN_CONNECTION_POOL_SIZE,3,"The minimum amount of connections allowed. More means more memory usage but takes away some impact from creating new connections");
        addConfigSetting(NUM_HELPER_THREADS,4,"Amount of threads that will perform slow JDBC operations (closing idle connections, returning connections to pool etc");
        addConfigSetting(RETURN_CONNECTION_TIMEOUT,900,"Defines a time a connection can remain checked out. After that it will be forced back into the connection pool");
        addConfigSetting(CONNECTION_TEST_FREQUENCY,0,"No idea what this does");
        addConfigSetting(MAX_CACHED_STATEMENTS,50,"Number of max cached statements on all connections. (Roughly 5 * expected pooled connections)");
        addConfigSetting(MAX_CACHED_STATEMENTS_PER_CONNECTION,5,"Number of max cached statements on a single connection");
        addConfigSetting(STATEMENT_CACHE_CLOSE_THREADS,1,"Number of threads to use when closing statements is deferred (happens when parent connection is still in use)");
        addConfigSetting(BANS_TABLE_NAME,"ban","The name to use for the Bans table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(GROUPS_TABLE_NAME,"group","The name to use for the Groups table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present");
        addConfigSetting(KITS_TABLE_NAME,"kits","The name to use for the Kits table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(OPERATORS_TABLE_NAME,"operators","The name to use for the Operators table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(PERMISSIONS_TABLE_NAME,"permissions","The name to use for the Permissions table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(PLAYERS_TABLE_NAME,"players","The name to use for the Players table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(RESERVELIST_TABLE_NAME,"reservelist","The name to use for the ReserveList table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(WARPS_TABLE_NAME,"warps","The name to use for the Warps table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
        addConfigSetting(WHITELIST_TABLE_NAME,"whitelist","The name to use for the WhiteList table. NOTE: Changing this here will require you to manually change the name of the table in the database (if present)");
    }
    
    public DatabaseConfiguration(CommentedConfigurationNode configNode) {
        // TODO: merge the parameter with the default values so that we have correct
        // default values for everything
        this.config=configNode;
        configureDefaultDatabase(configNode);
    }
    
    public static Object[] convert(String path) {
        return path.split("\\.");
    }

    /**
     * Get the type of datasource being used (MySQL, SQLite, XML, or 
     * (once we add support for it) H2.
     * @return
     */
    public String getDataSourceType() {
        return getString(DB_TYPE);
    }

    /**
     * Get the URL to the database.
     * This is a combination of host, port and database
     *
     * @param driver
     *         the JDBC driver name (ie: mysql or sqlite)
     *
     * @return database url
     */
    public String getDatabaseUrl(String driver) {
        if (getDataSourceType().equals(Database.SQLITE)){
            return "jdbc:sqlite:"+getDatabaseFile();
        }
        int port = getDatabasePort();
        return "jdbc:" + driver + "://" + getDatabaseHost() + ((port == 0) ? "" : (":" + port)) + "/" + getDatabaseName();
    }
    
    // private helper/convenience methods
    private int getInt(String key) {
        return config.getNode(convert(key)).getInt();
    }
    private String getString(String key) {
        return config.getNode(convert(key)).getString();
    }
    
    public String getDatabaseFile() {
        return getString(DB_FILE);
    }

    /**
     * Get the database host, defaulting to localhost
     *
     * @return database host
     */
    public String getDatabaseHost() {
        return getString(DB_HOST);
    }
    
   
    
    /**
     * Get the database port
     *
     * @return The configured port or 0
     */
    public int getDatabasePort() {
        return getInt(DB_PORT);
    }

    /**
     * Get the name of the database. Defaults to 'canarymod'
     *
     * @return database name
     */
    public String getDatabaseName() {
        return getString(DB_NAME);
    }

    /**
     * Get database user
     * This might be null if the datasource is not a password protected database type such as XML.
     *
     * @return database username
     */
    public String getDatabaseUser() {
        return getString(DB_USERNAME);
    }

    /**
     * Get database password.
     * This might be null if the datasource is not a password protected database type such as XML.
     *
     * @return database password
     */
    public String getDatabasePassword() {
        return getString(DB_PASSWORD);
    }

    /**
     * FIXME: What would it mean for this to return -1?
     * 
     * Get the maximum number of concurrent connections to the database.
     * This might be null if the datasource is not a connection oriented database type such as XML.
     *
     * @return database maximum connections
     */
    public int getDatabaseMaxConnections() {
        return getInt(MAX_CONNECTIONS);
    }

    /**
     * Defines the total number PreparedStatements a DataSource will cache.
     * The pool will destroy the least-recently-used PreparedStatement when it hits this limit.
     *
     * @return config for max cached statements
     */
    public int getMaxCachedStatements() {
        return getInt(MAX_CACHED_STATEMENTS);
    }

    /**
     * Defines how many statements each pooled Connection is allowed to own.
     * You can set this to a bit more than the number of PreparedStatements
     * your application frequently uses, to avoid churning.
     *
     * @return config for max num of pooled statements per connection
     */
    public int getMaxCachedStatementsPerConnection() {
        return getInt(MAX_CACHED_STATEMENTS_PER_CONNECTION);
    }

    /**
     * If greater than zero, the Statement pool will defer physically close()ing cached Statements
     * until its parent Connection is not in use by any client or internally (in e.g. a test) by the pool itself.
     *
     * @return config num of threads used to defer closing statements
     */
    public int getNumStatementCloseThreads() {
        return getInt(STATEMENT_CACHE_CLOSE_THREADS);
    }

    /**
     * Defines the interval of checking validity of pooled connections in seconds.
     *
     * @return connection re-check interval
     */
    public int getConnectionTestFrequency() {
        return getInt(CONNECTION_TEST_FREQUENCY);
    }

    /**
     * Defines the time in seconds a connection can stay checked out, before it is returned to the connection pool.
     *
     * @return num of seconds a connection can stay checked out
     */
    public int getReturnConnectionTimeout() {
        return getInt(RETURN_CONNECTION_TIMEOUT);
    }

    /**
     * Defines the amount of threads to use when executing slow JDBC operations,
     * such as closing connections and statements.
     *
     * @return num of threads to use for heavy JDBC operations
     */
    public int getNumHelperThreads() {
        return getInt(NUM_HELPER_THREADS);
    }

    /**
     * Defines the minimum amount of connections to keep alive in the connection pool.
     *
     * @return min amount of connections
     */
    public int getMinPoolSize() {
        return getInt(MIN_CONNECTION_POOL_SIZE);
    }

    /**
     * Defines the maximum allowed number of connections in the connection pool.
     *
     * @return max allowed connections in pool
     */
    public int getMaxPoolSize() {
        return getInt(MAX_CONNECTION_POOL_SIZE);
    }

    /**
     * Number of seconds that Connections in excess of minPoolSize
     * should be permitted to remain idle in the pool before being culled.
     * Set 0 to turn off culling
     *
     * @return seconds to keep excess connections
     */
    public int getMaxExcessConnectionsIdleTime() {
        return getInt(MAX_EXCESS_CONNECTIONS_IDLE_TIME);
    }

    /**
     * Determines how many connections at a time to acquire when the pool is exhausted.
     *
     * @return connections to acquire
     */
    public int getAcquireIncrement() {
        return getInt(ACQUIRE_INCREMENT);
    }

    /**
     * Time to keep idle connections in the pool before they are closed and discarded.
     *
     * @return keep-alive time of connections in pool
     */
    public int getMaxConnectionIdleTime() {
        return getInt(MAX_CONNECTION_IDLE_TIME);
    }

    public String getBansTableName() {
        return getString(BANS_TABLE_NAME);
    }

    public String getGroupsTableName() {
        return getString(GROUPS_TABLE_NAME);
    }

    public String getKitsTableName() {
        return getString(KITS_TABLE_NAME);
    }

    public String getOpertatorsTableName() {
        return getString(OPERATORS_TABLE_NAME);
    }

    public String getPermissionsTableName() {
        return getString(PERMISSIONS_TABLE_NAME);
    }

    public String getPlayersTableName() {
        return getString(PLAYERS_TABLE_NAME);
    }

    public String getReservelistTableName() {
        return getString(RESERVELIST_TABLE_NAME);
    }

    public String getWarpsTableName() {
        return getString(WARPS_TABLE_NAME);
    }

    public String getWhitelistTableName() {
        return getString(WHITELIST_TABLE_NAME);
    }
}
