package hunternif.voxarch.sandbox.castle.dsl


// Castle DSL could look like this.
// Each inline function has DslBuilder as receiver.
// DslBuilder implements the "Builder" pattern.

// Castle DOM tree:
/*
ward(styleClass = "ward") {
    // option A for sizes etc: use CSS for this type
    // option B for sizes:
    size = parent.width mapTo {random(0.5, 0.7) * it.width}
    shape = randomBody()
    ...

    // building in the center of the ward.
    // The center alignment will be defined in CSS.
    turret("keep") {
        // decoration with 4 turrets in corners
        fourCorners("keep_deco") {
            // the actual turret that is copied in every corner
            turret {}
        }
    }
    // castle Keep attached to one of the walls.
    // The walls don't need to be built at this point.
    randomWall {
        turret("keep") {
            fourCorners {
                turret {}
            }
        }
    }
    // a tower in each ward corner
    allCorners { // each child must look the same, so same seed?
        turret("ward_turret") {
            randomNestedTurrets()
        }
    }
    // normal walls between "corners"
    allWalls {
        wall("ward_wall") {}
    }
}
*/

// Rules for secondary parameters like size, style. Sorta like CSS.
/*
style(styleClass = "ward") {
    // vx means voxel, i.e. exact block size
    width { 30.vx to 50.vx } // random size, get seed from parent
    // parent is a built turret instance
    if (parent.width > 6) {
        shape = randomBody()
    } else {
        shape = SQUARE
    }
}
style("ward_turret") {
    // Ensure that all immediate children will have the same style:
    seed = parent.seed
    width {
        // calculates randomized size. Width is always rounded to even.
        min = 2.vx
        max = 12.vx
        10.pct to 80.pct // random percent of parent width.
    }
    height {
        min = 4.vx
        100.pct // 100% of parent height
    }
    bodyShape = randomBody() // uses seed
    roofShape = randomRoof()
    if (level < 1) {
        bottomShape = FOUNDATION
    } else {
        bottomShape = random(TAPERED, FOUNDATION)
    }
}
style("ward_wall") {
    paddingTop { 4.vx }
    height { 100.pct }
}
style("keep") {
    height {
        100.pct to 200.pct
    }
    bottomShape = FOUNDATION
    paddingBottom {
        4.vx to 20.vx // effectively creates elevation
    }
    // Place in the center of the parent Wall or Room
    align = CENTER
}
style("keep_deco") {
    // This prevents decorations when the turret is too small
    if (parent.width <= 4) {
        visibility = GONE
    }
}
*/

// Upsides to the tree structure:
// 1. Easy to read.
// 2. Separates the boilerplate code for deciding sizes, and the turrets DSL
// 3. This is strictly an improvement over current approach!

// Downsides to the tree structure:
// 1. Bridges that connect turrets are described separately.
// 2. If I want to decide some decoration based on turret size, I need to find
//    the size of parent. To do that I will need to recursively find the sizes
//    of all parents.
//      - CSS helps! use @media 'min-width'

// Rules
// CSS sizes propagate from parent to child. Parent's attributes should not be
// decided based on child's random sizes!
// Parent passes to children its modified random seed (e.g. seed + i).
// The CSS style can decide to use the parent's seed, so that all immediate
// children of that parent will look the same. But the children's children still
// have their own seeds!
