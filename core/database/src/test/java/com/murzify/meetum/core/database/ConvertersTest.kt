package com.murzify.meetum.core.database

import org.junit.Assert
import org.junit.Test
import java.util.Currency
import java.util.Date
import java.util.UUID

class ConvertersTest {

    private val converters = Converters()
    private val testUUID = "e1f238b5-b7c5-48f8-9e15-679dfc0bb7a6"
    @Test
    fun `should return correct string uuid`() {
        val actual = converters.fromUUID(UUID.fromString(testUUID))
        Assert.assertEquals(testUUID, actual)
    }

    @Test
    fun `should return correct uuid from string`() {
        val expected = UUID.fromString(testUUID)
        val actual = converters.toUUID(testUUID)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return correct string currency`() {
        val expected = "USD"
        val actual = converters.fromCurrency(Currency.getInstance(expected))
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return currency from string`() {
        val expected = Currency.getInstance("USD")
        val actual = converters.toCurrency("USD")
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return timestamp`() {
        val expected = 123123L
        val actual = converters.fromDate(Date(expected))
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return date from timestamp`() {
        val expected = Date(123123)
        val actual = converters.toDate(123123)
        Assert.assertEquals(expected, actual)
    }

}