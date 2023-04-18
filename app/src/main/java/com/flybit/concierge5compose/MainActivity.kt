package com.flybit.concierge5compose

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.*
import com.flybit.concierge5compose.ui.theme.Concierge5ComposeTheme
import com.flybits.commons.library.api.FlybitsManager
import com.flybits.commons.library.api.results.callbacks.BasicResultCallback
import com.flybits.commons.library.exceptions.FlybitsException
import com.flybits.commons.library.logging.VerbosityLevel
import com.flybits.concierge.Concierge
import com.flybits.concierge.FlybitsConciergeConfiguration
import com.flybits.concierge.enums.ConciergeOptions
import com.flybits.concierge.enums.Container
import com.flybits.flybitscoreconcierge.idps.AnonymousConciergeIDP

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Concierge5ComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column() {

        val context = LocalContext.current
        Text(text = "Hello $name!")

        Button(onClick = {
            println("test315 button on click")
            init(context)
        }) {
            Text(text = "Connect to Flybits")
        }
    }
}

fun c4Fragment(applicationContext: Context): Fragment {
    return Concierge.fragment(applicationContext,
            Container.Categories,
            null,
            arrayListOf(ConciergeOptions.DisplayNavigation(),
                    ConciergeOptions.Settings,
                    ConciergeOptions.Notifications)
    )
}

fun init(context: Context) {

    Concierge.setLoggingVerbosity(VerbosityLevel.ALL)
    val configurationBuilder = FlybitsConciergeConfiguration.Builder(context)
            .setProjectId("C0C7D7D7-9716-4223-B4DB-1C9CC560E3C3")
            .setGateWayUrl("https://api.demo.flybits.com")
            .setWebService("localhost:3000")
            .build()

    Concierge.configure(configurationBuilder, emptyList(), context)
    Concierge.connect(context, AnonymousConciergeIDP(), basicResultCallback = object : BasicResultCallback {
        override fun onException(exception: FlybitsException) {
            println("test315 onException")
        }

        override fun onSuccess() {
            println("test315 onSucess")
        }
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Concierge5ComposeTheme {
        Greeting("Android")
    }
}