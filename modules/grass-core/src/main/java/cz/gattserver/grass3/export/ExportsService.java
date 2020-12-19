package cz.gattserver.grass3.export;

import java.io.File;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

public interface ExportsService {

	File createPDFReport(JRDataSource jrDataSource, Map<String, Object> params, String reportFileName, ExportType type);
}
