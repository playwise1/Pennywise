package com.example.pennywise.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.pennywise.data.AppDatabase
import com.example.pennywise.data.Expense
import com.example.pennywise.util.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SmsReceiver", "Message Received")
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            messages?.forEach { sms ->
                val sender = sms.originatingAddress
                val messageBody = sms.messageBody
                
                Log.d("SmsReceiver", "Received SMS from $sender: $messageBody")

                // Filter for likely bank SMS (scan for keywords if sender is generic? Usually sender is like "HDFCBK" etc)
                // For now, let's just parse everything that matches the pattern.
                
                val parsed = SmsParser.parseSms(messageBody)
                if (parsed != null) {
                    val db = AppDatabase.getDatabase(context)
                    val dao = db.expenseDao()
                    
                    val expense = Expense(
                        amount = parsed.amount,
                        merchant = parsed.merchant,
                        timestamp = System.currentTimeMillis(),
                        category = parsed.category,
                        rawMessage = messageBody
                    )
                    
                    val pendingResult = goAsync()
                    scope.launch {
                        try {
                            dao.insertExpense(expense)
                            Log.d("SmsReceiver", "Expense saved: $expense")
                        } catch (e: Exception) {
                            Log.e("SmsReceiver", "Error saving expense", e)
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
            }
        }
    }
}
