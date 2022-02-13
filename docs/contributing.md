## How to contribute

This community seeks the following types of contributions:

- **Ideas**: participate in an issue thread or start your own to have your voice heard.
- **Bug reporting**: report any bug you encounter while using the API.
- **Open issues**: feel free to work on any Github issue that is not currently assigned to someone else.

Kindly, do *not* open a PR with work that is not listed as a Github issue or you haven't reached out first.


## Building the project

After you clone the repository locally, you will notice that the build will fail.

This is because one of the scripts used to push a new version to Maven requires some credentials to
be present.

In order to fix this add the following to your `/local.properties` file.

```
sonatypeStagingProfileId=
ossrhUsername=
ossrhPassword=
signing.keyId=
signing.key=
signing.password=
```

and you are set. You can try running the sample app and everything should work. If
not, [open a new issue](https://github.com/alexstyl/contactstore/issues/new)