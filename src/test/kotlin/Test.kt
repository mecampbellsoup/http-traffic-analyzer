package httptrafficanalyzer.tests

import kotlin.test.assertEquals
import org.junit.Test

class TestSource() {
    @Test
    fun f() {
        val string = "foo bar baz"
        assertEquals(string.length, 11)
    }
}
