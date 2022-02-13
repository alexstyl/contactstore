ContactStore provides an optional `com.alexstyl:contactstore-test` dependency with testing utilities
around contacts.

## ContactStore in unit tests

As contacts are part of the Android framework, unit testing can be tricky. Developers are either
required to depend on frameworks such as Robolectric (which can slow down builds) or implement their
own fake abstraction around contacts to hide the dependency to the framework.

ContactStore provides a test implementation that is suitable for testing purposes,
called `TestContactStore`.

You can initialize the store by providing a list of `StoredContact`s. This can be used for emulating
scenarios where a pre-populated store is needed.

> ⚠️ The implementation of this class tries to match the behavior of AOSP as much as possible.

> Different OEMs might have altered the behavior of their ContactProvider and as a result different results might be returned. Do not use this class as a source of truth of how a real device will behave.

> This implementation is meant for unit testing purposes without the need of running the tests on a real Android device or the use of frameworks such as Robolectric.

### Example of usage

```kotlin
val contactStore = TestContactStore(
    contactsSnapshot = listOf(
        StoredContact(
            contactId = 0,
            firstName = "Paolo"
        ),
        StoredContact(
            contactId = 1,
            firstName = "Kim",
            isStarred = true
        )
    )
)
```

You can use this store as if you were running on a real device. You can fetch contacts from it
(using `fetchContacts()`) or update the contacts it holds (using `execute()`). The internal snapshot
will be updated accordingly.

## Test matchers

The `samePropertiesAs()` matchers return a matcher that matches when the examining object is
logically equal to the passing Contact or ContactGroup, minus any ids or lookup keys.

This can be useful in testing scenarios where you do not have access to ids, such as Android Tests.