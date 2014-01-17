VoxelArchitecture
=================

This ambitious little project attemps to find an algorithm or a framework for procedural building of 3-dimensiomal voxel architectural structures, such that:
* Structures can have clearly defined style, e.g. consistent usage of certain elements (columns, long corridors, parapets etc), building materials (cobblesone, sandstone etc) and geometrical shapes (square arches, round arches, protruding roof etc);
* Structures have multiple floors, which can be connected with rooms several floors high;
* Structures analyze and make use of underlying terrain, e.g. a castle can have towers on mountain peaks, bridges over ravines and over rough or uneven ground.
* Buildings can have complex corridor systems.[?]

Formal grammars can be useful in generating such structures.

The program can be used for procedural building in many applications, such as Minecraft and its clones. The architecture of the program should be general enough to allow specific implementations define the format in which to store the voxel data and paste it into actual voxel worlds. The Bridge pattern could help make the program abstract from implementations.

Projects
--------
* VoxArchCore will contain all the generation algorithms for buildings and interfaces for voxel storage.
* VoxArchMCAPI will act as a reference implementation of voxel storage; the actual interface implementations will act as adaptors to a Minecraft world.
* VoxArchMCMod will be a complete Minecraft Forge Mod for experimenting with building structures.
