package hunternif.voxarch.editor.file

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import hunternif.voxarch.editor.blueprint.Blueprint
import kotlin.reflect.KClass

private val jsonMapper: JsonMapper by lazy {
    val module = SimpleModule()

    module.addSerializer(BlueprintSerializer())
    module.addDeserializer(Blueprint::class.java, BlueprintDeserializer())

    JsonMapper().apply {
        registerModule(module)
    }
}

fun serializeToJsonStr(value: Any, pretty: Boolean = false): String =
    if (pretty) jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
    else jsonMapper.writeValueAsString(value)

fun <T : Any> deserializeJson(str: String, clazz: KClass<T>): T =
    jsonMapper.readValue(str, clazz.javaObjectType)

private class BlueprintSerializer : StdSerializer<Blueprint>(Blueprint::class.java) {
    override fun serialize(
        value: Blueprint,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        gen.writeObject(value.mapToJson())
    }
}

private class BlueprintDeserializer : StdDeserializer<Blueprint>(Blueprint::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Blueprint? {
        return p.readValueAs(JsonBlueprint::class.java)?.mapJson()
    }
}