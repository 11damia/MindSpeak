package cat.dam.mindspeak.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import cat.dam.mindspeak.firebase.FirebaseManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object SupabaseStorageProfileUtil {
    private const val TAG = "SupabaseStorageProfileUtil"

    private val supabaseClient = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_KEY
    ) {
        install(Storage)
    }

    private suspend fun extractFileNameFromUrl(url: String): String? {
        return url.substringAfterLast("/").takeIf { it.isNotBlank() }
    }

    suspend fun uploadProfileImage(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val firebaseManager = FirebaseManager()
                val userData = firebaseManager.obtenirDadesUsuari()
                val currentProfileImageUrl = userData?.profileImage

                // Generar un nombre de archivo único
                val filename = "${UUID.randomUUID()}.jpg"

                // Convertir la Uri a un archivo
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                val outputStream = FileOutputStream(file)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Subir el archivo al bucket 'profile'
                val storageClient = supabaseClient.storage

                // Crear el bucket si no existe
                try {
                    storageClient.createBucket(SupabaseConfig.BUCKET_NAME_PROFILE)
                } catch (e: Exception) {
                    Log.d(TAG, "El bucket ya existe o error: ${e.message}")
                }

                // Subir el archivo
                val byteArray = file.readBytes()
                storageClient.from(SupabaseConfig.BUCKET_NAME_PROFILE).upload(
                    path = filename,
                    data = byteArray,
                    upsert = true
                )

                // Obtener la URL pública
                var publicUrl = storageClient.from(SupabaseConfig.BUCKET_NAME_PROFILE).publicUrl(filename)

                // Asegurarse que la URL tiene el formato correcto
                if (!publicUrl.startsWith("http")) {
                    publicUrl = "https://$publicUrl"
                }

                // Eliminar la imagen anterior si existe
                currentProfileImageUrl?.let { oldUrl ->
                    try {
                        val oldFileName = extractFileNameFromUrl(oldUrl)
                        oldFileName?.let { fileName ->
                            storageClient.from(SupabaseConfig.BUCKET_NAME_PROFILE).delete(fileName)
                            Log.d(TAG, "Imagen anterior eliminada: $fileName")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error eliminando imagen anterior", e)
                    }
                }

                Log.d(TAG, "Imagen subida correctamente: $publicUrl")

                // Limpiar el archivo temporal
                file.delete()

                publicUrl
            } catch (e: Exception) {
                Log.e(TAG, "Error subiendo la imagen al bucket 'profile'", e)
                throw e
            }
        }
    }

    // Función para eliminar una imagen de perfil específica
    suspend fun deleteProfileImage(imageUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                val storageClient = supabaseClient.storage
                val fileName = extractFileNameFromUrl(imageUrl)

                fileName?.let {
                    storageClient.from(SupabaseConfig.BUCKET_NAME_PROFILE).delete(it)
                    Log.d(TAG, "Imagen eliminada: $it")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error eliminando imagen de perfil", e)
            }
        }
    }
}