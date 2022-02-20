!!! info "Permission required"

    The examples shown below assume that your app has already been granted the [WRITE_CONTACTS](https://developer.android.com/reference/android/Manifest.permission#WRITE_CONTACTS)
    permission. This is not handled by Contact Store.

## Insert a new contact

Contact Store provides a flexible way of creating new contacts. The following example shows how to
create a new contact:

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
    }
}
```

The `insert()` function takes an optional `InternetAccount` parameter. The new contact will be
stored in the given account if it is editable.

!!! tip

    You can query all available accounts by using the [AccountManager](https://developer.android.com/reference/android/accounts/AccountManager) class.

## Update an existing contact

All contacts emitted by Contact Store are immutable. In order to modify a contact, you first need to
get a reference of the contact from the store. The `Contact.mutableCopy()` function returns a
version of the contact that you can modify. Only the values queried can be updated. This is by
design, in order to prevent accidental value overrides.

Updating the copy's values will not directly update the contacts of the device. When you are happy
with your changes and want to commit them, pass the updated contact into `update()`.

The following code modifies a contact's note:

```kotlin
val foundContacts = store.fetchContacts(
    predicate = ContactLookup(contactId = 5L),
    columnsToFetch = listOf(ContactColumn.Note)
).first()
if (foundContacts.isEmpty()) return // the contact was not found

val contact = foundContacts.first()

store.execute {
    update(contact.mutableCopy {
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
