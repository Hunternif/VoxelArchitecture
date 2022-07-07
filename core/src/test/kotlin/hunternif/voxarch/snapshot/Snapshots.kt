package hunternif.voxarch.snapshot

import hunternif.voxarch.magicavoxel.readVoxFile
import hunternif.voxarch.util.assertStorageEquals
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.JUnitCore
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import org.reflections.Reflections
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.extension

class Snapshots {
    private val reflections = Reflections("hunternif.voxarch.snapshot")
    private val junit = JUnitCore().apply {
        addListener(object : RunListener() {
            override fun testFailure(failure: Failure) {
                Assert.fail(failure.trace)
            }
        })
    }

    @Test
    fun verifyAllSnapshots() {
        clearSnapshotsDir()
        generateAllSnapshots()
        val errors = doVerifyAll()
        assert(errors.isEmpty()) {
            "Snapshots failed:\n" + errors.joinToString("\n")
        }
    }

    private fun clearSnapshotsDir() = FileUtils.deleteDirectory(SNAPSHOTS_DIR.toFile())

    private fun generateAllSnapshots() {
        val testClasses = reflections.getSubTypesOf(BaseSnapshotTest::class.java)
        for (testClass in testClasses) {
            junit.run(testClass)
        }
    }

    /** Returns errors */
    private fun doVerifyAll(): List<String> {
        val errors = mutableListOf<String>()
        REFERENCES_DIR.onDirs { refDir ->
            refDir.onFiles { refFile ->
                val testFile = SNAPSHOTS_DIR.resolve("${refDir.fileName}/${refFile.fileName}")
                if (Files.exists(testFile)) {
                    if (refFile.extension.lowercase() == "png")
                        errors.addAll(compareImages(refFile, testFile))
                    else if (refFile.extension.lowercase() == "vox") {
                        errors.addAll(compareVox(refFile, testFile))
                    }
                } else {
                    errors.add("Missing snapshot $refFile")
                }
            }
        }
        return errors
    }

    /** Returns errors */
    private fun compareImages(ref: Path, test: Path): List<String> {
        val refImage = ImageIO.read(ref.toFile())
        val testImage = ImageIO.read(test.toFile())
        assert(refImage.width == testImage.width) { "width mismatch in $ref" }
        assert(refImage.height == testImage.height) { "height mismatch in $ref" }
        for (x in 0 until refImage.width) {
            for (y in 0 until refImage.height) {
                if (refImage.getRGB(x, y) != testImage.getRGB(x, y)) {
                    return listOf("image mismatch in $ref")
                }
            }
        }
        return emptyList()
    }

    /** Returns errors */
    private fun compareVox(ref: Path, test: Path): List<String> {
        val refVox = readVoxFile(ref)
        val testVox = readVoxFile(test)
        try {
            assertStorageEquals(refVox, testVox)
        } catch (e: AssertionError) {
            return listOf("vox file mismatch in $ref, ${e.message ?: ""}")
        }
        return emptyList()
    }

    companion object {
        val REFERENCES_DIR: Path = Paths.get("./src/test/snapshots")
        val SNAPSHOTS_DIR = BaseSnapshotTest.SNAPSHOTS_DIR

        private fun Path.onDirs(action: (Path) -> Unit) {
            Files.newDirectoryStream(this).use { dirs ->
                for (dir in dirs) {
                    check(Files.isDirectory(dir)) { "Illegal file $dir" }
                    action(dir)
                }
            }
        }

        private fun Path.onFiles(action: (Path) -> Unit) {
            Files.newDirectoryStream(this).use { files ->
                for (file in files) {
                    check(Files.isRegularFile(file)) { "Illegal file $file" }
                    action(file)
                }
            }
        }
    }
}