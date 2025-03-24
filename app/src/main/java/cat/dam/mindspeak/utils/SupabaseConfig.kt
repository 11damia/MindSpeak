package cat.dam.mindspeak.utils

object SupabaseConfig {
    // Substituïu amb les vostres credencials de Supabase
    const val SUPABASE_URL = "https://ncntykwlraaqhhlbofls.supabase.co"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5jbnR5a3dscmFhcWhobGJvZmxzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDIyNDc2MjYsImV4cCI6MjA1NzgyMzYyNn0.2SjeJRJh_zbdWywC-eW1dZyurZbsAOvI-vsl4vTCRbY"
    const val BUCKET_NAME = "useremotions"

    fun getPublicImageUrl(fileName: String): String {
        return "$SUPABASE_URL/storage/v1/object/public/$BUCKET_NAME/$fileName"
    }
}