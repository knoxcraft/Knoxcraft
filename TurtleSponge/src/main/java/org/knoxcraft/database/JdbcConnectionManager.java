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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.knoxcraft.database.exceptions.DatabaseAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Represents a connection (pool) manager for all sorts of JDBC connections.
 * In our particular case that is mysql and sqlite.
 * For sqlite, due to the minimal nature of it,
 * there need to be a separate handling.
 * TODO: Configure statement caching!
 *
 * @author Chris Ksoll (damagefilter)
 * @author Jason Jones (darkdiplomat)
 */
public class JdbcConnectionManager {
    
    private static Logger log=LoggerFactory.getLogger(JdbcConnectionManager.class);
    
    private ComboPooledDataSource cpds; // The data source pool ;)
    private Connection nonManaged; // For those that bypass the manager/unable to use the manager
    private SQLType type;

    private static JdbcConnectionManager instance;

    /**
     * Instantiates the connection manager
     *
     * @param type
     *         the database type
     *
     * @throws SQLException
     */
    private JdbcConnectionManager(SQLType type) throws SQLException {
        DatabaseConfiguration cfg = DatabaseConfiguration.getDbConfig();
        cpds = new ComboPooledDataSource();
        this.type = type;
        if (type.usesJDBCManager()) {
            try {
                cpds.setDriverClass(type.getClassPath());
                cpds.setJdbcUrl(cfg.getDatabaseUrl(type.getIdentifier()));
                cpds.setUser(cfg.getDatabaseUser());
                cpds.setPassword(cfg.getDatabasePassword());

                // For settings explanations see
                // http://javatech.org/2007/11/c3p0-connectionpool-configuration-rules-of-thumb/
                // https://community.jboss.org/wiki/HowToConfigureTheC3P0ConnectionPool?_sscc=t

                //connection pooling
                cpds.setAcquireIncrement(cfg.getAcquireIncrement());
                cpds.setMaxIdleTime(cfg.getMaxConnectionIdleTime());
                cpds.setMaxIdleTimeExcessConnections(cfg.getMaxExcessConnectionsIdleTime());
                cpds.setMaxPoolSize(cfg.getMaxPoolSize());
                cpds.setMinPoolSize(cfg.getMinPoolSize());
                cpds.setNumHelperThreads(cfg.getNumHelperThreads());
                cpds.setUnreturnedConnectionTimeout(cfg.getReturnConnectionTimeout());
                cpds.setIdleConnectionTestPeriod(cfg.getConnectionTestFrequency());

                //Statement pooling
                cpds.setMaxStatements(cfg.getMaxCachedStatements());
                cpds.setMaxStatementsPerConnection(cfg.getMaxCachedStatementsPerConnection());
                cpds.setStatementCacheNumDeferredCloseThreads(cfg.getNumStatementCloseThreads());
            }
            catch (PropertyVetoException e) {
                log.error("Failed to configure the connection pool!", e);
            }
            //Test connection...
            //If this fails it throws an SQLException so we're notified
            Connection c = cpds.getConnection();
            c.close();
        }
        else {
            String url="jdbc:sqlite:db/knoxcraft.db";
            //nonManaged = DriverManager.getConnection(cfg.getDatabaseUrl(type.getIdentifier()), cfg.getDatabaseUser(), cfg.getDatabasePassword());
            nonManaged = DriverManager.getConnection(url);
            nonManaged.close();
        }
    }

    /**
     * Get the SQL Database type.
     *
     * @return the type
     */
    public SQLType getType() {
        return this.type;
    }

    /**
     * Create a new instance of this connection manager.
     *
     * @return an instance of the manager
     *
     * @throws DatabaseAccessException
     */
    private static JdbcConnectionManager getInstance() throws DatabaseAccessException {
        DatabaseConfiguration config=DatabaseConfiguration.getDbConfig();
        String dataSourceType=config.getDataSourceType();
        if (instance == null) {
            try {
                SQLType type = SQLType.forName(dataSourceType);
                if (type == null) {
                    throw new DatabaseAccessException(dataSourceType + " is not a valid JDBC Database type or has not been registered for use.");
                }
                instance = new JdbcConnectionManager(type);
            }
            catch (SQLException e) {
                throw new DatabaseAccessException("Unable to instantiate Connection Pool!", e);
            }
        }
        return instance;
    }

    /**
     * Get a connection form the connection pool.
     *
     * @return connection from the pool
     */
    public static Connection getConnection() {
        try {
            JdbcConnectionManager cman = getInstance();
            if (!cman.type.usesJDBCManager()) {
                if (cman.nonManaged != null) {
                    if (!cman.nonManaged.isClosed()) {
                        return cman.nonManaged;
                    }
                }
                // TODO: read from the appropriate file, if it exists
                // Getting SQLite to work has been a huge hack
                DatabaseConfiguration cfg = DatabaseConfiguration.getDbConfig();
                String dataSourceType=cfg.getDataSourceType();
                if (dataSourceType.equals(Database.SQLITE)) {
                    cman.nonManaged=DriverManager.getConnection(cfg.getDatabaseUrl(Database.SQLITE));
                }
                cman.nonManaged = DriverManager.getConnection(cfg.getDatabaseUrl(cman.type.getIdentifier()), cfg.getDatabaseUser(), cfg.getDatabasePassword());
                return cman.nonManaged;
            }
            return cman.cpds.getConnection();
        }
        catch (SQLException e) {
            log.error("Couldn't get a Connection from pool!", e);
            return null;
        }
        catch (DatabaseAccessException e) {
            log.error("Couldn't get a Connection from pool!", e);
            return null;
        }
    }

    /**
     * Shut down the connection pool.
     * Should be called when the system is reloaded or goes down to prevent data loss.
     */
    public static void shutdown() {
        if (instance == null) {
            // already shut down or never instantiated (perhaps because we're running on a non-jdbc database)
            return;
        }
        instance.cpds.close();
        if (instance.nonManaged != null) {
            try {
                instance.nonManaged.close();
            }
            catch (SQLException e) {
                log.warn("Non-Managed connection could not be closed. Whoops!", e);
            }
        }
        instance = null;
    }
}
