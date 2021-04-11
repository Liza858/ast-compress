import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNull
import kotlin.test.assertTrue


class CompressionsTest {
    companion object {
        private const val TEST_DATA_PATH_EMPTY = "src/test/resources/testProjects/empty"
        private const val TEST_DATA_PATH_1 = "src/test/resources/testProjects/project1"
        private const val TEST_DATA_PATH_2 = "src/test/resources/testProjects/project2"
        private const val TEST_DATA_BIG_PROJECT = "src/test/resources/testProjects/javapoet"
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
    fun `test many compressions`() {
        val compressionsNumber = 10000
        var exception: Exception? = null
        try {
            runCompress(File(TEST_DATA_PATH_1), compressionsNumber, File(TMP_DIRECTORY), FILE_FOR_TREES)
            runCompress(File(TEST_DATA_PATH_2), compressionsNumber, File(TMP_DIRECTORY), FILE_FOR_TREES)
        } catch (e: Exception) {
            exception = e
        }
        assertNull(exception)
    }

    @Test
    fun `test empty project`() {
        for (i in 0..20) {
            val results = runCompress(File(TEST_DATA_PATH_EMPTY), i, File(TMP_DIRECTORY), FILE_FOR_TREES)
            assertTrue(results.isEmpty())
        }
    }

    @Test
    fun `test simple projects`() {
        val compressionsNumber = 30
        var exception: Exception? = null
        try {
            for (i in 0..compressionsNumber) {
                runCompress(File(TEST_DATA_PATH_1), compressionsNumber, File(TMP_DIRECTORY), FILE_FOR_TREES)
                runCompress(File(TEST_DATA_PATH_2), compressionsNumber, File(TMP_DIRECTORY), FILE_FOR_TREES)
            }
        } catch (e: Exception) {
            exception = e
        }
        assertNull(exception)
    }

    @Test
    fun `test big project`() {
        val compressionsNumber = 30
        var exception: Exception? = null
        try {
            runCompress(File(TEST_DATA_BIG_PROJECT), compressionsNumber, File(TMP_DIRECTORY), FILE_FOR_TREES)
        } catch (e: Exception) {
            exception = e
        }
        assertNull(exception)
    }
}