package com.flybit.concierge5compose

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import kotlin.math.log

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset.Zero
            }
        }
    }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .size(100.dp)
            .nestedScroll(nestedScrollConnection)
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        val activity = context.getActivity()

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

        Text(stringResource(id = R.string.aLongText), modifier = Modifier.padding(2.dp))
        Text(stringResource(id = R.string.aLongText), modifier = Modifier.padding(2.dp))

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
            FragmentContainer(
                modifier = Modifier.height(300.dp),
                fragmentManager = fragmentManager,
                commit = {
                    add(it, C5Fragment(context), "conciergefragment")
                })
        }

//        repeat(100) {

        Text(stringResource(id = R.string.aLongText), modifier = Modifier.padding(2.dp))
        Text(stringResource(id = R.string.aLongText), modifier = Modifier.padding(2.dp))
        Text(stringResource(id = R.string.aLongText), modifier = Modifier.padding(2.dp))
//        }

    }
}


@Composable
private fun ScrollBoxes() {
    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .size(100.dp)
            .verticalScroll(rememberScrollState())
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}

/***
 * Display a fragment into a Compose
 */
@Composable
fun FragmentContainer(
    modifier: Modifier = Modifier, fragmentManager: FragmentManager,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val containerId by rememberSaveable { mutableStateOf(View.generateViewId()) }
    var initialized by rememberSaveable { mutableStateOf(false) }

    AndroidView(modifier = modifier, factory = { context ->
        FragmentContainerView(context).apply { id = containerId }
//        FragmentContainerView(context).apply { id = View.generateViewId() }
    }, onRelease = {

        fragmentManager.findFragmentByTag("conciergefragment")?.let { fragment ->
            fragmentManager.commitNow(true) {
                Log.e("test315", "remove the fragment")
                remove(fragment)
            }
        }

    }, update = { view ->
//
        fragmentManager.commit {
            Log.e("test315", "add the fragment")

            commit(view.id)
        }

//        if (!initialized) {
//            fragmentManager.commit { commit(view.id) }
//            initialized = true
//        } else {
//            fragmentManager.onContainerAvailable(view)
//        }
    })
}

/** Access to package-private method in FragmentManager through reflection */
private fun FragmentManager.onContainerAvailable(view: FragmentContainerView) {
    val method = FragmentManager::class.java.getDeclaredMethod(
        "onContainerAvailable",
        FragmentContainerView::class.java
    )
    method.isAccessible = true
    method.invoke(this, view)
}

fun c4Banner(applicationContext: Context): Fragment {
    return Concierge.fragment(
        applicationContext,
        Container.None,
        null,
        arrayListOf(ConciergeOptions.Horizontal, ConciergeOptions.Style(ContentStyle.BANNER))
    )
}

fun C4Expose(applicationContext: Context): Fragment {
    return Concierge.fragment(
        applicationContext, Container.Expose, null, arrayListOf(
            ConciergeOptions.ExposeTitle
                ("expose title"), ConciergeOptions
                .ExposeCallToAction("call to action")
        )
    )
}

fun c4Fragment(applicationContext: Context): Fragment {
    return Concierge.fragment(
        applicationContext, Container.Categories, null,
//            arrayListOf(ConciergeOptions.DisplayNavigation(),
//                    ConciergeOptions.Settings,
//                    ConciergeOptions.Notifications)
        arrayListOf()
    )
}

fun C5Fragment(applicationContext: Context): Fragment {
    return Concierge.fragment(
        applicationContext,
        Container.Configured,
//            arrayListOf(ConciergeParams.ZonesFilter(Concierge.zonesConfiguration(applicationContext, listOf("carousel")
//            ))),

        arrayListOf(
            ConciergeParams.ZonesFilter(
                Concierge.zonesConfiguration(
                    applicationContext, listOf("carousel")
                )
            )
        ),
        arrayListOf(
            ConciergeOptions.DisplayNavigation("c5 content showing here"),
            ConciergeOptions.Settings,
            ConciergeOptions.Notifications
        )
    )
}

fun init(context: Context) {
    Concierge.setLoggingVerbosity(VerbosityLevel.ALL)


    val configurationBuilder = FlybitsConciergeConfiguration.Builder(context)
        .setProjectId("81A5A577-E2FC-4B68-9977-04C22B09B1F9")
        .setGatewayUrl("https://api.tdunmndb289.flybits.com")
        .setWebService("https://static-files-concierge.tdunmndb289.flybits.com/latest")
        .build()

    Concierge.configure(configurationBuilder, emptyList(), context)
}

fun connect(context: Context) {
    Concierge.connect(
        context,
        AnonymousConciergeIDP(),
        basicResultCallback = object : BasicResultCallback {
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