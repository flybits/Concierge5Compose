package com.flybit.concierge5compose

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.*
import com.flybit.concierge5compose.ui.theme.Concierge5ComposeTheme
import com.flybits.commons.library.api.FlybitsManager
import com.flybits.commons.library.api.results.callbacks.BasicResultCallback
import com.flybits.commons.library.exceptions.FlybitsException
import com.flybits.commons.library.logging.VerbosityLevel
import com.flybits.concierge.Concierge
import com.flybits.concierge.FlybitsConciergeConfiguration
import com.flybits.concierge.enums.ConciergeOptions
import com.flybits.concierge.enums.ConciergeParams
import com.flybits.concierge.enums.Container
import com.flybits.concierge.enums.ContentStyle
import com.flybits.flybitscoreconcierge.conciergeidps.connect
import com.flybits.flybitscoreconcierge.idps.AnonymousConciergeIDP

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    is ComponentActivity -> baseContext.getActivity()
    else -> null
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        init(context)
//        val supportFragmentManager = this.getActivity()?.supportFragmentManager!!
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

//    val context = LocalContext.current

    Column() {

        val context = LocalContext.current
        val activity = context.getActivity()

//        Text(text = "Hello $name!")

        Row() {
            Button(onClick = {
                println("test315 button on click")
                connect(context)

            }) {
                Text(text = "Connect to Flybits")
            }
            Button(onClick = {

                Concierge.disconnect(context, object : BasicResultCallback {
                    override fun onException(exception: FlybitsException) {
                        println("test315 disconnected onSuccess")
                    }

                    override fun onSuccess() {
                        println("test315 disconnected onSuccess")
                    }
                })
            }) {
                Text(text = "disconnect from Flybit")
            }
        }




        activity?.supportFragmentManager?.let { fragmentManager ->
//            FragmentContainer(fragmentManager = fragmentManager, commit = {
//                add(it, c4Banner(context))
//            })
//            FragmentContainer(fragmentManager = fragmentManager, commit = {
//                C4Expose(context)
//            })

//                        FragmentContainer(modifier = Modifier, fragmentManager = fragmentManager, commit = {
//                add(it, c4Fragment(context))
//            })

            FragmentContainer(modifier = Modifier, fragmentManager = fragmentManager, commit = {
                add(it, C5Fragment(context))
            })
        }

    }
}

@Composable
fun FragmentContainer(modifier: Modifier = Modifier, fragmentManager: FragmentManager,
        commit: FragmentTransaction.(containerId: Int) -> Unit) {
    val containerId by rememberSaveable { mutableStateOf(View.generateViewId()) }
    var initialized by rememberSaveable { mutableStateOf(false) }
    AndroidView(modifier = modifier, factory = { context ->
        FragmentContainerView(context).apply { id = containerId }
    }, update = { view ->
        if (!initialized) {
            fragmentManager.commit { commit(view.id) }
            initialized = true
        } else {
            fragmentManager.onContainerAvailable(view)
        }
    })
}

/** Access to package-private method in FragmentManager through reflection */
private fun FragmentManager.onContainerAvailable(view: FragmentContainerView) {
    val method = FragmentManager::class.java.getDeclaredMethod("onContainerAvailable", FragmentContainerView::class.java)
    method.isAccessible = true
    method.invoke(this, view)
}

fun c4Banner(applicationContext: Context): Fragment {
    return Concierge.fragment(applicationContext, Container.None, null, arrayListOf(ConciergeOptions.Horizontal, ConciergeOptions.Style(ContentStyle.BANNER)))
}

fun C4Expose(applicationContext: Context): Fragment {
    return Concierge.fragment(applicationContext, Container.Expose, null, arrayListOf(ConciergeOptions.ExposeTitle
    ("expose title"), ConciergeOptions
            .ExposeCallToAction("call to action")))
}

fun c4Fragment(applicationContext: Context): Fragment {
    return Concierge.fragment(applicationContext, Container.Categories, null,
//            arrayListOf(ConciergeOptions.DisplayNavigation(),
//                    ConciergeOptions.Settings,
//                    ConciergeOptions.Notifications)
            arrayListOf())
}

fun C5Fragment(applicationContext: Context): Fragment {
    return Concierge.fragment(applicationContext,
            Container.Configured,
            arrayListOf(),
            arrayListOf(ConciergeOptions.DisplayNavigation("c5 content showing here"),
                    ConciergeOptions.Settings,
                    ConciergeOptions.Notifications)
    )
}

fun init(context: Context) {
    Concierge.setLoggingVerbosity(VerbosityLevel.ALL)
    val configurationBuilder = FlybitsConciergeConfiguration.Builder(context)
            .setProjectId("35F6A9F5-579B-4229-815C-7D994CD50F9C").setGatewayUrl("https://api.demo.flybits.com")
            .setWebService("localhost:3000").build()
    Concierge.configure(configurationBuilder, emptyList(), context)
}
fun connect(context: Context) {
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