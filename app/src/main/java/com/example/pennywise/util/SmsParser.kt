package com.example.pennywise.util

import com.example.pennywise.data.Expense
import java.util.regex.Pattern

object SmsParser {

    fun parseSms(body: String): Expense? {
        // 1. Initial Filter: Only process messages about money leaving
        // We look for keywords like "Sent", "Debited", "Spent", "Paid", or "Trxn"
        if (!body.contains("Sent", ignoreCase = true) &&
            !body.contains("Debited", ignoreCase = true) &&
            !body.contains("Spent", ignoreCase = true) &&
            !body.contains("Paid", ignoreCase = true) &&
            !body.contains("Trxn", ignoreCase = true) &&
            !body.contains("withdrawn", ignoreCase = true)
        ) {
            return null
        }

        // 2. Extract Amount
        // Finds "Rs" or "INR" followed by digits (e.g., "Rs. 200.00", "INR 50")
        // Handles optional dots, spaces, and commas
        val amountRegex = Pattern.compile("(?i)(?:Rs|INR)[\\s.]*([\\d,]+(?:\\.\\d{2})?)")
        val amountMatcher = amountRegex.matcher(body)

        var amount = 0.0
        if (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "") ?: "0"
            amount = amountStr.toDoubleOrNull() ?: 0.0
        } else {
            return null // No money found, ignore message
        }

        // 3. Extract Merchant Logic
        var merchant = "Unknown"
        var category = "General"

        // Handle ATM Withdrawals first
        if (body.contains("withdrawn", ignoreCase = true) && body.contains("ATM", ignoreCase = true)) {
            merchant = "ATM Withdrawal"
            category = "Cash"
        } else {
            // Strategy: Look for the text after "to", "at", or "via"
            // It captures text until it hits a "stop word" like "on", "from", "Ref", "ending", etc.
            val merchantRegex = Pattern.compile("(?i)(?:to|at|via)\\s+([a-zA-Z0-9._@\\s&-]+)")
            val merchantMatcher = merchantRegex.matcher(body)

            // We iterate through matches to find the best candidate.
            // For "Debited ... from Kotak ... to Vyapar", we want the "to" match, not "from".
            while (merchantMatcher.find()) {
                val candidate = merchantMatcher.group(1)?.trim() ?: ""

                // Filter out common banking words that might be mistaken for merchants
                if (candidate.lowercase() !in listOf("kotak", "bank", "ac", "account", "credit", "debit", "upi")) {
                    merchant = candidate
                    // If we found a valid-looking merchant, stop looking (usually the last 'to' is the receiver)
                    // However, in "from Kotak to Vyapar", the second match is the winner.
                }
            }

            // 4. Cleanup Merchant Name
            merchant = cleanMerchantName(merchant)

            // 5. Categorize
            category = getCategory(merchant, body)
        }

        // 6. Return Expense Object (Use your actual Expense entity class here)
        // Note: I am returning an Expense object directly as requested by your earlier prompts.
        // If you need ParsedExpense, just change the return type.
        return Expense(
            amount = amount,
            merchant = merchant,
            category = category,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun cleanMerchantName(rawName: String): String {
        var name = rawName

        // Stop at common delimiters that appear after merchant names
        val stopWords = listOf(" on ", " from ", " ref ", " via ", " bal ", " ending ", ".upi", " not ")
        for (word in stopWords) {
            if (name.contains(word, ignoreCase = true)) {
                name = name.substringBefore(word)
            }
        }

        // If it looks like a UPI ID (e.g., "vyapar.123@hdfc"), take only the part before '@'
        if (name.contains("@")) {
            name = name.substringBefore("@")
        }

        // Remove dots if they are separator chars (like in "vyapar.1702...")
        name = name.replace(".", " ")

        // Capitalize words
        return name.trim().split(" ").joinToString(" ") {
            it.lowercase().replaceFirstChar { char -> char.uppercase() }
        }
    }

    private fun getCategory(merchant: String, body: String): String {
        val text = "$merchant $body".lowercase()
        return when {
            text.contains("swiggy") || text.contains("zomato") || text.contains("dominos") || text.contains("mcdonalds") -> "Food"
            text.contains("uber") || text.contains("ola") || text.contains("rapido") || text.contains("petrol") || text.contains("fuel") -> "Transport"
            text.contains("amazon") || text.contains("flipkart") || text.contains("myntra") || text.contains("ajio") -> "Shopping"
            text.contains("jio") || text.contains("airtel") || text.contains("vi") || text.contains("recharge") -> "Bills"
            text.contains("netflix") || text.contains("spotify") || text.contains("movie") -> "Entertainment"
            text.contains("apollo") || text.contains("pharmacy") || text.contains("medplus") -> "Health"
            text.contains("blinkit") || text.contains("bigbasket") || text.contains("zepto") || text.contains("grocery") -> "Groceries"
            else -> "General"
        }
    }
}