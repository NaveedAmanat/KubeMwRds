package com.idev4.rds.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

@Component
public class ReportComponent {

    private final File file = null;
    @Autowired
    ServletContext context;

    public byte[] generateReport(String inputFileName, Map<String, Object> params, JRDataSource dataSource) {
        String REPORTS_BASEPATH = context.getRealPath("") + "WEB-INF" + file.separator + "classes" + file.separator + "reports"
                + file.separator;

        // String REPORTS_BASEPATH = "D:\\Reports\\Reports\\";

        params.put("LOGO_IMG", REPORTS_BASEPATH + "logo.jpg");
        byte[] bytes = null;
        try {
            String jrxml = REPORTS_BASEPATH + inputFileName;
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            bytes = getReportPdf(jasperPrint);
        } catch (JRException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public byte[] generateReport(String inputFileName, Map<String, Object> params, JRDataSource dataSource, boolean isXls) {
        String REPORTS_BASEPATH = context.getRealPath("") + "WEB-INF" + file.separator + "classes" + file.separator + "reports"
                + file.separator;
        params.put("LOGO_IMG", REPORTS_BASEPATH + "logo.jpg");
        byte[] bytes = null;
        try {
            String jrxml = REPORTS_BASEPATH + inputFileName;
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxml);
            if (isXls) {
                params.put(JRParameter.IS_IGNORE_PAGINATION, true);
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            if (isXls) {
                bytes = getReportXlsx(jasperPrint);
            } else {
                bytes = getReportPdf(jasperPrint);
            }

        } catch (JRException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private byte[] getReportXlsx(final JasperPrint jp) throws JRException {
        JRXlsxExporter xlsxExporter = new JRXlsxExporter();

        ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
        xlsxExporter.setExporterInput(new SimpleExporterInput(jp));
        xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));
        SimpleXlsxReportConfiguration xlsxreportConfig = new SimpleXlsxReportConfiguration();
        xlsxreportConfig.setDetectCellType(true);
        xlsxreportConfig.setCollapseRowSpan(true);
        xlsxreportConfig.setRemoveEmptySpaceBetweenRows(true);
        xlsxreportConfig.setForcePageBreaks(false);
        xlsxreportConfig.setWrapText(false);
        xlsxreportConfig.setCollapseRowSpan(true);
        xlsxExporter.setConfiguration(xlsxreportConfig);
        xlsxExporter.exportReport();

        return xlsReport.toByteArray();

    }

    private byte[] getReportPdf(final JasperPrint jp) throws JRException {
        return JasperExportManager.exportReportToPdf(jp);
    }
}
