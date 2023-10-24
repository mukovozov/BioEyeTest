package com.example.bioeyetest.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import javax.inject.Inject

interface FileManager {

    suspend fun getOrCreateFile(fileName: String): File
    suspend fun writeFile(file: File, data: String): Result<Unit>
}

class FileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileManager {

    private val baseDirPath = context.cacheDir.absolutePath

    override suspend fun getOrCreateFile(fileName: String): File {
        return withContext(Dispatchers.IO) {
            val file = File(baseDirPath, fileName)

            if (file.parentFile?.exists() == false) {
                file.parentFile?.mkdirs()
            }

            if (!file.exists()) {
                file.createNewFile()
            }

            file
        }
    }

    override suspend fun writeFile(file: File, data: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                FileWriter(file).use { fos ->
                    BufferedWriter(fos).use {
                        it.write(data)
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}