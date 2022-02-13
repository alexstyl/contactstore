All fetch operations in ContactStore come from the `ContactStore#fetchContacts()` function. The
function creates a [Flow](https://developer.android.com/kotlin/flow) which will emit the contacts
requested. This Flow never completes and will continue emitting as soon as an update to the contacts
is detected.

Each contact in ContactStore comes with some basic information, such as their contact ID, their
display name and whether they are starred or not.

## Fetching all contacts (Contact List)

The following snippet returns all contacts present in the device.

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts()
    .collect { contacts ->
        val contactString = contacts.joinToString(", ") {
            "displayName = ${it.displayName}," +
                    " isStarred = ${it.isStarred}," +
                    " id = ${it.contactId}"
        }
        println("Contacts emitted: $contactString")
    }
```

> ⚠️ Your app must have already been granted the [READ_CONTACTS](https://developer.android.com/reference/android/Manifest.permission#READ_CONTACTS)
permission before collecting the flow.

## Fetching contact details (Contact Details Screen)

If you need to query specific details about a contact, the following sample returns a
contact's [Structured Names](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredName)
and phone numbers:

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts(
    predicate = ContactLookup(
        inContactIds = listOf(contactId)
    ),
    columnsToFetch = listOf(
        ContactColumn.NAMES,
        ContactColumn.PHONES
    )
)
    .collect { contacts ->
        val contact = contacts.firstOrNull()
        if (contact == null) {
            println("Contact not found")
        } else {
            val phones = contact.phones
            val contactString = contacts.joinToString(", ") { contact ->
                "Names = ${contact.firstName} ${contact.middleName} ${contact.lastName} " +
                        "phones = ${phones.map { "${it.value} (${it.label})" }}"
            }
            println("Contacts emitted: $contactString")
        }
    }
```

Always make sure to query only the `ContactColumn`s that you need. In the case where a property is
accessed which was not queried, an `Exception` is thrown.

You can use Contact Store to retrieve any information that is linked to any contact of the device.
Such information could be phone numbers, e-mails, postal addresses and others.

> 💡 You can use the `standardColumns()` function to request all available Columns.

### Available columns

The following table shows the mapping between Android's original API and ContactStore's
ContactColumn:

| CommonDataKinds | ContactColumn | Contact's field(s) populated |
| --- | --- | --- | 
| [Phone](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone) | Phones | phones |
| [Email](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Email) | Mails | mails |
| [Event](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Event) | Events | events |
| [GroupMembership](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.GroupMembership) | GroupMembership | groups |
| [Note](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Note) | Note | note |
| [StructuredPostal](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredPostal) | PostalAddresses | postalAddresses |
| [Photo](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Photo) | Image | imageData |
| [StructuredName](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredName) | Names | prefix, firstName, middleName, lastName, suffix, phoneticFirstName, phoneticMiddleName, phoneticLastName |
| [SipAddresses](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.SipAddress) | SipAddresses | sipAddresses |
| [Relations](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Relation) | Relations | relations |
| [Organization](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Organization) | Organization | organization, jobTitle |
| [Nickname](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Nickname) | Nickname | nickname |
| [ImAddresses](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Im) | ImAddresses | imAddresses |
| [WebAddresses](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Website) | WebAddresses | webAddresses |

## Search (Contact Lookup)

Passing
a [ContactPredicate](https://github.com/alexstyl/contactstore/blob/main/library/src/main/java/com/alexstyl/contactstore/ContactPredicate.kt)
to `ContactStore#fetchContacts()` function will affect which contacts will be returned.

There are different predicates used for different use cases.

Fetching all details of a specific contact (i.e for a Contact Details screen) might look like this:

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts(
    predicate = ContactLookup(
        inContactIds = listOf(contactId)
    ),
    columnsToFetch = standardColumns()
)
    .collect { contacts ->
        val contact = contacts.firstOrNull()
        if (contact == null) {
            // Contact not found
        } else {
            // Update your screen using contact
        }
    }

```

You are not limited by looking up contacts by IDs. Other provided predicates allow you to query for
contact's names, phones numbers and emails.

## Linked accounts

You can use Contact Store to fetch information about a contact's linked accounts. This can be used
to allow your app to start a call on WhatsApp, open someone's conversation on Telegram and more.

### Fetching a contact's linked accounts

Similar to fetching a contact's details, you need to provide the `ContactColumn#LinkedAccountValues`
with the account type of the account you want to query while using the store. Here is an example of
fetching a contact's WhatsApp details:

```kotlin
contactStore.fetchContacts(
    predicate = ContactLookup(listOf(contactId)),
    columnsToFetch = listOf(
        LinkedAccountValues("com.whatsapp")
    )
).collect {
    val contact = it.firstOrNull()
    if (contact != null) {
        // access the details using contact.linkedAccountValues
    }
}
```

The above sample asumes that the device owner has the WhatsApp app installed and logged in. If
WhatsApp is not installed, then the emitted value will be `null`.

Each account type can return a different number of items. WhatsApp, for example, currently returns 3
different items (one for calling the contact, one for video calling and one for opening the
conversation with that contact).

### Launching linked account actions

When you hold a refence to a contact with its LinkedAccountValues queried, use the `mimetype` in
order to use the one you need.

The following example launches the conversation of a contact on WhatsApp:

```kotlin
val linkedAccountValue = contact.linkedAccountValues.firstOrNull {
    it.mimetype == "vnd.android.cursor.item/vnd.com.whatsapp.profile"
}
if (linkedAccountValue != null) {
    val intent = Intent(
        Intent.ACTION_VIEW, ContentUris.withAppendedId(
            ContactsContract.Data.CONTENT_URI, linkedAccountValue.id
        )
    )
    startActivity(intent)
}
```

### Common account types

| App name | Account Type | 
| --- | --- | 
| WhatsApp | `com.whatsapp` | 
| Telegram | `org.telegram.messenger` | 
| Signal | `org.thoughtcrime.securesms` | 
| Viber | `com.viber.voip` | 
| Kik | `kik.android` | 
| Duo | `com.google.android.apps.tachyon` | 
| Threema | `ch.threema.app` | 
