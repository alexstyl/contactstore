package com.alexstyl.contactstore

import android.accounts.AccountManager
import android.accounts.AuthenticatorDescription
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SyncAdapterType
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.ContactsContract
import android.util.Xml
import org.xmlpull.v1.XmlPullParser

internal class AccountInfoResolver(
    private val context: Context,
    private val accountManager: AccountManager,
    private val packageManager: PackageManager,
) {
    fun fetchLinkedAccountMimeTypes(): List<LinkedAccountMimeType> {
        val allAuthTypes = accountManager.authenticatorTypes.toList()
        val adapterTypes = ContentResolver.getSyncAdapterTypes().toList()
        val authTypes = onlyContactSyncable(allAuthTypes, adapterTypes)

        return authTypes
            .mapNotNull { description ->
                loadContactsXml(description.packageName)?.let {
                    parseContactsXml(parser = it, packageName = description.packageName)
                }
            }.flatten()
    }

    private fun parseContactsXml(
        parser: XmlResourceParser,
        packageName: String
    ): List<LinkedAccountMimeType> {
        val attrs = Xml.asAttributeSet(parser)
        var type: Int
        while (parser.next().also { type = it } != XmlPullParser.START_TAG
            && type != XmlPullParser.END_DOCUMENT) {
            // Drain comments and whitespace
        }
        check(type == XmlPullParser.START_TAG) { "No start tag found" }
        val depth = parser.depth
        val returningList = mutableListOf<LinkedAccountMimeType>()
        while ((parser.next()
                .also { type = it } != XmlPullParser.END_TAG || parser.depth > depth)
            && type != XmlPullParser.END_DOCUMENT
        ) {
            val name = parser.name
            if (type == XmlPullParser.START_TAG && CONTACTS_DATA_KIND == name) {
                val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ContactsDataKind)
                try {
                    val iconId =
                        typedArray.getResourceId(R.styleable.ContactsDataKind_android_icon, 0)
                    val mimeType =
                        typedArray.getString(R.styleable.ContactsDataKind_android_mimeType)
                    val summaryColumn =
                        typedArray.getString(R.styleable.ContactsDataKind_android_summaryColumn)
                    val detailColumn =
                        typedArray.getString(R.styleable.ContactsDataKind_android_detailColumn)

                    val resources: Resources
                    try {
                        resources = packageManager.getResourcesForApplication(packageName)
                    } catch (e: PackageManager.NameNotFoundException) {
                        continue
                    }
                    try {
                        val icon: Drawable = getDrawableCompat(resources, iconId)
                        returningList.add(
                            LinkedAccountMimeType(
                                mimetype = requireNotNull(mimeType),
                                summaryColumn = summaryColumn.orEmpty(),
                                detailColumn = detailColumn.orEmpty(),
                                icon = icon,
                                packageName = packageName
                            )
                        )
                    } catch (e: Resources.NotFoundException) {
                        continue
                    }
                } finally {
                    typedArray.recycle()
                }
            }
        }
        return returningList
    }

    @Suppress("DEPRECATION")
    private fun getDrawableCompat(
        resources: Resources,
        iconId: Int
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        resources.getDrawable(iconId, null)
    } else {
        resources.getDrawable(iconId)
    }

    private fun onlyContactSyncable(
        auths: List<AuthenticatorDescription>,
        syncTypes: List<SyncAdapterType>
    ): List<AuthenticatorDescription> {
        val syncableTypes = syncTypes
            .filter { it.authority == ContactsContract.AUTHORITY }
            .map { it.accountType }

        return auths.filter { syncableTypes.contains(it.type) }
    }


    /**
     * Code taken from AOSP Contacts app. See LocalizedNameResolver.
     *
     * Returns the CONTACTS_STRUCTURE metadata (aka "contacts.xml") in the given apk package.
     *
     * This method looks through all services in the package that handle sync adapter
     * intents for the first one that contains CONTACTS_STRUCTURE metadata. We have to look
     * through all sync adapters in the package in case there are contacts and other sync
     * adapters (eg, calendar) in the same package.
     *
     * Returns `null` if the package has no CONTACTS_STRUCTURE metadata.  In this case
     * the account type *will* be initialized with minimal configuration.
     */
    @SuppressLint("WrongConstant")
    private fun loadContactsXml(resPackageName: String): XmlResourceParser? {
        val intent = Intent(SYNC_META_DATA).setPackage(resPackageName)
        val intentServices = packageManager.queryIntentServices(
            intent,
            PackageManager.GET_SERVICES or PackageManager.GET_META_DATA
        )
        for (resolveInfo in intentServices) {
            val serviceInfo = resolveInfo.serviceInfo ?: continue
            for (metadataName in METADATA_CONTACTS_NAMES) {
                val parser = serviceInfo.loadXmlMetaData(packageManager, metadataName)
                if (parser != null) {
                    return parser
                }
            }
        }

        // Package was found, but that doesn't contain the CONTACTS_STRUCTURE metadata.
        return null
    }

    private companion object {
        const val CONTACTS_DATA_KIND = "ContactsDataKind"
        const val SYNC_META_DATA = "android.content.SyncAdapter"

        /**
         * The metadata name for so-called "contacts.xml".
         *
         * On LMP and later, we also accept the "alternate" name.
         * This is to allow sync adapters to have a contacts.xml without making it visible on older
         * platforms. If you modify this also update the corresponding list in
         * ContactsProvider/PhotoPriorityResolver
         */
        val METADATA_CONTACTS_NAMES = listOf(
            "android.provider.ALTERNATE_CONTACTS_STRUCTURE",
            "android.provider.CONTACTS_STRUCTURE"
        )
    }
}
