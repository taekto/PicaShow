package io.b101.picashow

//import io.b101.picashow.ui.page.AddSchedulePage
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.b101.picashow.database.AppDatabase
import io.b101.picashow.repository.ThemeRepository
import io.b101.picashow.ui.components.BottomNavigation
import io.b101.picashow.ui.components.BottomNavigationItem
import io.b101.picashow.ui.components.CustomAlertDialog
import io.b101.picashow.ui.components.TopAppBar
import io.b101.picashow.ui.page.DetailScheduleUI
import io.b101.picashow.ui.page.DiaryPage
import io.b101.picashow.ui.page.MainPage
import io.b101.picashow.ui.page.firstPage
import io.b101.picashow.ui.page.textTutorialDone
import io.b101.picashow.ui.page.tutorialPage
import io.b101.picashow.ui.theme.MainBackground
import io.b101.picashow.ui.theme.imgMint
import io.b101.picashow.ui.theme.imgPurple
import io.b101.picashow.viewmodel.ThemeViewModel
import io.b101.picashow.viewmodel.ThemeViewModelFactory
import io.b101.picashow.viewmodel._myInfo
import kotlinx.coroutines.CoroutineScope

var tutorialStateCheck = mutableStateOf(false)
var deviceId = mutableStateOf("00000000")
@Composable
fun MainScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }

    var showAppBarAndNavBar by remember { mutableStateOf(true) }  // 상태 변수 추가
    val updatedNavController = rememberUpdatedState(navController)

    var showDialogTitle by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val themeDao = AppDatabase.getDatabase(context).themeDao()
    val themeRepository = ThemeRepository(themeDao)
    val themeViewModelFactory = ThemeViewModelFactory(themeRepository)

    val themeViewModel = viewModel<ThemeViewModel>(
        factory = themeViewModelFactory
    )

    // NavController의 back stack entry를 관찰
    val block: suspend CoroutineScope.() -> Unit = {
        updatedNavController.value.addOnDestinationChangedListener { _, destination, _ ->
            title = when (destination.route) {
                "firstPage" -> "Images"
                "secondPage" -> "Schedule"
                "thirdPage" -> "Diary"
                "addSchedulePage", "detailPage/{scheduleSeq}" -> "" // 이제 detailPage에서도 타이틀을 비웁니다.
                else -> "Schedule"
            }
            // addSchedulePage와 detailPage에서는 AppBar와 NavBar를 표시하지 않습니다.
            showAppBarAndNavBar = destination.route != "tutorialPage" && destination.route != "addSchedulePage" && destination.route?.contains("detailPage") != true

        }
    }
    LaunchedEffect(updatedNavController.value, block)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)  // 전체 배경색을 설정합니다.
    ) {
        val currentRoute = navController.currentDestination?.route

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            tutorialStateCheck.value = _myInfo.value?.isTutorial!!
            deviceId.value = _myInfo.value?.deviceId.toString()
            // showAppBarAndNavBar의 값에 따라 TopAppBar 표시
            if (showAppBarAndNavBar && tutorialStateCheck.value) {
//                val currentRoute = navController.currentDestination?.route
                TopAppBar(
                    startContent = {
                       when(currentRoute) {
                           "secondPage" -> {
                               Icon(
                                   Icons.Default.AccountCircle,
                                   contentDescription = "modify",
                                   tint = Color.White,
                                   modifier = Modifier.clickable {
                                       textTutorialDone.value = true
                                       navController.navigate("tutorialPage")
                                   }
                               )
                           }
                       }
                    },
                    title = title,
                    showIcon = currentRoute == "secondPage", // secondPage 일 때만 showIcon을 true로 설정
                    endContent = {
                        when (currentRoute) {
                            "secondPage" -> {
                                // 두 번째 페이지에서는 추가 버튼을 표시합니다.
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = Color.White,
                                    modifier = Modifier.clickable {
                                        navController.navigate("addSchedulePage")
                                    }
                                )
                            }
                            else -> Box {} // 다른 페이지에서는 아무것도 표시하지 않습니다.
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                val startDestination = if (!tutorialStateCheck.value) "tutorialPage" else "secondPage"
                NavHost(navController = navController, startDestination = startDestination) {
                    composable("firstPage") { FirstPage() }
                    composable("secondPage") { SecondPage(navController) }
                    composable("thirdPage") { ThirdPage() }
                    composable("addSchedulePage") { DetailScheduleUI(scheduleSeq = "0", navController = navController) }
//                    composable("addSchedulePage") { AddSchedulePage(navController) }
                    composable("tutorialPage") { tutorialPage(navController) }
                    composable("detailPage/{scheduleSeq}") { backStackEntry ->
                        // 여기에서 DetailPage Composable을 호출하고, scheduleSeq 파라미터를 전달합니다.
                        // 라우트에서 파라미터를 추출합니다.
                        val scheduleSeq = backStackEntry.arguments?.getString("scheduleSeq")
                        // null 체크
                        if (scheduleSeq != null) {
                            // DetailScheduleUI 컴포저블을 호출하고 필요한 파라미터를 전달합니다.
                            DetailScheduleUI(navController = navController, scheduleSeq = scheduleSeq)
                        } else {
                            // 여기서 에러를 처리하거나 다른 화면으로 돌아갑니다.
                            Log.e("Navigation", "scheduleSeq is null")
                            CustomAlertDialog(
                                title = "Error",
                                description = "일시적인 오류입니다.\n반복할 시 관리자에게 문의해주세요.",
                                onConfirm = {
                                    showDialogTitle = false
                                }
                            )
                        }
                    }
                }
            }
                val bottomNavItems = listOf(
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.paintdiary),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                colorFilter = if (currentRoute == "firstPage") ColorFilter.tint(
                                    imgPurple) else ColorFilter.tint(
                                    imgMint) // 선택 여부에 따라 색상을 설정합니다.
                            )
                        },
                        selected = navController.currentDestination?.route == "firstPage",
                        onClick = { if(navController.currentDestination?.route!="firstPage") navController.navigate("firstPage") }
                    ),
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.calender),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                colorFilter = if (currentRoute == "secondPage") ColorFilter.tint(imgPurple) else ColorFilter.tint(
                                    imgMint) // 선택 여부에 따라 색상을 설정합니다.
                            )
                        },
                        selected = navController.currentDestination?.route == "secondPage" || navController.previousBackStackEntry == null,
                        onClick = { if(navController.currentDestination?.route!="secondPage") navController.navigate("secondPage") }
                    ),
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.today),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp),
                                colorFilter = if (currentRoute == "thirdPage") ColorFilter.tint(imgPurple) else ColorFilter.tint(
                                    imgMint) // 선택 여부에 따라 색상을 설정합니다.
                            )
                        },
                        selected = navController.currentDestination?.route == "thirdPage",
                        onClick = { if(navController.currentDestination?.route!="thirdPage") navController.navigate("thirdPage") }
                    )
                )
            if (showAppBarAndNavBar && tutorialStateCheck.value) {
                BottomNavigation(
                    items = bottomNavItems
                )
            }
        }
    }
}

@Composable
fun FirstPage() {
    firstPage()
}

@Composable
fun SecondPage(navController : NavHostController) {
    // TODO 두번째 사실상 메인 페이지 - 할일 페이지임
    MainPage(navController)
}

@Composable
fun ThirdPage() {
    DiaryPage()
}