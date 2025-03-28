package cat.dam.mindspeak.ui.screens.user

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cat.dam.mindspeak.R
import cat.dam.mindspeak.firebase.FirebaseManager
import cat.dam.mindspeak.model.UserViewModel
import cat.dam.mindspeak.ui.theme.CustomColors
import cat.dam.mindspeak.utils.SupabaseStorageProfileUtil
import cat.dam.mindspeak.utils.SupabaseStorageUtil
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun SettingsUser(
    localCustomColors: ProvidableCompositionLocal<CustomColors>,
    navController: NavController,
    userViewModel: UserViewModel
) {
    val userData by userViewModel.userData.collectAsState()
    val firebaseManager = remember { FirebaseManager() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State for profile image URL and upload success message
    var profileImageUrl by remember { mutableStateOf(userData.profileImage ?: "") }
    var showUpdateSuccess by remember { mutableStateOf(false) }

    // Load profile image from Firebase on initial launch
    LaunchedEffect(Unit) {
        val storedProfileImage = firebaseManager.getProfileImage() // Fetch the profile image URL
        if (storedProfileImage.isNotEmpty()) {
            profileImageUrl = storedProfileImage
        }
    }

    // Image picker launcher to select image from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    val uploadedProfileUrl = SupabaseStorageProfileUtil.uploadProfileImage(context, it)
                    profileImageUrl = uploadedProfileUrl

                    userViewModel.updateUserData(profileImage = uploadedProfileUrl)
                    firebaseManager.updateUserFieldFromComposable("profileImage", uploadedProfileUrl)

                    showUpdateSuccess = true
                } catch (e: Exception) {
                    Toast.makeText(context,
                        context.getString(R.string.error_upload_icon), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (showUpdateSuccess) {
        AlertDialog(
            onDismissRequest = { showUpdateSuccess = false },
            title = { Text(stringResource(R.string.update_succes)) },
            text = { Text(stringResource(R.string.text_update_good)) },
            confirmButton = {
                Button(onClick = { showUpdateSuccess = false }) {
                    Text("OK")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                // Fila para el lápiz de editar foto de perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lapiz_icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(localCustomColors.current.text1),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Fit
                    )
                }
            }
            item {
                // Fila para la imagen de perfil
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (profileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = stringResource(R.string.profile_pic),
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape), // Redondear la imagen
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.user_icon),
                            contentDescription = stringResource(R.string.description_img),
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape), // Redondear la imagen
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .zIndex(1f)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    SettingItem(
                        label = stringResource(R.string.name_settings),
                        initialValue = userData.nom ?: "",
                        localCustomColors = localCustomColors,
                        onUpdate = { newValue ->
                            userViewModel.updateUserData(nom = newValue)
                            firebaseManager.updateUserFieldFromComposable("nom", newValue)
                            showUpdateSuccess = true
                        }
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    SettingItem(
                        label = stringResource(R.string.surname_settings),
                        initialValue = userData.cognom ?: "",
                        localCustomColors = localCustomColors,
                        onUpdate = { newValue ->
                            userViewModel.updateUserData(cognom = newValue)
                            firebaseManager.updateUserFieldFromComposable("cognom", newValue)
                            showUpdateSuccess = true
                        }
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp)) {
                        Text(
                            text = stringResource(R.string.mail_user),
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = localCustomColors.current.text1
                            )
                        )

                        Text(
                            userData.email ?: stringResource(R.string.no_available),
                            style = TextStyle(
                                color = localCustomColors.current.text1,
                                fontSize = 25.sp
                            ),
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            stringResource(R.string.contact_info),
                            style = TextStyle(
                                color = localCustomColors.current.text2,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    SettingItem(
                        label = stringResource(R.string.telephone),
                        initialValue = userData.telefon ?: "",
                        localCustomColors = localCustomColors,
                        onUpdate = { newValue ->
                            userViewModel.updateUserData(telefon = newValue)
                            firebaseManager.updateUserFieldFromComposable("telefon", newValue)
                            showUpdateSuccess = true
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Logout
                    Button(
                        onClick = {
                            FirebaseManager.logoutUser()
                            userViewModel.clearUserData() // Limpiar los datos del usuario
                            navController.navigate("login") {
                                popUpTo("user") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(stringResource(R.string.logout))
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.persona_settings_user),
                    contentDescription = null,
                    modifier = Modifier
                        .zIndex(0f)
                        .size(150.dp)
                        .align(Alignment.BottomEnd),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    label: String,
    initialValue: String,
    localCustomColors: ProvidableCompositionLocal<CustomColors>,
    onUpdate: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        currentValue = initialValue
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp)
    ) {
        Text(
            label,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = localCustomColors.current.text1
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.weight(1f)
                )

                // Botón de confirmar (Check verde)
                IconButton(
                    onClick = {
                        if (currentValue != initialValue) {
                            onUpdate(currentValue)
                        }
                        isEditing = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.confirm),
                        tint = Color.Green,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Botón de cancelar (X roja)
                IconButton(
                    onClick = {
                        currentValue = initialValue
                        isEditing = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cancel),
                        tint = Color.Red,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                Text(
                    currentValue.ifEmpty { stringResource(R.string.no_available) },
                    style = TextStyle(
                        color = localCustomColors.current.text1,
                        fontSize = 25.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Botón de edición (Lápiz)
                IconButton(
                    onClick = { isEditing = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit),
                        tint = localCustomColors.current.text1,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}