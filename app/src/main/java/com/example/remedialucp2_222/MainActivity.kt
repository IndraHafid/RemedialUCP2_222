package com.example.remedialucp2_222

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.remedialucp2_222.ui.LibraryApp
import com.example.remedialucp2_222.ui.theme.RemedialUCP2_222Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemedialUCP2_222Theme {
                LibraryApp()
            }
        }
    }
}