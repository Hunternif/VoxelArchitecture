package hunternif.voxarch.plan

var Node.height: Double
    get() = when (this) {
        is Room -> this.height
        is Wall -> this.height
        else -> 0.0
    }
    set(value) {
        when (this) {
            is Room -> this.height = value
            is Wall -> this.height = value
        }
    }

var Node.width: Double
    get() = when (this) {
        is Room -> this.width
        else -> 0.0
    }
    set(value) {
        when (this) {
            is Room -> this.width = value
        }
    }

var Node.length: Double
    get() = when (this) {
        is Room -> this.length
        is Wall -> this.length
        else -> 0.0
    }
    set(value) {
        when (this) {
            is Room -> this.length = value
            is Wall -> this.length = value
        }
    }