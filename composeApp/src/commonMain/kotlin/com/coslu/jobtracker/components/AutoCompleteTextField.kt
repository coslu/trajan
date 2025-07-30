package com.coslu.jobtracker.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    value: MutableState<String>,
    modifier: Modifier,
    autoCompleteMap: Map<String, Int>,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { value }
    var textFileValue by remember { mutableStateOf(TextFieldValue(text)) }
    val list = autoCompleteMap.toList().sortedByDescending { it.second }
    var filteredList = list.filter {
        it.first.startsWith(
            textFileValue.text,
            ignoreCase = true
        ) && it.first.isNotEmpty()
    }.take(3)
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        TextField(
            value = textFileValue,
            label = { Text(label) },
            modifier = modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            onValueChange = {
                textFileValue = it
                text = it.text
                filteredList = list.filter { item ->
                    item.first.startsWith(
                        textFileValue.text,
                        ignoreCase = true
                    ) && item.first.isNotEmpty()
                }.take(3)
                expanded = true
            },
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded && filteredList.isNotEmpty(),
            { expanded = false },
        ) {
            filteredList.forEach {
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                        text = it.first
                        textFileValue = TextFieldValue(text, selection = TextRange(text.length))
                        expanded = false
                    },
                    text = { BigProperty(it.first) }
                )
            }
        }
    }
}