import model.Issue;
import model.Project;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportAdmin {
    public static void main(String[] args) {
        Workbook workbook = new XSSFWorkbook();
        List<Issue> lstIssue = new ArrayList<>();
        List<Project> lstProject = BTRedMine.getInstance().getAllProjects();
        for (Project p : lstProject) {
            List<Issue> _l = BTRedMine.getInstance().getAllIssuesInProject(p.id);
            ErrorLogger.getInstance().log("Project size: " + _l.size());
            lstIssue.addAll(_l);
        }
        lstIssue = lstIssue.stream().filter(Utils.distinctByKey(p -> p.id)).collect(Collectors.toList());
        SimpleDateFormat xxx = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat yyy = new SimpleDateFormat("yyyyMMddHHmmss");
        lstIssue = lstIssue.stream().filter(m -> m.assigned_to != null).collect(Collectors.toList());
        Map<String, List<Issue>> lstGroupded = lstIssue.stream().collect(Collectors.groupingBy(w -> w.assigned_to.name));
        Set<String> groupedNameKeySet = lstGroupded.keySet();
        List<String> groupedNameKeySetSorted = new ArrayList<>(groupedNameKeySet).stream().sorted().collect(Collectors.toList());
        for (String s : groupedNameKeySetSorted) {
            //moi ng 1 sheet
            Sheet sheet = workbook.createSheet(s);
            sheet.setColumnWidth(0, 10000);
            sheet.setColumnWidth(1, 15000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 25000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 4000);
            sheet.setColumnWidth(8, 4000);
            sheet.setColumnWidth(9, 4000);

            Row header = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
//            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
//            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);

            headerStyle.setFont(font);

            //header
            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Dự án");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Công việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(2);
            headerCell.setCellValue("Loại");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(3);
            headerCell.setCellValue("Mô tả");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(4);
            headerCell.setCellValue("Tình trạng");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(5);
            headerCell.setCellValue("Ngày tạo việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(6);
            headerCell.setCellValue("Ngày hết hạn");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(7);
            headerCell.setCellValue("Ngày cập nhật");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(8);
            headerCell.setCellValue("Thời gian dự kiến thực tế");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(9);
            headerCell.setCellValue("Thời gian thực hiện thực tế");
            headerCell.setCellStyle(headerStyle);

            CellStyle style = workbook.createCellStyle();
            style.setWrapText(true);

            CellStyle styleBold = workbook.createCellStyle();
            styleBold.setWrapText(true);
            styleBold.setFont(font);


            //cell
            List<Issue> _ls = lstGroupded.get(s);
            for (int i = 0; i < _ls.size(); i++) {
                Issue _i = _ls.get(i);
                Row row = sheet.createRow(i + 1);
                Cell cell = row.createCell(0);
                cell.setCellValue(_i.project.name);
                cell.setCellStyle(styleBold);

                cell = row.createCell(1);
                cell.setCellValue(_i.subject);
                cell.setCellStyle(style);

                cell = row.createCell(2);
                cell.setCellValue(_i.tracker.name);
                cell.setCellStyle(style);

                cell = row.createCell(3);
                cell.setCellValue(_i.description);
                cell.setCellStyle(style);

                cell = row.createCell(4);
                cell.setCellValue(_i.status.name);
                cell.setCellStyle(styleBold);

                cell = row.createCell(5);
                cell.setCellValue(xxx.format(_i.created_on));
                cell.setCellStyle(style);

                cell = row.createCell(6);
                cell.setCellValue(_i.due_date);
                cell.setCellStyle(style);

                cell = row.createCell(7);
                cell.setCellValue(xxx.format(_i.updated_on));
                cell.setCellStyle(style);

                Issue detail = BTRedMine.getInstance().getDetailsIssue(_i.id);


                cell = row.createCell(8);
                if (detail.total_estimated_hours != null)
                    cell.setCellValue(detail.total_estimated_hours.toString());
                else
                    cell.setCellValue("");
                cell.setCellStyle(style);

                cell = row.createCell(9);
                cell.setCellValue(detail.total_spent_hours);
                cell.setCellStyle(style);
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream("StatisticJob-" + yyy.format(new Date()) + ".xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

