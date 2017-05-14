package com.phonecompany.service;

import com.phonecompany.annotations.ServiceStereotype;
import com.phonecompany.service.interfaces.XSSFService;
import com.phonecompany.service.xssfHelper.RowDataSet;
import com.phonecompany.service.xssfHelper.SheetDataSet;
import com.phonecompany.service.xssfHelper.TableDataSet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.apache.poi.xssf.usermodel.charts.XSSFLineChartData;
import org.javatuples.Pair;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@ServiceStereotype
public class XSSFServiceImpl<K, V> implements XSSFService<K, V> {

    private static final String FILE_NAME = "report-";
    private static final String FILE_FORMAT = ".xlsx";
    private static final int DISTANCE_BETWEEN_TABLES = 25;
    private static final int FIRST_ROW_INDEX = 0;

    @Override
    public void generateReport(SheetDataSet<K, V> excelSheet) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String sheetName = excelSheet.getSheetName();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        int rowPosition = 0;
        for (TableDataSet<K, V> tableDataSet : excelSheet.getTableDataSets()) {
            this.createTable(sheet, rowPosition, tableDataSet);
            rowPosition += DISTANCE_BETWEEN_TABLES;
        }
        this.saveWorkBook(workbook);
    }

    private void createTable(XSSFSheet sheet, int rowPosition,
                             TableDataSet<K, V> tableDataSet) {
        this.createTableHeading(sheet, rowPosition++, tableDataSet.getTableDataSetName());
        int initialRowPosition = rowPosition;
        for (RowDataSet<K, V> rowDataSet : tableDataSet.getRowDataSets()) {
            int colPosition = 1;
            XSSFRow row = this.generateRowHeading(sheet, rowPosition++, rowDataSet.getRowName());
            this.fillRow(row, colPosition, rowDataSet.getRowValues());
        }
        RowDataSet<K, V> firstTableRow = tableDataSet.getRowDataSets().get(FIRST_ROW_INDEX);
        this.generateColHeadings(sheet.createRow(rowPosition), firstTableRow.getRowValues());
        int rowValuesNumber = firstTableRow.getRowValues().size();
        this.drawChart(sheet, initialRowPosition, rowPosition, rowValuesNumber); //TODO: is side effect
    }

    private void createTableHeading(XSSFSheet sheet, int rowPosition, String tableHeading) {
        sheet.addMergedRegion(new CellRangeAddress(rowPosition, rowPosition, 2, 4));
        XSSFRow tableNameRow = sheet.createRow(rowPosition);
        XSSFCell tableNameCell = tableNameRow.createCell(2);
        tableNameCell.setCellType(CellType.STRING);
        tableNameCell.setCellValue(tableHeading);
    }

    private XSSFRow generateRowHeading(XSSFSheet sheet, int rowPosition, String headingName) {
        XSSFRow row = sheet.createRow(rowPosition);
        XSSFCell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(headingName);
        return row;
    }

    private void fillRow(XSSFRow row, int colPosition, List<Pair<K, V>> values) {
        for (Pair<K, V> pair : values) {
            this.createCell(row, colPosition++, pair.getValue0());
        }
    }

    private void createCell(XSSFRow row, int cellPosition, K cellValue) {
        XSSFCell cell = row.createCell(cellPosition);
        cell.setCellValue((Long) cellValue); //TODO: get rid of the cast
    }

    private void generateColHeadings(XSSFRow row, List<Pair<K, V>> rowValues) {
        int cellPosition = 1;
        for (Pair<K, V> pair : rowValues) {
            XSSFCell cell = row.createCell(cellPosition++);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(pair.getValue1().toString()); //TODO: get rid of the cast
        }
    }

    private void drawChart(XSSFSheet sheet, int initialRowPosition, int rowIndex, int lastColIndex) {

        XSSFChart chart = this.createChart(sheet, initialRowPosition);
        this.useGapsOnBlankCells(chart);

        // Create data for the chart
        XSSFLineChartData data = chart.getChartDataFactory().createLineChartData();

        // Define chart AXIS
        ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        // add chart series for each
        this.addChartSeries(sheet, data, initialRowPosition, rowIndex, lastColIndex);

        // Plot the chart with the inputs from data and chart axis
        chart.plot(data, bottomAxis, leftAxis);
        this.noSmoothedLinesForChart(chart);
    }

    private void noSmoothedLinesForChart(XSSFChart chart) {
        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        for (CTLineChart ch : plotArea.getLineChartList()) {
            for (CTLineSer ser : ch.getSerList()) {
                CTBoolean ctBool = CTBoolean.Factory.newInstance();
                ctBool.setVal(false);
                ser.setSmooth(ctBool);
            }
        }
    }

    private void useGapsOnBlankCells(XSSFChart chart) {
        CTDispBlanksAs disp = CTDispBlanksAs.Factory.newInstance();
        disp.setVal(STDispBlanksAs.GAP);
        chart.getCTChart().setDispBlanksAs(disp);
    }

    private void addChartSeries(XSSFSheet sheet, LineChartData data,
                                int initialRowPosition, int rowIndex,
                                int lastColIndex) {
        ChartDataSource<String> x = this.getDateRangeDataSource(sheet, rowIndex, lastColIndex);
        while (initialRowPosition < rowIndex) {
            ChartDataSource<Number> y = this.getValuesDatasource(sheet, initialRowPosition++, lastColIndex);
            LineChartSeries lineChartSeries = data.addSeries(x, y);
            lineChartSeries.setTitle(new CellReference(initialRowPosition - 1, 0));
        }
    }

    private ChartDataSource<String> getDateRangeDataSource(XSSFSheet sheet,
                                                           int rowIndex,
                                                           int lastCellPosition) {
        return DataSources.fromStringCellRange(sheet,
                new CellRangeAddress(rowIndex, rowIndex, 1, lastCellPosition));
    }

    private ChartDataSource<Number> getValuesDatasource(XSSFSheet sheet,
                                                        int rowIndex,
                                                        int collCount) {
        return DataSources.fromNumericCellRange(sheet,
                new CellRangeAddress(rowIndex, rowIndex, 1, collCount));
    }

    private XSSFChart createChart(XSSFSheet sheet, int rowIndex) {
        // Create a drawing canvas on the worksheet
        XSSFDrawing drawing = sheet.createDrawingPatriarch();

        // Define anchor points in the worksheet to position the chart
        XSSFClientAnchor anchor = drawing.createAnchor(
                0, 0, 0, 0, 7, rowIndex - 1,
                17, rowIndex + 20);

        // Create the chart object based on the anchor point
        XSSFChart chart = drawing.createChart(anchor);

        // Define legends for the line chart and set the position of the legend
        XSSFChartLegend legend = chart.getOrCreateLegend();
        legend.setPosition(LegendPosition.BOTTOM);
        return chart;
    }

    private void saveWorkBook(XSSFWorkbook workbook) {
        try {
            FileOutputStream out = new FileOutputStream(FILE_NAME + LocalDate.now() + FILE_FORMAT);
            workbook.write(out);
            workbook.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
