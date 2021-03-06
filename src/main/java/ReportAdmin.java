import model.Detail;
import model.Issue;
import model.Journal;
import model.Project;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportAdmin {
    public static void main(String[] args) throws ParseException {
        ///BTRedMine.getInstance().RDP("xxx");
//        createExcel();
        if (args.length != 2)
        {
            ErrorLogger.getInstance().log("Syntax: java -jar xxx.jar [StartDate] [EndDate]");
            return;
        }
        SimpleDateFormat zzz = new SimpleDateFormat("dd/MM/yyyy");
        Date _startDate = zzz.parse(args[0]);
        Date _endDate = zzz.parse(args[1]);

        createExcel(_startDate, _endDate);
    }

    static void createExcel(Date _startDate, Date _endDate) throws ParseException {

        SimpleDateFormat sz = new SimpleDateFormat("yyyy-MM-dd");
        if (_startDate.compareTo(_endDate) > 0) {
            ErrorLogger.getInstance().log("Ngay bat dau, ket thuc khong hop le!");
            return;
        }

        List<Issue> lstIssue = new ArrayList<>();
        List<Project> lstProject = BTRedMine.getInstance().getAllProjects();
        for (Project p : lstProject) {
            List<Issue> _l = BTRedMine.getInstance().getAllIssuesInProject(p.id);
            ErrorLogger.getInstance().log("Issue size: " + _l.size());

            //Lay ra danh sach bao cao theo dai ngay
            _l = _l.stream().filter(m -> {
                try {
                    return (_startDate.compareTo(m.created_on) < 0 && _endDate.compareTo(m.created_on) > 0) ||
                            (_startDate.compareTo(sz.parse(m.start_date)) < 0 && _endDate.compareTo(sz.parse(m.start_date)) > 0);
                } catch (ParseException e) {
                    return false;
                }
            }).collect(Collectors.toList());
            lstIssue.addAll(_l);
        }
        Workbook workbook = new XSSFWorkbook();
        SimpleDateFormat xxx = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat yyy = new SimpleDateFormat("yyyyMMddHHmmss");

        lstIssue = lstIssue.stream().filter(Utils.distinctByKey(p -> p.id)).collect(Collectors.toList());
        lstIssue = lstIssue.stream().filter(m -> m.assigned_to != null).collect(Collectors.toList());
        Map<String, List<Issue>> lstGroupded = lstIssue.stream().collect(Collectors.groupingBy(w -> w.assigned_to.name));
        Set<String> groupedNameKeySet = lstGroupded.keySet();
        List<String> groupedNameKeySetSorted = new ArrayList<>(groupedNameKeySet).stream().sorted().collect(Collectors.toList());
        for (String s : groupedNameKeySetSorted) {
            ErrorLogger.getInstance().log("Đang thực hiện....");
            //moi ng 1 sheet
            Sheet sheet = workbook.createSheet(s);
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 10000);
            sheet.setColumnWidth(2, 15000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 4000);
            sheet.setColumnWidth(8, 4000);
            sheet.setColumnWidth(9, 4000);
            sheet.setColumnWidth(10, 4000);

            Row header = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);

            headerStyle.setFont(font);

            //header
            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("ID");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Dự án");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(2);
            headerCell.setCellValue("Công việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(3);
            headerCell.setCellValue("Loại");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(4);
            headerCell.setCellValue("Tình trạng");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(5);
            headerCell.setCellValue("Ngày hết hạn");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(6);
            headerCell.setCellValue("Ngày cập nhật");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(7);
            headerCell.setCellValue("Thời gian dự kiến thực hiện");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(8);
            headerCell.setCellValue("Thời gian thực hiện thực tế");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(9);
            headerCell.setCellValue("Người giao việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(10);
            headerCell.setCellValue("Ngày giao việc");
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
                cell.setCellValue(_i.id);
                cell.setCellStyle(styleBold);

                cell = row.createCell(1);
                cell.setCellValue(_i.project.name);
                cell.setCellStyle(style);

                cell = row.createCell(2);
                cell.setCellValue(_i.subject);
                cell.setCellStyle(style);

                cell = row.createCell(3);
                cell.setCellValue(_i.tracker.name);
                cell.setCellStyle(style);

                cell = row.createCell(4);
                cell.setCellValue(_i.status.name);
                cell.setCellStyle(styleBold);

                Issue detail = BTRedMine.getInstance().getDetailsIssue(_i.id);

                //Lay ra thoi gian nhan viec
                Journal journal = detail.journals.stream().filter(m -> m.details.stream()
                        .allMatch(n -> n.name.equals("status_id") &&
                                (n.old_value.equals("1") && n.new_value.equals("3")
                                        || n.old_value.equals("1") && n.new_value.equals("2"))
                        )).findFirst().orElse(null);

                cell = row.createCell(5);
                cell.setCellValue(_i.due_date);
                cell.setCellStyle(style);

                //Thoi gian hoan thanh/da thuc hien con viec
                journal = detail.journals.stream().filter(m -> m.details.stream()
                        .allMatch(n -> n.name.equals("status_id") &&
                                (n.old_value.equals("2") && n.new_value.equals("3")
                                        || n.old_value.equals("2") && n.new_value.equals("8")
                                        || n.old_value.equals("2") && n.new_value.equals("4"))
                        )).findFirst().orElse(null);
                cell = row.createCell(6);
                if (journal != null)
                    cell.setCellValue(xxx.format(journal.created_on));
                else
                    cell.setCellValue("");
                cell.setCellStyle(style);


                cell = row.createCell(7);
                if (detail.total_estimated_hours != null)
                    cell.setCellValue(detail.total_estimated_hours.toString().replace(",", "."));
                else
                    cell.setCellValue("");
                cell.setCellStyle(style);

                cell = row.createCell(8);
                cell.setCellValue(detail.total_spent_hours);
                cell.setCellStyle(style);

                cell = row.createCell(9);
                cell.setCellValue(_i.author.name);
                cell.setCellStyle(style);
                cell = row.createCell(10);
                cell.setCellValue(xxx.format(_i.created_on));
                cell.setCellStyle(style);
            }
        }

        try {
            ErrorLogger.getInstance().log("Viết ra file....");
            SimpleDateFormat  zzz = new SimpleDateFormat("ddMM");
            String filename = "StatisticJob-" + zzz.format(_startDate) + "-" + zzz.format(_endDate) + "-" + yyy.format(new Date()) + ".xlsx";
            FileOutputStream outputStream = new FileOutputStream(filename);
            workbook.write(outputStream);
            workbook.close();

            ErrorLogger.getInstance().log("File kết quả: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

       ErrorLogger.getInstance().log("Hoàn thành.");
    }

    static void createExcel() {
        Workbook workbook = new XSSFWorkbook();
        List<Issue> lstIssue = new ArrayList<>();
        List<Project> lstProject = BTRedMine.getInstance().getAllProjects();
        for (Project p : lstProject) {
            List<Issue> _l = BTRedMine.getInstance().getAllIssuesInProject(p.id);
            ErrorLogger.getInstance().log("Issue size: " + _l.size());
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
            sheet.setColumnWidth(0, 2000);
            sheet.setColumnWidth(1, 10000);
            sheet.setColumnWidth(2, 15000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 25000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 4000);
            sheet.setColumnWidth(8, 4000);
            sheet.setColumnWidth(9, 4000);
            sheet.setColumnWidth(10, 4000);
            sheet.setColumnWidth(11, 4000);
            sheet.setColumnWidth(12, 4000);

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
            headerCell.setCellValue("ID");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Dự án");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(2);
            headerCell.setCellValue("Công việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(3);
            headerCell.setCellValue("Loại");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(4);
            headerCell.setCellValue("Mô tả");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(5);
            headerCell.setCellValue("Tình trạng");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(6);
            headerCell.setCellValue("Ngày nhận việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(7);
            headerCell.setCellValue("Ngày hết hạn");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(8);
            headerCell.setCellValue("Ngày cập nhật");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(9);
            headerCell.setCellValue("Thời gian dự kiến thực hiện");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(10);
            headerCell.setCellValue("Thời gian thực hiện thực tế");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(11);
            headerCell.setCellValue("Người giao việc");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(12);
            headerCell.setCellValue("Ngày giao việc");
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
                cell.setCellValue(_i.id);
                cell.setCellStyle(styleBold);

                cell = row.createCell(1);
                cell.setCellValue(_i.project.name);
                cell.setCellStyle(style);

                cell = row.createCell(2);
                cell.setCellValue(_i.subject);
                cell.setCellStyle(style);

                cell = row.createCell(3);
                cell.setCellValue(_i.tracker.name);
                cell.setCellStyle(style);

                cell = row.createCell(4);
                cell.setCellValue(_i.description);
                cell.setCellStyle(style);

                cell = row.createCell(5);
                cell.setCellValue(_i.status.name);
                cell.setCellStyle(styleBold);

                Issue detail = BTRedMine.getInstance().getDetailsIssue(_i.id);

                //Lay ra thoi gian nhan viec
                Journal journal = detail.journals.stream().filter(m -> m.details.stream()
                        .allMatch(n -> n.name.equals("status_id") &&
                                (n.old_value.equals("1") && n.new_value.equals("3")
                                        || n.old_value.equals("1") && n.new_value.equals("2"))
                        )).findFirst().orElse(null);

                //Thoi gian nhan viec
                cell = row.createCell(6);
                if (journal != null) {
                    cell.setCellValue(xxx.format(journal.created_on));
                } else {
                    cell.setCellValue("");
                }

                cell.setCellStyle(style);

                cell = row.createCell(7);
                cell.setCellValue(_i.due_date);
                cell.setCellStyle(style);

                //Thoi gian hoan thanh/da thuc hien con viec
                journal = detail.journals.stream().filter(m -> m.details.stream()
                        .allMatch(n -> n.name.equals("status_id") &&
                                (n.old_value.equals("2") && n.new_value.equals("3")
                                        || n.old_value.equals("2") && n.new_value.equals("8")
                                        || n.old_value.equals("2") && n.new_value.equals("4"))
                        )).findFirst().orElse(null);
                cell = row.createCell(8);
                if (journal != null)
                    cell.setCellValue(xxx.format(journal.created_on));
                else
                    cell.setCellValue("");
                cell.setCellStyle(style);


                cell = row.createCell(9);
                if (detail.total_estimated_hours != null)
                    cell.setCellValue(detail.total_estimated_hours.toString());
                else
                    cell.setCellValue("");
                cell.setCellStyle(style);

                cell = row.createCell(10);
                cell.setCellValue(detail.total_spent_hours);
                cell.setCellStyle(style);

                cell = row.createCell(11);
                cell.setCellValue(_i.author.name);
                cell.setCellStyle(style);
                cell = row.createCell(12);
                cell.setCellValue(xxx.format(_i.created_on));
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

