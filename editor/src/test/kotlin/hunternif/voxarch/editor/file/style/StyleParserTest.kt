package hunternif.voxarch.editor.file.style

import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.select
import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.sandbox.castle.turret.RoofShape
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class StyleParserTest {
    @Test
    fun `parse empty stylesheet`() {
        val rules = parseStylesheet("")
        assertRulesEqual(emptyList(), rules)
    }

    @Test
    fun `parse empty rule`() {
        val rules = parseStylesheet(".selector { }")
        val expected = Rule(select("selector"))
        assertRulesEqual(listOf(expected), rules)
    }

    @Test(expected = StyleParseException::class)
    fun `parse invalid empty selector`() {
        parseStylesheet("{ prop: value }")
    }

    @Test(expected = StyleParseException::class)
    fun `parse invalid unmatched brace`() {
        parseStylesheet(".selector { prop: value")
    }

    @Test(expected = StyleParseException::class)
    fun `parse invalid prop without value`() {
        parseStylesheet("""
            .selector {
                prop1:
                prop2: value
            }
        """.trimIndent())
    }

    @Test
    fun `parse unknown selector into asterisk`() {
        val rules = parseStylesheet("selector? { width: 100% }")
        val expected = Rule().apply {
//            width { 100.pct }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse base selector`() {
        val rules = parseStylesheet("""
                .selector {
                    visibility: VISIBLE
                }
            """.trimIndent())
        val expected = Rule(select("selector")).apply {
//            visible()
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse inline selector`() {
        val rules = parseStylesheet(".selector { height: 1; width: 2 }")
        val expected = Rule(select("selector")).apply {
//            height { 1.vx }
//            width { 2.vx }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse multiple rules with comments`() {
        val rules = parseStylesheet("""
            .selector { }
            
            /** My other selector */
            .selector {
                # comment-only line
                depth: 3 // EOL comment
            }
        """.trimIndent())
        val expected = listOf(
            Rule(select("selector")),
            Rule(select("selector")).apply {
//                depth { 3.vx }
            },
        )
        assertRulesEqual(expected, rules)
    }

    @Test
    fun `parse class selector`() {
        val rules = parseStylesheet("""
            .tower-4 {
                diameter: 4
            }
        """.trimIndent())
        val expected = Rule(select("tower-4")).apply {
//            diameter { 1.vx }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse type + class selector`() {
        val rules = parseStylesheet("""
            Room.tower {
                height: inherit
                width: 4 ~ 50%    // randomized value, 4 is natural size in voxels
            }
        """.trimIndent())
        val expected = Rule(select(Room::class.java).style("tower")).apply {
//            height { inherit() }
//            width { 4.vx to 50.pct }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse multiple AND-combined class selectors`() {
        val rules = parseStylesheet("""
            .tower.tall.a {
                height: 150%
            }
        """.trimIndent())
        val expected = Rule(select("tower", "tall", "a")).apply {
//            height { 150.pct }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse multiple OR-combined selectors`() {
        val rules = parseStylesheet("""
            .tower, .tall.room, PolyRoom {
                rotation: 0.0
            }
        """.trimIndent())
        val expected = Rule(
            select("tower"),
            select("tall", "room"),
            select(PolyRoom::class.java),
        ).apply {
//            rotation { set(0.0) }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse multiline selector list`() {
        val rules = parseStylesheet("""
            Wall,
            Floor
            { }
        """.trimIndent())
        val expected = Rule(
            select(Wall::class.java),
            select(Floor::class.java),
        )
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse descendant selector`() {
        val rules = parseStylesheet("""
            [.spire-castle] .tower {
                roof-shape: SPIRE
            }
        """.trimIndent())
        val expected = Rule(selectDescendantOf("spire-castle").style("tower")).apply {
//            roofShape { set(RoofShape.SPIRE) }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse multiple OR-combined descendant selectors`() {
        val rules = parseStylesheet("""
            [.inner-wall, .outer-wall] Wall {
                depth: -1
            }
        """.trimIndent())
        val expected = Rule(
            selectDescendantOf("inner-wall") +
                selectDescendantOf("outer-wall") +
                select(Wall::class.java)
        ).apply {
//            depth { set(-1.vx) }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse direct child selector`() {
        val rules = parseStylesheet("""
            .tower-wall > .window {
                padding-x: 1
            }
        """.trimIndent())
        val expected = Rule(selectChildOf("tower-wall").style("window")).apply {
//            paddingX { 1.vx }
        }
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    @Ignore("Blueprint execution not implemented yet")
    fun `parse blueprint execution rule`() {
        val rules = parseStylesheet("""
            .turret {
                blueprint: "Turret Decor BP"
            }
        """.trimIndent())
        val expected = Rule(select("turret"))
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse spaced selector`() {
        val rules = parseStylesheet("Room Wall .abc .f123 { }")
        val expected = Rule(
            select(Room::class.java, Wall::class.java).style("abc", "f123")
        )
        assertRulesEqual(listOf(expected), rules)
    }

    @Test
    fun `parse any selector`() {
        val rules = parseStylesheet("""
            * {
                visibility: gone
            }
        """.trimIndent())
        val expected = Rule().apply {
//            gone()
        }
        assertRulesEqual(listOf(expected), rules)
    }

    private fun assertRulesEqual(expected: List<Rule>, actual: List<Rule>) {
        assertEquals(expected.size, actual.size)
        expected.zip(actual).forEach { (exp, act) ->
            assertEquals(exp.toString(), act.toString())
        }
    }
}