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

package org.knoxcraft.database.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jdom2.Content;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.knoxcraft.database.Column;
import org.knoxcraft.database.DataAccess;
import org.knoxcraft.database.Database;
import org.knoxcraft.database.Column.DataType;
import org.knoxcraft.database.exceptions.DatabaseAccessException;
import org.knoxcraft.database.exceptions.DatabaseReadException;
import org.knoxcraft.database.exceptions.DatabaseTableInconsistencyException;
import org.knoxcraft.database.exceptions.DatabaseWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.mchange.v1.lang.BooleanUtils;

/**
 * Represent access to an XML database
 *
 * @author Chris (damagefilter)
 */
public class XmlDatabase extends Database {

    private Logger log=LoggerFactory.getLogger(XmlDatabase.class);

    private XmlDatabase() {
        File path = new File("db/");

        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new ExceptionInInitializerError("Unable to create database directories... Please check your read/write permissions and try again");
            }
        }
    }

    private static XmlDatabase instance;

    public static XmlDatabase getInstance() {
        if (instance == null) {
            instance = new XmlDatabase();
        }
        return instance;
    }

    /**
     * Used to serialize the XML data into a bytestream
     */
    private XMLOutputter xmlSerializer = new XMLOutputter(Format.getPrettyFormat().setExpandEmptyElements(false).setOmitDeclaration(true).setOmitEncoding(true).setLineSeparator("\n"));

    private SAXBuilder fileBuilder = new SAXBuilder();

    /**
     * Used to store the table properties
     */
    private Map<String, Element> tableProperties = Maps.newConcurrentMap();

    @Override
    public void insert(DataAccess data) throws DatabaseWriteException {
        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new DatabaseWriteException("Failed to create database XML file: " + data.getName());
                }
            }
            catch (IOException e) {
                throw new DatabaseWriteException(e.getMessage());
            }
        }
        Document dbTable;

        try {
            dbTable = verifyTable(file, data.getName());
            insertData(file, data, dbTable, true);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (DatabaseTableInconsistencyException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void insertAll(List<DataAccess> data) throws DatabaseWriteException {
        DataAccess first = data.get(0);
        File file = new File("db/" + first.getName() + ".xml");

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new DatabaseWriteException("Failed to create database XML file: " + first.getName());
                }
            }
            catch (IOException e) {
                throw new DatabaseWriteException(e.getMessage());
            }
        }
        Document dbTable;

        try {
            dbTable = verifyTable(file, first.getName());
            for (DataAccess da : data) {
                insertData(file, da, dbTable, false);
            }
            write(file, dbTable);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (DatabaseTableInconsistencyException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void load(DataAccess data, Map<String, Object> filters) throws DatabaseReadException {
        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            throw new DatabaseReadException("Table " + data.getName() + " does not exist!");
        }

        try {
            Document table = verifyTable(file, data.getName());
            loadData(data, table, filters);
        }
        catch (JDOMException e) {
            throw new DatabaseReadException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseReadException(e.getMessage(), e);
        }
        catch (DatabaseAccessException e) {
            throw new DatabaseReadException(e.getMessage(), e);
        }
    }

    @Override
    public void loadAll(DataAccess typeTemplate, List<DataAccess> datasets, Map<String, Object> filters) throws DatabaseReadException {
        File file = new File("db/" + typeTemplate.getName() + ".xml");

        if (!file.exists()) {
            throw new DatabaseReadException("Table " + typeTemplate.getName() + " does not exist!");
        }

        try {
            Document table = verifyTable(file, typeTemplate.getName());
            loadAllData(typeTemplate, datasets, table, filters);
        }
        catch (JDOMException e) {
            throw new DatabaseReadException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseReadException(e.getMessage(), e);
        }
        catch (DatabaseAccessException e) {
            throw new DatabaseReadException(e.getMessage(), e);
        }
    }

    @Override
    public void update(DataAccess data, Map<String, Object> filters) throws DatabaseWriteException {
        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            throw new DatabaseWriteException("Table " + data.getName() + " does not exist!");
        }

        try {
            Document table = verifyTable(file, data.getName());
            updateData(file, table, data, filters, true);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (DatabaseTableInconsistencyException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void updateAll(DataAccess data, Map<DataAccess, Map<String, Object>> list) throws DatabaseWriteException {

        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            throw new DatabaseWriteException("Table " + data.getName() + " does not exist!");
        }

        try {
            Document table = verifyTable(file, data.getName());
            for (DataAccess da : list.keySet()) {
                updateData(file, table, da, list.get(da), false);
            }
            write(file, table);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (DatabaseTableInconsistencyException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void remove(DataAccess data, Map<String, Object> filters) throws DatabaseWriteException {
        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            throw new DatabaseWriteException("Table " + data.getName() + " does not exist!");
        }

        try {
            Document table = verifyTable(file, data.getName());
            removeData(file, table, filters, false);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void removeAll(DataAccess data, Map<String, Object> filters) throws DatabaseWriteException {
        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            throw new DatabaseWriteException("Table " + data.getName() + " does not exist!");
        }

        try {
            Document table = verifyTable(file, data.getName());
            removeData(file, table, filters, true);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    @Override
    public void updateSchema(DataAccess data) throws DatabaseWriteException {
        File file = new File("db/" + data.getName() + ".xml");

        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new DatabaseWriteException("Failed to create database XML file: " + data.getName());
                }
            }
            catch (IOException e) {
                throw new DatabaseWriteException(e.getMessage(), e);
            }
        }
        try {
            Document table = verifyTable(file, data.getName());

            if (table.getRootElement().getChild("tableProperties") == null) {
                table.getRootElement().addContent(generateProperties(data));
            }

            HashSet<Column> tableLayout = data.getTableLayout();

            for (Column column : tableLayout) {
                if (table.getRootElement().getChild("tableProperties").getChild(column.columnName()) == null) {
                    setPropertyFor(column, table.getRootElement().getChild("tableProperties"));
                }
            }
            for (Element element : table.getRootElement().getChildren()) {
                if (!element.getName().equals("tableProperties")) {
                    addFields(element, tableLayout);
                    removeFields(element, tableLayout);

                    // Clean out the old attribute data
                    for (Element child : element.getChildren()) {
                        child.getAttributes().clear();
                    }
                }
            }
            write(file, table);
        }
        catch (JDOMException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
        catch (DatabaseTableInconsistencyException e) {
            throw new DatabaseWriteException(e.getMessage(), e);
        }
    }

    private Document initFile(File file, String rootName) throws IOException {
        Document doc = new Document(new Element(rootName));
        write(file, doc);
        return doc;
    }

    /**
     * Adds new fields to the Element, according to the given layout set.
     *
     * @param element
     * @param layout
     */
    private void addFields(Element element, HashSet<Column> layout) {
        for (Column column : layout) {
            boolean found = false;

            for (Element child : element.getChildren()) {
                if (child.getName().equals(column.columnName())) {
                    found = true;
                }
            }
            if (!found) {
                element.addContent(new Element(column.columnName()));
            }
        }
    }

    /**
     * Removes fields from the given element that are not contained in the given layout
     *
     * @param element
     * @param layout
     */
    private void removeFields(Element element, HashSet<Column> layout) {
        for (Element child : element.getChildren()) {
            boolean found = false;

            for (Column column : layout) {
                if (child.getName().equals(column.columnName())) {
                    found = true;
                }
            }
            if (!found) {
                child.detach();
            }
        }
    }

    /**
     * Inserts data into the XML file. This does NOT update data.
     * It will create a new entry if there isn't the exact same already present
     *
     * @param file
     * @param data
     * @param dbTable
     *
     * @throws IOException
     * @throws DatabaseTableInconsistencyException
     */
    private void insertData(File file, DataAccess data, Document dbTable, boolean write) throws IOException, DatabaseTableInconsistencyException {
        HashMap<Column, Object> entry = data.toDatabaseEntryList();

        if (data.isInconsistent()) {
            // Just an extra precaution
            throw new DatabaseTableInconsistencyException("DataAccess is marked inconsistent!");
        }
        Element set = new Element("entry");

        for (Column column : entry.keySet()) {

            Element col = new Element(column.columnName());
            addToElement(dbTable, col, entry.get(column), column);
            set.addContent(col);
        }
        dbTable.getRootElement().addContent(set);
        if (write) {
            write(file, dbTable);
        }
    }

    /**
     * Updates an already existing element in the document.
     * IMPORTANT: the lengths of fields and content array must have been checked before this method is called!
     *
     * @param file
     * @param table
     * @param filters
     *
     * @throws IOException
     * @throws DatabaseTableInconsistencyException
     * @throws DatabaseWriteException
     */
    private void updateData(File file, Document table, DataAccess data, Map<String, Object> filters, boolean write) throws IOException, DatabaseTableInconsistencyException, DatabaseWriteException {
        boolean hasUpdated = false;
        String[] fields = new String[filters.size()];
        filters.keySet().toArray(fields); // We know those are strings
        for (Element element : table.getRootElement().getChildren()) {
            int equalFields = 0;

            for (String field : fields) {
                Element child = element.getChild(field);

                if (child != null) {
                    if (child.getText().equals(String.valueOf(filters.get(field)))) {
                        equalFields++;
                    }
                }
            }
            if (equalFields != fields.length) {
                continue; // Not the entry we're looking for
            }

            if (data.isInconsistent()) {
                // Just an extra precaution
                throw new DatabaseTableInconsistencyException("DataAccess is marked inconsistent!");
            }

            HashMap<Column, Object> dataSet = data.toDatabaseEntryList();
            for (Column column : dataSet.keySet()) {
                Element child = element.getChild(column.columnName());

                if (child == null) {
                    throw new DatabaseTableInconsistencyException("Column " + column.columnName() + " does not exist. Update table schema or fix DataAccess!");
                }
                // Do not change auto-increment fields
                if (column.autoIncrement()) {
                    continue;
                }
                addToElement(table, child, dataSet.get(column), column);
                hasUpdated = true;
            }
        }
        if (hasUpdated && write) {
            write(file, table);
        }
        else {
            // No fields found, that means it is a new entry
            if (write) {
                insertData(file, data, table, true);
            }
        }
    }

    private void removeData(File file, Document table, Map<String, Object> filters, boolean removeAll) throws IOException {
        ArrayList<Element> toremove = new ArrayList<Element>();
        String[] fields = new String[filters.size()];
        filters.keySet().toArray(fields); // We know those are strings
        for (Element element : table.getRootElement().getChildren()) {
            int equalFields = 0;

            for (String field : fields) {
                Element child = element.getChild(field);

                if (child != null) {
                    if (child.getText().equals(String.valueOf(filters.get(field)))) {
                        equalFields++;
                    }
                }
            }
            if (equalFields != fields.length) {
                continue; // Not the entry we're looking for
            }
            // table.getRootElement().removeContent(element);
            toremove.add(element);
            if (!removeAll) {
                // Just remove one row
                break;
            }
        }
        for (Element e : toremove) {
            e.detach();
        }
        write(file, table);
    }

    private void loadData(DataAccess data, Document table, Map<String, Object> filters) throws DatabaseAccessException {
        String[] fields = new String[filters.size()];
        filters.keySet().toArray(fields); // We know those are strings
        for (Element element : table.getRootElement().getChildren()) {
            int equalFields = 0;

            for (String field : fields) {
                Element child = element.getChild(field);

                if (child != null) {
                    if (child.getText().equals(String.valueOf(filters.get(field)))) {
                        equalFields++;
                    }
                }
            }
            if (equalFields != fields.length) {
                continue; // Not the entry we're looking for
            }
            HashMap<String, Object> dataSet = new HashMap<String, Object>();
            for (Element child : element.getChildren()) {
                DataType type = DataType.fromString(getTableProperties(table, data.getName(), data.getInstance()).getChild(child.getName()).getAttributeValue("data-type"));
                addTypeToMap(child, dataSet, type, data.getInstance());
            }
            data.load(dataSet);
            return;
        }
    }

    private void loadAllData(DataAccess template, List<DataAccess> datasets, Document table, Map<String, Object> filters) throws DatabaseAccessException {
        String[] fields = new String[filters.size()];
        filters.keySet().toArray(fields); // We know those are strings

        sortElements(table); // pre-sort so if tableProperties is moved from the top it will be returned

        Element properties = getTableProperties(table, template.getName(), template);

        for (Element element : table.getRootElement().getChildren()) {
            if (!element.getName().equals("tableProperties")) {
                int equalFields = 0;

                for (String field : fields) {
                    Element child = element.getChild(field);

                    if (child != null) {
                        if (child.getText().equals(String.valueOf(filters.get(field)))) {
                            equalFields++;
                        }
                    }
                }
                if (equalFields != fields.length) {
                    continue; // Not the entry we're looking for
                }
                HashMap<String, Object> dataSet = new HashMap<String, Object>();

                for (Element child : element.getChildren()) {
                    DataType type = DataType.fromString(properties.getChild(child.getName()).getAttributeValue("data-type"));

                    addTypeToMap(child, dataSet, type, template.getInstance());
                }
                DataAccess da = template.getInstance();

                da.load(dataSet);
                datasets.add(da);
            }
        }
    }

    /**
     * Performs a field-by-field comparison for the two given Contents.
     * First they must be of type Element and then the fields are checked against each other.
     * If the number of equal fields does not match the number of child elements ot Content a,
     * this method will return false, true otherwise
     *
     * @param a
     * @param b
     *
     * @return
     */
    private boolean elementEquals(Content a, Content b) {
        if (!(b instanceof Element)) {
            return false;
        }
        if (!(a instanceof Element)) {
            return false;
        }
        if (a == b) {
            return true;
        }

        Element elB = (Element)b;
        Element elA = (Element)a;

        if (elA.getContentSize() != elB.getContentSize()) {
            return false;
        }
        int equalHits = 0;

        for (Element el : elA.getChildren()) {
            for (Element other : elB.getChildren()) {
                if (el.getName().equals(other.getName())) {
                    if (el.getText().equalsIgnoreCase(other.getText())) {
                        equalHits++;
                    }
                }
            }
        }
        return equalHits == elA.getChildren().size();
    }

    /**
     * Generates the next auto-increment ID for this table
     *
     * @param doc
     * @param col
     *
     * @return
     *
     * @throws DatabaseTableInconsistencyException
     */
    private int getIncrementId(Document doc, Column col) throws DatabaseTableInconsistencyException {
        // Search from last to first content entry for a valid element
        int id;
        int index = doc.getRootElement().getChildren().size() - 1;

        if (index < 1) { // Skip first index as that would be the tableProperties
            // No data in this document yet, start at 1
            return 1;
        }
        Element c = doc.getRootElement().getChildren().get(index);

        try {
            String num = c.getChildText(col.columnName());

            if (num != null) {
                id = Integer.valueOf(num);
                id++;
                return id;
            }
            else {
                // That means there is no data;
                return 1;
            }
        }
        catch (NumberFormatException e) {
            throw new DatabaseTableInconsistencyException(col.columnName() + " is not an incremental field. Fix your DataAccess!");
        }
    }

    /**
     * Add data to a data set from the given xml element and type
     *
     * @param child
     * @param dataSet
     * @param type
     */
    private void addTypeToMap(Element child, HashMap<String, Object> dataSet, DataType type, DataAccess template) {
        boolean isList = false;
        try {
            isList = tableProperties.get(template.getName()).getChild(child.getName()).getAttribute("is-list").getBooleanValue();
        }
        catch (DataConversionException dcex) {
        }
        switch (type) {
            case BYTE:
                if (isList) {
                    ArrayList<Byte> values = new ArrayList<Byte>();

                    for (Element el : child.getChildren()) {
                        try {
                            values.add((Byte)typeParse(el.getValue(), child.getName(), type, template.getInstance()));
                        }
                        catch (IllegalAccessException e) {
                            log.debug("XML Database - Byte - List", e);
                        }
                        catch (NoSuchFieldException e) {
                            log.debug("XML Database - Byte - List", e);
                        }
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    try {
                        dataSet.put(child.getName(), typeParse(child.getValue(), child.getName(), type, template.getInstance()));
                    }
                    catch (IllegalAccessException e) {
                        log.debug("XML Database - Byte - NonList", e);
                    }
                    catch (NoSuchFieldException e) {
                        log.debug("XML Database - Byte - NonList", e);
                    }
                }
                break;

            case SHORT:
                if (isList) {
                    ArrayList<Short> values = new ArrayList<Short>();

                    for (Element el : child.getChildren()) {
                        try {
                            values.add((Short)typeParse(el.getValue(), child.getName(), type, template.getInstance()));
                        }
                        catch (IllegalAccessException e) {
                            log.debug("XML Database - Short - List", e);
                        }
                        catch (NoSuchFieldException e) {
                            log.debug("XML Database - Short - List", e);
                        }
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    try {
                        dataSet.put(child.getName(), typeParse(child.getValue(), child.getName(), type, template.getInstance()));
                    }
                    catch (IllegalAccessException e) {
                        log.debug("XML Database - Short - NonList", e);
                    }
                    catch (NoSuchFieldException e) {
                        log.debug("XML Database - Short - NonList", e);
                    }
                }
                break;

            case INTEGER:
                if (isList) {
                    ArrayList<Integer> values = new ArrayList<Integer>();

                    for (Element el : child.getChildren()) {
                        try {
                            values.add((Integer)typeParse(el.getValue(), child.getName(), type, template.getInstance()));
                        }
                        catch (IllegalAccessException e) {
                            log.debug("XML Database - Integer - List", e);
                        }
                        catch (NoSuchFieldException e) {
                            log.debug("XML Database - Integer - List", e);
                        }
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    try {
                        dataSet.put(child.getName(), typeParse(child.getValue(), child.getName(), type, template.getInstance()));
                    }
                    catch (IllegalAccessException e) {
                        log.debug("XML Database - Integer - NonList", e);
                    }
                    catch (NoSuchFieldException e) {
                        log.debug("XML Database - Integer - NonList", e);
                    }
                }
                break;

            case LONG:
                if (isList) {
                    ArrayList<Long> values = new ArrayList<Long>();

                    for (Element el : child.getChildren()) {
                        try {
                            values.add((Long)typeParse(el.getValue(), child.getName(), type, template.getInstance()));
                        }
                        catch (IllegalAccessException e) {
                            log.debug("XML Database - Long - List", e);
                        }
                        catch (NoSuchFieldException e) {
                            log.debug("XML Database - Long - List", e);
                        }
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    try {
                        dataSet.put(child.getName(), typeParse(child.getValue(), child.getName(), type, template.getInstance()));
                    }
                    catch (IllegalAccessException e) {
                        log.debug("XML Database - Long - NonList", e);
                    }
                    catch (NoSuchFieldException e) {
                        log.debug("XML Database - Long - NonList", e);
                    }
                }
                break;

            case FLOAT:
                if (isList) {
                    ArrayList<Float> values = new ArrayList<Float>();

                    for (Element el : child.getChildren()) {
                        try {
                            values.add((Float)typeParse(el.getValue(), child.getName(), type, template.getInstance()));
                        }
                        catch (IllegalAccessException e) {
                            log.debug("XML Database - Float - List", e);
                        }
                        catch (NoSuchFieldException e) {
                            log.debug("XML Database - Float - List", e);
                        }
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    try {
                        dataSet.put(child.getName(), typeParse(child.getValue(), child.getName(), type, template.getInstance()));
                    }
                    catch (IllegalAccessException e) {
                        log.debug("XML Database - Float - NonList", e);
                    }
                    catch (NoSuchFieldException e) {
                        log.debug("XML Database - Float - NonList", e);
                    }
                }
                break;

            case DOUBLE:
                if (isList) {
                    ArrayList<Double> values = new ArrayList<Double>();

                    for (Element el : child.getChildren()) {
                        try {
                            values.add((Double)typeParse(el.getValue(), child.getName(), type, template.getInstance()));
                        }
                        catch (IllegalAccessException e) {
                            log.debug("XML Database - Double - List", e);
                        }
                        catch (NoSuchFieldException e) {
                            log.debug("XML Database - Double - List", e);
                        }
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    try {
                        dataSet.put(child.getName(), typeParse(child.getValue(), child.getName(), type, template.getInstance()));
                    }
                    catch (IllegalAccessException e) {
                        log.debug("XML Database - Double - NonList", e);
                    }
                    catch (NoSuchFieldException e) {
                        log.debug("XML Database - Double - NonList", e);
                    }
                }
                break;

            case STRING:
                if (isList) {
                    ArrayList<String> values = new ArrayList<String>();

                    for (Element el : child.getChildren()) {
                        values.add(el.getText());
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    dataSet.put(child.getName(), child.getText());
                }
                break;

            case BOOLEAN:
                if (isList) {
                    ArrayList<Boolean> values = new ArrayList<Boolean>();

                    for (Element el : child.getChildren()) {
                        values.add(Boolean.valueOf(el.getText()));
                    }
                    dataSet.put(child.getName(), values);
                }
                else {
                    dataSet.put(child.getName(), Boolean.valueOf(child.getText()));
                }
                break;

            default:
                break;
        }
    }

    /**
     * Adds data to an element
     *
     * @param element
     * @param obj
     *
     * @throws DatabaseTableInconsistencyException
     */
    private void addToElement(Document doc, Element element, Object obj, Column col) throws DatabaseTableInconsistencyException {
        if (col.autoIncrement()) {
            element.setText(String.valueOf(getIncrementId(doc, col)));
        }
        else if (col.isList()) {
            List<?> entries = (List<?>)obj;

            // First detach everything so there won't be dupes
            element.getChildren().clear();

            if (obj == null) {
                return;
            }
            // Add fresh data
            for (Object entry : entries) {
                element.addContent(new Element("list-element").setText(String.valueOf(entry)));
            }
        }
        else {
            element.setText(String.valueOf(obj));
        }
    }

    private void write(File file, Document doc) throws IOException {
        sortElements(doc);
        RandomAccessFile f = new RandomAccessFile(file.getPath(), "rw");
        f.getChannel().lock();
        f.setLength(0);
        f.write(xmlSerializer.outputString(doc).getBytes(Charset.forName("UTF-8")));
        f.close();
    }

    private void sortElements(Document doc) {
        // Need tableProperties to be at the top
        doc.getRootElement().sortChildren(
                new Comparator<Element>() {
                    @Override
                    public int compare(Element o1, Element o2) {
                        if (o1.getName().equals("tableProperties")) {
                            return -1;
                        }
                        else if (o2.getName().equals("tableProperties")) {
                            return 1;
                        }
                        else {
                            return Integer.valueOf(o1.getChildText("id")).compareTo(Integer.valueOf(o2.getChildText("id")));
                        }
                    }
                }
                                         );

        /* Why is this necessary? */
        for (Element e : doc.getRootElement().getChildren()) {
            e.sortChildren(new Comparator<Element>() {
                               @Override
                               public int compare(Element o1, Element o2) {
                                   return o1.getName().compareTo(o2.getName());
                               }
                           }
                          );
        }
    }

    private Document verifyTable(File file, String root) throws IOException, JDOMException {
        //Document document;
        if (file.length() <= 0) {
            // Don't even try reading this file
            return initFile(file, root);
        }

        FileInputStream in = new FileInputStream(file);
        try {
            return fileBuilder.build(in);
        }
        catch (JDOMParseException e) {
            // Assume the file is damaged. Make a backup, and do it again.
            File dir = new File("db/damaged_db/" + System.currentTimeMillis() + "/");
            if (dir.mkdirs()) {
                Files.move(file, new File(dir, file.getName()));
            }
            else {
                log.warn("Failed to back up damaged database files...");
            }
            return initFile(file, root);
        }
        finally {
            in.close();
        }
    }

    private Comparable typeParse(String value, String field, DataType type, DataAccess defValTempl) throws NoSuchFieldException, IllegalAccessException {
        switch (type) {
            case BYTE:
                try {
                    return Byte.parseByte(value);
                }
                catch (NumberFormatException nfex) {
                    return defValTempl.getClass().getField(field).getByte(defValTempl);
                }

            case SHORT:
                try {
                    return Short.parseShort(value);
                }
                catch (NumberFormatException nfex) {
                    return defValTempl.getClass().getField(field).getShort(defValTempl);
                }

            case INTEGER:
                try {
                    return Integer.parseInt(value);
                }
                catch (NumberFormatException nfex) {
                    return defValTempl.getClass().getField(field).getInt(defValTempl);
                }
            case LONG:
                try {
                    return Long.parseLong(value);
                }
                catch (NumberFormatException nfex) {
                    return defValTempl.getClass().getField(field).getLong(defValTempl);
                }

            case FLOAT:
                try {
                    return Float.parseFloat(value);
                }
                catch (NumberFormatException nfex) {
                    return defValTempl.getClass().getField(field).getFloat(defValTempl);
                }
            case DOUBLE:
                try {
                    return Double.parseDouble(value);
                }
                catch (NumberFormatException nfex) {
                    return defValTempl.getClass().getField(field).getDouble(defValTempl);
                }
            case BOOLEAN:
                return BooleanUtils.parseBoolean(value);
            case STRING:
            default:
                return value;
        }
    }

    private Element generateProperties(DataAccess template) {
        Element properties = new Element("tableProperties");
        log.debug("tableProperties is installing...");
        for (Field field : template.getClass().getFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof Column) {
                    Column column = (Column)annotation;
                    setPropertyFor(column, properties);
                }
            }
        }
        tableProperties.put(template.getName(), properties);
        return properties;
    }

    private void setPropertyFor(Column column, Element tableProperties) {
        Element col = new Element(column.columnName());
        col.setAttribute("auto-increment", String.valueOf(column.autoIncrement()));
        col.setAttribute("data-type", column.dataType().name());
        col.setAttribute("column-type", column.columnType().name());
        col.setAttribute("is-list", String.valueOf(column.isList()));
        col.setAttribute("not-null", String.valueOf(column.notNull()));
        tableProperties.addContent(col);
    }

    private Element getTableProperties(Document table, String tableName, DataAccess template) {
        if (!tableProperties.containsKey(tableName)) {
            if (table.getRootElement().getChild("tableProperties") == null) {
                table.getRootElement().addContent(generateProperties(template));
            }
            else {
                tableProperties.put(tableName, table.getRootElement().getChild("tableProperties"));
            }
        }
        return tableProperties.get(tableName);
    }
}
