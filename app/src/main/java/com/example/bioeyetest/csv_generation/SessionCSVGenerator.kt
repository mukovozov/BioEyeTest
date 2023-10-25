package com.example.bioeyetest.csv_generation

import com.example.bioeyetest.face_recognition.FaceRecognitionData
import com.example.bioeyetest.utils.DispatchersProvider
import com.example.bioeyetest.utils.FileManager
import com.example.bioeyetest.utils.format
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
    private val dispatchersProvider: DispatchersProvider,
) : SessionCSVGenerator {
    override suspend fun generateCSV(
        data: List<FaceRecognitionData>,
        fileName: String
    ): Result<File> {
        return withContext(dispatchersProvider.io) {
            val header = listOf(TIMESTAMP_CSV_HEADER, IS_FACE_DETECTED_CSV_HEADER)
                .joinToString(postfix = NEXT_LINE)


            val formattedData = data.joinToString(separator = NEXT_LINE) {
                listOf(it.timestampMillis.format(), it.result.binaryValue).joinToString()
            }

            val file = fileManager.getOrCreateFile("$fileName$CSV_FILE_EXTENSION")
            fileManager
                .writeFile(file, header + formattedData)
                .map { file }
        }
    }

    private companion object {
        const val TIMESTAMP_CSV_HEADER = "timestamp"
        const val IS_FACE_DETECTED_CSV_HEADER = "is_face_detected"
        const val NEXT_LINE = "\n"

        const val CSV_FILE_EXTENSION = ".csv"
    }
}