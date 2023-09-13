package com.flybit.concierge5compose

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.flybit.concierge5compose.ui.theme.Concierge5ComposeTheme
import com.flybits.commons.library.api.results.callbacks.BasicResultCallback
import com.flybits.commons.library.exceptions.FlybitsException
import com.flybits.commons.library.logging.VerbosityLevel
import com.flybits.concierge.Concierge
import com.flybits.concierge.ConciergeConstants
import com.flybits.concierge.ConciergeConstants.BROADCAST_CONCIERGE_EVENT
import com.flybits.concierge.FlybitsConciergeConfiguration
import com.flybits.concierge.enums.ConciergeCustomerStatus
import com.flybits.concierge.enums.ConciergeOptions
import com.flybits.concierge.enums.ConciergeParams
import com.flybits.concierge.enums.Container
import com.flybits.concierge.enums.ContentStyle
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
        init(context)//Should init this in Application class
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
fun ComposableBroadcastReceiver(
    action: String,
    event: (context: Context, intent: Intent?) -> Unit,
) {
    // Grab the current context in this part of the UI tree
    val context = LocalContext.current

    // Safely use the latest event lambda passed to the function
    val currentOnSystemEvent by rememberUpdatedState(event)

    // If either context or systemAction changes, unregister and register again
    DisposableEffect(context, action) {
        val intentFilter = IntentFilter(action)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("MainActivity", "onReceive called.")
                context?.let {
                    currentOnSystemEvent(it, intent)
                }
            }
        }

        context.registerReceiver(broadcast, intentFilter)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}

@Composable
fun Greeting(name: String) {
    Column() {
        val context = LocalContext.current
        val activity = context.getActivity()
        var broadcastIntent: Intent? = null
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
            /***
             * Example to display a Banner view
             */
//            FragmentContainer(fragmentManager = fragmentManager, commit = {
//                add(it, c4Banner(context))
//            })
            /***
             * Example to display a Expose view
             */
//            FragmentContainer(fragmentManager = fragmentManager, commit = {
//                C4Expose(context)
//            })
            /***
             * Example to display a common List view
             */
//            FragmentContainer(modifier = Modifier, fragmentManager = fragmentManager, commit = {
//                add(it, c4Fragment(context))
//            })

            /***
             * Example to display a C5 view
             */
            FragmentContainer(modifier = Modifier, fragmentManager = fragmentManager, commit = {
                replace(it, C5Fragment(context))
            })
        }

        ComposableBroadcastReceiver(BROADCAST_CONCIERGE_EVENT, ::handleOnReceive)
    }
}

fun handleOnReceive(context: Context, intent: Intent?) {
    // For example getting a dummy value
    val value = intent?.extras?.getString(ConciergeConstants.CONCIERGE_IDENTIFIER)
    val actionableLink = intent?.extras?.getString(ConciergeConstants.ACTIONABLE_URL)
    val customerStatus: ConciergeCustomerStatus? = intent?.extras?.getParcelable(ConciergeConstants.CONCIERGE_AUTHENTICATION_STATUS)
    val analyticsType = intent?.extras?.getString(ConciergeConstants.ANALYTICS_TYPE)
    val contentData: HashMap<String, Any>? = intent?.extras?.getSerializable(ConciergeConstants.CONTENT_DATA)
            as HashMap<String, Any>?
    val contentInfo: HashMap<String, String>? = intent?.extras?.getSerializable(
        ConciergeConstants
            .CONTENT_INFO) as HashMap<String, String>?
    val hasContent = intent?.extras?.getString(ConciergeConstants.HAS_CONTENT)

    actionableLink?.let {
        Log.d("BroadcastActionLink", value.toString() + " / " + actionableLink.toString())
    }
    analyticsType?.let {
        Log.d("BroadcastAnalytics", value.toString() + " / " +
                it.toString() + " / " + contentData.toString() + " / " + contentInfo.toString())
    }
    customerStatus?.let {
        Log.d("BroadcastStatus", value.toString() + " / " + it.toString())
    }
    hasContent?.let {
        Log.d("BroadcastHasContent", value.toString() + " / " + it)
    }

    actionableLink?.let {
        context.let { ctx ->
            Concierge.handleActionableLink(
                ctx,
                Uri.parse(actionableLink),
                conciergeOptions = arrayListOf(ConciergeOptions.Identifier("123")),
                requestEvents = ConciergeParams.RequestEvents()/*,
                    ConciergeTheme.FileName("custom_theming1.json")*/)
                ?.let { fragment ->
                    context.getActivity()?.supportFragmentManager?.let {fm ->
                        FragmentContainer(modifier = Modifier, fragmentManager = fm, commit = {
                            add(it, fragment)
                        })
                    }
                } ?: run {
                Log.i("", "Returns null")
            }
        }
    }
}

/***
 * Display a fragment into a Compose
 */
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
            arrayListOf(ConciergeParams.RequestEvents()),
            arrayListOf(ConciergeOptions.DisplayNavigation("c5 content showing here"),
                    ConciergeOptions.Settings,
                    ConciergeOptions.Notifications)
    )
}

fun init(context: Context) {
    Concierge.setLoggingVerbosity(VerbosityLevel.ALL)
    val configurationBuilder = FlybitsConciergeConfiguration.Builder(context)
            .setProjectId("F1BFE6D1-0DB6-4863-A3E5-56434C2075FB")
            .setGatewayUrl("https://api.demo.flybits.com")
            .setWebService("https://static-files-concierge.tdunmndb289.flybits.com/latest")
            .build()
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