Contact lookup is a common use case used in apps such as contact search, finding a
contact by using a phone number and e-mail suggestion. Contact Store provides ways to quickly look up
contacts stored on the device by providing a `ContactPredicate`
when [reading contacts](./read_contacts.md)

The following example uses the `ContactLookup` predicate to retrieve a contact with a given contact
id:

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts(
    predicate = ContactLookup(contactId),
    columnsToFetch = allContactColumns()
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

There are different kind of Predicates you can use:

### ContactLookup

This predicate will return the contact with the passing contact id.

### PhoneLookup

This predicate will return any contacts that contain part of the phone number provided.

### MailLookup

This predicate will return any contacts that contain part of the mail address provided.

### NameLookup

This predicate will return any contacts that contain part of the name.