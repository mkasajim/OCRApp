package com.mkas.ocrapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkas.ocrapp.R

/**
 * Composable function for the Home screen of the OCR application.
 *
 * This screen displays the app title, a button to select an image for OCR,
 * and a subtitle guiding the user.
 *
 * @param onSelectImageClick Callback invoked when the "Select Image" button is clicked.
 * @param modifier [Modifier] to be applied to the layout.
 */
@Composable
fun HomeScreen(
    onSelectImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary, // Updated
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        // Main Select Image Button
        Button(
            onClick = onSelectImageClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Updated
                contentColor = MaterialTheme.colorScheme.onPrimary // Updated
            )
        ) {
            Text(
                text = stringResource(R.string.select_image),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Subtitle
        Text(
            text = stringResource(R.string.choose_image_subtitle),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Updated
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        
        // Spacer(modifier = Modifier.height(64.dp)) // Spacer can be kept or adjusted as needed for layout
        // RecentScansSection has been removed
    }
}

// RecentScansSection composable removed

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onSelectImageClick = { }
        )
    }
}
