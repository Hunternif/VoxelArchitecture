package hunternif.voxarch.editor.file

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import kotlin.reflect.KClass

private val jsonMapper: JsonMapper by lazy {
    val module = SimpleModule()

    JsonMapper().apply {
        registerModule(module)
    }
}

fun serializeToJsonStr(value: Any, pretty: Boolean = false): String =
    if (pretty) jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
    else jsonMapper.writeValueAsString(value)

fun <T : Any> deserializeJson(str: String, clazz: KClass<T>): T =
    jsonMapper.readValue(str, clazz.javaObjectType)
