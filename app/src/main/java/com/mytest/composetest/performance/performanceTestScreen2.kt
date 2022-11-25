package com.mytest.composetest.performance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mytest.composetest.MainScreen
import com.mytest.composetest.ui.theme.ComposeTestTheme

@Composable
fun ContactList(
    modifier: Modifier = Modifier,
    contacts: List<ContactModel>,
    comparator: Comparator<ContactModel>? = null,
    headerText: String
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp)
    ) {
        item {
            Text(headerText)
            Divider()
        }

        items(contacts.sortedBy { it.id }) { item ->
            Text(item.name)
            Divider(modifier = Modifier.padding(5.dp))
        }
    }
}

data class ContactModel(val id: Int, val name: String)

@Composable
fun DeriveStateOfSample(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    val submitEnabled = username.isNotEmpty()
//    val submitEnabled by remember {
//        derivedStateOf { username.isNotEmpty() }
//    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        TextField(modifier = Modifier.fillMaxWidth(), value = username, onValueChange = { username = it })
        Button(
            onClick = { }, enabled = submitEnabled, modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun DeriveStateOfSample2(modifier: Modifier = Modifier) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val mergedText = firstName +  " " + lastName

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Row {

            OutlinedTextField(modifier = Modifier.weight(1f),
                value = firstName,
                label = { Text("First Name") },
                onValueChange = { firstName = it })
            OutlinedTextField(modifier = Modifier.weight(1f),
                value = lastName,
                label = { Text("Last Name") },
                onValueChange = { lastName = it })
        }

        Divider(modifier = Modifier.padding(10.dp))
        Text(mergedText, modifier= Modifier.align(Alignment.CenterHorizontally))
    }
}


@Preview(showBackground = true)
@Composable
fun PerformanceDefaultPreview() {
    ComposeTestTheme {
        DeriveStateOfSample()
    }
}