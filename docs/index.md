# Overview

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
    implementation 'com.alexstyl:contactstore:0.12.0'
    
    // optional dependency for tests
    testImplementation 'com.alexstyl:contactstore-test:0.12.0'
}
```

## Building the project

After you clone the repository locally, you will notice that the build will fail.

This is because one of the scripts used to push a new version to Maven requires some credentials to be present.

In order to fix this add the following to your `/local.properties` file.
```
sonatypeStagingProfileId=
ossrhUsername=
ossrhPassword=
signing.keyId=
signing.key=
signing.password=
```

and you are set. You can try running the sample app and everything should work. If not, open a new issue.

## Getting Help

To report a specific problem or feature request, [open a new issue on Github][2].

## License

Apache 2.0. See the [LICENSE](/LICENSE) file for details.

## Author

Made by Alex Styl. [Follow @alexstyl](https://www.twitter.com/alexstyl) on Twitter for future updates.

[1]: https://github.com/alexstyl/contactstore/releases
[2]: https://github.com/alexstyl/contactstore/issues
