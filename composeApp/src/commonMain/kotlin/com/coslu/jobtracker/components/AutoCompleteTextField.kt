package com.coslu.jobtracker.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterialApi::class)
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
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            value = textFileValue,
            label = { Text(label) },
            modifier = modifier,
            onValueChange = {
                textFileValue = it
                if (it.text != text)
                    expanded = true
                text = it.text
            },
            singleLine = true
        )
        DropdownMenu(
            expanded,
            { expanded = false },
            properties = PopupProperties(focusable = false)
        ) {
            list.filter {
                it.first.startsWith(
                    textFileValue.text,
                    ignoreCase = true
                ) && it.first.isNotEmpty()
            }.take(3).forEach {
                DropdownMenuItem(
                    modifier = Modifier.padding(5.dp),
                    onClick = {
                        text = it.first
                        textFileValue = TextFieldValue(text, selection = TextRange(text.length))
                        expanded = false
                    }
                ) {
                    BigProperty(it.first)
                }
            }
        }
    }
}