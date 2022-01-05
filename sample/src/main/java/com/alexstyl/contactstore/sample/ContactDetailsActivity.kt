package com.alexstyl.contactstore.sample

import android.content.ContentUris
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.alexstyl.contactstore.Contact
import com.alexstyl.contactstore.ContactColumn.LinkedAccountValues
import com.alexstyl.contactstore.ContactPredicate.ContactLookup
import com.alexstyl.contactstore.ContactStore
import com.alexstyl.contactstore.imageUri
import com.alexstyl.contactstore.sample.ui.SetupSystemUi
import com.alexstyl.contactstore.sample.ui.theme.SampleAppTheme
import com.alexstyl.contactstore.shareVCardIntent
import com.alexstyl.contactstore.standardColumns
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@ExperimentalCoilApi
@AndroidEntryPoint
class ContactDetailsActivity : ComponentActivity() {

    @Inject
    lateinit var contactStore: ContactStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contactId = requireNotNull(intent.extras)
            .getLong(EXTRA_CONTACT_ID)

        val contact = runBlocking {
            contactStore.fetchContacts(
                predicate = ContactLookup(listOf(contactId)),
                columnsToFetch = standardColumns() + listOf(
                    LinkedAccountValues("com.whatsapp"),
                    LinkedAccountValues("org.thoughtcrime.securesms"),
                    LinkedAccountValues("org.telegram.messenger"),
                    LinkedAccountValues("com.viber.voip"),
                    LinkedAccountValues("kik.android"),
                    LinkedAccountValues("com.google.android.apps.tachyon"),
                    LinkedAccountValues("ch.threema.app")
                )
            ).first().firstOrNull()
        }
        if (contact == null) {
            Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setContent {
            ContactDetailsScreen(
                contact = contact,
                onUpClick = { finish() },
                onShareClick = {
                    val intent = shareVCardIntent(requireNotNull(contact.lookupKey))
                    startActivity(intent)
                }
            )
        }
    }

    @Composable
    private fun ContactDetailsScreen(
        contact: Contact,
        onUpClick: () -> Unit = {},
        onShareClick: () -> Unit = {},
    ) {
        SampleAppTheme {
            SetupSystemUi()
            Scaffold {
                Box {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        item {
                            ContactDetails(contact)
                        }
                        contact.phones.forEach { phone ->
                            item {
                                Contactable(
                                    icon = drawableResource(R.drawable.ic_call),
                                    label = "Phone",
                                    value = phone.value.raw,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${phone.value.raw}")
                                        }
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                        contact.mails.forEach { mail ->
                            item {
                                Contactable(
                                    icon = drawableResource(R.drawable.ic_mail),
                                    label = "Mail",
                                    value = mail.value.raw,
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("mailto:${mail.value.raw}")
                                        }
                                        startActivity(intent)
                                    }
                                )
                            }
                        }

                        contact.events.forEach { event ->
                            item {
                                val date = event.value
                                val value = if (date.year == null) {
                                    "${date.dayOfMonth}/${date.month}"
                                } else {
                                    "${date.dayOfMonth}/${date.month}/${date.year}"
                                }
                                Contactable(
                                    icon = drawableResource(R.drawable.ic_event),
                                    label = "Event",
                                    value = value,
                                    onClick = {
                                    }
                                )
                            }
                        }

                        contact.linkedAccountValues.forEach { value ->
                            item {
                                Contactable(
                                    icon = value.icon,
                                    label = value.summary,
                                    value = value.detail,
                                    onClick = {
                                        runCatching {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW, ContentUris.withAppendedId(
                                                    ContactsContract.Data.CONTENT_URI, value.id
                                                )
                                            )
                                            startActivity(intent)
                                        }.exceptionOrNull()?.run {
                                            Toast.makeText(
                                                this@ContactDetailsActivity,
                                                message ?: "There was an error", Toast.LENGTH_LONG
                                            ).show()
                                            printStackTrace()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    NavBar(
                        onUpClick = onUpClick,
                        onShareClick = onShareClick
                    )
                }
            }
        }
    }

    @Composable
    private fun ContactDetails(contact: Contact) {
        Spacer(Modifier.height(40.dp))
        Image(
            rememberImagePainter(
                contact.imageUri,
                builder = {
                    transformations(CircleCropTransformation())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                },
            ),
            modifier = Modifier.size(96.dp),
            contentDescription = null
        )
        Spacer(Modifier.height(20.dp))
        Text(
            contact.displayName.orEmpty()
                .ifEmpty { stringResource(R.string.anonymous) },
            style = MaterialTheme.typography.h2
        )
        Spacer(Modifier.height(40.dp))
    }

    @Composable
    private fun drawableResource(drawableResId: Int): Drawable? {
        return ContextCompat.getDrawable(LocalContext.current, drawableResId)
    }

    @Composable
    private fun Contactable(onClick: () -> Unit, icon: Drawable?, label: String, value: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            onClick = onClick
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        value,
                        style = MaterialTheme.typography.body1
                    )
                }
                Image(
                    painter = rememberImagePainter(data = icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 8.dp)
                )
            }
        }
    }

    @Composable
    private fun NavBar(onUpClick: () -> Unit, onShareClick: () -> Unit) {
        Row {
            Surface(
                color = Color.Transparent,
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp),
                onClick = onUpClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Go back",
                    modifier = Modifier.wrapContentSize(
                        align = Alignment.Center
                    )
                )

            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = Color.Transparent,
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp),
                onClick = onShareClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = "Share",
                    modifier = Modifier.wrapContentSize(
                        align = Alignment.Center
                    )
                )

            }
        }
    }

    companion object {
        const val EXTRA_CONTACT_ID = "extra_contact_id"
    }
}