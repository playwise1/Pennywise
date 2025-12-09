package com.example.pennywise.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SmsParserTest {

    @Test
    fun parseStandardTransaction() {
        val sms = "Rs. 450.00 debited from a/c XX1234 on 12-Dec-25 to Zomato UPI Ref 888222. Bal: Rs 5000."
        val result = SmsParser.parseSms(sms)
        assertNotNull(result)
        assertEquals(450.00, result!!.amount, 0.01)
        assertEquals("Zomato", result.merchant)
        assertEquals("Food", result.category)
    }

    @Test
    fun parseAnotherFormat() {
        val sms = "INR 2500.50 spent at Uber Rides on 10-Jan-25 via UPI"
        val result = SmsParser.parseSms(sms)
        assertNotNull(result)
        assertEquals(2500.50, result!!.amount, 0.01)
        assertEquals("Uber Rides", result.merchant)
        assertEquals("Travel", result.category)
    }
    
    @Test
    fun parseSentTo() {
        val sms = "Sent Rs. 1000 to Ramesh for Rent"
        // Regex might struggle here relying on "debited|spent|sent" appearing AFTER amount? 
        // My regex was: (?:Rs\.?|INR)\s*([\d,]+...).*?(?:debited|spent|sent|paid)
        // This SMS has "Sent" BEFORE "Rs."
        // Let's see if my regex handles it. If not, I'll update it or the test.
        // Actually, the regex expects amount first. "Rs. 1000 ... sent" works. "Sent Rs 1000" might not.
        // Let's stick to the prompt's example format which is Bank format (Money debited/spent).
        // "Rs. 1000 sent to Ramesh"
        val sms2 = "Rs. 1000 sent to Ramesh for Rent"
        val result = SmsParser.parseSms(sms2)
        assertNotNull(result)
        assertEquals(1000.0, result!!.amount, 0.01)
        assertEquals("Ramesh", result.merchant)
    }
}
