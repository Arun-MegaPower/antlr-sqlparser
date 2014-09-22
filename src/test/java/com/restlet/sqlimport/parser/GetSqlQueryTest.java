package com.restlet.sqlimport.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import com.restlet.sqlimport.report.Report;
import com.restlet.sqlimport.report.ReportStatus;
import com.restlet.sqlimport.util.Util;


public class GetSqlQueryTest {

	private Report report = new Report();
	private GetSqlQuery getSqlQuery = new GetSqlQuery(report);
	private Util util = new Util();

	@Test
	public void testRead_standard() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/standard.sql");
		final InputStream in = new FileInputStream(file);

		// When
		final List<String> lines = getSqlQuery.getSqlQuerys(in);

		// Then
		assertEquals(3, lines.size());

		assertEquals(ReportStatus.TO_PARSE, report.getReportLineByQuerys().get(lines.get(0)).getReportStatus());
		assertEquals(ReportStatus.TO_PARSE, report.getReportLineByQuerys().get(lines.get(1)).getReportStatus());
		assertEquals(ReportStatus.TO_PARSE, report.getReportLineByQuerys().get(lines.get(2)).getReportStatus());

	}

	@Test
	public void testRead_mysql() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/mysql.sql");
		final InputStream in = new FileInputStream(file);

		// When
		final List<String> lines = getSqlQuery.getSqlQuerys(in);

		// Then
		assertEquals(5, lines.size());
	}

	@Test
	public void testRead_postgres() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/postgres.sql");
		final InputStream in = new FileInputStream(file);

		// When
		final List<String> lines = getSqlQuery.getSqlQuerys(in);

		// Then
		assertEquals(9, lines.size());
	}

	@Test
	public void testRead_oracle1() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/oracle1.sql");
		final InputStream in = new FileInputStream(file);

		// When
		final List<String> lines = getSqlQuery.getSqlQuerys(in);

		// Then
		assertEquals(4, lines.size());
	}

	@Test
	public void testRead_oracle2() throws FileNotFoundException {
		// Given
		final File file = util.getFileByClassPath("/oracle2.sql");
		final InputStream in = new FileInputStream(file);

		// When
		final List<String> lines = getSqlQuery.getSqlQuerys(in);

		// Then
		assertEquals(3, lines.size());
	}


}
