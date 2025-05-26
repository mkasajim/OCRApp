package com.mkas.ocrapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mkas.ocrapp.R // Added for string resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Screen enum class - Assuming it's defined in MainActivity.kt or accessible globally
// enum class Screen { HOME, RESULTS }

/**
 * ViewModel for managing the UI state and business logic of the OCR application.
 *
 * This ViewModel handles screen navigation, text extraction from images,
 * clipboard operations, and overall UI state management.
 */
class MainViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    /**
     * Represents the current screen being displayed in the UI.
     */
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _extractedText = MutableStateFlow("")
    /**
     * Holds the text extracted from the selected image.
     */
    val extractedText: StateFlow<String> = _extractedText.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    /**
     * URI of the image selected by the user for text extraction. Null if no image is selected.
     */
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    /**
     * Indicates whether an image processing operation (text extraction) is currently in progress.
     */
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    /**
     * Updates the current screen displayed in the UI.
     * @param screen The [Screen] to navigate to.
     */
    fun updateScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    /**
     * Clears the extracted text, selected image URI, resets processing state,
     * and navigates back to the home screen.
     */
    fun clearTextAndState() {
        _extractedText.value = ""
        _selectedImageUri.value = null
        _isProcessing.value = false // Ensure processing is reset
        _currentScreen.value = Screen.HOME
    }

    /**
     * Processes the selected image to extract text using ML Kit Text Recognition.
     * Updates UI states based on the success or failure of the operation.
     *
     * @param context The application context.
     * @param uri The [Uri] of the image to process.
     */
    fun processImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            _selectedImageUri.value = uri
            _extractedText.value = "" // Clear previous text

            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        _extractedText.value = visionText.text
                        _isProcessing.value = false
                        _currentScreen.value = Screen.RESULTS
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, context.getString(R.string.text_recognition_failed, e.localizedMessage ?: "Unknown error"), Toast.LENGTH_LONG).show()
                        _extractedText.value = ""
                        _isProcessing.value = false
                        _selectedImageUri.value = null
                        _currentScreen.value = Screen.HOME
                    }
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.error_preparing_image, e.localizedMessage ?: "Unknown error"), Toast.LENGTH_LONG).show()
                _extractedText.value = ""
                _isProcessing.value = false
                _selectedImageUri.value = null
                _currentScreen.value = Screen.HOME
            }
        }
    }

    /**
     * Copies the given text to the system clipboard.
     * Displays a toast message confirming the action.
     *
     * @param context The application context.
     * @param text The text to be copied.
     */
    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.extracted_text_clipboard_label), text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, context.getString(R.string.text_copied), Toast.LENGTH_SHORT).show()
    }
}
