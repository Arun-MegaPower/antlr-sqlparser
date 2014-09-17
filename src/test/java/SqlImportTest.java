

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import com.restlet.sqlimport.model.Column;
import com.restlet.sqlimport.model.Database;
import com.restlet.sqlimport.model.Table;
import com.restlet.sqlimport.parser.SqlImport;
import com.restlet.sqlimport.util.Util;

/**
 * Test : SQL import.
 */
public class SqlImportTest {

	private SqlImport sqlImport = new SqlImport();
	private Util util = new Util();

	@Test
	public void testRead_nofile() {

		final InputStream is = null;

		// When
		final Database database = sqlImport.read(is);

		assertNull(database);
	}

	@Test
	public void testRead_file() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/test.txt");
		final InputStream in = new FileInputStream(file);

		// When
		final String content = util.read(in);

		assertEquals("Ligne 1\nLigne 2", content);
	}

	@Test
	public void testRead_with_sqlfile() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/import1.sql");
		final InputStream in = new FileInputStream(file);

		// When
		final Database database = sqlImport.read(in);

		assertEquals(2, database.getTables().size());
		final Table table1 = database.getTables().get(0);
		assertEquals("films", table1.getName());
		assertEquals(6, table1.getColumnByNames().keySet().size());
		final Table col2 = database.getTables().get(1);
		assertEquals("distributors", col2.getName());
		assertEquals(2, col2.getColumnByNames().keySet().size());
		assertNotNull(col2.getColumnByNames().get("did"));
		final Column column = col2.getColumnByNames().get("did");
		assertEquals("did",column.getName());
	}

	@Test
	public void testRead_select() throws FileNotFoundException {
		// Given
		final String sql = "SELECT log AS x FROM t1 \n" +
				"GROUP BY x                   \n" +
				"HAVING count(*) >= 4         \n" +
				"ORDER BY max(n) + 0          \n";

		// When
		final Database database = sqlImport.read(sql);

		assertNotNull(database);
	}

}
