import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;;

class Transaction {
    enum Type {
        INCOME, EXPENSE
    }

    Type type;
    double amount;
    String category;
    String description;
    LocalDate date;

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Transaction(Transaction.Type type, double amount, String category, String description, LocalDate date) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    @Override
    public String toString() {
        return  type + ","+ amount + "," + category + ","
                + description + "," + date ;
    }

}

public class Tracker {
    private static List<Transaction> transactions = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);
    private static String fileName;

    public static void main(String[] args) throws IOException {
        System.out.println("please enter the file name");
        fileName = sc.nextLine();
        loadFromFile(fileName);

        while (true) {
            display();

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addTransaction(Transaction.Type.INCOME);
                    break;
                case 2:
                    addTransaction(Transaction.Type.EXPENSE);
                    break;
                case 3:
                    MonthlySummary();
                    break;
                case 4:
                    loadFromFile(fileName);
                    break;

                case 5: 
                    saveToFile(fileName);
                    System.out.println("Data saved. Exiting...");
                    break;
                case 6:
                    System.out.println("Exit");
                    return;
                
                default:
                    System.out.println("Invalid choice.");
            }
        }

    }

    public static void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename,true))) {
            for (Transaction t : transactions) {
                writer.println(t);
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
        transactions.clear();
    }

    public static void addTransaction(Transaction.Type type) {
        System.out.print("Enter amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();

        System.out.print("Enter category like income = Salary/Business , expense = Food/Rent/Travel");
        System.out.println("Enter category : ");
        String category = sc.nextLine();

        System.out.print("Enter description: ");
        String description = sc.nextLine();

        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateInput = sc.nextLine();
        LocalDate date = LocalDate.parse(dateInput);
        transactions.add(new Transaction(type, amount, category, description, date));
        System.out.println("Transaction added.");
    }

    private static void MonthlySummary() {
        transactions.clear();
        loadFromFile(fileName);
        System.out.print("Enter month and year (yyyy-MM): ");
        String input = sc.nextLine();
        YearMonth selectedMonth = YearMonth.parse(input);

        double incomeTotal = 0, expenseTotal = 0;

        System.out.println("\n--- Monthly Summary for " + selectedMonth + " ---");
        for (Transaction t : transactions) {
            if (YearMonth.from(t.getDate()).equals(selectedMonth)) {
                if (t.getType() == Transaction.Type.INCOME)
                    incomeTotal += t.getAmount();
                else
                    expenseTotal += t.getAmount();
            }
        }

        System.out.println("Total Income: " + incomeTotal);
        System.out.println("Total Expenses: " + expenseTotal);
        System.out.println("Net Savings: " + (incomeTotal - expenseTotal));
    }

    public static void display() {
        System.out.println( "Choose an option: \n1. Add Income \n2. Add Expense \n3. View Monthly Summary \n4. Load Transactions from file \n5. Save \n6. Exit \nEnter Your choice");
    }

    public static void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line: " + line);
                String[] parts = line.split(",");
                try {
                    Transaction.Type transactionType = Transaction.Type.valueOf(parts[0].trim().toUpperCase());
                    double amount = Double.parseDouble(parts[1].trim());
                    String category = parts[2].trim();
                    String description = parts[3].trim();
                    LocalDate date = LocalDate.parse(parts[4].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    transactions.add(new Transaction(transactionType, amount, category, description, date));
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid transaction data due to parsing error: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        System.out.println("Loaded transactions count: " + transactions.size() + " from  " + fileName);

    }

}
