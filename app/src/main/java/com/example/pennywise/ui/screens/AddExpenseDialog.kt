package com.example.pennywise.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, merchant: String, category: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Bills",
        "Entertainment",
        "Health",
        "Groceries",
        "General",
        "Cash"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val metallicGold = Color(0xFFD4AF37)
    val darkOlive = Color(0xFF1B2615)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = darkOlive,
        title = { Text(text = "Add Manual Expense", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount", color = Color.White.copy(alpha = 0.7f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = metallicGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = metallicGold
                    ),
                    textStyle = TextStyle(color = Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant", color = Color.White.copy(alpha = 0.7f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = metallicGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = metallicGold
                    ),
                    textStyle = TextStyle(color = Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category", color = Color.White.copy(alpha = 0.7f)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = metallicGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = metallicGold
                        ),
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedCategory = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    onConfirm(amountDouble, merchant, selectedCategory)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = metallicGold,
                    contentColor = Color.Black
                )
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = metallicGold)
            }
        }
    )
}
