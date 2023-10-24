package com.example.bioeyetest.csv_generation

import com.example.bioeyetest.face_recognition.FaceRecognitionData
import com.example.bioeyetest.utils.FileManager
import com.example.bioeyetest.utils.toIso8601
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

interface SessionCSVGenerator {
    suspend fun generateCSV(
        data: List<FaceRecognitionData>,
        fileName: String
    ): Result<File>
}

class SessionCSVGeneratorImpl @Inject constructor(
    private val fileManager: FileManager,
) : SessionCSVGenerator {
    override suspend fun generateCSV(
        data: List<FaceRecognitionData>,
        fileName: String
    ): Result<File> {
        return withContext(Dispatchers.IO) {
            val header = listOf("timestamp", "is_face_detected")
                .joinToString(separator = ", ", postfix = "\n")


            val formattedData = data.joinToString(separator = "\n") {
                listOf(it.timestampMillis.toIso8601(), it.result.binaryValue).joinToString()
            }

            val file = fileManager.getOrCreateFile("$fileName$CSV_FILE_EXTENSION")
            fileManager
                .writeFile(file, header + formattedData)
                .map { file }
        }
    }

    private companion object {
        const val CSV_FILE_EXTENSION = ".csv"
    }
}