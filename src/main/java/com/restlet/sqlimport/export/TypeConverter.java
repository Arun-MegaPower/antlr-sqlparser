package com.restlet.sqlimport.export;

import com.restlet.sqlimport.model.Column;
import com.restlet.sqlimport.model.Database;
import com.restlet.sqlimport.model.Table;
import com.restlet.sqlimport.report.Report;
import com.restlet.sqlimport.report.ReportLineStatus;

/**
 * Conversion of types from SQL to Entity store
 */
public class TypeConverter {

	public static final String TYPE_STRING = "String";
	public static final String TYPE_FLOAT = "Float";
	public static final String TYPE_INTEGER = "Integer";
	public static final String TYPE_BOOLEAN = "Boolean";

	private final Report report;

	/**
	 * Constructor.
	 * @param report Report (must not be null)
	 */
	public TypeConverter(final Report report) {
		this.report = report;
	}

	/**
	 * Convert SQL type to Entity store type for all columns of the database.
	 * @param database Database
	 */
	public void convertTypeFromSQLToEntityStore(final Database database) {
		for(final Table table : database.getTables()) {
			for(final Column column : table.getColumnByNames().values()) {
				column.setConvertedType(
						convertTypeFromSQLToEntityStore(column.getType()));
			}
		}
	}

	/**
	 * Convert SQL type to Entity store type.
	 * @param sqlType SQL type
	 * @return Entity store type
	 */
	public String convertTypeFromSQLToEntityStore(final String sqlType) {
		if(sqlType == null) {
			return null;
		}
		if(isString(sqlType)) {
			return TYPE_STRING;
		}
		if(isFloat(sqlType)) {
			return TYPE_FLOAT;
		}
		if(isInteger(sqlType)) {
			return TYPE_INTEGER;
		}
		if(isBoolean(sqlType)) {
			return TYPE_BOOLEAN;
		}
		report.addMessage(ReportLineStatus.UNKNOWN_SQL_TYPE, sqlType);
		return TYPE_STRING;
	}

	/**
	 * Indicates if the SQL type corresponds to the String type in the Entity store.
	 * @param sqlType SQL type
	 * @return boolean
	 */
	public boolean isString(final String sqlType) {
		if("CHAR".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("VARCHAR".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("VARCHAR2".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("CHARACTER".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("character varying".equalsIgnoreCase(sqlType)) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates if the SQL type corresponds to the Float type in the Entity store.
	 * @param sqlType SQL type
	 * @return boolean
	 */
	public boolean isFloat(final String sqlType) {
		if("DECIMAL".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("NUMBER".equalsIgnoreCase(sqlType)) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates if the SQL type corresponds to the Integer type in the Entity store.
	 * @param sqlType SQL type
	 * @return boolean
	 */
	public boolean isInteger(final String sqlType) {
		if("INT".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("INTEGER".equalsIgnoreCase(sqlType)) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates if the SQL type corresponds to the Boolean type in the Entity store.
	 * @param sqlType SQL type
	 * @return boolean
	 */
	public boolean isBoolean(final String sqlType) {
		if("BOOL".equalsIgnoreCase(sqlType)) {
			return true;
		}
		if("BOOLEAN".equalsIgnoreCase(sqlType)) {
			return true;
		}
		return false;
	}

}
