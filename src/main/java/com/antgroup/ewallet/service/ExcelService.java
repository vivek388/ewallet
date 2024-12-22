package com.antgroup.ewallet.service;

import com.antgroup.ewallet.model.entity.BasePayment;
import com.antgroup.ewallet.model.entity.PaymentQuote;
import com.antgroup.ewallet.model.entity.Transaction;
import com.antgroup.ewallet.model.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.math.BigDecimal;

@Service
public class ExcelService {
    private static String databasePath = System.getProperty("user.dir") + "/database/Database.xlsx";

    public List<User> getAllUserData() {
        List<User> users = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(User.sheet);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                User user = new User();
                user.setId(getCellNumericValue(row, 0));
                user.setUsername(getCellStringValue(row, 1));
                user.setPassword(getCellStringValue(row, 2));
                user.setBalance(getCellNumericValue(row, 3));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public Long UserExist(String username, String password) {
        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheet(User.sheet);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell cellUsername = row.getCell(1);
                Cell cellPassword = row.getCell(2);

                if (cellUsername.getStringCellValue().equals(username) &&
                        cellPassword.getStringCellValue().equals(password)) {
                    double dId = getCellNumericValue(row, 0);
                    return (long) dId; // A welcome page or some success response
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (long) 0;
    }

    public User getUserById(String id) {
        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheet(User.sheet);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell cellId = row.getCell(0);
                String stringId = String.valueOf((long) cellId.getNumericCellValue());
                ;
                if (stringId.equals(id)) {
                    User user = new User();
                    user.setId(cellId.getNumericCellValue());
                    user.setBalance(getCellNumericValue(row, 3));
                    return user; // A welcome page or some success response
                }
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Transaction getTransactionByPaymentId(double id) {

        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Transactions"); // Change this to your actual sheet name

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                double currId = getCellNumericValue(row, 0);
                if (id == currId) {
                    String dateTime = getCellStringValue(row, 1);
                    String customerId = getCellStringValue(row, 2);
                    double amount = getCellNumericValue(row, 3);
                    String details = getCellStringValue(row, 4);
                    String statusCode = getCellStringValue(row, 5);
                    String status = getCellStringValue(row, 6);
                    String statusMessage = getCellStringValue(row, 7);
                    String paymentRequestId = getCellStringValue(row, 8);
                    String payCurrency = getCellStringValue(row, 9);
                    String payAmount = getCellStringValue(row, 10);
                    String payToCurrency = getCellStringValue(row, 11);
                    String payToAmount = getCellStringValue(row, 12);
                    String paymentTime = getCellStringValue(row, 13);
                    String quoteId = getCellStringValue(row, 14);
                    String quotePair = getCellStringValue(row, 15);
                    String quotePrice = getCellStringValue(row, 16);
                    String pspId = getCellStringValue(row, 17);
                    String acquirerId = getCellStringValue(row, 18);
                    String promoJson = getCellStringValue(row, 19);
                    String refundId = getCellStringValue(row, 20);

                    Transaction transaction = new Transaction(id,
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateTime), customerId, amount,
                            details, statusCode, status, statusMessage, paymentRequestId, payCurrency, payAmount,
                            payToCurrency, payToAmount, paymentTime, quoteId, quotePair, quotePrice, pspId,
                            acquirerId, promoJson, refundId);

                    return transaction;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Transaction> getAlipayTransactions(String paymentRequestId, String pspId, String acquirerId) {
        List<Transaction> transactions = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet("Transactions"); // Change this to your actual sheet name

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (paymentRequestId.equals(getCellStringValue(row, 8))) {
                    if (pspId.equals(getCellStringValue(row, 17)) && acquirerId.equals(getCellStringValue(row, 18))) {
                        double id = getCellNumericValue(row, 0);
                        String dateTime = getCellStringValue(row, 1);
                        String customerId = getCellStringValue(row, 2);
                        double amount = getCellNumericValue(row, 3);
                        String details = getCellStringValue(row, 4);
                        String statusCode = getCellStringValue(row, 5);
                        String status = getCellStringValue(row, 6);
                        String statusMessage = getCellStringValue(row, 7);
                        String payCurrency = getCellStringValue(row, 9);
                        String payAmount = getCellStringValue(row, 10);
                        String payToCurrency = getCellStringValue(row, 11);
                        String payToAmount = getCellStringValue(row, 12);
                        String paymentTime = getCellStringValue(row, 13);
                        String quoteId = getCellStringValue(row, 14);
                        String quotePair = getCellStringValue(row, 15);
                        String quotePrice = getCellStringValue(row, 16);
                        String promoJson = getCellStringValue(row, 19);
                        String refundId = getCellStringValue(row, 20);

                        Transaction transaction = new Transaction(id,
                                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateTime), customerId, amount,
                                details, statusCode, status, statusMessage, paymentRequestId, payCurrency, payAmount,
                                payToCurrency, payToAmount, paymentTime, quoteId, quotePair, quotePrice, pspId,
                                acquirerId, promoJson, refundId);

                        transactions.add(transaction);
                    }
                }
            }
            return transactions;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Transaction> getLatestTransactions(String customerId, int count) {
        List<Transaction> transactions = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(Transaction.sheet);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (customerId.equals(getCellStringValue(row, 2))) {
                    double id = getCellNumericValue(row, 0);
                    String dateTime = getCellStringValue(row, 1);
                    double amount = getCellNumericValue(row, 3);
                    String details = getCellStringValue(row, 4);
                    String statusCode = getCellStringValue(row, 5);
                    String status = getCellStringValue(row, 6);
                    String statusMessage = getCellStringValue(row, 7);
                    String paymentRequestId = getCellStringValue(row, 8);
                    String payCurrency = getCellStringValue(row, 9);
                    String payAmount = getCellStringValue(row, 10);
                    String payToCurrency = getCellStringValue(row, 11);
                    String payToAmount = getCellStringValue(row, 12);
                    String paymentTime = getCellStringValue(row, 13);
                    String quoteId = getCellStringValue(row, 14);
                    String quotePair = getCellStringValue(row, 15);
                    String quotePrice = getCellStringValue(row, 16);
                    String pspId = getCellStringValue(row, 17);
                    String acquirerId = getCellStringValue(row, 18);
                    String promoJson = getCellStringValue(row, 19);
                    String refundId = getCellStringValue(row, 20);

                    Transaction transaction = new Transaction(id,
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateTime), customerId, amount,
                            details, statusCode, status, statusMessage, paymentRequestId, payCurrency, payAmount,
                            payToCurrency, payToAmount, paymentTime, quoteId, quotePair, quotePrice, pspId,
                            acquirerId, promoJson, refundId);
                    transactions.add(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Transaction> customerTransactions = new ArrayList<>();

        // Filter transactions by customerId
        for (Transaction transaction : transactions) {
            if (transaction.getCustomerId().equals(customerId)) {
                customerTransactions.add(transaction);
            }
        }

        // Sort by dateTime in descending order (latest first)
        Collections.sort(customerTransactions, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2) {
                return t2.getDateTime().compareTo(t1.getDateTime());
            }
        });

        // Return the latest 3 transactions
        return customerTransactions.size() > count ? customerTransactions.subList(0, count) : customerTransactions;
    }

    public double addTransaction(String customerId, double amount, String details,
                                 String statusCode, String status, String statusMessage, String paymentRequestId, String paymentTime,
                                 BasePayment paymentAmount, BasePayment payToAmount, PaymentQuote quote, String pspId,
                                 String acquirerId, String promoJson, String refundRequestId) {
        String transactionSheetName = Transaction.sheet; // Change this to your actual sheet name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = new Date();
        String dateTime = sdf.format(date);

        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet userSheet = workbook.getSheet(User.sheet);

            for (int rowIndex = 1; rowIndex <= userSheet.getLastRowNum(); rowIndex++) {
                Row row = userSheet.getRow(rowIndex);
                Cell cellId = row.getCell(0);
                String stringId = String.valueOf((long) cellId.getNumericCellValue());

                if (stringId.equals(customerId)) {
                    Cell balanceCell = row.getCell(3);
                    double currentBalance = balanceCell.getNumericCellValue();
                    BigDecimal originalBigDecimal = BigDecimal.valueOf(currentBalance);
                    BigDecimal additionBigDecimal = BigDecimal.valueOf(amount);

                    // Perform the subtraction
                    BigDecimal newBigDecimal = originalBigDecimal.add(additionBigDecimal);

                    // Convert back to double
                    double newAmount = newBigDecimal.doubleValue();
                    balanceCell.setCellValue(newAmount);
                    break;
                }
            }

            Sheet sheet = workbook.getSheet(transactionSheetName);
            int lastRowNum = sheet.getLastRowNum();

            double nextId = 1; // Start with 1 if there are no transactions
            try {
                if (lastRowNum > 0) {
                    Row lastRow = sheet.getRow(lastRowNum);
                    double lastId = getCellNumericValue(lastRow, 0);
                    nextId = lastId + 1;
                }
            } catch (Exception e) {

            }

            // Create a new row for the transaction
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(nextId); // Transaction ID
            newRow.createCell(1).setCellValue(dateTime); // Date and Time
            newRow.createCell(2).setCellValue(customerId); // Customer ID
            newRow.createCell(3).setCellValue(amount); // Amount
            newRow.createCell(4).setCellValue(details); // Details
            newRow.createCell(5).setCellValue(statusCode);
            newRow.createCell(6).setCellValue(status);
            newRow.createCell(7).setCellValue(statusMessage);
            newRow.createCell(8).setCellValue(paymentRequestId);
            if (paymentAmount != null) {
                newRow.createCell(9).setCellValue(paymentAmount.currency);
                newRow.createCell(10).setCellValue(paymentAmount.value);
            }
            if (payToAmount != null) {
                newRow.createCell(11).setCellValue(payToAmount.currency);
                newRow.createCell(12).setCellValue(payToAmount.value);
            }
            newRow.createCell(13).setCellValue(paymentTime);
            if (quote != null) {
                newRow.createCell(14).setCellValue(quote.quoteId);
                newRow.createCell(15).setCellValue(quote.quoteCurrencyPair);
                newRow.createCell(16).setCellValue(quote.quotePrice);
            }

            if (pspId != null) {
                newRow.createCell(17).setCellValue(pspId);
            }

            if (acquirerId != null) {
                newRow.createCell(18).setCellValue(acquirerId);
            }

            if (promoJson != null) {
                newRow.createCell(19).setCellValue(promoJson);
            }

            if (refundRequestId != null) {
                newRow.createCell(20).setCellValue(refundRequestId);
            }

            // Write changes back to the Excel file
            try (FileOutputStream fos = new FileOutputStream(new File(databasePath))) {
                workbook.write(fos);
            }

            return nextId;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void cancelTransaction(String paymentRequestId) {
        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(Transaction.sheet);

            BigDecimal accumulatedAmount = BigDecimal.ZERO;

            String customerId = "0";

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (paymentRequestId.equals(getCellStringValue(row, 8))) {
                    customerId = getCellStringValue(row, 2);
                    double amount = getCellNumericValue(row, 3);
                    BigDecimal addAmount = BigDecimal.valueOf(amount);
                    accumulatedAmount = accumulatedAmount.add(addAmount);
                    Cell amountCell = row.getCell(3);
                    amountCell.setCellValue((double)0);

                    Cell detailsCell = row.getCell(4);
                    detailsCell.setCellValue("[Canceled] " + getCellStringValue(row, 4));

                    row.getCell(5).setCellValue("ORDER_IS_CLOSED");;
                    row.getCell(6).setCellValue("F");;
                    row.getCell(7).setCellValue("The order is closed.");;
                }
            }

            Sheet userSheet = workbook.getSheet(User.sheet);

            for (int rowIndex = 1; rowIndex <= userSheet.getLastRowNum(); rowIndex++) {
                Row row = userSheet.getRow(rowIndex);
                Cell cellId = row.getCell(0);
                String stringId = String.valueOf((long) cellId.getNumericCellValue());

                if (stringId.equals(customerId)) {
                    Cell balanceCell = row.getCell(3);
                    double currentBalance = balanceCell.getNumericCellValue();
                    BigDecimal originalBigDecimal = BigDecimal.valueOf(currentBalance);

                    // Perform the subtraction
                    BigDecimal newBigDecimal = originalBigDecimal.subtract(accumulatedAmount);

                    // Convert back to double
                    double newAmount = newBigDecimal.doubleValue();
                    balanceCell.setCellValue(newAmount);
                    break;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(new File(databasePath))) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addApiCallLog(String fromUrl, String payload, String response, boolean verified) {
        String sheetName = "ApiCall";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = new Date();
        String dateTime = sdf.format(date);

        try (FileInputStream fis = new FileInputStream(new File(databasePath));
                Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            int lastRowNum = sheet.getLastRowNum();

            // Create a new row for the transaction
            Row newRow = sheet.createRow(lastRowNum + 1);
            newRow.createCell(0).setCellValue(fromUrl);
            newRow.createCell(1).setCellValue(dateTime);
            newRow.createCell(2).setCellValue(payload);
            newRow.createCell(3).setCellValue(response);
            newRow.createCell(4).setCellValue(verified);
            // Write changes back to the Excel file
            try (FileOutputStream fos = new FileOutputStream(new File(databasePath))) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCellStringValue(Row row, int rowIndex) {
        Cell cell = row.getCell(rowIndex);
        if (cell == null) {
            return null; // Return null if the cell is null
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue(); // Return string value
            case BLANK:
            default:
                return null; // Return null for blank cells or unsupported types
        }
    }

    public static double getCellNumericValue(Row row, int rowIndex) {
        Cell cell = row.getCell(rowIndex);
        if (cell == null) {
            return -1; // Return null if the cell is null
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue(); // Return numeric value
            case BLANK:
            default:
                return -1; // Return null for blank cells or unsupported types
        }
    }
}
