package com.example.bioeyetest.core.file

import android.content.Context
import com.example.bioeyetest.core.coroutines.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

interface FileManager {
    suspend fun getOrCreateFile(fileName: String): File
    suspend fun writeFile(file: File, data: String): Result<Unit>
}

class FileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
) : FileManager {

    //Could be injected from outside, but we don't need to store different files in different folders, so it's here
    private val baseDirPath = context.cacheDir.absolutePath

    override suspend fun getOrCreateFile(fileName: String): File {
        return withContext(dispatchersProvider.io) {
            val file = File(baseDirPath, fileName)

            if (file.parentFile?.exists() == false) {
                file.parentFile?.mkdirs()
            }

            if (!file.exists()) {
                // Lint issue, can't recognise I use Dispatchers.IO under the hood
                file.createNewFile()
            }

            file
        }
    }

    override suspend fun writeFile(file: File, data: String): Result<Unit> {
        return withContext(dispatchersProvider.io) {
            try {
                // Lint issue, can't recognise I use Dispatchers.IO under the hood
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