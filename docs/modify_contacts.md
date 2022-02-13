Inserting new contacts to the device, updating or deleting existing ones are done
using `ContactStore#execute`.

> ⚠️ Your app must have already been
granted the [WRITE_CONTACTS](https://developer.android.com/reference/android/Manifest.permission#WRITE_CONTACTS) 
permission before calling `execute()`.

## Insert a new contact

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

## Update an existing contact

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

## Deleting a contact

The following code shows how to delete a contact by id:

```kotlin
store.execute {
    delete(contactId = 5L)
}
```
