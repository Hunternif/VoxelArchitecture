package hunternif.voxarch.editor.file

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import kotlin.reflect.KClass

private val yamlMapper = YAMLMapper()

fun serializeToYamlStr(value: Any, pretty: Boolean = false): String =
    if (pretty) yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value)
    else yamlMapper.writeValueAsString(value)

fun <T : Any> deserializeYaml(str: String, clazz: KClass<T>): T =
    yamlMapper.readValue(str, clazz.javaObjectType)
