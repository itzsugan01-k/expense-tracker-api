package com.expensetracker.service.impl;

import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.exception.ExportException;
import com.expensetracker.mapper.ExpenseMapper;
import com.expensetracker.model.dto.request.ExpenseFilterRequest;
import com.expensetracker.model.dto.request.ExpenseRequest;
import com.expensetracker.model.dto.response.AddExpenseResponse;
import com.expensetracker.model.dto.response.ExpenseResponse;
import com.expensetracker.model.dto.response.ExpenseStatistics;
import com.expensetracker.model.dto.response.MonthlySummaryResponse;
import com.expensetracker.model.entity.Budget;
import com.expensetracker.model.entity.Category;
import com.expensetracker.model.entity.Expense;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.specification.ExpenseSpecification;
import com.expensetracker.utill.Helper;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository repository;
    private final Helper helper;


    public ExpenseServiceImpl(ExpenseRepository repository, Helper helper, UserRepository userRepository) {
        this.repository = repository;
        this.helper = helper;

    }


    @Override
    @Transactional
    public AddExpenseResponse addExpense(ExpenseRequest request) {
        AddExpenseResponse response = new AddExpenseResponse();


        Expense entity = ExpenseMapper.toEntity(request);
        entity.setUser(helper.getCurrentUser());
        repository.save(entity);

        Budget budget = helper.getMonthlyBudgets(YearMonth.from(request.getExpenseDate()));
        List<Expense> expenses = helper.getMonthlyExpenses(
                YearMonth.from(request.getExpenseDate()));
        double spent = expenses.stream().mapToDouble(Expense::getAmount).sum();

        response.setStatus(helper.getBudgetStatus(budget, spent));
        response.setExpense(ExpenseMapper.toResponse(entity));

        return response;
    }

    @Override
    public Page<ExpenseResponse> listAllExpense(Pageable pageable) {
        Page<Expense> expenses = repository.findByUser(helper.getCurrentUser(), pageable);

        return expenses.map(ExpenseMapper::toResponse);
    }

    @Override
    public ExpenseResponse getExpenseById(int id) {
        Expense expense = repository.findByIdAndUser
                (id, helper.getCurrentUser()).orElseThrow(() ->
                new ExpenseNotFoundException(id));
        return ExpenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse updateExpense(int id, ExpenseRequest expenseRequest) {
        Expense expense = repository.findByIdAndUser(id,
                helper.getCurrentUser()).orElseThrow(() -> new ExpenseNotFoundException(id));
        expense.setExpenseDate(expenseRequest.getExpenseDate());
        expense.setDescription(expenseRequest.getDescription());
        expense.setAmount(expenseRequest.getAmount());
        expense.setCategory(expenseRequest.getCategory());
        repository.save(expense);
        return ExpenseMapper.toResponse(expense);
    }

    @Override
    @Transactional
    public void deleteExpense(int id) {
        Expense expense = repository.findByIdAndUser
                (id, helper.getCurrentUser()).orElseThrow(() -> new ExpenseNotFoundException(id));
        repository.delete(expense);

    }

    @Override
    public List<ExpenseResponse> searchByDescription(String description) {
        List<Expense> expenses = repository.findByDescriptionContainingIgnoreCaseAndUser
                (description, helper.getCurrentUser());
        return expenses.stream().map(ExpenseMapper::toResponse).toList();
    }

    @Override
    public List<ExpenseResponse> filterByCategory(Category category) {
        List<Expense> expenses = repository.findByCategoryAndUser(category, helper.getCurrentUser());
        return expenses.stream().map(ExpenseMapper::toResponse).toList();
    }

    @Override
    public List<ExpenseResponse> dateBetweenExpenses(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = repository.findByExpenseDateBetweenAndUser(startDate, endDate, helper.getCurrentUser());
        return expenses.stream().map(ExpenseMapper::toResponse).toList();
    }

    @Override
    public MonthlySummaryResponse expenseStatistics(YearMonth yearMonth) {
        MonthlySummaryResponse summaryResponse = new MonthlySummaryResponse();
        ExpenseStatistics statistics = helper.setStatistics(yearMonth);
        summaryResponse.setTotalExpense(statistics.getTotalSpent());
        summaryResponse.setTotalTransactions(statistics.getTotalTransactions());
        summaryResponse.setYearMonth(yearMonth);
        summaryResponse.setCategorySummary(statistics.getCategorySummary());
        summaryResponse.setHighestExpense(statistics.getHighestExpense());
        summaryResponse.setLowestExpense(statistics.getLowestExpense());
        summaryResponse.setAverageExpense(statistics.getAverageExpense());

        return summaryResponse;
    }

    @Override
    public Page<ExpenseResponse> filterExpenses(ExpenseFilterRequest request, Pageable pageable) {
        Specification<Expense> specification = Specification.where(ExpenseSpecification.hasUser(helper.getCurrentUser()));
        if (request.getCategory() != null) {
            specification = specification.and(ExpenseSpecification.hasCategory(request.getCategory()));
        }
        if (request.getKeyword() != null) {
            specification = specification.and(ExpenseSpecification.hasKeyword(request.getKeyword()));
        }
        if (request.getMinAmount() != null) {
            specification = specification.and(ExpenseSpecification.amountGreaterThanOrEqualTo(request.getMinAmount()));
        }
        if (request.getMaxAmount() != null) {
            specification = specification.and(ExpenseSpecification.amountLessThanOrEqualTo(request.getMaxAmount()));
        }
        if (request.getStartDate() != null && request.getEndDate() != null) {
            specification = specification.and(ExpenseSpecification.betweenDates(request.getStartDate(), request.getEndDate()));
        } else if (request.getStartDate() != null) {
            specification = specification.and(ExpenseSpecification.hasStartDate(request.getStartDate()));
        } else if (request.getEndDate() != null) {
            specification = specification.and(ExpenseSpecification.hasEndDate(request.getEndDate()));
        }

        Page<Expense> expenses = repository.findAll(specification, pageable);
        return expenses.map(ExpenseMapper::toResponse);


    }

    @Override
    public ByteArrayInputStream exportCsv() {
        List<Expense> expenses = repository.findByUser(helper.getCurrentUser());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            writer.println("Id,Description,Amount,Category,Expense Date");
            for (Expense expense : expenses) {
                writer.println(expense.getId() + "," +
                        expense.getDescription() + "," +
                        expense.getAmount() + "," +
                        expense.getCategory() + "," +
                        expense.getExpenseDate());

            }
            writer.flush();
            byte[] byteArray = outputStream.toByteArray();
            return new ByteArrayInputStream(byteArray);
        }catch (Exception e) {
            throw new ExportException("Unable to export file", e);
        }

    }

    @Override
    public ByteArrayInputStream exportExcel() {
        List<Expense> expenses = repository.findByUser(helper.getCurrentUser());
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            CellStyle amountStyle = workbook.createCellStyle();
            amountStyle.setAlignment(HorizontalAlignment.RIGHT);
            DataFormat format = workbook.createDataFormat();
            amountStyle.setDataFormat(format.getFormat("\"₹\"#,##0.00"));

            Sheet sheet = workbook.createSheet("Expenses");
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Id",
                    "Description",
                    "Amount",
                    "Category",
                    "Expense Date"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 4));
            sheet.createFreezePane(0, 1);
            int rowNum = 1;
            for (Expense expense : expenses) {

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(expense.getId());
                row.createCell(1).setCellValue(expense.getDescription());
                Cell amountCell = row.createCell(2);
                amountCell.setCellValue(expense.getAmount());
                amountCell.setCellStyle(amountStyle);
                row.createCell(3).setCellValue(expense.getCategory().toString());
                row.createCell(4).setCellValue(expense.getExpenseDate());

            }
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            throw new ExportException("Unable to export file", e);
        }
    }

    @Override
    public ByteArrayInputStream exportPdf() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Expense> expenses = repository.findByUser(helper.getCurrentUser());
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, out);
            document.open();
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Expense Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated on : "
                    + LocalDate.now()));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell cell = new PdfPCell(new Phrase("Id", headerFont));
            table.addCell(cell);
            PdfPCell cell1 = new PdfPCell(new Phrase("Description", headerFont));
            table.addCell(cell1);
            PdfPCell cell2 = new PdfPCell(new Phrase("Amount", headerFont));
            table.addCell(cell2);
            PdfPCell cell3 = new PdfPCell(new Phrase("Category", headerFont));
            table.addCell(cell3);
            PdfPCell cell4 = new PdfPCell(new Phrase("Expense Date", headerFont));
            table.addCell(cell4);

            for (Expense expense : expenses) {
                table.addCell(String.valueOf(expense.getId()));
                table.addCell(expense.getDescription());
                PdfPCell amountCel = new PdfPCell(new Phrase(String.valueOf(expense.getAmount())));
                amountCel.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(amountCel);
                table.addCell(expense.getCategory().name());
                table.addCell(expense.getExpenseDate().toString());
            }
            document.add(table);

        } catch (Exception e) {
            throw new ExportException("Unable to export file", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }


}
