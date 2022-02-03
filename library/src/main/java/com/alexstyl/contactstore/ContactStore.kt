package com.alexstyl.contactstore

import android.accounts.AccountManager
import android.content.Context
import com.alexstyl.contactstore.ContactStore.Companion.newInstance
import com.alexstyl.contactstore.utils.DateTimeFormatParser
import kotlinx.coroutines.flow.Flow

/**
 * A store that can be used to retrieve information about the contacts of the device (via [fetchContacts]) or edit them (via [execute]).
 *
 * @see [newInstance]
 *
 */
public interface ContactStore {

    @Deprecated(
        "Prefer the version of this function that receives a lambda. This function will be removed in 1.0.0",
        ReplaceWith("execute {}")
    )
    public suspend fun execute(request: SaveRequest)

    public suspend fun execute(request: SaveRequest.() -> Unit)

    /**
     * Returns a [Flow] that emits all contacts matching the given [predicate].
     *
     * The Flow will continue emitting once a change is detected (i.e. an other app adds a new contact or a Content Provider syncs a new account) and never completes.
     *
     * @param predicate The conditions that a contact need to meet in order to be fetched
     * @param columnsToFetch The columns of the contact you need to be fetched
     * @param displayNameStyle The preferred style for the [Contact.displayName] to be returned. The fetched contacts' sorting order will match this option.
     */
    public fun fetchContacts(
        predicate: ContactPredicate? = null,
        columnsToFetch: List<ContactColumn> = emptyList(),
        displayNameStyle: DisplayNameStyle = DisplayNameStyle.Primary
    ): Flow<List<Contact>>

    public fun fetchContactGroups(
        predicate: GroupsPredicate? = null
    ): Flow<List<ContactGroup>>

    public companion object {
        /**
         * The entry point to ContactStore
         */
        public fun newInstance(context: Context): ContactStore {
            val contentResolver = context.contentResolver
            val resources = context.resources
            val contactsQueries = ContactQueries(
                contentResolver = contentResolver,
                dateParser = DateTimeFormatParser(),
                resources = context.resources,
                accountInfoResolver = AccountInfoResolver(
                    context,
                    context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager,
                    context.packageManager
                )
            )
            val groupQueries = ContactGroupQueries(
                contentResolver = contentResolver
            )
            return AndroidContactStore(
                contentResolver = contentResolver,
                newContactOperationsFactory = NewContactOperationsFactory(
                    resources
                ),
                existingContactOperationsFactory = ExistingContactOperationsFactory(
                    contentResolver,
                    resources,
                    contactsQueries
                ),
                contactQueries = contactsQueries,
                groupQueries = groupQueries,
                newGroupOperations = NewGroupOperationsFactory()
            )
        }
    }
}
