package cat.dam.mindspeak.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object SupabaseStorageUtil {
    private const val TAG = "SupabaseStorageUtil"

    private val supabaseClient = createSupabaseClient(
        supabaseUrl = SupabaseConfig.SUPABASE_URL,
        supabaseKey = SupabaseConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Storage)
    }

    suspend fun uploadImage(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                // Generar un nom de fitxer únic
                val filename = "${UUID.randomUUID()}.jpg"

                // Convertir l'Uri a un fitxer
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                val outputStream = FileOutputStream(file)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Pujar el fitxer a Supabase
                val storageClient = supabaseClient.storage

                // Crear el bucket si no existeix
                try {
                    storageClient.createBucket(SupabaseConfig.BUCKET_NAME)
                } catch (e: Exception) {
                    Log.d(TAG, "El bucket ja existeix o error: ${e.message}")
                }

                // Pujar el fitxer
                val byteArray = file.readBytes()
                storageClient.from(SupabaseConfig.BUCKET_NAME).upload(
                    path = filename,
                    data = byteArray,
                    upsert = true
                )

                // Obtenir l'URL pública
                var publicUrl = storageClient.from(SupabaseConfig.BUCKET_NAME).publicUrl(filename)

                // Asegurar que la URL tiene formato correcto
                if (!publicUrl.startsWith("http")) {
                    publicUrl = "https://$publicUrl"
                }

                Log.d(TAG, "Imatge pujada correctament: $publicUrl")

                // Netejar el fitxer temporal
                file.delete()

                publicUrl

            } catch (e: Exception) {
                Log.e(TAG, "Error pujant la imatge a Supabase", e)
                throw e
            }
        }
    }
}