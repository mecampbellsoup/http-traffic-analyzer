package http.traffic.analyzer.tests

import org.junit.Test
import kotlin.test.assertEquals

class TestSource() {
    @Test
    fun f() {
        val string = "foo bar baz"
        assertEquals(string.length, 11)
    }
}
