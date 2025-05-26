package com.mkas.ocrapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mkas.ocrapp.ui.HomeScreen
import com.mkas.ocrapp.ui.ProcessingOverlay
import com.mkas.ocrapp.ui.ResultsScreen
import com.mkas.ocrapp.ui.theme.OCRAppTheme

enum class Screen {
    HOME,
    RESULTS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OCRAppTheme {
                OCRApp()
            }
        }
    }
}

@Composable
fun OCRApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var extractedText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                isProcessing = true
                selectedImageUri = uri // Store the URI
                try {
                    val image = InputImage.fromFilePath(context, uri)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            extractedText = visionText.text
                            isProcessing = false
                            currentScreen = Screen.RESULTS
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Text recognition failed: ${e.message}", Toast.LENGTH_LONG).show()
                            extractedText = ""
                            isProcessing = false
                            selectedImageUri = null // Reset URI on failure
                            currentScreen = Screen.HOME
                        }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error preparing image: ${e.message}", Toast.LENGTH_LONG).show()
                    extractedText = ""
                    isProcessing = false
                    selectedImageUri = null // Reset URI on error
                    currentScreen = Screen.HOME
                }
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        onSelectImageClick = {
                            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                }
                Screen.RESULTS -> {
                    ResultsScreen(
                        extractedText = extractedText,
                        imageUri = selectedImageUri, // Pass the URI
                        onBackClick = {
                            currentScreen = Screen.HOME
                            extractedText = ""
                            selectedImageUri = null
                        },
                        onCopyTextClick = {
                            copyTextToClipboard(context, extractedText)
                        },
                        onSelectNewImageClick = {
                            currentScreen = Screen.HOME
                            extractedText = ""
                            selectedImageUri = null
                        }
                    )
                }
            }

            // Processing Overlay
            ProcessingOverlay(isVisible = isProcessing)
        }
    }
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Extracted Text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, context.getString(R.string.text_copied), Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun OCRAppPreview() {
    OCRAppTheme {
        OCRApp()
    }
}