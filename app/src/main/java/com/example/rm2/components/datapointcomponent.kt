package com.example.rm2.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rm2.ui.theme.RickAction
import com.example.rm2.ui.theme.RickTextPrimary

data class DataPoint(
    val title: String,
    val description:String
)

@Composable
fun DataPointComponent(dataPoint: DataPoint){
    Column {
        Text(text = dataPoint.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = RickAction
        )
        
        Text(text = dataPoint.description,
            fontSize = 24.sp,
            color = RickTextPrimary
        )
    }
}