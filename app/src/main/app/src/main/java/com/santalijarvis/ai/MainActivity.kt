package com.santalijarvis.ai

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val headerTag: String
)

enum class AiProvider(val label: String, val badge: String) {
    GEMINI("Gemini", "GEMINI 2.5"),
    CHATGPT("ChatGPT", "GPT-4o"),
    CLAUDE("Claude", "CLAUDE 3.7"),
    GROK("Grok", "GROK-3")
}

private val DeepSpace = Color(0xFF030712)
private val HudPanel = Color(0xFF0A1222)
private val UserCard = Color(0xFF172554)
private val NeonCyan = Color(0xFF00F0FF)
private val ElectricBlue = Color(0xFF38BDF8)
private val EmeraldOnline = Color(0xFF10B981)
private val AmberPro = Color(0xFFF59E0B)
private val AlertRed = Color(0xFFEF4444)
private val TextWhite = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = DeepSpace,
                    surface = HudPanel,
                    primary = NeonCyan,
                    onPrimary = Color.Black,
                    onSurface = TextWhite
                )
            ) {
                JarvisMastermindApp()
            }
        }
    }
}

@Composable
fun MasterArcReactor(isActive: Boolean, isElevenLabs: Boolean = false, sizeDp: Int = 48) {
    val infiniteTransition = rememberInfiniteTransition(label = "jarvis_master_core")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isActive) 420 else 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotationDeg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isActive) 3000 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(sizeDp.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f
            val coreColor = when {
                isActive -> EmeraldOnline
                isElevenLabs -> AmberPro
                else -> NeonCyan
            }

            drawCircle(color = coreColor.copy(alpha = 0.18f), radius = r * pulseScale, center = center)
            drawCircle(color = coreColor, radius = r * 0.90f, center = center, style = Stroke(2.dp.toPx()))
            drawCircle(color = ElectricBlue.copy(alpha = 0.65f), radius = r * 0.62f * pulseScale, center = center, style = Stroke(1.5.dp.toPx()))

            for (i in 0 until 6) {
                val angleRad = Math.toRadians((rotationDeg + i * 60f).toDouble())
                val start = Offset(
                    center.x + (r * 0.68f * cos(angleRad)).toFloat(),
                    center.y + (r * 0.68f * sin(angleRad)).toFloat()
                )
                val end = Offset(
                    center.x + (r * 0.86f * cos(angleRad)).toFloat(),
                    center.y + (r * 0.86f * sin(angleRad)).toFloat()
                )
                drawLine(color = coreColor, start = start, end = end, strokeWidth = 2.dp.toPx())
            }

            drawCircle(color = coreColor, radius = r * 0.28f, center = center)
        }
    }
}

fun handleDeviceCommandIfMatched(context: Context, rawPrompt: String): String? {
    val cmd = rawPrompt.trim().lowercase(Locale.ROOT)
    return try {
        when {
            cmd == "open youtube" || cmd == "youtube খোলো" || cmd == "youtube kholo" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                "🚀 YouTube চালু করা হচ্ছে, বস।"
            }
            cmd == "open whatsapp" || cmd == "whatsapp খোলো" || cmd == "whatsapp kholo" -> {
                val launch = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                if (launch != null) {
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launch)
                    "🚀 WhatsApp খোলা হচ্ছে, বস।"
                } else {
                    "WhatsApp ফোনে ইন্সটল করা নেই।"
                }
            }
            cmd.startsWith("search ") || cmd.startsWith("google ") -> {
                val query = rawPrompt.substringAfter(" ").trim()
                val encoded = URLEncoder.encode(query, "UTF-8")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                "🔍 Google-এ \"$query\" খোঁজা হচ্ছে।"
            }
            cmd.startsWith("call ") -> {
                val number = rawPrompt.substringAfter(" ").replace(" ", "")
                if (number.any { it.isDigit() }) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "📞 $number নম্বরে ডায়ালার খোলা হচ্ছে।"
                } else null
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

suspend fun queryMasterAiEngine(
    provider: AiProvider,
    apiKey: String,
    history: List<ChatMessage>,
    userPrompt: String,
    preferredLang: String,
    customPersona: String
): String {
    return withContext(Dispatchers.IO) {
        try {
            val systemInstruction = """
                You are SANTALI JARVIS AI, an elite, mastermind-level personal assistant inspired by Iron Man's JARVIS.
                Primary Language: $preferredLang (Default is Santali).
                Persona Directive: ${customPersona.ifBlank { "Address the user respectfully as Boss. Be sharp, intelligent, accurate, and natural." }}
                Rules:
                1. Reply primarily in $preferredLang. If $preferredLang is Santali, write natural, fluent Santali (Ol Chiki or clear Santali matching the user).
                2. You seamlessly understand Santali, Bengali, Hindi, and English.
                3. Keep answers structured, clear, and avoid excessive markdown asterisks (*) so human voice synthesis sounds natural.
            """.trimIndent()

            val recentHistory = history.takeLast(8)

            when (provider) {
                AiProvider.GEMINI -> {
                    val endpoint = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                    val conn = (endpoint.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        doOutput = true
                        connectTimeout = 15000
                        readTimeout = 25000
                    }

                    val contents = JSONArray()
                    recentHistory.forEach { msg ->
                        contents.put(JSONObject().apply {
                            put("role", if (msg.isUser) "user" else "model")
                            put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                        })
                    }
                    contents.put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
                    })

                    val payload = JSONObject().apply {
                        put("system_instruction", JSONObject().apply {
                            put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                        })
                        put("contents", contents)
                    }

                    conn.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                    val code = conn.responseCode
                    val resp = (if (code in 200..299) conn.inputStream else conn.errorStream).bufferedReader().use { it.readText() }
                    if (code in 200..299) {
                        JSONObject(resp).optJSONArray("candidates")
                            ?.optJSONObject(0)?.optJSONObject("content")
                            ?.optJSONArray("parts")?.optJSONObject(0)
                            ?.optString("text")?.replace("*", "")?.trim() ?: "No response."
                    } else {
                        "Gemini Error ($code): " + (JSONObject(resp).optJSONObject("error")?.optString("message") ?: resp)
                    }
                }

                AiProvider.CHATGPT, AiProvider.GROK -> {
                    val urlStr = if (provider == AiProvider.CHATGPT)
                        "https://api.openai.com/v1/chat/completions"
                    else
                        "https://api.x.ai/v1/chat/completions"
                    val modelName = if (provider == AiProvider.CHATGPT) "gpt-4o-mini" else "grok-3-latest"

                    val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        setRequestProperty("Authorization", "Bearer $apiKey")
                        doOutput = true
                        connectTimeout = 15000
                        readTimeout = 25000
                    }

                    val msgsArray = JSONArray().apply {
                        put(JSONObject().put("role", "system").put("content", systemInstruction))
                        recentHistory.forEach { msg ->
                            put(JSONObject().put("role", if (msg.isUser) "user" else "assistant").put("content", msg.text))
                        }
                        put(JSONObject().put("role", "user").put("content", userPrompt))
                    }

                    val payload = JSONObject().apply {
                        put("model", modelName)
                        put("messages", msgsArray)
                    }

                    conn.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                    val code = conn.responseCode
                    val resp = (if (code in 200..299) conn.inputStream else conn.errorStream).bufferedReader().use { it.readText() }
                    if (code in 200..299) {
                        JSONObject(resp).optJSONArray("choices")
                            ?.optJSONObject(0)?.optJSONObject("message")
                            ?.optString("content")?.replace("*", "")?.trim() ?: "No response."
                    } else {
                        "${provider.label} Error ($code): " + (JSONObject(resp).optJSONObject("error")?.optString("message") ?: resp)
                    }
                }

                AiProvider.CLAUDE -> {
                    val conn = (URL("https://api.anthropic.com/v1/messages").openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                        setRequestProperty("x-api-key", apiKey)
                        setRequestProperty("anthropic-version", "2023-06-01")
                        doOutput = true
                        connectTimeout = 15000
                        readTimeout = 25000
                    }

                    val msgsArray = JSONArray().apply {
                        recentHistory.forEach { msg ->
                            put(JSONObject().put("role", if (msg.isUser) "user" else "assistant").put("content", msg.text))
                        }
                        put(JSONObject().put("role", "user").put("content", userPrompt))
                    }

                    val payload = JSONObject().apply {
                        put("model", "claude-3-7-sonnet-latest")
                        put("max_tokens", 1024)
                        put("system", systemInstruction)
                        put("messages", msgsArray)
                    }

                    conn.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                    val code = conn.responseCode
                    val resp = (if (code in 200..299) conn.inputStream else conn.errorStream).bufferedReader().use { it.readText() }
                    if (code in 200..299) {
                        JSONObject(resp).optJSONArray("content")
                            ?.optJSONObject(0)?.optString("text")?.replace("*", "")?.trim() ?: "No response."
                    } else {
                        "Claude Error ($code): " + (JSONObject(resp).optJSONObject("error")?.optString("message") ?: resp)
                    }
                }
            }
        } catch (e: Exception) {
            "Network Error: ${e.localizedMessage ?: "Check internet connection"}"
        }
    }
}

suspend fun synthesizeElevenLabsAudio(
    cacheDir: File,
    elevenKey: String,
    voiceId: String,
    text: String
): File? {
    return withContext(Dispatchers.IO) {
        try {
            val cleanVoiceId = voiceId.trim().ifBlank { "JBFqnCBsd6RMkjVDRZzb" }
            val url = URL("https://api.elevenlabs.io/v1/text-to-speech/$cleanVoiceId")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("xi-api-key", elevenKey.trim())
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "audio/mpeg")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 25000
            }

            val payload = JSONObject().apply {
                put("text", text.take(550))
                put("model_id", "eleven_multilingual_v2")
                put("voice_settings", JSONObject().apply {
                    put("stability", 0.45)
                    put("similarity_boost", 0.82)
                    put("style", 0.35)
                    put("use_speaker_boost", true)
                })
            }

            conn.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }

            if (conn.responseCode in 200..299) {
                val outFile = File(cacheDir, "jarvis_voice_${System.currentTimeMillis()}.mp3")
                conn.inputStream.use { input ->
                    FileOutputStream(outFile).use { output -> input.copyTo(output) }
                }
                outFile
            } else null
        } catch (_: Exception) {
            null
        }
    }
}

fun saveChatHistory(prefs: android.content.SharedPreferences, messages: List<ChatMessage>) {
    val arr = JSONArray()
    messages.takeLast(50).forEach { msg ->
        arr.put(JSONObject().apply {
            put("text", msg.text)
            put("isUser", msg.isUser)
            put("headerTag", msg.headerTag)
        })
    }
    prefs.edit().putString("saved_chat_history_v2", arr.toString()).apply()
}

fun loadChatHistory(prefs: android.content.SharedPreferences, fallback: ChatMessage): List<ChatMessage> {
    val raw = prefs.getString("saved_chat_history_v2", null) ?: return listOf(fallback)
    return try {
        val arr = JSONArray(raw)
        if (arr.length() == 0) listOf(fallback)
        else (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            ChatMessage(
                text = obj.getString("text"),
                isUser = obj.getBoolean("isUser"),
                headerTag = obj.getString("headerTag")
            )
        }
    } catch (_: Exception) {
        listOf(fallback)
    }
}

@Composable
fun JarvisMastermindApp() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val prefs = remember { context.getSharedPreferences("jarvis_mastermind_vault", Context.MODE_PRIVATE) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    var selectedProvider by remember { mutableStateOf(AiProvider.GEMINI) }
    var geminiKey by remember { mutableStateOf(prefs.getString("key_gemini", "") ?: "") }
    var openaiKey by remember { mutableStateOf(prefs.getString("key_openai", "") ?: "") }
    var claudeKey by remember { mutableStateOf(prefs.getString("key_claude", "") ?: "") }
    var grokKey by remember { mutableStateOf(prefs.getString("key_grok", "") ?: "") }
    var elevenLabsKey by remember { mutableStateOf(prefs.getString("key_elevenlabs", "") ?: "") }
    var elevenVoiceId by remember { mutableStateOf(prefs.getString("eleven_voice_id", "JBFqnCBsd6RMkjVDRZzb") ?: "JBFqnCBsd6RMkjVDRZzb") }
    var customPersona by remember { mutableStateOf(prefs.getString("custom_persona", "") ?: "") }

    var showSettings by remember { mutableStateOf(false) }

    val languages = listOf("Santali", "Bengali", "Hindi", "English")
    var selectedLanguage by remember { mutableStateOf("Santali") }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var liveVoiceMode by remember { mutableStateOf(false) }
    var livePartialText by remember { mutableStateOf("") }
    var autoSpeak by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val liveModeRef = rememberUpdatedState(liveVoiceMode)
    val selectedLangRef = rememberUpdatedState(selectedLanguage)

    var activeRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    fun getCurrentProviderKey(provider: AiProvider): String {
        return when (provider) {
            AiProvider.GEMINI -> geminiKey
            AiProvider.CHATGPT -> openaiKey
            AiProvider.CLAUDE -> claudeKey
            AiProvider.GROK -> grokKey
        }
    }

    fun stopAllAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        tts?.stop()
        isSpeaking = false
    }

    fun stopRecognizer() {
        try {
            activeRecognizer?.stopListening()
            activeRecognizer?.destroy()
        } catch (_: Exception) {}
        activeRecognizer = null
        isListening = false
    }

    lateinit var startListeningCycle: () -> Unit

    fun onVoiceFinishedCallback() {
        isSpeaking = false
        if (liveModeRef.value) {
            mainHandler.postDelayed({
                if (liveModeRef.value) startListeningCycle()
            }, 450)
        }
    }

    fun fallbackDeviceTts(text: String) {
        val locale = when (selectedLangRef.value) {
            "Hindi" -> Locale("hi", "IN")
            "English" -> Locale.US
            else -> Locale("bn", "IN")
        }
        tts?.language = locale
        tts?.speak(text.take(450), TextToSpeech.QUEUE_FLUSH, null, "jarvis_fallback_utt")
    }

    fun speakHumanOrFallback(text: String, forceSpeak: Boolean = false) {
        if (!autoSpeak && !forceSpeak && !liveModeRef.value) {
            if (liveModeRef.value) {
                mainHandler.postDelayed({ if (liveModeRef.value) startListeningCycle() }, 400)
            }
            return
        }
        stopAllAudio()

        if (elevenLabsKey.isNotBlank()) {
            isSpeaking = true
            coroutineScope.launch {
                val audioFile = synthesizeElevenLabsAudio(context.cacheDir, elevenLabsKey, elevenVoiceId, text)
                if (audioFile != null && audioFile.exists()) {
                    try {
                        val mp = MediaPlayer().apply {
                            setDataSource(audioFile.absolutePath)
                            setOnCompletionListener {
                                audioFile.delete()
                                onVoiceFinishedCallback()
                            }
                            setOnErrorListener { _, _, _ ->
                                audioFile.delete()
                                fallbackDeviceTts(text)
                                true
                            }
                            prepare()
                            start()
                        }
                        mediaPlayer = mp
                    } catch (_: Exception) {
                        fallbackDeviceTts(text)
                    }
                } else {
                    fallbackDeviceTts(text)
                }
            }
        } else {
            fallbackDeviceTts(text)
        }
    }

    DisposableEffect(Unit) {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engine?.language = Locale("bn", "IN")
                engine?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { mainHandler.post { isSpeaking = true } }
                    override fun onDone(utteranceId: String?) { mainHandler.post { onVoiceFinishedCallback() } }
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) { mainHandler.post { onVoiceFinishedCallback() } }
                })
            }
        }
        tts = engine
        onDispose {
            stopRecognizer()
            stopAllAudio()
            engine?.shutdown()
        }
    }

    val initialGreeting = remember {
        ChatMessage(
            text = "ᱡᱚᱦᱟᱨ! SANTALI JARVIS MASTERMIND OS ᱥᱟᱯᱲᱟᱣ ᱢᱮᱱᱟᱜᱼᱟ।\nMulti-AI (Gemini, ChatGPT, Claude, Grok), ElevenLabs Human Voice এবং স্মার্ট ডিভাইস কন্ট্রোল সক্রিয় আছে। নির্দেশ দিন, বস!",
            isUser = false,
            headerTag = "JARVIS MASTERMIND • ONLINE"
        )
    }
    var messages by remember { mutableStateOf(loadChatHistory(prefs, initialGreeting)) }

    fun triggerSend(rawText: String, appendUserBubble: Boolean = true) {
        val cleanText = rawText.trim()
        if (cleanText.isEmpty() || isThinking) return
        inputText = ""
        livePartialText = ""

        if (appendUserBubble) {
            messages = messages + ChatMessage(cleanText, isUser = true, headerTag = "OPERATOR")
            saveChatHistory(prefs, messages)
            coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) }
        }

        val deviceActionReply = handleDeviceCommandIfMatched(context, cleanText)
        if (deviceActionReply != null) {
            messages = messages + ChatMessage(deviceActionReply, isUser = false, headerTag = "JARVIS • DEVICE ACTION")
            saveChatHistory(prefs, messages)
            coroutineScope.launch { listState.animateScrollToItem(messages.size - 1) }
            speakHumanOrFallback(deviceActionReply)
            return
        }

        val activeKey = getCurrentProviderKey(selectedProvider)
        if (activeKey.isBlank()) {
            liveVoiceMode = false
            val warn = "⚙️ ${selectedProvider.label}-এর API Key সেট করা নেই। ওপরের Settings (⚙️) বাটনে ট্যাপ করে Key সেভ করুন।"
            messages = messages + ChatMessage(warn, isUser = false, headerTag = "JARVIS • CONFIG REQUIRED")
            showSettings = true
            return
        }

        isThinking = true
        coroutineScope.launch {
            val aiReply = queryMasterAiEngine(
                provider = selectedProvider,
                apiKey = activeKey,
                history = messages.dropLast(1),
                userPrompt = cleanText,
                preferredLang = selectedLangRef.value,
                customPersona = customPersona
            )
            isThinking = false
            val voiceBadge = if (elevenLabsKey.isNotBlank()) " • HUMAN VOICE" else ""
            messages = messages + ChatMessage(
                text = aiReply,
                isUser = false,
                headerTag = "${selectedProvider.badge} • ${selectedLangRef.value.uppercase()}$voiceBadge"
            )
            saveChatHistory(prefs, messages)
            listState.animateScrollToItem(messages.size - 1)
            speakHumanOrFallback(aiReply)
        }
    }

    startListeningCycle = {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Speech recognition unavailable", Toast.LENGTH_SHORT).show()
            liveVoiceMode = false
        } else {
            stopRecognizer()
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            activeRecognizer = recognizer
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                val langCode = when (selectedLangRef.value) {
                    "Hindi" -> "hi-IN"
                    "English" -> "en-IN"
                    else -> "bn-IN"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
            }
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                    livePartialText = ""
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { isListening = false }
                override fun onError(error: Int) {
                    isListening = false
                    if (liveModeRef.value && !isThinking && !isSpeaking) {
                        mainHandler.postDelayed({ if (liveModeRef.value) startListeningCycle() }, 700)
                    }
                }
                override fun onResults(results: Bundle?) {
                    isListening = false
                    val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (!spoken.isNullOrBlank()) {
                        triggerSend(spoken)
                    } else if (liveModeRef.value) {
                        mainHandler.postDelayed({ if (liveModeRef.value) startListeningCycle() }, 500)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                    if (!partial.isNullOrBlank()) livePartialText = partial
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            recognizer.startListening(intent)
        }
    }

    var pendingLiveStart by remember { mutableStateOf(false) }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (pendingLiveStart) {
                liveVoiceMode = true
                pendingLiveStart = false
            }
            startListeningCycle()
        } else {
            pendingLiveStart = false
            Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
        }
    }

    fun startVoiceWithPermission(enableLiveLoop: Boolean) {
        if (getCurrentProviderKey(selectedProvider).isBlank()) {
            showSettings = true
            Toast.makeText(context, "আগে ${selectedProvider.label} API Key সেভ করুন", Toast.LENGTH_SHORT).show()
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            liveVoiceMode = enableLiveLoop
            startListeningCycle()
        } else {
            pendingLiveStart = enableLiveLoop
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopLiveVoice() {
        liveVoiceMode = false
        livePartialText = ""
        stopRecognizer()
        stopAllAudio()
    }

    if (showSettings) {
        var editGemini by remember { mutableStateOf(geminiKey) }
        var editOpenAi by remember { mutableStateOf(openaiKey) }
        var editClaude by remember { mutableStateOf(claudeKey) }
        var editGrok by remember { mutableStateOf(grokKey) }
        var editEleven by remember { mutableStateOf(elevenLabsKey) }
        var editVoiceId by remember { mutableStateOf(elevenVoiceId) }
        var editPersona by remember { mutableStateOf(customPersona) }

        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = HudPanel,
            title = {
                Text("MASTERMIND API & VOICE VAULT", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("🎙️ ELEVENLABS HUMAN VOICE", color = AmberPro, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = editEleven,
                        onValueChange = { editEleven = it },
                        label = { Text("ElevenLabs API Key", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editVoiceId,
                        onValueChange = { editVoiceId = it },
                        label = { Text("Voice ID (Default: George)", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = Color(0xFF1E293B))
                    Text("🧠 MULTI-AI ENGINE KEYS", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    OutlinedTextField(
                        value = editGemini,
                        onValueChange = { editGemini = it },
                        label = { Text("Google Gemini API Key", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editOpenAi,
                        onValueChange = { editOpenAi = it },
                        label = { Text("ChatGPT (OpenAI) API Key", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editClaude,
                        onValueChange = { editClaude = it },
                        label = { Text("Claude (Anthropic) API Key", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editGrok,
                        onValueChange = { editGrok = it },
                        label = { Text("Grok (xAI) API Key", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = Color(0xFF1E293B))
                    Text("⚡ CUSTOM AI PERSONA", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    OutlinedTextField(
                        value = editPersona,
                        onValueChange = { editPersona = it },
                        placeholder = { Text("যেমন: আমাকে বস বলবে, প্রফেশনাল উত্তর দেবে", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        geminiKey = editGemini.trim()
                        openaiKey = editOpenAi.trim()
                        claudeKey = editClaude.trim()
                        grokKey = editGrok.trim()
                        elevenLabsKey = editEleven.trim()
                        elevenVoiceId = editVoiceId.trim().ifBlank { "JBFqnCBsd6RMkjVDRZzb" }
                        customPersona = editPersona.trim()

                        prefs.edit()
                            .putString("key_gemini", geminiKey)
                            .putString("key_openai", openaiKey)
                            .putString("key_claude", claudeKey)
                            .putString("key_grok", grokKey)
                            .putString("key_elevenlabs", elevenLabsKey)
                            .putString("eleven_voice_id", elevenVoiceId)
                            .putString("custom_persona", customPersona)
                            .apply()

                        showSettings = false
                        Toast.makeText(context, "Mastermind Vault Updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("SAVE ALL", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("CLOSE", color = TextMuted)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF07132B), DeepSpace, Color(0xFF010308))))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Pro HUD Top Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HudPanel.copy(alpha = 0.97f))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    MasterArcReactor(
                        isActive = isThinking || isListening || isSpeaking,
                        isElevenLabs = elevenLabsKey.isNotBlank()
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SANTALI JARVIS PRO",
                            color = TextWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 1.3.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (getCurrentProviderKey(selectedProvider).isNotBlank()) EmeraldOnline else AlertRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isListening -> "LISTENING..."
                                    isThinking -> "${selectedProvider.badge} PROCESSING..."
                                    isSpeaking -> if (elevenLabsKey.isNotBlank()) "ELEVENLABS HUMAN VOICE..." else "SPEAKING..."
                                    elevenLabsKey.isNotBlank() -> "${selectedProvider.badge} • HUMAN VOICE"
                                    else -> "${selectedProvider.badge} • $selectedLanguage"
                                },
                                color = if (elevenLabsKey.isNotBlank()) AmberPro else NeonCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = {
                        autoSpeak = !autoSpeak
                        if (!autoSpeak) stopAllAudio()
                    }) {
                        Icon(
                            imageVector = if (autoSpeak) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Voice Toggle",
                            tint = if (autoSpeak) NeonCyan else TextMuted
                        )
                    }

                    IconButton(onClick = {
                        messages = listOf(initialGreeting)
                        saveChatHistory(prefs, messages)
                        Toast.makeText(context, "New Session Started", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "New Chat",
                            tint = TextMuted
                        )
                    }

                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "API Vault",
                            tint = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AI Engine Switcher Row (Gemini | ChatGPT | Claude | Grok)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    AiProvider.entries.forEach { provider ->
                        val active = selectedProvider == provider
                        val hasKey = getCurrentProviderKey(provider).isNotBlank()
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) ElectricBlue.copy(alpha = 0.22f) else Color(0xFF0F172A))
                                .border(
                                    width = if (active) 1.5.dp else 1.dp,
                                    color = if (active) ElectricBlue else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedProvider = provider }
                                .padding(vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (hasKey) EmeraldOnline else Color(0xFF475569))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = provider.label,
                                    color = if (active) TextWhite else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Language Selector Row (Santali Default)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    languages.forEach { lang ->
                        val active = selectedLanguage == lang
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) NeonCyan.copy(alpha = 0.18f) else Color(0xFF0F172A))
                                .border(
                                    width = if (active) 1.5.dp else 1.dp,
                                    color = if (active) NeonCyan else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedLanguage = lang }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = lang.uppercase(),
                                color = if (active) NeonCyan else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = NeonCyan.copy(alpha = 0.3f), thickness = 1.dp)

            // Chat Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(messages) { index, msg ->
                    Column(
                        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg.headerTag,
                            color = if (msg.isUser) ElectricBlue else NeonCyan,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        Surface(
                            color = if (msg.isUser) UserCard else HudPanel,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 2.dp,
                                bottomEnd = if (msg.isUser) 2.dp else 16.dp
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (msg.isUser) ElectricBlue.copy(alpha = 0.6f) else NeonCyan.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.widthIn(max = 320.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Text(
                                    text = msg.text,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )

                                if (!msg.isUser) {
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Speak",
                                            tint = if (elevenLabsKey.isNotBlank()) AmberPro else NeonCyan,
                                            modifier = Modifier
                                                .size(19.dp)
                                                .clickable { speakHumanOrFallback(msg.text, forceSpeak = true) }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        if (index > 0 && messages[index - 1].isUser) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Regenerate",
                                                tint = TextMuted,
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clickable {
                                                        triggerSend(messages[index - 1].text, appendUserBubble = false)
                                                    }
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = TextMuted,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                        type = "text/plain"
                                                        putExtra(Intent.EXTRA_TEXT, msg.text)
                                                    }
                                                    context.startActivity(Intent.createChooser(sendIntent, "Share JARVIS Reply"))
                                                }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = TextMuted,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable {
                                                    clipboard.setText(AnnotatedString(msg.text))
                                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Quick Bar with LIVE VOICE + Smart Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Surface(
                    color = EmeraldOnline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.2.dp, EmeraldOnline),
                    modifier = Modifier.clickable { startVoiceWithPermission(enableLiveLoop = true) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Live Voice",
                            tint = EmeraldOnline,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (elevenLabsKey.isNotBlank()) "LIVE HUMAN VOICE" else "LIVE VOICE",
                            color = EmeraldOnline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                val quickPrompts = when (selectedLanguage) {
                    "Santali" -> listOf("ᱡᱚᱦᱟᱨ JARVIS", "ᱥᱟᱱᱛᱟᱲᱤ ᱛᱮ ᱠᱟᱹᱦᱱᱤ ᱞᱟᱹᱭ ᱢᱮ", "Open YouTube")
                    "Bengali" -> listOf("হ্যালো JARVIS", "আজকের সেরা আইডিয়া দাও", "Open YouTube")
                    "Hindi" -> listOf("नमस्ते JARVIS", "आज कुछ नया सिखाओ", "Open YouTube")
                    else -> listOf("System Status", "Give me a pro tip", "Open YouTube")
                }

                quickPrompts.forEach { prompt ->
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
                        modifier = Modifier.clickable { triggerSend(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Command Dock
            Surface(color = HudPanel, modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Ask ${selectedProvider.label} ($selectedLanguage)...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = Color(0xFF1E293B),
                            focusedContainerColor = DeepSpace,
                            unfocusedContainerColor = DeepSpace,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = NeonCyan
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isListening) AlertRed else Color(0xFF0F172A))
                            .border(1.dp, if (isListening) AlertRed else NeonCyan.copy(alpha = 0.6f), CircleShape)
                            .clickable { startVoiceWithPermission(enableLiveLoop = false) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isListening) TextWhite else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { triggerSend(inputText) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        // Fullscreen Hands-Free Live Human Voice Overlay
        if (liveVoiceMode) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeepSpace.copy(alpha = 0.96f))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "${selectedProvider.badge} • LIVE VOICE",
                        color = if (elevenLabsKey.isNotBlank()) AmberPro else NeonCyan,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 2.sp
                    )

                    MasterArcReactor(
                        isActive = true,
                        isElevenLabs = elevenLabsKey.isNotBlank(),
                        sizeDp = 160
                    )

                    Text(
                        text = when {
                            isListening -> "🎙️ শুনছি... কথা বলুন ($selectedLanguage)"
                            isThinking -> "⚡ ${selectedProvider.label} উত্তর তৈরি করছে..."
                            isSpeaking -> if (elevenLabsKey.isNotBlank()) "🗣️ ElevenLabs Human Voice চলছে..." else "🔊 JARVIS কথা বলছে..."
                            else -> "প্রস্তুত..."
                        },
                        color = EmeraldOnline,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center
                    )

                    if (livePartialText.isNotBlank()) {
                        Surface(
                            color = HudPanel,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "\"$livePartialText\"",
                                color = TextWhite,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { stopLiveVoice() },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Stop Live",
                            tint = TextWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "STOP LIVE VOICE",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
