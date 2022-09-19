package hunternif.voxarch.editor.builder

import hunternif.voxarch.editor.AppState
import hunternif.voxarch.generator.GenAddRoom
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.generator.TurretGenerator

typealias GeneratorFactory = AppState.() -> IGenerator

private val generatorsByClass: Map<Class<out IGenerator>, GeneratorFactory> = mapOf(
    TurretGenerator::class.java to { TurretGenerator() },
    GenAddRoom::class.java to { GenAddRoom() },
)

val generatorsByName: Map<String, GeneratorFactory> =
    generatorsByClass.mapKeys { it.key.simpleName }


fun AppState.createGeneratorByName(classname: String): IGenerator? =
    generatorsByName[classname]?.let { it() }
