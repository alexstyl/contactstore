package com.alexstyl.contactstore.sample

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.alexstyl.contactstore.Contact
import com.alexstyl.contactstore.imageUri
import com.alexstyl.contactstore.sample.ContactListState.Loaded
import com.alexstyl.contactstore.sample.ContactListState.Loading
import com.alexstyl.contactstore.sample.ContactListState.PermissionRequired
import com.alexstyl.contactstore.sample.ui.theme.SampleAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleAppTheme {
                ContactListScreen(
                    onPermissionDenied = {
                        Toast
                            .makeText(this, R.string.permission_required, Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    fun ContactListScreen(
        viewModel: ContactListViewModel = hiltViewModel(),
        onPermissionDenied: () -> Unit
    ) {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            setupSystemUi()

            val requestPermission =
                rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
                    if (isGranted) {
                        viewModel.refreshList()
                    } else {
                        onPermissionDenied()
                    }
                }

            val state = viewModel.state.collectAsState().value
            when (state) {
                Loading -> LoadingScreen()
                is Loaded -> ContactList(
                    contacts = state.contacts,
                    onContactClick = { contact ->
                        val intent = Intent(Intent.ACTION_VIEW, contactUri(contact))
                        startActivity(intent)
                    })
                PermissionRequired -> {
                    SideEffect {
                        requestPermission.launch(Manifest.permission.READ_CONTACTS)
                    }
                    PermissionRationale()
                }
            }.exhaustive
        }
    }

    @Composable
    private fun setupSystemUi() {
        val systemUiController = rememberSystemUiController()
        val useDarkIcons = MaterialTheme.colors.isLight

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }
    }

    private fun contactUri(uri: Contact): Uri {
        return ContactsContract.Contacts.CONTENT_URI.buildUpon()
            .appendPath(uri.contactId.toString())
            .build()
    }
}

@Composable
fun PermissionRationale() {
    // in a real application, we might want to consider displaying the reason for the permission
    // for simplicity of this sample, this was omitted
    LoadingScreen()
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ContactList(contacts: List<Contact>, onContactClick: (Contact) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(
            vertical = 30.dp,
            horizontal = 16.dp
        )
    ) {
        item {
            Text(
                text = stringResource(R.string.contacts),
                style = MaterialTheme.typography.h1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 30.dp
                    )
            )
        }
        contacts.forEach { contact ->
            item {
                ContactRow(
                    contact,
                    onClick = onContactClick
                )
            }
        }
    }
}

@Composable
fun ContactRow(contact: Contact, onClick: (Contact) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .clickable { onClick(contact) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(
                    data = contact.imageUri,
                    builder = {
                        transformations(CircleCropTransformation())
                            .placeholder(R.drawable.ic_avatar_placeholder)
                            .error(R.drawable.ic_avatar_placeholder)
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = contact.displayName.orEmpty().ifEmpty { stringResource(R.string.anonymous) }
            )
        }
    }
}
