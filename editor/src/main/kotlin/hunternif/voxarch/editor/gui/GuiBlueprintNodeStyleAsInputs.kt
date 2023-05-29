package hunternif.voxarch.editor.gui

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.setBlueprintNodeStyle
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.PropBlueprint
import hunternif.voxarch.editor.blueprint.blueprintEditorStyleProperties
import hunternif.voxarch.editor.builder.PropEditorBuilder
import imgui.ImGui

/**
 * Renders Blueprint node style as a list of input UIs with checkboxes.
 */
class GuiBlueprintNodeStyleAsInputs(
    private val app: EditorApp,
    private val node: BlueprintNode,
) {
    private val rule: Rule = node.rule

    /** If history changes, we need to update items */
    private var lastAction: HistoryAction? = null

    private val builderCombo by lazy {
        ItemStringCombo(this, PropEditorBuilder,
            app.state.builderLibrary.buildersByName.keys)
    }

    private val blueprintCombo by lazy {
        ItemStringCombo(this, PropBlueprint,
            app.state.blueprintLibrary.blueprintsByName.keys)
    }

    @Suppress("TYPE_MISMATCH_WARNING", "UNCHECKED_CAST")
    val items: List<Item<*>> by lazy {
        blueprintEditorStyleProperties.mapNotNull { p ->
            when (p) {
                PropEditorBuilder -> builderCombo
                PropBlueprint -> blueprintCombo
                else -> when (p.default) {
                    is Int -> ItemNumber(this, p as Property<Int>)
                    is Long -> ItemNumber(this, p as Property<Long>)
                    is Float -> ItemNumber(this, p as Property<Float>)
                    is Double -> ItemNumber(this, p as Property<Double>)
                    is Enum<*> -> ItemEnum(this, p as Property<Enum<*>>)
                    else -> null
                }
            }
        }
    }

    init {
        refreshDeclarations()
    }

    fun render() {
        checkForUpdates()
        disabled {
            ImGui.textWrapped("Note that dynamic values may not be represented correctly here")
        }
        items.forEach { it.render() }
    }

    /** Update declaration content from the rule */
    private fun refreshDeclarations() {
        val propMap = rule.propertyMap // calculate it once
        items.forEach { it.updateDeclaration(propMap) }
    }

    private fun checkForUpdates() {
        val newLastAction = app.state.history.last()
        if (lastAction != newLastAction) {
            lastAction = newLastAction
            refreshDeclarations()
            updateBuilders()
            updateBlueprints()
        }
    }

    private fun updateBuilders() {
        // Check if any new builders were added to the library
        val builderMap = app.state.builderLibrary.buildersByName
        if (builderCombo.gui.values.size != builderMap.size) {
            blueprintCombo.gui.values = builderMap.keys.toList()
        }
    }

    private fun updateBlueprints() {
        // Check if any new blueprints were added to the library
        val bpMap = app.state.blueprintLibrary.blueprintsByName
        if (blueprintCombo.gui.values.size != bpMap.size) {
            blueprintCombo.gui.values = bpMap.keys.toList()
        }
    }

    private inline fun submitStyleChange(
        crossinline block: () -> Unit,
    ) {
        val oldDecls = rule.declarationsToString()
        block()
        val newDecls = rule.declarationsToString()
        app.setBlueprintNodeStyle(node, oldDecls, newDecls)
    }

    abstract class Item<V : Any>(
        val rootGui: GuiBlueprintNodeStyleAsInputs,
        val property: Property<V>,
    ) {
        protected val rule = rootGui.rule
        private val checkbox = GuiCheckbox("##enabled_${property.name}")
        var declaration: Declaration<V> = Declaration.defaultForProperty(property)
        lateinit var value: V

        private var enabled: Boolean = property in rule.propertyMap

        /**
         * Calculates value from the declaration, to display it in the UI.
         * Note that this can give incorrect results for dynamic values,
         * e.g. if it's %-based.
         */
        protected abstract fun findValue(declaration: Declaration<V>) : V

        fun render() {
            if (!::value.isInitialized) value = findValue(declaration)
            checkbox.render(enabled) {
                enabled = it
                if (enabled) {
                    rootGui.submitStyleChange { rule.add(declaration) }
                } else {
                    rootGui.submitStyleChange { rule.remove(declaration) }
                }
            }
            ImGui.sameLine()
            disabled(!enabled) {
                withWidth(100f) {
                    renderInput()
                }
            }
        }

        protected abstract fun renderInput()

        /** Called when the underlying rule was updated from the outside, e.g. history */
        fun updateDeclaration(propMap: PropertyMap) {
            val newDeclaration = propMap[property]
            if (newDeclaration != null) {
                enabled = true
                declaration = newDeclaration
                value = findValue(newDeclaration)
            } else {
                enabled = false
            }
        }
    }

    class ItemNumber<N : Number>(
        rootGui: GuiBlueprintNodeStyleAsInputs,
        property: Property<N>,
        min: Float = -999f,
        max: Float = 999f,
    ) : Item<N>(rootGui, property) {
        private val gui = GuiInputFloat(property.name, min = min, max = max,
            mantissaFormat = "%.2f")

        // For dynamic properties, this will be incorrect
        override fun findValue(declaration: Declaration<N>): N =
            declaration.value.invoke(property.default, 0L)

        override fun renderInput() {
            gui.render(value.toFloat()) {
                @Suppress("UNCHECKED_CAST")
                value = gui.newValue as N
                rootGui.submitStyleChange { declaration.value = set(value) }
            }
        }
    }

    class ItemEnum<E : Enum<E>>(
        rootGui: GuiBlueprintNodeStyleAsInputs,
        property: Property<E>,
    ) : Item<E>(rootGui, property) {
        private val values = property.valType.enumConstants
        private val gui = GuiCombo(property.name, *values)

        override fun findValue(declaration: Declaration<E>): E =
            declaration.value.invoke(property.default, 0L)

        override fun renderInput() {
            gui.render(value) {
                value = it
                rootGui.submitStyleChange { declaration.value = set(value) }
            }
        }
    }

    class ItemStringCombo(
        rootGui: GuiBlueprintNodeStyleAsInputs,
        property: Property<String>,
        values: Iterable<String>,
    ) : Item<String>(rootGui, property) {
        val gui = GuiCombo(property.name, values)

        override fun findValue(declaration: Declaration<String>): String =
            declaration.value.invoke(property.default, 0L)

        override fun renderInput() {
            gui.render(value) {
                value = it
                rootGui.submitStyleChange { declaration.value = set(value) }
            }
        }
    }
}