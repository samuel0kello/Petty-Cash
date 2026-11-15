package com.example.pettycash.mai

import com.example.pettycash.mai.components.formatKes
import org.junit.Assert.assertEquals
import org.junit.Test

class MaiFormatTest {

    @Test
    fun formatKesUsesThousandsSeparators() {
        assertEquals("KES 1,500", formatKes(1500))
        assertEquals("KES 48,200", formatKes(48200))
        assertEquals("KES 0", formatKes(0))
    }
}
