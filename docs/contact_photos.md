Contact Store supports both high resolution images of contacts and thumbnails.

High res images can be memory heavy, so it is recommended to use them only when it makes sense to
show a good amount of detail in your UI. For the case of rendering a list of contacts, it is advices
to use thumbnails instead.

## Loading high res images

In order to get the high resolution image of a contact, you need to fetch the contact using
the `Image` ContactColumn. This will populate the `imageData` property of the contact. The ImageData
object holds a `ByteArray` of the image. You can provide this array to your image loading library of
choice (such as Coil, Glide or Picasso):

```kotlin
val store = ContactStore.newInstance(application)

contactStore.fetchContacts(
    predicate = ContactLookup(listOf(contactId)),
    columnsToFetch = listOf(Image)
).collect {
    val contact = it.firstOrNull()
    if (contact != null) {
        imageView.loadAny(contact.imageData?.raw)
    }
}
```

> ⚠️ The image loading library you are using might not load images from ByteArrays out of the box.
> Checkout this issue to learn [how to enable such functionality in Coil](https://github.com/coil-kt/coil/issues/171)

## Loading thumbnail images

You can get the thumbnail of any contact by using the `thumbnailUri` extension. As the name
suggests, the uri points to the thumbnail image of the contact.

You can use the uri with your favorite image loading library to render it on your UI.