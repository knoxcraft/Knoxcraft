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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field of a DataAccess object as a column in a database table.
 * This annotation also describes the column so that it will be handled properly in the database
 *
 * @author Chris (damagefilter)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    public enum DataType {
        INTEGER(Integer.class),
        FLOAT(Float.class),
        DOUBLE(Double.class),
        LONG(Long.class),
        SHORT(Short.class),
        BYTE(Byte.class),
        STRING(String.class),
        BOOLEAN(Boolean.class);

        private Class<?> cls;

        DataType(Class<?> cls) {
            this.cls = cls;
        }

        public boolean isAssignable(Class<?> cls) {
            return this.cls.isAssignableFrom(cls);
        }

        public static DataType fromString(String in) {
            for (DataType t : DataType.values()) {
                if (in.equalsIgnoreCase(t.name())) {
                    return t;
                }
            }
            return STRING;
        }

        public Class<?> getTypeClass() {
            return cls;
        }
    }

    public enum ColumnType {
        UNIQUE,
        PRIMARY,
        NORMAL;
    }

    String columnName();

    DataType dataType();

    ColumnType columnType() default ColumnType.NORMAL;

    /**
     * Should we auto-increment the value of this field?
     */
    boolean autoIncrement() default false;

    /**
     * Is this field an implementation of the List interface?
     */
    boolean isList() default false;

    boolean notNull() default false;
}
