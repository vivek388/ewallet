package com.antgroup.ewallet.service

import com.antgroup.ewallet.model.entity.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

@Service
class ExcelService {
    val allUserData: List<User>
        get() {
            val users: MutableList<User> =
                ArrayList()

            try {
                FileInputStream(File(databasePath)).use { fis ->
                    WorkbookFactory.create(fis).use { workbook ->
                        val sheet =
                            workbook.getSheet(User.Companion.sheet)
                        for (rowIndex in 1..sheet.lastRowNum) {
                            val row = sheet.getRow(rowIndex)
                            val user = User()
                            user.setId(getCellNumericValue(row, 0))
                            user.username = getCellStringValue(row, 1)
                            user.password = getCellStringValue(row, 2)
                            user.balance = getCellNumericValue(row, 3)
                            users.add(user)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return users
        }

    fun UserExist(username: String, password: String): Long {
        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet(User.Companion.sheet)
                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex)
                        val cellUsername = row.getCell(1)
                        val cellPassword = row.getCell(2)

                        if (cellUsername.stringCellValue == username &&
                            cellPassword.stringCellValue == password
                        ) {
                            val dId = getCellNumericValue(row, 0)
                            return dId.toLong() // A welcome page or some success response
                        }
                    }
                    workbook.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return 0L
    }

    fun getUserById(id: String?): User? {
        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet(User.Companion.sheet)
                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex)
                        val cellId = row.getCell(0)
                        val stringId = cellId.numericCellValue.toLong().toString()

                        if (stringId == id) {
                            val user = User()
                            user.setId(cellId.numericCellValue)
                            user.balance = getCellNumericValue(row, 3)
                            return user // A welcome page or some success response
                        }
                    }
                    workbook.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun getTransactionByPaymentId(id: Double): Transaction? {
        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet("Transactions") // Change this to your actual sheet name
                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex)

                        val currId = getCellNumericValue(row, 0)
                        if (id == currId) {
                            val dateTime = getCellStringValue(row, 1)
                            val customerId = getCellStringValue(row, 2)
                            val amount = getCellNumericValue(row, 3)
                            val details = getCellStringValue(row, 4)
                            val statusCode = getCellStringValue(row, 5)
                            val status = getCellStringValue(row, 6)
                            val statusMessage = getCellStringValue(row, 7)
                            val paymentRequestId = getCellStringValue(row, 8)
                            val payCurrency = getCellStringValue(row, 9)
                            val payAmount = getCellStringValue(row, 10)
                            val payToCurrency = getCellStringValue(row, 11)
                            val payToAmount = getCellStringValue(row, 12)
                            val paymentTime = getCellStringValue(row, 13)
                            val quoteId = getCellStringValue(row, 14)
                            val quotePair = getCellStringValue(row, 15)
                            val quotePrice = getCellStringValue(row, 16)
                            val pspId = getCellStringValue(row, 17)
                            val acquirerId = getCellStringValue(row, 18)
                            val promoJson = getCellStringValue(row, 19)
                            val refundId = getCellStringValue(row, 20)

                            val transaction = Transaction(
                                id,
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateTime), customerId, amount,
                                details, statusCode, status, statusMessage, paymentRequestId, payCurrency, payAmount,
                                payToCurrency, payToAmount, paymentTime, quoteId, quotePair, quotePrice, pspId,
                                acquirerId, promoJson, refundId
                            )

                            return transaction
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getAlipayTransactions(paymentRequestId: String, pspId: String, acquirerId: String): List<Transaction>? {
        val transactions: MutableList<Transaction> = ArrayList()

        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet("Transactions") // Change this to your actual sheet name
                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex)

                        if (paymentRequestId == getCellStringValue(row, 8)) {
                            if (pspId == getCellStringValue(row, 17) && acquirerId == getCellStringValue(row, 18)) {
                                val id = getCellNumericValue(row, 0)
                                val dateTime = getCellStringValue(row, 1)
                                val customerId = getCellStringValue(row, 2)
                                val amount = getCellNumericValue(row, 3)
                                val details = getCellStringValue(row, 4)
                                val statusCode = getCellStringValue(row, 5)
                                val status = getCellStringValue(row, 6)
                                val statusMessage = getCellStringValue(row, 7)
                                val payCurrency = getCellStringValue(row, 9)
                                val payAmount = getCellStringValue(row, 10)
                                val payToCurrency = getCellStringValue(row, 11)
                                val payToAmount = getCellStringValue(row, 12)
                                val paymentTime = getCellStringValue(row, 13)
                                val quoteId = getCellStringValue(row, 14)
                                val quotePair = getCellStringValue(row, 15)
                                val quotePrice = getCellStringValue(row, 16)
                                val promoJson = getCellStringValue(row, 19)
                                val refundId = getCellStringValue(row, 20)

                                val transaction = Transaction(
                                    id,
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateTime),
                                    customerId,
                                    amount,
                                    details,
                                    statusCode,
                                    status,
                                    statusMessage,
                                    paymentRequestId,
                                    payCurrency,
                                    payAmount,
                                    payToCurrency,
                                    payToAmount,
                                    paymentTime,
                                    quoteId,
                                    quotePair,
                                    quotePrice,
                                    pspId,
                                    acquirerId,
                                    promoJson,
                                    refundId
                                )

                                transactions.add(transaction)
                            }
                        }
                    }
                    return transactions
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun getLatestTransactions(customerId: String, count: Int): List<Transaction> {
        val transactions: MutableList<Transaction> = ArrayList()

        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet(Transaction.Companion.sheet)
                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex)

                        if (customerId == getCellStringValue(row, 2)) {
                            val id = getCellNumericValue(row, 0)
                            val dateTime = getCellStringValue(row, 1)
                            val amount = getCellNumericValue(row, 3)
                            val details = getCellStringValue(row, 4)
                            val statusCode = getCellStringValue(row, 5)
                            val status = getCellStringValue(row, 6)
                            val statusMessage = getCellStringValue(row, 7)
                            val paymentRequestId = getCellStringValue(row, 8)
                            val payCurrency = getCellStringValue(row, 9)
                            val payAmount = getCellStringValue(row, 10)
                            val payToCurrency = getCellStringValue(row, 11)
                            val payToAmount = getCellStringValue(row, 12)
                            val paymentTime = getCellStringValue(row, 13)
                            val quoteId = getCellStringValue(row, 14)
                            val quotePair = getCellStringValue(row, 15)
                            val quotePrice = getCellStringValue(row, 16)
                            val pspId = getCellStringValue(row, 17)
                            val acquirerId = getCellStringValue(row, 18)
                            val promoJson = getCellStringValue(row, 19)
                            val refundId = getCellStringValue(row, 20)

                            val transaction = Transaction(
                                id,
                                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(dateTime), customerId, amount,
                                details, statusCode, status, statusMessage, paymentRequestId, payCurrency, payAmount,
                                payToCurrency, payToAmount, paymentTime, quoteId, quotePair, quotePrice, pspId,
                                acquirerId, promoJson, refundId
                            )
                            transactions.add(transaction)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val customerTransactions: MutableList<Transaction> = ArrayList()

        // Filter transactions by customerId
        for (transaction in transactions) {
            if (transaction.customerId == customerId) {
                customerTransactions.add(transaction)
            }
        }

        // Sort by dateTime in descending order (latest first)
        Collections.sort(
            customerTransactions
        ) { t1, t2 -> t2.dateTime.compareTo(t1.dateTime) }

        // Return the latest 3 transactions
        return if (customerTransactions.size > count) customerTransactions.subList(0, count) else customerTransactions
    }

    fun addTransaction(
        customerId: String?, amount: Double, details: String?,
        statusCode: String?, status: String?, statusMessage: String?, paymentRequestId: String?, paymentTime: String?,
        paymentAmount: BasePayment?, payToAmount: BasePayment?, quote: PaymentQuote?, pspId: String?,
        acquirerId: String?, promoJson: String?, refundRequestId: String?
    ): Double {
        val transactionSheetName: String = Transaction.Companion.sheet // Change this to your actual sheet name
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = Date()
        val dateTime = sdf.format(date)

        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val userSheet = workbook.getSheet(User.Companion.sheet)
                    for (rowIndex in 1..userSheet.lastRowNum) {
                        val row = userSheet.getRow(rowIndex)
                        val cellId = row.getCell(0)
                        val stringId = cellId.numericCellValue.toLong().toString()

                        if (stringId == customerId) {
                            val balanceCell = row.getCell(3)
                            val currentBalance = balanceCell.numericCellValue
                            val originalBigDecimal = BigDecimal.valueOf(currentBalance)
                            val additionBigDecimal = BigDecimal.valueOf(amount)

                            // Perform the subtraction
                            val newBigDecimal = originalBigDecimal.add(additionBigDecimal)

                            // Convert back to double
                            val newAmount = newBigDecimal.toDouble()
                            balanceCell.setCellValue(newAmount)
                            break
                        }
                    }

                    val sheet = workbook.getSheet(transactionSheetName)
                    val lastRowNum = sheet.lastRowNum

                    var nextId = 1.0 // Start with 1 if there are no transactions
                    try {
                        if (lastRowNum > 0) {
                            val lastRow = sheet.getRow(lastRowNum)
                            val lastId = getCellNumericValue(lastRow, 0)
                            nextId = lastId + 1
                        }
                    } catch (e: Exception) {
                    }

                    // Create a new row for the transaction
                    val newRow = sheet.createRow(lastRowNum + 1)
                    newRow.createCell(0).setCellValue(nextId) // Transaction ID
                    newRow.createCell(1).setCellValue(dateTime) // Date and Time
                    newRow.createCell(2).setCellValue(customerId) // Customer ID
                    newRow.createCell(3).setCellValue(amount) // Amount
                    newRow.createCell(4).setCellValue(details) // Details
                    newRow.createCell(5).setCellValue(statusCode)
                    newRow.createCell(6).setCellValue(status)
                    newRow.createCell(7).setCellValue(statusMessage)
                    newRow.createCell(8).setCellValue(paymentRequestId)
                    if (paymentAmount != null) {
                        newRow.createCell(9).setCellValue(paymentAmount.currency)
                        newRow.createCell(10).setCellValue(paymentAmount.value)
                    }
                    if (payToAmount != null) {
                        newRow.createCell(11).setCellValue(payToAmount.currency)
                        newRow.createCell(12).setCellValue(payToAmount.value)
                    }
                    newRow.createCell(13).setCellValue(paymentTime)
                    if (quote != null) {
                        newRow.createCell(14).setCellValue(quote.quoteId)
                        newRow.createCell(15).setCellValue(quote.quoteCurrencyPair)
                        newRow.createCell(16).setCellValue(quote.quotePrice)
                    }

                    if (pspId != null) {
                        newRow.createCell(17).setCellValue(pspId)
                    }

                    if (acquirerId != null) {
                        newRow.createCell(18).setCellValue(acquirerId)
                    }

                    if (promoJson != null) {
                        newRow.createCell(19).setCellValue(promoJson)
                    }

                    if (refundRequestId != null) {
                        newRow.createCell(20).setCellValue(refundRequestId)
                    }

                    FileOutputStream(File(databasePath)).use { fos ->
                        workbook.write(fos)
                    }
                    return nextId
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return -1.0
    }

    fun cancelTransaction(paymentRequestId: String) {
        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet(Transaction.Companion.sheet)
                    var accumulatedAmount = BigDecimal.ZERO

                    var customerId: String? = "0"

                    for (rowIndex in 1..sheet.lastRowNum) {
                        val row = sheet.getRow(rowIndex)

                        if (paymentRequestId == getCellStringValue(row, 8)) {
                            customerId = getCellStringValue(row, 2)
                            val amount = getCellNumericValue(row, 3)
                            val addAmount = BigDecimal.valueOf(amount)
                            accumulatedAmount = accumulatedAmount.add(addAmount)
                            val amountCell = row.getCell(3)
                            amountCell.setCellValue(0.0)

                            val detailsCell = row.getCell(4)
                            detailsCell.setCellValue("[Canceled] " + getCellStringValue(row, 4))

                            row.getCell(5).setCellValue("ORDER_IS_CLOSED")

                            row.getCell(6).setCellValue("F")

                            row.getCell(7).setCellValue("The order is closed.")
                        }
                    }

                    val userSheet = workbook.getSheet(User.Companion.sheet)

                    for (rowIndex in 1..userSheet.lastRowNum) {
                        val row = userSheet.getRow(rowIndex)
                        val cellId = row.getCell(0)
                        val stringId = cellId.numericCellValue.toLong().toString()

                        if (stringId == customerId) {
                            val balanceCell = row.getCell(3)
                            val currentBalance = balanceCell.numericCellValue
                            val originalBigDecimal = BigDecimal.valueOf(currentBalance)

                            // Perform the subtraction
                            val newBigDecimal = originalBigDecimal.subtract(accumulatedAmount)

                            // Convert back to double
                            val newAmount = newBigDecimal.toDouble()
                            balanceCell.setCellValue(newAmount)
                            break
                        }
                    }
                    FileOutputStream(File(databasePath)).use { fos ->
                        workbook.write(fos)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun addApiCallLog(fromUrl: String?, payload: String?, response: String?, verified: Boolean) {
        val sheetName = "ApiCall"
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = Date()
        val dateTime = sdf.format(date)

        try {
            FileInputStream(File(databasePath)).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheet(sheetName)
                    val lastRowNum = sheet.lastRowNum

                    // Create a new row for the transaction
                    val newRow = sheet.createRow(lastRowNum + 1)
                    newRow.createCell(0).setCellValue(fromUrl)
                    newRow.createCell(1).setCellValue(dateTime)
                    newRow.createCell(2).setCellValue(payload)
                    newRow.createCell(3).setCellValue(response)
                    newRow.createCell(4).setCellValue(verified)
                    FileOutputStream(File(databasePath)).use { fos ->
                        workbook.write(fos)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val databasePath = System.getProperty("user.dir") + "/database/Database.xlsx"

        fun getCellStringValue(row: Row, rowIndex: Int): String? {
            val cell = row.getCell(rowIndex)
                ?: return null // Return null if the cell is null

            return when (cell.cellType) {
                CellType.STRING -> cell.stringCellValue // Return string value
                CellType.BLANK -> null // Return null for blank cells or unsupported types
                else -> null
            }
        }

        fun getCellNumericValue(row: Row, rowIndex: Int): Double {
            val cell = row.getCell(rowIndex)
                ?: return -1.0 // Return null if the cell is null

            return when (cell.cellType) {
                CellType.NUMERIC -> cell.numericCellValue // Return numeric value
                CellType.BLANK -> -1.0 // Return null for blank cells or unsupported types
                else -> -1.0
            }
        }
    }
}
