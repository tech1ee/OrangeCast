package com.example.orangecast.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colors = MaterialTheme.colors.copy(
                    primary = AppColors.colorPrimary,
                    secondary = AppColors.colorSecondary,
                    background = AppColors.colorBackground
                )
            ) {
                Home()
            }
        }
    }
}
