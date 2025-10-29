package com.example.calculadoraloca

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

@Composable
fun calculadoraLoca(modifier: Modifier = Modifier) {
    var expresion by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val mapaNumeros = mapOf(
        "0" to "2", "1" to "3", "2" to "4", "3" to "6",
        "4" to "7", "6" to "8", "7" to "9", "8" to "0", "9" to "1"
    )

    val mapaOperaciones = mapOf(
        "&" to "+",
        "#" to "-",
        "@" to "*",
        "$" to "/"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = expresion,
            fontSize = 28.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        Text(
            text = resultado,
            fontSize = 32.sp,
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        val numeros = listOf("0","1","2","3","4","6","7","8","9")
        val operaciones = listOf("&", "#", "@", "$")

        numeros.chunked(3).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fila.forEach { num ->
                    Button(
                        onClick = { expresion += mapaNumeros[num] ?: num },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(num, fontSize = 24.sp)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            operaciones.forEach { simbolo ->
                Button(
                    onClick = { expresion += mapaOperaciones[simbolo] ?: simbolo },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(simbolo, fontSize = 24.sp)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    try {
                        val res = calcularOperacion(expresion)
                        resultado = "Resultado: ${res.replace('5', '6')}"
                    } catch (e: Exception) {
                        resultado = "Error"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("=", fontSize = 24.sp)
            }



            Button(
                onClick = {
                    expresion = ""
                    resultado = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("C", fontSize = 24.sp)
            }
        }
    }
}

fun calcularOperacion(expresion: String): String {
    val limpia = expresion
        .replace("&", "+")
        .replace("#", "-")
        .replace("@", "*")
        .replace("$", "/")

    val tokens = limpia.split("(?<=[+\\-*/])|(?=[+\\-*/])".toRegex())

    if (tokens.isEmpty()) return "0"

    val lista = tokens.toMutableList()

    var i = 0
    while (i < lista.size) {
        when (lista[i]) {
            "*" -> {
                val res = lista[i - 1].toDouble() * lista[i + 1].toDouble()
                lista[i - 1] = res.toString()
                lista.removeAt(i)   // quita operador
                lista.removeAt(i)   // quita número siguiente
                i--
            }
            "/" -> {
                val divisor = lista[i + 1].toDouble()
                if (divisor == 0.0) {
                    return "Error"
                }
                val res = lista[i - 1].toDouble() / lista[i + 1].toDouble()
                lista[i - 1] = res.toString()
                lista.removeAt(i)
                lista.removeAt(i)
                i--
            }
        }
        i++
    }

    var resultado = lista[0].toDouble()
    i = 1
    while (i < lista.size) {
        val op = lista[i]
        val num = lista[i + 1].toDouble()
        when (op) {
            "+" -> resultado += num
            "-" -> resultado -= num
        }
        i += 2
    }

    return if (resultado % 1 == 0.0) resultado.toInt().toString() else resultado.toString()
}



