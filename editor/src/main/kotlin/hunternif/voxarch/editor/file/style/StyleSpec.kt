package hunternif.voxarch.editor.file.style

/*

Specification for "CSS" style text format.

selector {
    property: value
    ...
}

// semicolon is optional, allows inlining:
selector { prop1: value1; prop2: value2 }

// class selector:
.tower-4 {
    diameter: 4
}

// type + class selector:
Room.tower {
    height: 100%
    width: 4 ~ 50%    // randomized value, 4 is natural size in voxels
}

// multiple AND-combined class selectors:
.tower.tall {
    height: 150%
}

// multiple OR-combined selectors:
.tower, .room, Prop {
    rotation: 0
}

// descendant selector:
[.spire-castle] .tower {
    roof-shape: SPIRE
}

// multiple OR-combined selector:
[.inner-wall, .outer-wall] wall {
    depth: 1
}

// direct child selector:
.tower-wall > .window {
    padding-x: 1
    height: 4
}

// blueprint execution:
.turret {
    blueprint: "Turret Decor BP"
}

 */