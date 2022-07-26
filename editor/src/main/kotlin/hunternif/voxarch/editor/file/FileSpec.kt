package hunternif.voxarch.editor.file

/*

Project folder structure:

/ project
  - metadata.yaml
  - nodes.xml
  / voxels
    - generated.vox
    - imported-1.vox
    ...
  / generators
    - turret.kt
    - castle.kt
    ...

All contents are stored as separate files in a folder.
The folder could be zipped into a single file.


0. Metadata
Format version, project name etc.

1. Nodes
Serialized to XML. Raw node objects are mapped to a DTO class and serialized
via annotations.

    Example:
    <structure origin="(0, 0, 0)">
        <room>
            <wall>
            <wall>
        </room>
    </structure>


2. Voxels
Serialized to VOX.


3. Generators
Using text format that matches Kotlin "Castle DSL" code.
- also could be XML, because tree structure.
Serializers will be code-generated from annotated DSL builder methods.
    Example: see CastleWardTest.kt

*/