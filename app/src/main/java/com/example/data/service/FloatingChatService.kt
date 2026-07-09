package com.example.data.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.ui.theme.*
import com.example.utils.StickerManager
import com.example.utils.StickerImage
import com.example.utils.StickerMetadata
import kotlin.math.roundToInt

class FloatingChatService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null
    
    // Lifecycle registries required to host Compose inside an Android Service safely
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        // Initialize lifecycle for Jetpack Compose runtime support
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setupFloatingOverlay()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        return START_NOT_STICKY
    }

    private fun setupFloatingOverlay() {
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // Layout parameters supporting transparency, positioning, and focus toggles
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 200
            y = 300
        }

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FloatingChatService)
            setViewTreeViewModelStoreOwner(this@FloatingChatService)
            setViewTreeSavedStateRegistryOwner(this@FloatingChatService)
            
            setContent {
                var isExpanded by remember { mutableStateOf(false) }
                
                // State to update window coordinates dynamically
                var offsetX by remember { mutableStateOf(params.x.toFloat()) }
                var offsetY by remember { mutableStateOf(params.y.toFloat()) }

                LaunchedEffect(offsetX, offsetY) {
                    params.x = offsetX.roundToInt()
                    params.y = offsetY.roundToInt()
                    try {
                        windowManager.updateViewLayout(this@apply, params)
                    } catch (e: Exception) {
                        // Handler for intermediate attaching phase
                    }
                }

                // Dynamic sizing depending on expanded state
                LaunchedEffect(isExpanded) {
                    if (isExpanded) {
                        params.width = 340.dp.value.roundToInt() * 3 // Approx pixel scale
                        params.height = 450.dp.value.roundToInt() * 3
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    } else {
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    }
                    try {
                        windowManager.updateViewLayout(this@apply, params)
                    } catch (e: Exception) {}
                }

                if (!isExpanded) {
                    FloatingBubbleView(
                        onTap = { isExpanded = true },
                        onDrag = { dx, dy ->
                            offsetX += dx
                            offsetY += dy
                        }
                    )
                } else {
                    FloatingChatCardView(
                        onMinimize = { isExpanded = false },
                        onClose = { stopSelf() },
                        onDrag = { dx, dy ->
                            offsetX += dx
                            offsetY += dy
                        }
                    )
                }
            }
        }

        try {
            windowManager.addView(composeView, params)
            lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memuat overlay: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        // Mitigation of Memory Leaks: Ensure all views are removed and lifecycle context is destroyed.
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        composeView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {}
        }
        composeView = null
        store.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

/**
 * Bubble Overlay mengambang (Floating Chat Head) yang dapat di drag.
 */
@Composable
fun FloatingBubbleView(
    onTap: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = HeartsPink,
        shape = CircleShape,
        shadowElevation = 8.dp,
        modifier = modifier
            .size(64.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .clickable { onTap() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Buka Chat",
                tint = PremiumWhite,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

data class FloatingMessage(
    val sender: String,
    val text: String,
    val stickerId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Jendela Chat Komunitas yang mengambang (Overlay Window).
 */
@Composable
fun FloatingChatCardView(
    onMinimize: () -> Unit,
    onClose: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                FloatingMessage("Sparky", "Halo kakak semua! Selamat datang di H2H Live Chat!"),
                FloatingMessage("Aline", "Keren banget bisa ngobrol sambil nonton MV!"),
                FloatingMessage("RianH2H", "Suka banget fitur stiker barunya, lucu abis!", "sticker_spark_wink")
            )
        )
    }

    var textInput by remember { mutableStateOf("") }
    var selectedSticker by remember { mutableStateOf<String?>(null) }
    var showStickerPanel by remember { mutableStateOf(false) }

    // Throttle & Keamanan: Mencegah spam pesan (rate limiting)
    var lastSentTime by remember { mutableStateOf(0L) }
    val throttleIntervalMs = 2000L // Batas 2 detik antar pengiriman pesan/stiker

    val stickers = StickerManager.getAvailableStickers()

    Surface(
        color = PremiumDarkGray,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 12.dp,
        modifier = modifier
            .width(320.dp)
            .height(420.dp)
            .border(2.dp, HeartsPink.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Drag handle header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PremiumBlack, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount.x, dragAmount.y)
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "H2H Floating Chat",
                        color = PremiumWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMinimize, modifier = Modifier.size(28.dp)) {
                        Text("_", color = PremiumLightGray, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = DestructiveRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Message Stream Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(PremiumBlack.copy(alpha = 0.2f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = msg.sender,
                                color = HeartsPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Baru Saja",
                                color = PremiumMediumGray,
                                fontSize = 9.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        if (msg.text.isNotEmpty()) {
                            Surface(
                                color = PremiumMediumGray.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = PremiumWhite,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Jika menyertakan stiker, render stiker dari Storage menggunakan Coil
                        if (msg.stickerId != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            StickerImage(stickerId = msg.stickerId, modifier = Modifier.size(60.dp))
                        }
                    }
                }
            }

            // Input Bar & Action controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PremiumDarkGray)
                    .padding(8.dp)
            ) {
                if (showStickerPanel) {
                    // Panel pemilih Stiker Whitelist
                    Text(
                        text = "Kirim Stiker Whitelist:",
                        color = PremiumLightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stickers.forEach { sticker ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PremiumMediumGray)
                                    .clickable {
                                        // Sistem Keamanan & Throttle Check
                                        val currentTime = System.currentTimeMillis()
                                        if (currentTime - lastSentTime < throttleIntervalMs) {
                                            // Tampilkan feedback error throttle
                                            // Dalam service, kita panggil context.applicationContext
                                        } else {
                                            // Whitelist Validation Check
                                            if (StickerManager.isValidStickerId(sticker.id)) {
                                                messages = messages + FloatingMessage(
                                                    sender = "Saya (Me)",
                                                    text = "",
                                                    stickerId = sticker.id
                                                )
                                                lastSentTime = currentTime
                                                showStickerPanel = false
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                StickerImage(stickerId = sticker.id, modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sticker toggle button
                    IconButton(
                        onClick = { showStickerPanel = !showStickerPanel },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("😀", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Text Input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(PremiumMediumGray)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (textInput.isEmpty()) {
                            Text("Tulis pesan...", color = PremiumLightGray, fontSize = 13.sp)
                        }
                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            textStyle = TextStyle(color = PremiumWhite, fontSize = 13.sp),
                            cursorBrush = SolidColor(HeartsPink),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Send Button
                    IconButton(
                        onClick = {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastSentTime < throttleIntervalMs) {
                                // Rate limited
                            } else {
                                if (textInput.isNotBlank()) {
                                    messages = messages + FloatingMessage(
                                        sender = "Saya (Me)",
                                        text = textInput
                                    )
                                    textInput = ""
                                    lastSentTime = currentTime
                                }
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (textInput.isNotBlank()) HeartsPink else PremiumMediumGray)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = PremiumWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
