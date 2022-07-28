package hunternif.voxarch.editor.file

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import hunternif.voxarch.vector.Vec3
import kotlin.reflect.KClass

private val xmlMapper: XmlMapper by lazy {
    val module = SimpleModule()
    module.addSerializer(Vec3Serializer())
    module.addDeserializer(Vec3::class.java, Vec3Deserializer())
    XmlMapper().apply {
        registerModule(module)
    }
}


fun serializeToXmlStr(value: Any, pretty: Boolean = false): String =
    if (pretty) xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
    else xmlMapper.writeValueAsString(value)

fun <T : Any> deserializeXml(str: String, clazz: KClass<T>): T =
    xmlMapper.readValue(str, clazz.javaObjectType)


private class Vec3Serializer : StdSerializer<Vec3>(Vec3::class.java) {
    override fun serialize(
        value: Vec3,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        gen.writeString(value.run { "($x, $y, $z)" })
    }
}

private class Vec3Deserializer : StdDeserializer<Vec3>(Vec3::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Vec3 {
        val str = p.valueAsString
        // strip '(' & ')', then separate the numbers
        val nums = str.subSequence(1, str.length - 1).split(", ")
        val x = nums[0].toDouble()
        val y = nums[1].toDouble()
        val z = nums[2].toDouble()
        return Vec3(x, y, z)
    }
}