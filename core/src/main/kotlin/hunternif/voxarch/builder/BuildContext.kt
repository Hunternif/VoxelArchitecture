package hunternif.voxarch.builder

import hunternif.voxarch.world.Environment

class BuildContext(val env: Environment) {
    val materials = MaterialConfig()
    val builders = BuilderConfig()
}