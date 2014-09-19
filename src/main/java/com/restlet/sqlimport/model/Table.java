package com.restlet.sqlimport.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table
 */
public class Table {

	/**
	 * name
	 */
	private String name;
	/**
	 * Columns by names
	 */
	private Map<String,Column> columnByNames = new HashMap<String,Column>();
	/**
	 * Column which is the primary key
	 */
	private Column primaryKey;
	/**
	 * Foreign keys
	 */
	private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();

	/**
	 * Get the foreign key which corresponding to the column.
	 * @param column Column
	 * @return Foreign key (null if not found)
	 */
	public ForeignKey getForeignKeyForColumnNameOrigin(final Column column) {
		for(final ForeignKey foreignKey : getForeignKeys()) {
			for(final String columnNameOrigin : foreignKey.getColumnNameOrigins()) {
				if(column.getName().equals(columnNameOrigin)) {
					return foreignKey;
				}
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Map<String, Column> getColumnByNames() {
		return columnByNames;
	}

	public void setColumnByNames(final Map<String, Column> columnByNames) {
		this.columnByNames = columnByNames;
	}

	public Column getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(final Column primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(final List<ForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

}
