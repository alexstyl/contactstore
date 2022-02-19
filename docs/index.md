# Introduction

Contact Store is a modern API that aims to make access to contacts on Android devices simple to use
for everyone.

[The default way of accessing contacts on Android](https://developer.android.com/guide/topics/providers/contacts-provider)
is based off ContentProviders. Despite powerful, it can be error-prone and frustrating to use. In
addition, given that the API is based off the metaphor of tables in a database, it can be hard to
understand how to set everything up.

Contact Store is a refreshed take to the contacts API by providing a simpler mental model to work
with. It provides solutions to contacts' most frequent use cases and utilises modern best practices
and language features.

## State of development

This API is currently under development. There might be breaking changes between versions,
even though I am extremely cautious to keep them to a minimum or provide automatic ways to migrate to
(such as via Kotlin's Deprecated tools).

As soon as version 1.0.0 is released, there will be no breaking changes between version.

## Installation

Using Gradle:

```gradle
repositories {
  ...
  mavenCentral()
}

dependencies {
    implementation 'com.alexstyl:contactstore:0.14.1'
    
    // optional dependency for tests
    testImplementation 'com.alexstyl:contactstore-test:0.14.1'
}
```

## Getting Help

To report a specific problem or feature request, [open a new issue on Github][2].

## License

Apache 2.0. See the [LICENSE](/LICENSE) file for details.

## Author

Made by Alex Styl. [Follow @alexstyl](https://www.twitter.com/alexstyl) on Twitter for future
updates.

[1]: https://github.com/alexstyl/contactstore/releases

[2]: https://github.com/alexstyl/contactstore/issues
