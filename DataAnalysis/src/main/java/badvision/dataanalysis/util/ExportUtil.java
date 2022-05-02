package badvision.dataanalysis.util;

import badvision.dataanalysis.model.Subject;
import badvision.dataanalysis.model.*;
import badvision.dataanalysis.model.RecordedResponse.TrialRecord;
import badvision.dataanalysis.model.TrialRecordDetails.TrialRecordBody;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class for exporting excel data Borrows heavily from
 * https://github.com/Adobe-Consulting-Services/aem-epic-tool/blob/master/src/main/java/com/adobe/acs/epic/util/ReportUtil.java
 * which I also wrote and left in an open source (Apache) license so usage in
 * this context is above-board.
 *
 * @author brobert
 */
public class ExportUtil {

    public static void saveReport(File dir, List<Subject> subjects) throws FileNotFoundException, IOException {
        try ( FileOutputStream target = new FileOutputStream(new File(dir, "report.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            String[] subjectsHeader = {
                "Number",
                "IP",
                "City",
                "Region",
                "Lat",
                "Lon",
                "Start",
                "End",
                "Restart Count",
                "Trial order",
                "Age",
                "Device",
                "Device Other",
                "User Agent",
                "Client Hint - Agent",
                "Client Hint - Mobile",
                "Client Hint - Platform",
                "Position",
                "Impairment",
                "Impairment - Other",
                "Impairment - Frequency",
                "LikeMetropolis",
                "Physical",
                "Feedback"
            };
            subjects.forEach(Subject::performSubjectAnalysis);

            addSheet("Subjects", workbook, subjects, subjectsHeader,
                    Subject::getNumber,
                    Subject::getIp,
                    s -> s.getLocation().getCity(),
                    s -> s.getLocation().getRegion(),
                    s -> s.getLocation().getLoc().split(",")[0],
                    s -> s.getLocation().getLoc().split(",")[1],
                    s -> StringUtil.formatDate(s.getStartTime()),
                    s -> StringUtil.formatDate(s.getEndTime()),
                    Subject::getRestartCount,
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getTrialOrder()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getAge()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getDevice()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getDeviceOther()),
                    s -> getOpt(s.getStartRecord(), r -> r.getDevice().getUserAgent()),
                    s -> getOpt(s.getStartRecord(), r -> r.getDevice().getClientHints().getAgent()),
                    s -> getOpt(s.getStartRecord(), r -> r.getDevice().getClientHints().getMobile()),
                    s -> getOpt(s.getStartRecord(), r -> r.getDevice().getClientHints().getPlatform()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getPosition()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getImpairment()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getImpairmentOther()),
                    s -> getOpt(s.getStartRecord(), r -> r.getRequest().getBody().getIfreq()),
                    s -> getOpt(s.getEndRecord(), r -> r.getRequest().getBody().getLikeMetropolis()),
                    s -> getOpt(s.getEndRecord(), r -> r.getRequest().getBody().getPhysical()),
                    s -> getOpt(s.getEndRecord(), r -> r.getRequest().getBody().getFeedback())
            );
            String[] trialDataHeader = {
                "Subject",
                "QWERTY No Hints Mean",
                "QWERTY No Hints Std",
                "Metro No Hints Mean",
                "Metro No Hints Std",
                "QWERTY Hints Mean",
                "QWERTY Hints Std",
                "Metro Hints Mean",
                "Metro Hints Std"
            };
            Iterable<Subject> completedSubjects = () -> subjects.stream().filter(Subject::isComplete).iterator();

            addSheet("WPM", workbook, completedSubjects, trialDataHeader,
                    Subject::getNumber,
                    s -> getOpt(s.getTrialRecord(0), r -> r.getRequest().getBody().getWpmMean()),
                    s -> getOpt(s.getTrialRecord(0), r -> r.getRequest().getBody().getWpmStd()),
                    s -> getOpt(s.getTrialRecord(1), r -> r.getRequest().getBody().getWpmMean()),
                    s -> getOpt(s.getTrialRecord(1), r -> r.getRequest().getBody().getWpmStd()),
                    s -> getOpt(s.getTrialRecord(2), r -> r.getRequest().getBody().getWpmMean()),
                    s -> getOpt(s.getTrialRecord(2), r -> r.getRequest().getBody().getWpmStd()),
                    s -> getOpt(s.getTrialRecord(3), r -> r.getRequest().getBody().getWpmMean()),
                    s -> getOpt(s.getTrialRecord(3), r -> r.getRequest().getBody().getWpmStd())
            );

            String[] factors = {"Layout", "Hints"};
            String[][] levels = {{"QWERTY", "Metro"}, {"No hints", "Hints"}};

            addAnovaSheet("WPM Mean Anova", workbook, AnalysisUtil.performMultipleAnova(selectData(completedSubjects, TrialRecordBody::getWpmMean), factors, levels));
            addAnovaSheet("WPM Std Anova", workbook, AnalysisUtil.performMultipleAnova(selectData(completedSubjects, TrialRecordBody::getWpmStd), factors, levels));

            addSheet("Accuracy", workbook, completedSubjects, trialDataHeader,
                    Subject::getNumber,
                    s -> getOpt(s.getTrialRecord(0), r -> r.getRequest().getBody().getAccuracyMean()),
                    s -> getOpt(s.getTrialRecord(0), r -> r.getRequest().getBody().getAccuracyStd()),
                    s -> getOpt(s.getTrialRecord(1), r -> r.getRequest().getBody().getAccuracyMean()),
                    s -> getOpt(s.getTrialRecord(1), r -> r.getRequest().getBody().getAccuracyStd()),
                    s -> getOpt(s.getTrialRecord(2), r -> r.getRequest().getBody().getAccuracyMean()),
                    s -> getOpt(s.getTrialRecord(2), r -> r.getRequest().getBody().getAccuracyStd()),
                    s -> getOpt(s.getTrialRecord(3), r -> r.getRequest().getBody().getAccuracyMean()),
                    s -> getOpt(s.getTrialRecord(3), r -> r.getRequest().getBody().getAccuracyStd())
            );

            addAnovaSheet("Accuracy Mean Anova", workbook, AnalysisUtil.performMultipleAnova(selectData(completedSubjects, TrialRecordBody::getAccuracyMean), factors, levels));
            addAnovaSheet("Accuracy Std Anova", workbook, AnalysisUtil.performMultipleAnova(selectData(completedSubjects, TrialRecordBody::getAccuracyStd), factors, levels));

            addSheet("Time per key", workbook, completedSubjects, trialDataHeader,
                    Subject::getNumber,
                    s -> getOpt(s.getTrialRecord(0), r -> r.getRequest().getBody().getTimePerKeystrokeMean()),
                    s -> getOpt(s.getTrialRecord(0), r -> r.getRequest().getBody().getTimePerKeystrokeStd()),
                    s -> getOpt(s.getTrialRecord(1), r -> r.getRequest().getBody().getTimePerKeystrokeMean()),
                    s -> getOpt(s.getTrialRecord(1), r -> r.getRequest().getBody().getTimePerKeystrokeStd()),
                    s -> getOpt(s.getTrialRecord(2), r -> r.getRequest().getBody().getTimePerKeystrokeMean()),
                    s -> getOpt(s.getTrialRecord(2), r -> r.getRequest().getBody().getTimePerKeystrokeStd()),
                    s -> getOpt(s.getTrialRecord(3), r -> r.getRequest().getBody().getTimePerKeystrokeMean()),
                    s -> getOpt(s.getTrialRecord(3), r -> r.getRequest().getBody().getTimePerKeystrokeStd())
            );

            addAnovaSheet("Key Time Mean Anova", workbook, AnalysisUtil.performMultipleAnova(selectData(completedSubjects, TrialRecordBody::getTimePerKeystrokeMean), factors, levels));
            addAnovaSheet("Key Time Std Anova", workbook, AnalysisUtil.performMultipleAnova(selectData(completedSubjects, TrialRecordBody::getTimePerKeystrokeStd), factors, levels));

            workbook.write(target);
        }
    }

    private static void addAnovaSheet(String title, XSSFWorkbook workbook, MultuWayAnovaResults res) {
        String[] headers = new String[]{
            "Test", "p-value", "f-value", "means", "stds"
        };
        addSheet(title, workbook, res.getResults().entrySet(), headers,
                Map.Entry::getKey,
                e -> e.getValue().getPValue(),
                e -> e.getValue().getFValue(),
                e -> e.getValue().getSeriesStatistics()
                        .stream()
                        .map(DescriptiveStatistics::getMean)
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")),
                e -> e.getValue().getSeriesStatistics()
                        .stream()
                        .map(DescriptiveStatistics::getStandardDeviation)
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
        );

    }

    public static <A> String getString(A obj, Function<A, String> f) {
        return (obj == null) ? null : f.apply(obj);
    }

    public static List<List<Double>> selectData(Iterable<Subject> subjects, Function<TrialRecordBody, Double> selectFunction) {
        List<List<Double>> res = new ArrayList<>();
        for (int idx = 0; idx < 4; idx++) {
            int i = idx;
            res.add(StreamSupport.stream(subjects.spliterator(), false)
                    .map(s -> s.getTrialRecord(i).orElseThrow().getRequest().getBody())
                    .map(selectFunction).collect(Collectors.toList()));
        }
        return res;
    }

    public static <A, B> B getOpt(Optional<A> obj, Function<A, B> f) {
        return getOpt(obj, f, null);
    }

    public static <A, B> B getOpt(Optional<A> obj, Function<A, B> f, B defaultValue) {
        return obj.map(f).orElse(defaultValue);
    }

    public static <T> void addSheet(String title, XSSFWorkbook workbook, Iterable<T> data, String[] header, Function<T, Object>... getters) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);
        XSSFSheet sheet = title != null ? workbook.createSheet(title) : workbook.createSheet();
        sheet.setDisplayRowColHeadings(false);
        sheet.createFreezePane(0, 1);
        if (!data.iterator().hasNext()) {
            sheet.createRow(0).createCell(0).setCellValue("There were no entries");
            return;
        }
        int headerRows = 0;
        if (header != null) {
            headerRows++;
            XSSFRow headerRow = sheet.createRow(0);
            headerRow.setRowStyle(headerStyle);
            for (int col = 0; col < header.length; col++) {
                XSSFCell cell = headerRow.createCell(col);
                cell.setCellValue(header[col]);
                cell.setCellStyle(headerStyle);
            }
        }
        Iterator<T> iter = data.iterator();
        int size = 0;
        for (int r = 0; iter.hasNext(); r++) {
            size++;
            XSSFRow row = sheet.createRow(r + headerRows);
            T rowData = iter.next();
            for (int col = 0; col < getters.length; col++) {
                Object val = getters[col].apply(rowData);
                if (val == null) {
                    continue;
                }
                XSSFCell cell = row.createCell(col);
                cell.setCellStyle(cellStyle);
                if (val instanceof Number) {
                    cell.setCellValue(((Number) val).doubleValue());
                } else {
                    cell.setCellValue(String.valueOf(val));
                }
            }
        }
        ExportUtil.autosizeColumns(sheet, getters.length);
        sheet.setAutoFilter(new CellRangeAddress(0, size, 0, getters.length - 1));
    }

    private static XSSFCellStyle createCellStyle(Workbook workbook) {
        XSSFCellStyle xstyle = (XSSFCellStyle) workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        xstyle.setFont(font);
        return xstyle;
    }

    public static CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle headerStyle = createCellStyle(workbook);
        XSSFColor header = new XSSFColor(new byte[]{(byte) 79, (byte) 129, (byte) 189});
        headerStyle.setFillForegroundColor(header);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.getFont().setColor(IndexedColors.WHITE.index);
        return headerStyle;
    }

    public static int MIN_COL_WIDTH = 6 << 8;
    public static int MAX_COL_WIDTH = 120 << 8;

    public static void autosizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
            int cw = (int) (sheet.getColumnWidth(i) * 0.8);
            // increase width to accommodate drop-down arrow in the header
            sheet.setColumnWidth(i, Math.max(Math.min(cw, MAX_COL_WIDTH), MIN_COL_WIDTH));
        }
    }
}
