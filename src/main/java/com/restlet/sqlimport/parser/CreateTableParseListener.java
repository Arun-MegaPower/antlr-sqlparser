package com.restlet.sqlimport.parser;

import com.restlet.sqlimport.model.Column;
import com.restlet.sqlimport.model.Database;
import com.restlet.sqlimport.model.ForeignKey;
import com.restlet.sqlimport.model.Table;
import com.restlet.sqlimport.parser.SqlParser.Alter_table_add_constraintContext;
import com.restlet.sqlimport.parser.SqlParser.Alter_table_stmtContext;
import com.restlet.sqlimport.parser.SqlParser.Any_nameContext;
import com.restlet.sqlimport.parser.SqlParser.Column_constraint_not_nullContext;
import com.restlet.sqlimport.parser.SqlParser.Column_constraint_primary_keyContext;
import com.restlet.sqlimport.parser.SqlParser.Column_defContext;
import com.restlet.sqlimport.parser.SqlParser.Column_default_valueContext;
import com.restlet.sqlimport.parser.SqlParser.Column_nameContext;
import com.restlet.sqlimport.parser.SqlParser.Create_table_stmtContext;
import com.restlet.sqlimport.parser.SqlParser.Fk_origin_column_nameContext;
import com.restlet.sqlimport.parser.SqlParser.Fk_target_column_nameContext;
import com.restlet.sqlimport.parser.SqlParser.Foreign_key_clauseContext;
import com.restlet.sqlimport.parser.SqlParser.Foreign_tableContext;
import com.restlet.sqlimport.parser.SqlParser.Indexed_columnContext;
import com.restlet.sqlimport.parser.SqlParser.NameContext;
import com.restlet.sqlimport.parser.SqlParser.Source_table_nameContext;
import com.restlet.sqlimport.parser.SqlParser.Table_constraint_foreign_keyContext;
import com.restlet.sqlimport.parser.SqlParser.Table_constraint_primary_keyContext;
import com.restlet.sqlimport.parser.SqlParser.Table_nameContext;
import com.restlet.sqlimport.parser.SqlParser.Type_nameContext;
import com.restlet.sqlimport.util.Util;

public class CreateTableParseListener extends SqlBaseListener {

	private static boolean DEBUG = false;

	private final SqlParser sqlParser;

	private final Database database;

	Table table;
	Column column;
	ForeignKey foreignKey;

	boolean inCreateTable = false; // CREATE TABLE
	boolean inColumnDef = false; // Column definition in CREATE TABLE
	boolean inTypeName = false; // Column type in the column definition in CREATE TABLE
	boolean inTable_constraint_primary_key = false; // PRIMARY KEY in CREATE TABLE
	boolean inAlter_table_stmt = false; // ALTER TABLE
	boolean inAlter_table_add_constraint = false; // ALTER TABLE with ADD CONSTRAINT
	boolean inTable_constraint_foreign_key = false; // FOREIGN KEY

	Util util = new Util();

	/**
	 * Constructor.
	 * @param sqlParser SQL parser
	 * @param database Database
	 */
	public CreateTableParseListener(final SqlParser sqlParser, final Database database) {
		this.sqlParser = sqlParser;
		this.database = database;
	}

	/**
	 * Used only for debug, its called for each token based on the token "name".
	 */
	@Override
	public void exitAny_name(final Any_nameContext ctx) {
		if(DEBUG) {
			System.out.println(ctx.getText() + " - ctx : " + ctx.toInfoString(sqlParser));
		}
	}

	//--- CREATE TABLE

	/**
	 * enter CREATE TABLE
	 */
	@Override
	public void enterCreate_table_stmt(final Create_table_stmtContext ctx) {
		inCreateTable = true;
		table = new Table();
	}

	/**
	 * exit CREATE TABLE
	 */
	@Override
	public void exitCreate_table_stmt(final Create_table_stmtContext ctx) {
		database.getTables().add(table);
		table = null;
		inCreateTable = false;
	}

	/**
	 * Table name
	 */
	@Override
	public void exitTable_name(final Table_nameContext ctx) {
		if(inCreateTable) {
			table.setName(util.unformatSqlName(ctx.getText()));
		}
	}

	//--- Column definition

	/**
	 * enter Column definition
	 */
	@Override
	public void enterColumn_def(final Column_defContext ctx) {
		inColumnDef = true;
		if(inCreateTable) {
			column = new Column();
		}
	}

	/**
	 * exit Column definition
	 */
	@Override
	public void exitColumn_def(final Column_defContext ctx) {
		if(inCreateTable) {
			if((column != null) && (column.getName() != null)) {
				table.getColumnByNames().put(column.getName(), column);
			}
			column = null;
		}
		inColumnDef = false;
	}

	/**
	 * Column name
	 */
	@Override
	public void exitColumn_name(final Column_nameContext ctx) {
		if(inCreateTable && inColumnDef) {
			column.setName(util.unformatSqlName(ctx.getText()));
		}
	}

	//--- Column type

	/**
	 * enter Column type
	 */
	@Override
	public void enterType_name(final Type_nameContext ctx) {
		inTypeName = true;
	}

	/**
	 * exit column type
	 */
	@Override
	public void exitType_name(final Type_nameContext ctx) {
		inTypeName = false;
	}

	/**
	 * Name. It could be : <br/>
	 * - type name
	 */
	@Override
	public void exitName(final NameContext ctx) {
		if(inCreateTable && inColumnDef && inTypeName) {
			if(column.getType() == null) {
				column.setType(util.unformatSqlName(ctx.getText()));
			} else {
				column.setType(column.getType() + " " + util.unformatSqlName(ctx.getText()));
			}
		}
	}

	//--- ALTER TABLE

	@Override
	public void enterAlter_table_stmt(final Alter_table_stmtContext ctx) {
		inAlter_table_stmt = true;
	}

	@Override
	public void exitAlter_table_stmt(final Alter_table_stmtContext ctx) {
		inAlter_table_stmt = false;
	}

	@Override
	public void exitSource_table_name(final Source_table_nameContext ctx) {
		if(inAlter_table_stmt) {
			table = database.getTableForName(util.unformatSqlName(ctx.getText()));
		}
	}

	//--- Add constraint

	@Override
	public void enterAlter_table_add_constraint(
			final Alter_table_add_constraintContext ctx) {
		inAlter_table_add_constraint = true;
	}

	@Override
	public void exitAlter_table_add_constraint(
			final Alter_table_add_constraintContext ctx) {
		inAlter_table_add_constraint = false;
	}

	//--- Constraints

	//--- Default

	@Override
	public void exitColumn_default_value(final Column_default_valueContext ctx) {
		if(inCreateTable && inColumnDef) {
			column.setDefaultValue(util.unformatSqlName(ctx.getText()));
		}
	}

	//--- Not Null

	@Override
	public void exitColumn_constraint_not_null(
			final Column_constraint_not_nullContext ctx) {
		if(inCreateTable && inColumnDef) {
			column.setIsNotNull(true);
		}
	}

	//--- Primary Key in Column definition

	@Override
	public void exitColumn_constraint_primary_key(
			final Column_constraint_primary_keyContext ctx) {
		if(inCreateTable && inColumnDef) {
			table.getPrimaryKey().getColumnNames().add(column.getName());
		}
	}

	//--- Primary Key in CREATE TABLE or in ALTER TABLE

	@Override
	public void enterTable_constraint_primary_key(
			final Table_constraint_primary_keyContext ctx) {
		inTable_constraint_primary_key = true;
	}

	@Override
	public void exitTable_constraint_primary_key(
			final Table_constraint_primary_keyContext ctx) {
		inTable_constraint_primary_key = false;
	}

	@Override
	public void exitIndexed_column(final Indexed_columnContext ctx) {
		if((inCreateTable || inAlter_table_stmt) && inTable_constraint_primary_key) {
			final String columnName = util.unformatSqlName(ctx.getText());
			table.getPrimaryKey().getColumnNames().add(columnName);
		}
	}

	//--- Foreign Key in CREATE TABLE or in ALTER TABLE

	@Override
	public void enterTable_constraint_foreign_key(
			final Table_constraint_foreign_keyContext ctx) {
		inTable_constraint_foreign_key = true;
		if((inCreateTable || inAlter_table_stmt)) {
			foreignKey = new ForeignKey();
			foreignKey.setTableNameOrigin(table.getName());
		}
	}

	@Override
	public void exitTable_constraint_foreign_key(
			final Table_constraint_foreign_keyContext ctx) {
		if((inCreateTable || inAlter_table_stmt)) {
			foreignKey.setTableNameOrigin(table.getName());
			table.getForeignKeys().add(foreignKey);
			foreignKey = null;
		}
		inTable_constraint_foreign_key = false;
	}

	@Override
	public void enterForeign_key_clause(final Foreign_key_clauseContext ctx) {
		if(inCreateTable && inColumnDef) {
			foreignKey = new ForeignKey();
			foreignKey.setTableNameOrigin(table.getName());
			foreignKey.getColumnNameOrigins().add(column.getName());
		}
	}

	@Override
	public void exitForeign_key_clause(final Foreign_key_clauseContext ctx) {
		if((inCreateTable || inAlter_table_stmt) && inColumnDef) {
			foreignKey.setTableNameOrigin(table.getName());
			table.getForeignKeys().add(foreignKey);
			foreignKey = null;
		}
	}

	@Override
	public void exitForeign_table(final Foreign_tableContext ctx) {
		if((inCreateTable || inAlter_table_stmt)) {
			foreignKey.setTableNameTarget(util.unformatSqlName(ctx.getText()));
		}
	}

	@Override
	public void exitFk_origin_column_name(
			final Fk_origin_column_nameContext ctx) {
		if(foreignKey != null) {
			foreignKey.getColumnNameOrigins().add(util.unformatSqlName(ctx.getText()));
		}
	}

	@Override
	public void exitFk_target_column_name(
			final Fk_target_column_nameContext ctx) {
		if((inCreateTable || inAlter_table_stmt)) {
			foreignKey.getColumnNameTargets().add(util.unformatSqlName(ctx.getText()));
		}
	}

}
