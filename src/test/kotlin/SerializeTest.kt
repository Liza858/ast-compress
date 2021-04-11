import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals


class SerializeTest {
    companion object {
        private const val TEST_DATA_BIG_PROJECT = "src/test/resources/testProjects/javapoet"
        private const val TEST_DATA_PATH_1 = "src/test/resources/testProjects/project1"
        private const val TEST_DATA_PATH_2 = "src/test/resources/testProjects/project2"
        private const val TMP_DIRECTORY = "src/test/resources/tmp"
        private const val FILE_FOR_TREES = "compressions_result"

        @BeforeAll
        @JvmStatic
        fun createTmpDirectory() {
            val testRoot = File(TMP_DIRECTORY)
            if (!testRoot.exists()) {
                testRoot.mkdirs()
            }
        }

        @AfterAll
        @JvmStatic
        fun cleanUp() {
            File(TMP_DIRECTORY).deleteRecursively()
        }
    }

    @Test
    fun `test serialization single file`() {
        for (i in 0..20) {
            val results = runCompress(File(TEST_DATA_PATH_1), i, File(TMP_DIRECTORY), FILE_FOR_TREES)
            val deserializedResults = deserializeCompressionsResults(File(TMP_DIRECTORY, FILE_FOR_TREES))
            assertEquals(results, deserializedResults)
        }
    }

    @Test
    fun `test serialization multiple files`() {
        for (i in 0..20) {
            val results = runCompress(File(TEST_DATA_PATH_2), i, File(TMP_DIRECTORY), FILE_FOR_TREES)
            val deserializedResults = deserializeCompressionsResults(File(TMP_DIRECTORY, FILE_FOR_TREES))
            assertEquals(results, deserializedResults)
        }
    }

    @Test
    fun `test serialization big project`() {
        val results = runCompress(File(TEST_DATA_BIG_PROJECT), 20, File(TMP_DIRECTORY), FILE_FOR_TREES)
        val deserializedResults = deserializeCompressionsResults(File(TMP_DIRECTORY, FILE_FOR_TREES))
        assertEquals(results, deserializedResults)
    }
}