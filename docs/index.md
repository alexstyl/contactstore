# Introduction

![Contact Store - a modern contacts Android API](./assets/banner.png)

Contact Store is a modern API that makes access to contacts on Android devices simple to use.

[The default way of accessing contacts on Android](https://developer.android.com/guide/topics/providers/contacts-provider)
is based off ContentProviders. Despite powerful, ContentProviders can be error-prone and frustrating to use.

Contact Store is a refreshed take on the Contacts API. It provides solutions to contacts' most
frequent use cases and uses modern developer practices for an enjoyable developer experience.

## Quick Start

Install the API using Gradle. In your `app/build.gradle` include the following dependencies:

=== "Groovy"

    ```gradle
    repositories {
      ...
      mavenCentral()
    }
    
    dependencies {
        implementation 'com.alexstyl:contactstore:1.2.2'
        
        // extension functions for kotlin coroutines
        implementation 'com.alexstyl:contactstore-coroutines:1.2.2'
        
        // extension functions for rxJava 3
        implementation 'com.alexstyl:contactstore-reactive:1.2.2'
        
        // optional dependency for tests
        testImplementation 'com.alexstyl:contactstore-test:1.2.2'
    }
    ```

=== "Kotlin"

    ```kotlin
    repositories {
      ...
      mavenCentral()
    }
    
    dependencies {
        implementation("com.alexstyl:contactstore:1.2.2")
        
        // extension functions for kotlin coroutines
        implementation("com.alexstyl:contactstore-coroutines:1.2.2")
        
        // extension functions for rxJava 3
        implementation("com.alexstyl:contactstore-reactive:1.2.2")
        
        // optional dependency for tests
        testImplementation("com.alexstyl:contactstore-test:1.2.2")
    }
    ```

### Sample app

Prefer code to documentation? Checkout the [sample app](https://github.com/alexstyl/contactstore/tree/main/sample)

### Fetch all contacts

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts()
    .collect { contacts ->
        println("Contacts emitted: $contacts")
    }
```

### Get details of a specific contact

```kotlin
val store = ContactStore.newInstance(application)

store.fetchContacts(
    predicate = ContactLookup(contactId),
    columnsToFetch = allContactColumns()
)
    .collect { contacts ->
        val contact = contacts.firstOrNull()
        if (contact == null) {
            println("Contact not found")
        } else {
            println("Contact found: $contact")

            // Use contact.phones, contact.mails, contact.customDataItems and
        }
    }
```

### Insert a new contact into a Gmail account

```kotlin
val store = ContactStore.newInstance(application)

store.execute {
    insert(InternetAcount("paolo@gmail.com", "gmail.com")) {
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

### Update an existing Contact

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

### Delete a contact
```kotlin
store.execute {
    delete(contactId = 5L)
}
```

## Does Contact Store support all features the default Contacts API does?

Probably not and this is not the aim of the project. The existing Contacts API has been out there
for 10 years or so without much update. It is powerful given that you have access to an SQL-like
syntax. I am assuming that a lot of the features it provides were introduced because the platform
developers were coding against the ContactProvider interface instead of supporting the features app
developers would eventually end up using.

Keeping the API lean allows for faster iterations/releases too as there is less things to maintain.
I am not saying that eventually all features in the default API are not important or that they will
never make it to Contact Store. Instead, I would rather have the features and capabilities of the
API to be driven by dev requirements.

If you believe you are missing a specific feature, [open a new feature request on Github][1].

## Can I use this API from Java?

You should be able to use Contact Store through Java as you can call Kotlin code from Java, but it
won't be ideal. [Check this Github issue](https://github.com/alexstyl/contactstore/issues/58) and
write your experience.

## Getting Help

To report a specific problem or feature request, [open a new issue on Github][1].

## License

Apache 2.0. See the [LICENSE](https://github.com/alexstyl/contactstore/blob/main/LICENSE) file for details.

## Author

Made by Alex Styl. [Follow @alexstyl](https://www.twitter.com/alexstyl) on Twitter for future
updates.

[1]: https://github.com/alexstyl/contactstore/issues
