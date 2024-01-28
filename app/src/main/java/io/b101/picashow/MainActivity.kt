package io.b101.picashow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import io.b101.picashow.viewmodel.MemberViewModel
import io.b101.picashow.viewmodel.MemberViewModelFactory
import io.b101.picashow.viewmodel.please

class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Surface(color = MaterialTheme.colors.background) {
                val application = applicationContext as io.b101.picashow.PicaShowApp
                val memberViewModelFactory = MemberViewModelFactory(application.repository)
                val memberViewModel = viewModel<MemberViewModel>(
                    factory = memberViewModelFactory
                )
                memberViewModel.getMember(1L)
                val navController = rememberNavController()
                if(please.value) io.b101.picashow.MainScreen(navController = navController)
            }
        }
    }
}