package com.mkas.ocrapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkas.ocrapp.R

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
            color = Color(0xFF2196F3),
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
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
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
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        // Recent Scans Section (Optional - placeholder for now)
        RecentScansSection()
    }
}

@Composable
private fun RecentScansSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.recent_scans),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Placeholder for recent scans - can be implemented later
        Text(
            text = "No recent scans",
            fontSize = 14.sp,
            color = Color(0xFF999999),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            onSelectImageClick = { }
        )
    }
}
