package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.BizStockViewModel
import com.example.ui.viewmodel.NavigationDestination
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthFlowRobolectricTest {

    private lateinit var app: Application
    private lateinit var viewModel: BizStockViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        if (FirebaseApp.getApps(app).isEmpty()) {
            FirebaseApp.initializeApp(
                app,
                FirebaseOptions.Builder()
                    .setApplicationId("1:393619614756:android:3cb4edd2820147b36afcb1")
                    .setProjectId("gen-lang-client-0378172908")
                    .setApiKey("AIzaSyDummyKeyForLocalTestingPurposesOnly")
                    .build()
            )
        }
        viewModel = BizStockViewModel(app)
    }

    @Test
    fun `initial destination is SPLASH`() {
        assertEquals(NavigationDestination.SPLASH, viewModel.currentDestination.value)
    }

    @Test
    fun `unauthenticated user cannot access private dashboard and is redirected to LOGIN`() {
        // Ensure logged out
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)

        // Try navigating to DASHBOARD
        viewModel.setDestination(NavigationDestination.DASHBOARD)
        assertEquals(NavigationDestination.LOGIN, viewModel.currentDestination.value)

        // Try navigating to STOCK
        viewModel.setDestination(NavigationDestination.STOCK)
        assertEquals(NavigationDestination.LOGIN, viewModel.currentDestination.value)

        // Try navigating to SALES
        viewModel.setDestination(NavigationDestination.SALES)
        assertEquals(NavigationDestination.LOGIN, viewModel.currentDestination.value)
    }

    @Test
    fun `unauthenticated user can navigate between LOGIN and REGISTER`() {
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)

        viewModel.setDestination(NavigationDestination.REGISTER)
        assertEquals(NavigationDestination.REGISTER, viewModel.currentDestination.value)

        viewModel.setDestination(NavigationDestination.LOGIN)
        assertEquals(NavigationDestination.LOGIN, viewModel.currentDestination.value)
    }

    @Test
    fun `successful login enables access to private destinations`() {
        // Authenticate with valid non-blank inputs
        viewModel.login("directeur@testbizstock.com", "password123")
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals(NavigationDestination.DASHBOARD, viewModel.currentDestination.value)

        // Now navigating to STOCK is permitted
        viewModel.setDestination(NavigationDestination.STOCK)
        assertEquals(NavigationDestination.STOCK, viewModel.currentDestination.value)

        // Logout resets session and sends to LOGIN
        viewModel.logout()
        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(NavigationDestination.LOGIN, viewModel.currentDestination.value)
        assertEquals("", viewModel.userEmail.value)
    }
}
