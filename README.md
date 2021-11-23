# Contact Store

![Banner](/images/banner.png)

Access to contacts is one of the most frequent use cases in Android applications. Even if your app is
not a contact management app, there are various cases where you might need access to the device
contacts (such as referring other users to the app).

For developers to access the device
contacts, [they need to use ContentProviders](https://developer.android.com/guide/topics/providers/contacts-provider). This introduces a lot of frustrations and complications. For someone that has never worked with
`ContentProvider`s before, the documentation can be tedious to go through. The lack of a type-safe
API leads to repeated errors, developer frustration, along with a waste of time and resources for
the developer and the team.

Contact Store is a modern contacts Android API written in Kotlin. It utilises Coroutine's Flow to
notify the developer for updates happening to the Contacts database.

## Installation

Using Gradle:

```gradle
repositories {
  ...
  mavenCentral()
}

dependencies {
    implementation 'com.alexstyl:contactstore:0.4.0'
    
    // optional dependency for tests
    testImplementation 'com.alexstyl:contactstore-test:0.4.0'
}
```

## How to fetch contacts using Contact Store

The following sample returns a list of all contacts in the device. Each contact will contain an id,
a display name and whether they are starred or not:

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts()
    .collect { contacts ->
        val contactString = contacts.joinToString(", ") {
            "DisplayName = ${it.displayName}, isStarred = ${it.isStarred}, id = ${it.contactId}"
        }
        println("Contacts emitted: $contactString")
    }
```

⚠️ The user must have already granted
the [READ_CONTACTS](https://developer.android.com/reference/android/Manifest.permission#READ_CONTACTS)
permission before collecting the flow.

If you need to query specific details about a contact (commonly used in contact detail screens), the
following sample returns a
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

Always make sure to query only the columns that you need. In the case where a property is accessed
which was not queried, an `Exception` is thrown.

## How to modify contacts using Contact Store

The following snippets show how to edit contacts in the device. ⚠️ The user must have already
granted
the [WRITE_CONTACTS](https://developer.android.com/reference/android/Manifest.permission#WRITE_CONTACTS)
permission before calling `execute()`.

### Insert a new contact

```kotlin
val store = ContactStore.newInstance(application)

store.execute {
    insert {
        firstName = "Paolo"
        lastName = "Melendez"
        phone(
            value = PhoneNumber("555"),
            label = Label.PhoneNumberMobile
        )
        mail(
            address = "paolo@paolo.com",
            label = Label.LocationWork
        )
        event(
            dayOfMonth = 23,
            month = 11,
            year = 2021,
            label = Label.DateBirthday
        )
        postalAddress(
            street = "85 Somewhere Str",
            label = Label.LocationHome
        )
        webAddress(
            address = "paolo@paolo.com",
            label = Label.LocationWork
        )
        groupMembership(groupId = 123)
    }
}
```

### Update an existing contact

In order to update a contact, you first need to get a reference to the contact from the store. Only
the values queried will be updated. This is by design, in order to prevent accidental value
overrides.

The following code modifies a contact's note:

```kotlin
val foundContacts = store.fetchContacts(
    predicate = ContactLookup(inContactIds = listOf(5L)),
    columnsToFetch = listOf(ContactColumn.NOTE)
).first()
if (foundContacts.isEmpty()) return // the contact was not found

val contact = foundContacts.first()

store.execute {
    update(contact.mutableCopy().apply {
        note = Note("To infinity and beyond!")
    })
}
```

### Deleting a contact

The following code shows how to delete a contact by id:

```kotlin
store.execute {
    delete(contactId = 5L)
}
```

## Using ContactStore in unit tests (experimental)

The optional `com.alexstyl:contactstore-test` dependency provides a pure Kotlin implementation of `ContactStore`, named `TestContactStore`. 

This implementation is meant for unit testing purposes without the need of running the tests on a real Android device or the use of frameworks such as Robolectric. 

## Getting Help

To report a specific problem or feature request, [open a new issue on Github][2].

## License

Apache 2.0. See the [LICENSE](/LICENSE) file for details.

## Author

Made by Alex Styl. [Follow @alexstyl](https://www.twitter.com/alexstyl) on Twitter for future updates.

[1]: https://github.com/alexstyl/contactstore/releases
[2]: https://github.com/alexstyl/contactstore/issues
