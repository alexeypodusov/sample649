package ru.alexeypodusov.sample649.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.alexeypodusov.sample649.charactersfeature.presentation.compose.CharactersScreen
import ru.alexeypodusov.sample649.R
import ru.alexeypodusov.sample649.base.theme.Sample649Theme
import ru.alexeypodusov.sample649.base.theme.bottomBarAccent
import ru.alexeypodusov.sample649.graphfeature.presentation.compose.GraphScreen


class ComposeTabsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Sample649Theme {
                TabsScreen()
            }
        }
    }
}

@Composable
fun TabsScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationTest(navController)
        }
    ) {
        NavHost(
            navController,
            startDestination = BottomNavItem.CHARACTERS.screenRoute,
            modifier = Modifier.padding(it)
        ) {
            composable(BottomNavItem.CHARACTERS.screenRoute) {
                CharactersScreen()
            }
            composable(BottomNavItem.GRAPH.screenRoute) {
                GraphScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationTest(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.CHARACTERS,
        BottomNavItem.GRAPH
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach {
            BottomNavigationItem(
                icon = {
                    Icon(painterResource(id = it.icon),
                        contentDescription = stringResource(it.title))
                },
                label = {
                    Text(
                        text = stringResource(it.title),
                        fontSize = 12.sp
                    )
                },
                selectedContentColor = MaterialTheme.colors.bottomBarAccent,
                unselectedContentColor = MaterialTheme.colors.onSurface.copy(0.6f),
                alwaysShowLabel = true,
                selected = currentRoute == it.screenRoute,
                onClick = {
                    navController.navigate(it.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        val context = LocalContext.current
        Button(
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                .width(120.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.bottomBarAccent,
                contentColor = MaterialTheme.colors.onPrimary
            ),
            onClick = {
                context.startActivity(
                    Intent(context, TabsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        ) {
            Text(stringResource(R.string.view))
        }
    }
}

enum class BottomNavItem(
    val title: Int,
    val icon: Int,
    val screenRoute: String,
) {
    CHARACTERS(R.string.characters, R.drawable.ic_baseline_people_24, "characters"),
    GRAPH(R.string.graph, R.drawable.ic_baseline_equalizer_24, "graph")
}

