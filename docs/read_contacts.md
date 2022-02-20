Every contact comes with some basic information: the contact ID, the display name and whether they
are starred or not.

You can request for additional information such as their phone numbers or e-mail addresses by
specifying this in your requests.

!!! info "Permission required"

    The examples shown below assume that your app has already been granted the [READ_CONTACTS](https://developer.android.com/reference/android/Manifest.permission#READ_CONTACTS)
    permission. This is not handled by Contact Store.

The following snippet returns all contacts present in the device:

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts()
    .collect { contacts ->
        println("All contacts: ${contacts.joinToString(", ")}")
    }
```

## Columns to fetch

Depending on your use case, you might need to request additional information about a contact. You
can do this by specifying the `ContactColumn`s that you need. The following example showcase how to
get a single contact's `PhoneNumber`s and names:

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts(
    predicate = ContactLookup(contactId),
    columnsToFetch = listOf(
        ContactColumn.Names,
        ContactColumn.Phones
    )
)
    .collect { contacts ->
        val contact = contacts.firstOrNull()
        if (contact == null) {
            println("Contact not found")
        } else {
            // you can now access the contact's phone & names
            val phones = contact.phones
            val contactString = contacts.joinToString(", ") { contact ->
                "Names = ${contact.firstName} ${contact.middleName} ${contact.lastName} " +
                        "phones = ${phones.map { "${it.value} (${it.label})" }}"
            }
            println("Contacts found: $contactString")
        }
    }
```

Always make sure to query only the `ContactColumn`s that you need. In the case where a property is
accessed which was not queried, an `Exception` is thrown.

If you need to request all available columns, use the `allContactColumns()` function.

### Available columns

The following table shows the mapping between Android's original API and Contact Store's
ContactColumn:

| CommonDataKinds                                                                                                               | ContactColumn   | Contact's field(s) populated                                                                             |
|-------------------------------------------------------------------------------------------------------------------------------|-----------------|----------------------------------------------------------------------------------------------------------| 
| [Phone](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone)                      | Phones          | phones                                                                                                   |
| [Email](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Email)                      | Mails           | mails                                                                                                    |
| [Event](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Event)                      | Events          | events                                                                                                   |
| [GroupMembership](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.GroupMembership)  | GroupMembership | groups                                                                                                   |
| [Note](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Note)                        | Note            | note                                                                                                     |
| [StructuredPostal](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredPostal) | PostalAddresses | postalAddresses                                                                                          |
| [Photo](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Photo)                      | Image           | imageData                                                                                                |
| [StructuredName](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.StructuredName)    | Names           | prefix, firstName, middleName, lastName, suffix, phoneticFirstName, phoneticMiddleName, phoneticLastName |
| [SipAddresses](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.SipAddress)          | SipAddresses    | sipAddresses                                                                                             |
| [Relations](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Relation)               | Relations       | relations                                                                                                |
| [Organization](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Organization)        | Organization    | organization, jobTitle                                                                                   |
| [Nickname](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Nickname)                | Nickname        | nickname                                                                                                 |
| [ImAddresses](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Im)                   | ImAddresses     | imAddresses                                                                                              |
| [WebAddresses](https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Website)             | WebAddresses    | webAddresses                                                                                             |
| Custom data                                                                                                                   | CustomDataItems | customDataItems                                                                                          |

Most properties of the store come as a `LabeledValue`. Each LabeledValue contains the requested
information (such as a phone number) and a user defined `Label` object (such as 'Mobile', 'Birthday'
or custom ones).

The labels are used to make it simpler to differentiate similar data items on your apps UI.

You can use the `Label.getLocalizedString()` extension to get a localized version of any label.

The `LabeledValue` class also contains information about which synced account holds the data item.
This comes useful when you need to display information from specific account types (say Google).

### Custom data items

Custom data items include any data items provided by synced accounts that does not fit any other
column. It enables custom functionality such as video calling on WhatsApp or messaging via Telegram.

Each `CustomDataItem` contains the mimeType of the item (which represents what the item is, such as
a voice call), information for displaying the item on a UI (see summary, detail and icon), and the
account that holds the item.

You can start an activity using the item like so:

```kotlin
val intent = Intent(
    Intent.ACTION_VIEW, ContentUris.withAppendedId(
        ContactsContract.Data.CONTENT_URI, customDataItem.id
    )
)
startActivity(intent)
```

## Display Name Style

You can specify the style of the display name you prefer when fetching contacts:

The `Primary` (default) style will return display names in a "given name first" fashion for Western
names.

The `Alternative` style will return them in a "family name first" fashion instead.

The returned contacts will be sorted according to the style requested.