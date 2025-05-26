package com.mkas.ocrapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mkas.ocrapp.ui.HomeScreen
import com.mkas.ocrapp.ui.ProcessingOverlay
import com.mkas.ocrapp.ui.ResultsScreen
import com.mkas.ocrapp.ui.theme.OCRAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mkas.ocrapp.R // Added for string resources
import android.widget.Toast // Explicitly ensure Toast is imported

/**
 * Enum representing the different screens in the application.
 */
enum class Screen {
    /** The initial screen where users can select an image. */
    HOME,
    /** The screen that displays the OCR results (image and extracted text). */
    RESULTS
}

/**
 * The main activity of the OCR application.
 * Sets up the edge-to-edge display and hosts the main [OCRApp] composable.
 */
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

/**
 * The main composable function for the OCR application.
 *
 * This function sets up the overall app structure, including screen navigation,
 * state management via [MainViewModel], and the UI for different screens.
 *
 * @param mainViewModel The [MainViewModel] instance used for managing UI state and business logic.
 *                      Defaults to a ViewModel provided by [viewModel].
 */
@Composable
fun OCRApp(mainViewModel: MainViewModel = viewModel()) {
    val currentScreen by mainViewModel.currentScreen.collectAsStateWithLifecycle()
    val extractedText by mainViewModel.extractedText.collectAsStateWithLifecycle()
    val selectedImageUri by mainViewModel.selectedImageUri.collectAsStateWithLifecycle()
    val isProcessing by mainViewModel.isProcessing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                mainViewModel.processImage(context, uri)
            } else {
                Toast.makeText(context, context.getString(R.string.no_image_selected), Toast.LENGTH_SHORT).show()
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
                        imageUri = selectedImageUri,
                        onBackClick = {
                            mainViewModel.clearTextAndState()
                        },
                        onCopyTextClick = {
                            mainViewModel.copyToClipboard(context, extractedText)
                        },
                        onSelectNewImageClick = {
                            mainViewModel.clearTextAndState()
                        }
                    )
                }
            }

            // Processing Overlay
            ProcessingOverlay(isVisible = isProcessing)
        }
    }
}

// Removed copyTextToClipboard function as it's now in MainViewModel

@Preview(showBackground = true)
@Composable
fun OCRAppPreview() {
    OCRAppTheme {
        OCRApp() // This preview will use a default MainViewModel instance
    }
}
// Removed copyTextToClipboard function as it's now in MainViewModel