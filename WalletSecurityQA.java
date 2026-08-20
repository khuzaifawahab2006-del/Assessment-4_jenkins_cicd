public class WalletSecurityQA {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       STARTING WALLET SECURITY QA SYSTEM         ");
        System.out.println("==================================================");

        // Pre-configured simulation variables
        DigitalWallet alice = new DigitalWallet("ACC001", "Alice", "1234", 10000.0, 5000.0);
        DigitalWallet bob = new DigitalWallet("ACC002", "Bob", "5678", 50000.0, 1000.0);

        // QA Test 1: Normal transaction processing
        System.out.println("\n[Test 1] Normal Transaction (Deposit & Withdrawal)");
        alice.deposit(2000.0);
        System.out.println("Alice Balance after deposit: " + alice.getBalance("1234"));
        DigitalWallet.Response r1 = alice.withdraw(1000.0, "1234");
        System.out.println("Withdraw Status: " + r1.success + " | Message: " + r1.message + " | Balance: " + alice.getBalance("1234"));

        // QA Test 2: Insufficient balance validation
        System.out.println("\n[Test 2] Insufficient Balance Check");
        DigitalWallet.Response r2 = alice.withdraw(15000.0, "1234");
        System.out.println("Withdraw Status: " + r2.success + " | Message: " + r2.message);

        // QA Test 3: Daily constraint limits
        System.out.println("\n[Test 3] Daily Transaction Limit Check");
        DigitalWallet.Response r3 = alice.withdraw(9500.0, "1234");
        System.out.println("Withdraw Status: " + r3.success + " | Message: " + r3.message);

        // QA Test 4: Brute force security PIN lockout behavior
        System.out.println("\n[Test 4] Multiple Failed PIN Attempts Lockout");
        System.out.println("Attempt 1 with wrong PIN (9999): " + alice.verifyPin("9999"));
        System.out.println("Attempt 2 with wrong PIN (8888): " + alice.verifyPin("8888"));
        System.out.println("Attempt 3 with wrong PIN (7777): " + alice.verifyPin("7777"));
        System.out.println("Wallet Locked Status: " + alice.isLocked());
        DigitalWallet.Response r4 = alice.withdraw(100.0, "1234");
        System.out.println("Withdraw Try on Locked Wallet Status: " + r4.success + " | Message: " + r4.message);

        // Clean room workspace testing environment
        DigitalWallet charlie = new DigitalWallet("ACC003", "Charlie", "0000", 100000.0, 100000.0);

        // QA Test 5: Multi-rule suspicion heuristics detection
        System.out.println("\n[Test 5] Suspicious Transaction Detection Engine");
        System.out.println("Simulating high-frequency operational bursts (5 small deposits)...");
        for (int i = 0; i < 5; i++) {
            charlie.deposit(10.0);
        }
        DigitalWallet.Response r5a = charlie.withdraw(50.0, "0000");
        System.out.println("6th Rapid Transaction Flag Status: " + r5a.success + " | Message: " + r5a.message);

        System.out.println("\nTesting Outlier Large Transaction parameters...");
        DigitalWallet.Response r5b = charlie.withdraw(45000.0, "0000");
        System.out.println("Large Transaction (>40k) Flag Status: " + r5b.success + " | Message: " + r5b.message);

        // QA Test 6: In-flight duplicate transaction replays
        System.out.println("\n[Test 6] Replay/Duplicate Transaction Suppression");
        DigitalWallet wA = new DigitalWallet("ACC005", "UserA", "1111", 50000.0, 5000.0);
        DigitalWallet wB = new DigitalWallet("ACC006", "UserB", "2222", 50000.0, 1000.0);
        
        DigitalWallet.Response r6a = wA.transfer(wB, 500.0, "1111");
        System.out.println("First Transfer Target: " + r6a.success + " | Message: " + r6a.message);
        DigitalWallet.Response r6b = wA.transfer(wB, 500.0, "1111");
        System.out.println("Immediate Replay Transfer Blocked: " + r6b.success + " | Message: " + r6b.message);

        // QA Test 7: Negative ledger inputs handling
        System.out.println("\n[Test 7] Negative Amount Handling");
        DigitalWallet.Response r7 = wA.deposit(-500.0);
        System.out.println("Negative Balance Injection Blocked: " + r7.success + " | Message: " + r7.message);

        // QA Test 8: Rapid concurrent race-condition threat logging
        System.out.println("\n[Test 8] Concurrent Rapid Attack Simulation");
        DigitalWallet runnerWallet = new DigitalWallet("ACC007", "Runner", "9999", 50000.0, 100.0);
        DigitalWallet.Response r8a = runnerWallet.withdraw(100.0, "9999");
        DigitalWallet.Response r8b = runnerWallet.withdraw(100.0, "9999");
        System.out.println("Simultaneous Microsecond Hit 1 Status: " + r8a.success + " | Message: " + r8a.message);
        System.out.println("Simultaneous Microsecond Hit 2 Status: " + r8b.success + " | Message: " + r8b.message);

        System.out.println("\n==================================================");
        System.out.println("      ALL INTEGRATION & QA TESTS COMPLETE         ");
        System.out.println("==================================================");
    }
}
