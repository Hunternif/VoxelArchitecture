# VoxelArchitecture

This project is a framework for procedural generation of 3d voxel buildings, mainly castles. In the end, it should support the following features:
* Switchable architectural styles, which affect the shape and placement of rooms, decorations, building materials, etc. _E.g. choose between square/round arches, slanted/flat roofs._
* Complex internal & external structure. _E.g. corridors that go in multiple directions and bridges that cross over each other._
* Aesthetically pleasing appearance.
* Fully traversable interiors.
* Using underlying terrain as a guide. _E.g. build a castle on a hill, build bridges over ravines._

See [Wiki](https://github.com/Hunternif/VoxelArchitecture/wiki) for more details.

## Modules

* **`core`** contains all the generation algorithms for buildings and interfaces for voxel storage, with minimal dependencies, decoupled from concrete implementations such as Minecraft.
* **`mc-mod`** is a Minecraft Forge Mod for experimenting with building structures. It implements the voxel storage interfaces from core.
* **`editor`** is a powerful editor for manually constructing the node structure and generating voxels. It uses `LWJGL` for 3D rendering and `Dear-ImGui` for UI.


## `core` overview

The first part is a framework that represents abstract architectural nodes in code. It has yielded some interesting results:
* Builder classes that recursively generate voxels from a nested structure of nodes.
* Kotlin DSL for assembling the Node structure.
* Another Kotlin DSL for declaring a "DOM" (logical hierarchy of nodes) and a "Stylesheet" (relative positions and sizes of Nodes), similar to HTML & CSS.
* Support for MagicaVoxel VOX format.
* Unit-testing framework using 2D slices and 3D snapshots of generated voxels.

The second part is the procedural generation algorithm. It's still in early stages, and I'm trying out different approaches:
* declaratively specifying all structures in code
* randomly branching into different directions for different rooms
* WFC
* ...

## `editor` preview

[![Editor demo, click to see video](https://drive.google.com/uc?id=1Ur1-t52ME9Ok0qzZsVWvDeB8PInjo2bq)](https://video.twimg.com/ext_tw_video/1495597787773386754/pu/vid/1008x604/l7ox57HoSsarY-mF.mp4?tag=12)
