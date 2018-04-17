package jb.kgull

import commons.IndentedAppendable
import commons.nested
import org.junit.Assert.assertEquals
import org.junit.Test

class IndentedAppendableTest {
    @Test
    fun testSimple() {
        val root = IndentedAppendable().also {
            it.append("a")
            it.append("b")
            it.append("line1\nline2\nline3")
            it.append("c")
            it.append("line1\nline2\nline3")
            it.append("d")
            it.nested {
                it.append("a")
                it.append("b")
                it.append("line1\nline2\nline3")
                it.append("c")
                it.append("line1\nline2\nline3")
                it.append("d")
                it.nested {
                    it.append("a")
                    it.append("b")
                    it.append("line1\nline2\nline3")
                    it.append("c")
                    it.append("line1\nline2\nline3")
                    it.append("d")
                }
            }
            it.nested {
                it.append("a")
                it.append("b")
                it.append("line1\nline2\nline3")
                it.append("c")
                it.append("line1\nline2\nline3")
                it.append("d")
                it.nested {
                    it.append("a")
                    it.append("b")
                    it.append("line1\nline2\nline3")
                    it.append("c")
                    it.append("line1\nline2\nline3")
                    it.append("d")
                }
            }
        }

        assertEquals(
                """
abline1
line2
line3cline1
line2
line3dabline1
  line2
  line3cline1
  line2
  line3dabline1
    line2
    line3cline1
    line2
    line3dabline1
  line2
  line3cline1
  line2
  line3dabline1
    line2
    line3cline1
    line2
    line3d
                """.trim(),
                root.out.toString()
        )
    }
}