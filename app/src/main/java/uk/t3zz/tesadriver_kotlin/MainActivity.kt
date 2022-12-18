package com.example.tesadriver_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tesadriver_kotlin.ui.theme.TESADriverKotlinTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tesa_driver = TESADriverKotlin(
            protocol = "wss://",
            host = "t3zz.uk",
            path = "ws",
            epa = "eea433fff6a0ab0f26df3526bfe46a58fc36e7f8f373fc8995b989c39abadb988b4e132fc2a2cba37d4725f87a788b4ca2003ec531a8a5854af0b4a01a538518",
            app_type = AppType.T3zz,
            account_id = "TA134"
        )

        setContent {
            TESADriverKotlinTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {

                    Button(onClick = {
                        repeat(10) {
                                val command_response = tesa_driver.command(cmd = "GetBusinessAccount", payload = mutableMapOf())
                        }
                    }) {
                        Greeting("Android")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TESADriverKotlinTheme {
        Greeting("Android")
    }
}
