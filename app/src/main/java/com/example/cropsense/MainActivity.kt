package com.example.cropsense

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.cropsense.ui.theme.ConfidenceAmber
import com.example.cropsense.ui.theme.ConfidenceGreen
import com.example.cropsense.ui.theme.ConfidenceRed
import com.example.cropsense.ui.theme.CropSenseTheme
import com.example.cropsense.ui.theme.SuccessContainer
import com.example.cropsense.ui.theme.SuccessOnContainer
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CropSenseTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            imageUri = uri
            viewModel.clearAll()
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            bitmap?.let {
                imageUri = saveBitmap(context, it)
                viewModel.clearAll()
            }
        }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "CropSense",
                        fontWeight = FontWeight.Light,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    // Clear / reset button — visible when there's content to clear
                    if (imageUri != null) {
                        IconButton(onClick = {
                            imageUri = null
                            viewModel.clearAll()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))

            /* ---------- IMAGE PREVIEW ---------- */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Select or capture an image",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Selected crop image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- ACTION BUTTONS ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery", fontWeight = FontWeight.Normal)
                }

                OutlinedButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Camera,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Camera", fontWeight = FontWeight.Normal)
                }
            }

            Spacer(Modifier.height(16.dp))

            /* ---------- ANALYZE BUTTON ---------- */
            Button(
                onClick = {
                    imageUri?.let {
                        viewModel.uploadImage(context, it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = imageUri != null && !viewModel.loading.value,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                if (viewModel.loading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Analyzing…",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        "Analyze Image",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- ERROR CARD ---------- */
            AnimatedVisibility(
                visible = viewModel.error.value != null,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
            ) {
                viewModel.error.value?.let { errorMsg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = errorMsg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.error.value = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            /* ---------- NON-LEAF WARNING ---------- */
            viewModel.result.value?.let { result ->
                if (result.is_leaf == false) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🚫",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Text(
                                    text = result.message
                                        ?: "This doesn't appear to be a leaf. Please upload a clear leaf image.",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            /* ---------- RESULT CARD ---------- */
            viewModel.result.value?.let { result ->
                if (result.is_leaf != false && result.crop != null && result.disease != null && result.confidence != null) {

                    val isHealthy = result.disease.contains("healthy", ignoreCase = true)

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isHealthy) SuccessContainer
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                // Header
                                Text(
                                    text = if (isHealthy) "✅  Healthy Plant" else "Results",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isHealthy) SuccessOnContainer
                                    else MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(Modifier.height(20.dp))

                                // Crop
                                ResultField(
                                    label = "Crop",
                                    value = result.crop,
                                    valueColor = if (isHealthy) SuccessOnContainer
                                    else MaterialTheme.colorScheme.onSurface
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )

                                // Disease
                                ResultField(
                                    label = "Disease",
                                    value = result.disease,
                                    valueColor = if (isHealthy) SuccessOnContainer
                                    else MaterialTheme.colorScheme.onSurface
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )

                                // Confidence with bar
                                ConfidenceSection(
                                    confidence = result.confidence,
                                    isHealthy = isHealthy
                                )

                                // Low confidence warning
                                if (result.confidence < 0.6f) {
                                    Spacer(Modifier.height(16.dp))

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "⚠️",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                                            )
                                            Text(
                                                text = "Low confidence. Try a clearer image for better results.",
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                style = MaterialTheme.typography.bodySmall,
                                                lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/* ---------- REUSABLE COMPOSABLES ---------- */

@Composable
private fun ResultField(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ConfidenceSection(
    confidence: Float,
    isHealthy: Boolean
) {
    val percentage = (confidence * 100).toInt()
    val barColor = when {
        confidence >= 0.8f -> ConfidenceGreen
        confidence >= 0.6f -> ConfidenceAmber
        else -> ConfidenceRed
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Confidence",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium,
                color = if (isHealthy) SuccessOnContainer else barColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(52.dp)
            )
            Spacer(Modifier.width(12.dp))
            LinearProgressIndicator(
                progress = { confidence },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isHealthy) SuccessOnContainer else barColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}


fun saveBitmap(context: android.content.Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
    return Uri.fromFile(file)
}