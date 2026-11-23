package ua.edu.dut.finance.report;

import org.junit.jupiter.api.Test;
import ua.edu.dut.finance.model.Transaction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionReportGeneratorTest {
    @Test
    void createsCategoryAndMonthBarsAtOneStarPerThousandHryvnias() {
        String report = TransactionReportGenerator.generateTextExpenseReport(List.of(
                new Transaction("01-01-2024", -2_500, "Продукти"),
                new Transaction("02-01-2024", -500, "Кава"),
                new Transaction("01-02-2024", 50_000, "Зарплата")));

        assertTrue(report.contains("Продукти: 2500.00 грн **"), report);
        assertTrue(report.contains("Кава: 500.00 грн "));
        assertTrue(report.contains("2024-01: 3000.00 грн ***"));
        assertTrue(report.contains("Витрати за місяцями"));
    }
}
