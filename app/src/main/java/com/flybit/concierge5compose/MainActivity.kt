package com.flybit.concierge5compose

//import com.flybits.concierge.Concierge
//import com.flybits.concierge.FlybitsConciergeConfiguration
//import com.flybits.concierge.enums.ConciergeOptions
//import com.flybits.concierge.enums.ConciergeParams
//import com.flybits.concierge.enums.Container
//import com.flybits.concierge.enums.ContentStyle
//import com.flybits.flybitscoreconcierge.conciergeidps.connect
//import com.flybits.flybitscoreconcierge.idps.AnonymousConciergeIDP
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.*
import com.flybit.concierge5compose.ui.theme.Concierge5ComposeTheme
import com.flybits.android.kernel.KernelScope
import com.flybits.android.push.PushScope
import com.flybits.commons.library.api.FlybitsManager
import com.flybits.commons.library.api.idps.AnonymousIDP
import com.flybits.commons.library.api.results.callbacks.BasicResultCallback
import com.flybits.commons.library.exceptions.FlybitsException
import com.flybits.commons.library.logging.VerbosityLevel
import com.flybits.context.ContextScope

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    is ComponentActivity -> baseContext.getActivity()
    else -> null
}
lateinit var flybitsManager: FlybitsManager
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
                flybitsManager.disconnect(resultCallback)
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
//            FragmentContainer(
//                modifier = Modifier.height(300.dp),
//                fragmentManager = fragmentManager,
//                commit = {
//                    add(it, C5Fragment(context), "conciergefragment")
//                })
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
//
//fun c4Banner(applicationContext: Context): Fragment {
//    return Concierge.fragment(
//        applicationContext,
//        Container.None,
//        null,
//        arrayListOf(ConciergeOptions.Horizontal, ConciergeOptions.Style(ContentStyle.BANNER))
//    )
//}
//
//fun C4Expose(applicationContext: Context): Fragment {
//    return Concierge.fragment(
//        applicationContext, Container.Expose, null, arrayListOf(
//            ConciergeOptions.ExposeTitle
//                ("expose title"), ConciergeOptions
//                .ExposeCallToAction("call to action")
//        )
//    )
//}
//
//fun c4Fragment(applicationContext: Context): Fragment {
//    return Concierge.fragment(
//        applicationContext, Container.Categories, null,
////            arrayListOf(ConciergeOptions.DisplayNavigation(),
////                    ConciergeOptions.Settings,
////                    ConciergeOptions.Notifications)
//        arrayListOf()
//    )
//}
//
//fun C5Fragment(applicationContext: Context): Fragment {
//    return Concierge.fragment(
//        applicationContext,
//        Container.Configured,
////            arrayListOf(ConciergeParams.ZonesFilter(Concierge.zonesConfiguration(applicationContext, listOf("carousel")
////            ))),
//
//        arrayListOf(
//            ConciergeParams.ZonesFilter(
//                Concierge.zonesConfiguration(
//                    applicationContext, listOf("carousel")
//                )
//            )
//        ),
//        arrayListOf(
//            ConciergeOptions.DisplayNavigation("c5 content showing here"),
//            ConciergeOptions.Settings,
//            ConciergeOptions.Notifications
//        )
//    )
//}

fun init(context: Context) {
    FlybitsManager.setLoggingVerbosity(VerbosityLevel.ALL)

    flybitsManager = FlybitsManager.Builder(context)
            .setProjectId("F40B1DAA-565C-4652-9C92-700873D0598E")
            .setGatewayURL("https://api.demo.flybits.com")
            .build()
}
val resultCallback = object : BasicResultCallback {
    override fun onSuccess() {
        Log.e("test315", "onSuccess: ")
    }

    override fun onException(exception: FlybitsException) {
        Log.e("test315", "onException: ")

    }
}
fun connect(context: Context) {
    flybitsManager.connect(
            AnonymousIDP(), resultCallback
    )
        FlybitsManager.addScope(PushScope.get(context))
        FlybitsManager.addScope(ContextScope(context))
        FlybitsManager.addScope(KernelScope.get(context))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Concierge5ComposeTheme {
        Greeting("Android")
    }
}