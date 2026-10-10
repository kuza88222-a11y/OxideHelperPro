package com.oxide.helper

import android.app.PictureInPictureParams
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object CodeDatabase {
    val smartDatabase: List<String> = buildList {
        addAll(listOf("1234", "0000", "1111", "1212", "1004", "1313", "1010", "1221", "1231", "1122",
                      "1324", "1414", "1515", "2000", "2024", "2025", "2121", "2222", "2323", "2424"))
        for (i in 0..99) {
            val pair = i.toString().padStart(2, '0')
            add("$pair$pair")
        }
        addAll(listOf("0123", "1234", "2345", "3456", "4567", "5678", "6789", "9876", "8765", "4321"))
        for (i in 0..9999) {
            val code = i.toString().padStart(4, '0')
            if (!contains(code)) add(code)
        }
    }.distinct()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OxideHelperApp() }
    }

    fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder().setAspectRatio(Rational(1, 1)).build()
            enterPictureInPictureMode(params)
        }
    }
}

@Composable
fun OxideHelperApp() {
    val context = LocalContext.current
    val database = remember { CodeDatabase.smartDatabase }
    var currentIndex by remember { mutableIntStateOf(0) }
    val triedCodes = remember { mutableStateListOf<String>() }
    
    val availableCodes = remember(database, triedCodes) {
        database.filter { it !in triedCodes }
    }

    val currentCode = availableCodes.getOrNull(currentIndex) ?: "ГОТОВО"

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("OXIDE HELPER PRO", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = currentCode, fontSize = 64.sp, fontWeight = FontWeight.Bold, letterSpacing = 8.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Осталось в базе: ${availableCodes.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            if (availableCodes.isNotEmpty()) {
                                triedCodes.add(currentCode)
                                currentIndex = 0
                                vibratePhone(context)
                            }
                        },
                        modifier = Modifier.height(60.dp).width(160.dp)
                    ) {
                        Text("СЛЕДУЮЩИЙ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { (context as? MainActivity)?.enterPipMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.height(60.dp).width(80.dp)
                    ) {
                        Text("PiP", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

fun vibratePhone(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(50)
    }
}
