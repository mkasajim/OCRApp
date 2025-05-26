package com.mkas.ocrapp.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border // Keep this if you prefer foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.mkas.ocrapp.R

/**
 * Composable function for the Results screen.
 *
 * This screen displays the selected image (if available) and the extracted text.
 * It provides options to switch between image and text views via tabs (if image is present),
 * copy the extracted text, select a new image, or go back.
 *
 * @param extractedText The text extracted from the image.
 * @param imageUri The [Uri] of the selected image. Can be null if no image was processed or if processing failed.
 * @param onBackClick Callback invoked when the back navigation action is triggered.
 * @param onCopyTextClick Callback invoked when the "Copy Text" button is clicked.
 * @param onSelectNewImageClick Callback invoked when the "Select New Image" or "Try Another Image" button is clicked.
 * @param modifier [Modifier] to be applied to the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    extractedText: String,
    imageUri: Uri? = null,
    onBackClick: () -> Unit,
    onCopyTextClick: () -> Unit,
    onSelectNewImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.tab_image), stringResource(R.string.tab_text))

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1, // Good practice for titles
                        overflow = TextOverflow.Ellipsis // Good practice for titles
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal)
            )
        },
        bottomBar = { // MODIFIED: Moved BottomToolbar to the bottomBar slot
            if (extractedText.isNotEmpty()) {
                BottomToolbar(
                    onCopyTextClick = onCopyTextClick,
                    onSelectNewImageClick = onSelectNewImageClick
                )
            }
        }
    ) { innerPadding -> // innerPadding now accounts for TopAppBar AND BottomToolbar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply Scaffold's innerPadding
        ) {
            if (imageUri != null && extractedText.isNotEmpty()) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 16.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // Main Content
            Box(
                // This Box will fill the remaining space between TabRow (if any) and BottomToolbar
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth() // Ensure it takes full width
            ) {
                when {
                    extractedText.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_text_detected),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, // Updated
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = onSelectNewImageClick,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.try_another_image),
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    imageUri == null -> {
                        TextDisplayCard(
                            extractedText = extractedText,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp) // Padding around the card
                        )
                    }
                    selectedTabIndex == 0 -> {
                        ImageDisplayCard(
                            imageUri = imageUri,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp) // Padding around the card
                        )
                    }
                    else -> { // Text Tab
                        TextDisplayCard(
                            extractedText = extractedText,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp) // Padding around the card
                        )
                    }
                }
            }

            // BottomToolbar REMOVED from here
        }
    }
}

/**
 * Displays the selected image within a Card.
 *
 * @param imageUri The [Uri] of the image to display.
 * @param modifier [Modifier] to be applied to the Card.
 */
@Composable
private fun ImageDisplayCard(
    imageUri: Uri,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier // Apply the passed modifier (size and outer padding) to the Card
            // .fillMaxWidth() // This is redundant if 'modifier' already contains .fillMaxSize()
            .border( // This foundation border is applied over the Card's area
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline, // Updated
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Updated
        )
        // For M3 Card, you could also use the `border = BorderStroke(...)` parameter instead of `Modifier.border`
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Selected image",
            modifier = Modifier
                .fillMaxSize() // Image fills the Card
                .padding(8.dp), // Padding for the image content within the Card
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Displays the extracted text within a Card, with vertical scrolling.
 *
 * @param extractedText The text to display.
 * @param modifier [Modifier] to be applied to the Card.
 */
@Composable
private fun TextDisplayCard(
    extractedText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier // Apply the passed modifier (size and outer padding) to the Card
            // .fillMaxWidth() // This is redundant if 'modifier' already contains .fillMaxSize()
            .border( // This foundation border is applied over the Card's area
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline, // Updated
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Updated
        )
        // For M3 Card, you could also use the `border = BorderStroke(...)` parameter instead of `Modifier.border`
    ) {
        Text(
            text = extractedText,
            modifier = Modifier
                .fillMaxSize() // Text composable fills the Card
                .padding(16.dp) // Padding for the text content within the Card
                .verticalScroll(rememberScrollState()),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface, // Updated
            lineHeight = 24.sp
        )
    }
}

/**
 * Displays a bottom toolbar with buttons for "Copy Text" and "Select New Image".
 *
 * @param onCopyTextClick Callback for the "Copy Text" button.
 * @param onSelectNewImageClick Callback for the "Select New Image" button.
 */
@Composable
private fun BottomToolbar(
    onCopyTextClick: () -> Unit,
    onSelectNewImageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Background color for the toolbar area can be added here if desired, e.g. .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 16.dp), // Padding within the toolbar itself
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onCopyTextClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.copy_text),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        OutlinedButton(
            onClick = onSelectNewImageClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary // Updated
            )
            // border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary) // Default outlined button border is usually sufficient
        ) {
            Text(
                text = stringResource(R.string.select_new_image),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenPreview() {
    MaterialTheme {
        ResultsScreen(
            extractedText = "This is some sample extracted text from an image. It can be quite long and should scroll properly when there's more content than can fit on the screen.",
            imageUri = null, // Simulating imageUri from a real scenario would require a placeholder or actual Uri
            onBackClick = { },
            onCopyTextClick = { },
            onSelectNewImageClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultsScreenNoTextPreview() {
    MaterialTheme {
        ResultsScreen(
            extractedText = "",
            imageUri = null,
            onBackClick = { },
            onCopyTextClick = { },
            onSelectNewImageClick = { }
        )
    }
}