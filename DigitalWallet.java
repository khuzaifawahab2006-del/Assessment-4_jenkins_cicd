import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DigitalWallet {
    private String accountId;
    private String ownerName;
    private String pin;
    private double dailyLimit;
    private double balance;
    private int failedPinAttempts;
    private boolean isLocked;
    private List<Transaction> history;

    public static class Transaction {
        public LocalDateTime timestamp;
        public String type;
        public double amount;
        public String status;
        public String remarks;

        public Transaction(String type, double amount, String status, String remarks) {
            this.timestamp = LocalDateTime.now();
            this.type = type;
            this.amount = amount;
            this.status = status;
            this.remarks = remarks;
        }
    }

    public DigitalWallet(String accountId, String ownerName, String pin, double dailyLimit, double initialBalance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.pin = pin;
        this.dailyLimit = dailyLimit;
        this.balance = initialBalance;
        this.failedPinAttempts = 0;
        this.isLocked = false;
        this.history = new ArrayList<>();
    }

    public String getAccountId() { return this.accountId; }
    public boolean isLocked() { return this.isLocked; }

    public boolean verifyPin(String inputPin) {
        if (this.isLocked) return false;
        if (this.pin.equals(inputPin)) {
            this.failedPinAttempts = 0;
            return true;
        } else {
            this.failedPinAttempts++;
            if (this.failedPinAttempts >= 3) {
                this.isLocked = true;
            }
            return false;
        }
    }

    public String getBalance(String inputPin) {
        if (this.isLocked) return "Account locked due to multiple failed PIN attempts.";
        if (!this.pin.equals(inputPin)) return "Invalid PIN.";
        return String.valueOf(this.balance);
    }

    public Response deposit(double amount) {
        if (amount <= 0) {
            return new Response(false, "Deposit amount must be positive.");
        }
        this.balance += amount;
        addHistory("Deposit", amount, "Success", "");
        return new Response(true, "Deposit successful.");
    }

    public Response withdraw(double amount, String inputPin) {
        if (this.isLocked) return new Response(false, "Account locked.");
        if (amount <= 0) return new Response(false, "Withdrawal amount must be positive.");
        if (!verifyPin(inputPin)) return new Response(false, "Invalid PIN.");
        if (amount > this.balance) return new Response(false, "Insufficient balance.");
        if (!checkDailyLimit(amount)) return new Response(false, "Daily transaction limit exceeded.");

        FraudCheck fraud = checkFraud(amount);
        if (fraud.isSuspicious) {
            addHistory("Withdrawal", amount, "Flagged/Suspicious", fraud.reason);
            return new Response(false, "Transaction blocked: " + fraud.reason);
        }

        this.balance -= amount;
        addHistory("Withdrawal", amount, "Success", "");
        return new Response(true, "Withdrawal successful.");
    }

    public Response transfer(DigitalWallet targetWallet, double amount, String inputPin) {
        if (this.isLocked) return new Response(false, "Account locked.");
        if (amount <= 0) return new Response(false, "Transfer amount must be positive.");
        if (!verifyPin(inputPin)) return new Response(false, "Invalid PIN.");
        if (amount > this.balance) return new Response(false, "Insufficient balance.");
        if (!checkDailyLimit(amount)) return new Response(false, "Daily transaction limit exceeded.");

        FraudCheck fraud = checkFraud(amount);
        if (fraud.isSuspicious) {
            addHistory("Transfer", amount, "Flagged/Suspicious", fraud.reason);
            return new Response(false, "Transaction blocked: " + fraud.reason);
        }

        if (isDuplicateTransaction("Transfer", amount, targetWallet.getAccountId())) {
            return new Response(false, "Duplicate transaction detected.");
        }

        this.balance -= amount;
        targetWallet.deposit(amount);
        addHistory("Transfer", amount, "Success", "To Account: " + targetWallet.getAccountId());
        return new Response(true, "Transfer successful.");
    }

    private void addHistory(String type, double amount, String status, String remarks) {
        this.history.add(new Transaction(type, amount, status, remarks));
    }

    private boolean checkDailyLimit(double amount) {
        LocalDate today = LocalDate.now();
        double dailyTotal = 0;
        for (Transaction tx : this.history) {
            if (tx.timestamp.toLocalDate().equals(today) && 
                tx.status.equals("Success") && 
                (tx.type.equals("Withdrawal") || tx.type.equals("Transfer"))) {
                dailyTotal += tx.amount;
            }
        }
        return (dailyTotal + amount) <= this.dailyLimit;
    }

    private FraudCheck checkFraud(double amount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(10);

        if (this.failedPinAttempts >= 3 || this.isLocked) {
            return new FraudCheck(true, "Multiple failed PIN attempts.");
        }

        if (amount > 40000.0) {
            return new FraudCheck(true, "Large transaction amount.");
        }

        long recentTxCount = 0;
        for (Transaction tx : this.history) {
            if (tx.timestamp.isAfter(tenMinutesAgo)) {
                recentTxCount++;
            }
        }
        if (recentTxCount >= 5) {
            return new FraudCheck(true, "More than 5 transactions in 10 minutes.");
        }

        double totalSuccessAmount = 0;
        int successCount = 0;
        for (Transaction tx : this.history) {
            if (tx.status.equals("Success")) {
                totalSuccessAmount += tx.amount;
                successCount++;
            }
        }
        if (successCount >= 3) {
            double avgAmount = totalSuccessAmount / successCount;
            if (amount > (avgAmount * 10)) {
                return new FraudCheck(true, "Unusual transaction amount compared to history.");
            }
        }

        return new FraudCheck(false, "");
    }

    private boolean isDuplicateTransaction(String type, double amount, String targetId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);
        
        for (int i = history.size() - 1; i >= 0; i--) {
            Transaction tx = history.get(i);
            if (tx.timestamp.isBefore(oneMinuteAgo)) break;
            if (tx.type.equals(type) && tx.amount == amount && tx.remarks.contains(targetId) && tx.status.equals("Success")) {
                return true;
            }
        }
        return false;
    }

    public List<Transaction> getHistory() { return this.history; }

    public static class Response {
        public boolean success;
        public String message;
        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    private static class FraudCheck {
        boolean isSuspicious;
        String reason;
        public FraudCheck(boolean isSuspicious, String reason) {
            this.isSuspicious = isSuspicious;
            this.reason = reason;
        }
    }
}
