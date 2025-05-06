package com.example.datossinmvvm.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.datossinmvvm.User
import com.example.datossinmvvm.UserDao
import com.example.datossinmvvm.UserDatabase
import kotlinx.coroutines.launch

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = remember { crearDatabase(context) }
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        TextField(
            value = id,
            onValueChange = { id = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            singleLine = true
        )

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val user = User(firstName = firstName, lastName = lastName)
            coroutineScope.launch {
                agregarUsuario(user, dao)
            }
            firstName = ""
            lastName = ""
        }) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                dataUser.value = getUsers(dao)
            }
        }) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))

        Text(text = dataUser.value, fontSize = 18.sp)
    }
}

fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun getUsers(dao: UserDao): String {
    val users = dao.getAll()
    return users.joinToString(separator = "\n") { "${it.firstName} - ${it.lastName}" }
}

suspend fun agregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error insertando: ${e.message}")
    }
}
