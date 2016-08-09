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

package net.canarymod.database;

import java.util.HashMap;

import com.google.common.collect.Maps;

/**
 * Helper class so we can easily identify the driver type further down the code
 *
 * @author Jason Jones (darkdiplomat)
 * @author Chris Ksoll (damagefilter)
 */
public final class SQLType {
    private static final HashMap<DriverContainer, SQLType> driverRegistry = Maps.newHashMap();
    private final DriverContainer container;

    private SQLType(DriverContainer container) {
        this.container = container;
    }

    private static class DriverContainer {
        public final String classpath;
        public final String identifier;
        public final boolean useJDBCManager;

        private DriverContainer(String identifier, String classpath, boolean useJDBCManager) {
            this.classpath = classpath;
            this.identifier = identifier;
            this.useJDBCManager = useJDBCManager;
        }

        public final boolean equals(Object obj) {
            return obj instanceof DriverContainer &&
                    ((DriverContainer)obj).classpath.equals(this.classpath) &&
                    ((DriverContainer)obj).identifier.equals(this.identifier) &&
                    ((DriverContainer)obj).useJDBCManager == this.useJDBCManager;
        }
    }

    public String getClassPath() {
        return this.container.classpath;
    }

    public String getIdentifier() {
        return this.container.identifier;
    }

    public boolean usesJDBCManager() {
        return this.container.useJDBCManager;
    }

    public static SQLType forName(String name) {
        for (SQLType t : driverRegistry.values()) {
            if (t.getIdentifier().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public static SQLType registerSQLDriver(String identifier, String classpath) {
        return registerSQLDriver(identifier, classpath, true);
    }

    public static SQLType registerSQLDriver(String identifier, String classpath, boolean useJDBCManager) {
        DriverContainer temp = new DriverContainer(identifier, classpath, useJDBCManager);
        if (!driverRegistry.containsKey(temp)) {
            SQLType newType = new SQLType(temp);
            driverRegistry.put(temp, newType);
            return newType;
        }
        return null;
    }

    static {
        registerSQLDriver("sqlite", "org.sqlite.JDBC", false);
    }
}