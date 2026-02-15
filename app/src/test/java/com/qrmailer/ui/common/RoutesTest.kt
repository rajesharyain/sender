package com.qrmailer.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutesTest {

    @Test
    fun homeRoute_isCorrect() {
        assertEquals("home", Routes.HOME)
    }

    @Test
    fun qrOwnerEmailRoute_isCorrect() {
        assertEquals("qr_owner_email", Routes.QR_OWNER_EMAIL)
    }

    @Test
    fun qrScanRoute_isCorrect() {
        assertEquals("qr_scan", Routes.QR_SCAN)
    }

    @Test
    fun routes_areUnique() {
        val routes = listOf(Routes.HOME, Routes.QR_OWNER_EMAIL, Routes.QR_SCAN)
        assertEquals(routes.size, routes.distinct().size)
    }
}
