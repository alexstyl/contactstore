This page contains best practices when using the API to ensure the best UX possible.

## Performance considerations

### Use a Predicate to quickly resolve phone numbers

Instead of fetching all contacts' phone numbers and try to find the contact containing a phone
number yourself, use the [PhoneLookup]() predicate.

This predicate will try to return the contacts containing the given phone number.

Internally, Contact Store utilises a high performant query provided by Android, which makes it ideal
for such scenarios.

## UX considerations

#### Use the displayName property when showing the name on the UI

Each contact fetched from the store comes with their `displayName` property populated. You do not
need the `Names` column to retrieve this information.

The `displayName` might be populated after other properties that are contained in a contact (such as
their phone number, if they do not have a name). There is also a high chance that this is the way
other apps to represent a contact's name as well.

#### Use extensions functions to format phone numbers and postal addresses

Instead of displaying the raw value of phone numbers and postal address to your UI, prefer using the
extension functions found in FormattedStrings.kt. This will convert your properties into formats
best suited for UI purposes.