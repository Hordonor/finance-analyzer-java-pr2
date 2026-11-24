package ua.edu.dut.finance;

import ua.edu.dut.finance.analysis.TransactionAnalyzer;
import ua.edu.dut.finance.io.TransactionCSVReader;
import ua.edu.dut.finance.model.Transaction;
import ua.edu.dut.finance.report.TransactionReportGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static final String DEFAULT_MONTH = "01-2024";

    private Main() {
    }

    public static void main(String[] args) {
        try {
            boolean printTransactions = Arrays.asList(args).contains("--print-transactions");
            String[] parameters = Arrays.stream(args)
                    .filter(argument -> !argument.equals("--print-transactions"))
                    .toArray(String[]::new);

            List<Transaction> transactions = parameters.length > 0
                    ? TransactionCSVReader.readTransactions(parameters[0])
                    : readBundledTransactions();
            String monthYear = parameters.length > 1 ? parameters[1] : DEFAULT_MONTH;

            if (printTransactions) {
                transactions.forEach(System.out::println);
                System.out.println();
            }

            TransactionReportGenerator.printBalanceReport(TransactionAnalyzer.calculateTotalBalance(transactions));
            TransactionReportGenerator.printTransactionsCountByMonth(monthYear,
                    TransactionAnalyzer.countTransactionsByMonth(transactions, monthYear));
            TransactionReportGenerator.printTopExpensesReport(TransactionAnalyzer.findTopExpenses(transactions));
            TransactionReportGenerator.printExpensesByPeriodReport(monthYear, transactions);
            System.out.println();
            TransactionReportGenerator.printTextExpenseReport(transactions);
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println("Не вдалося обробити транзакції: " + exception.getMessage());
            System.err.println("Використання: mvn exec:java -Dexec.args=\"<CSV-файл або URL> [MM-yyyy]\"");
            System.exit(1);
        }
    }

    private static List<Transaction> readBundledTransactions() throws IOException {
        try (InputStream input = Main.class.getResourceAsStream("/transactions.csv")) {
            if (input == null) {
                throw new IOException("Не знайдено ресурс transactions.csv.");
            }
            return TransactionCSVReader.parseTransactions(TransactionCSVReader.readLines(input));
        }
    }
}
