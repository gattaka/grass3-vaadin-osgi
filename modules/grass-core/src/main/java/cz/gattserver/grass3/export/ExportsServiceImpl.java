package cz.gattserver.grass3.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.springframework.stereotype.Service;

import cz.gattserver.grass3.exception.GrassException;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service
public class ExportsServiceImpl implements ExportsService {

	private static final int TEMP_FILE_ATTEMPTS = 10000;
	public static final String BASEDIR = System.getProperty("java.io.tmpdir");

	/**
	 * Vytváří dočasný soubor v tmp
	 * 
	 * @param name
	 *            základ jména souboru
	 */
	public static File createTempFile(String name) {
		File baseDir = new File(BASEDIR);
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_FILE_ATTEMPTS; counter++) {
			File tempFile = new File(baseDir, baseName + counter + name);
			if (!tempFile.exists())
				return tempFile;
		}
		throw new IllegalStateException("Failed to create file within " + TEMP_FILE_ATTEMPTS + " attempts (tried "
				+ baseName + "0 to " + baseName + (TEMP_FILE_ATTEMPTS - 1) + ')');
	}

	@Override
	public File createPDFReport(JRDataSource jrDataSource, Map<String, Object> params, String reportFileName,
			ExportType type) {
		try {
			String path = "/jasper/";
			InputStream jasperReportStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path + reportFileName + ".jasper");
			params.put("SUBREPORT_DIR", path);

			File tmpFile = createTempFile("Report.pdf");
			FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);

			JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
			JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
			JasperPrint jasperPrint = jasperFillManager.fill(jasperReportStream, params, jrDataSource);

			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fileOutputStream);
			if (ExportType.PRINT == type)
				exporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT, "this.print();");

			exporter.exportReport();
			fileOutputStream.close();

			return tmpFile;
		} catch (Exception e) {
			throw new GrassException("Export se nezdařil", e);
		}
	}

}
