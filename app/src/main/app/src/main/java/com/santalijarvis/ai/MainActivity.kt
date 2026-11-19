package com.santalijarvis.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(val text: String, val isUser: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                SantaliJarvisScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SantaliJarvisScreen() {
    val languages = listOf("Santali", "Bengali", "Hindi", "English")
    var selectedLanguage by remember { mutableStateOf("Santali") }
    var inputText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    text = "ᱡᱚᱦᱟᱨ! ᱤᱧ SANTALI JARVIS AI ᱠᱟᱱᱟᱧ। ᱪᱮᱫ ᱤᱧ ᱜᱚᱲᱚ ᱫᱟᱲᱮᱭᱟᱢᱟ?",
                    isUser = false
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SANTALI JARVIS AI", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Language: $selectedLanguage", fontSize = 12.sp, color = Color(0xFF00E5FF))
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        languages.forEach { lang ->
                            FilterChip(
                                selected = selectedLanguage == lang,
                                onClick = { selectedLanguage = lang },
                                label = { Text(lang.take(3), fontSize = 11.sp) },
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D1117))
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            color = if (msg.isUser) Color(0xFF1F6FEB) else Color(0xFF161B22),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("ᱚᱞ ᱢᱮ / বার্তা লিখুন...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMsg = inputText
                            inputText = ""
                            val reply = when (selectedLanguage) {
                                "Santali" -> "[$selectedLanguage] ᱟᱢᱟᱜ ᱠᱟᱛᱷᱟ ᱧᱟᱢ ᱮᱱᱟ: \"$userMsg\" (AI API ᱡᱚᱲᱟᱣ ᱛᱟᱭᱚᱢ ᱯᱩᱨᱟᱹ ᱛᱮᱞᱟ ᱧᱟᱢᱚᱜᱼᱟ)"
                                "Bengali" -> "[$selectedLanguage] আপনার বার্তা পাওয়া গেছে: \"$userMsg\" (Gemini API যুক্ত হলে আসল উত্তর মিলবে)"
                                "Hindi" -> "[$selectedLanguage] आपका संदेश मिला: \"$userMsg\" (AI जुड़ने पर पूरा जवाब मिलेगा)"
                                else -> "[$selectedLanguage] Received: \"$userMsg\" (Full AI reply coming in next step)"
                            }
                            messages = messages + ChatMessage(userMsg, true) + ChatMessage(reply, false)
                        }
                    },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Send")
                }
            }
        }
    }
}
