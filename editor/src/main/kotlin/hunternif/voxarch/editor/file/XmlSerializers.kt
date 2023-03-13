package hunternif.voxarch.editor.file

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.plan.Node
import hunternif.voxarch.vector.Vec3
import kotlin.reflect.KClass

private val xmlMapper: XmlMapper by lazy {
    val module = SimpleModule()

    module.addSerializer(Vec3Serializer())
    module.addDeserializer(Vec3::class.java, Vec3Deserializer())

    module.addSerializer(NodeSerializer())
    module.addDeserializer(Node::class.java, NodeDeserializer())

    module.addSerializer(SceneObjectSerializer())
    module.addDeserializer(SceneObject::class.java, SceneObjectDeserializer())

    module.addSerializer(BlueprintSerializer())
    module.addDeserializer(Blueprint::class.java, BlueprintDeserializer())

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

private class NodeSerializer : StdSerializer<Node>(Node::class.java) {
    override fun serialize(
        value: Node,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        gen.writeObject(value.mapToXmlNode())
    }
}

private class NodeDeserializer : StdDeserializer<Node>(Node::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Node? {
        return p.readValueAs(XmlNode::class.java)?.mapXmlNode()
    }
}

private class SceneObjectSerializer : StdSerializer<SceneObject>(SceneObject::class.java) {
    override fun serialize(
        value: SceneObject,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        gen.writeObject(value.mapToXml())
    }
}

private class SceneObjectDeserializer : StdDeserializer<SceneObject>(SceneObject::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): SceneObject? {
        return p.readValueAs(XmlSceneObject::class.java)?.mapXml()
    }
}

private class BlueprintSerializer : StdSerializer<Blueprint>(Blueprint::class.java) {
    override fun serialize(
        value: Blueprint,
        gen: JsonGenerator,
        provider: SerializerProvider
    ) {
        gen.writeObject(value.mapToXml())
    }
}

private class BlueprintDeserializer : StdDeserializer<Blueprint>(Blueprint::class.java) {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Blueprint? {
        return p.readValueAs(XmlBlueprint::class.java)?.mapXml()
    }
}