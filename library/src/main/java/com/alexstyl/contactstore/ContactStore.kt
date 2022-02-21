package com.alexstyl.contactstore

import android.accounts.AccountManager
import android.content.Context
import androidx.annotation.WorkerThread
import com.alexstyl.contactstore.ContactStore.Companion.newInstance
import com.alexstyl.contactstore.utils.DateTimeFormatParser

/**
 * A store that can be used to retrieve information about the contacts of the device (via [fetchContacts]) or edit them (via [execute]).
 *
 * @see [newInstance]
 *
 */
public interface ContactStore {

    /**
     * Executes all the operations in the given [SaveRequest].
     *
     * This function is blocking. It will complete as soon as all operations are processed.
     *
     */
    @WorkerThread
    public fun execute(builder: SaveRequest.() -> Unit)

    /**
     * Returns a [FetchRequest] that emits all contacts matching the given [predicate] when collected.
     *
     * @param predicate The conditions that a contact need to meet in order to be fetched
     * @param columnsToFetch The columns of the contact you need to be fetched
     * @param displayNameStyle The preferred style for the [Contact.displayName] to be returned. The fetched contacts' sorting order will match this option.
     */
    public fun fetchContacts(
        predicate: ContactPredicate? = null,
        columnsToFetch: List<ContactColumn> = emptyList(),
        displayNameStyle: DisplayNameStyle = DisplayNameStyle.Primary
    ): FetchRequest<List<Contact>>

    /**
     * Returns a [FetchRequest] that emits all contact groups matching the given [predicate] when collected.
     *
     * @param predicate The conditions that a contact group need to meet in order to be fetched
     */
    public fun fetchContactGroups(
        predicate: GroupsPredicate? = null
    ): FetchRequest<List<ContactGroup>>

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
                ),
                rawContactQueries = RawContactQueries(
                    contentResolver = contentResolver
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
                contactGroupOperations = GroupOperationsFactory(),
                fetchRequestFactory = FetchRequestFactory(
                    contactQueries = contactsQueries,
                    groupQueries = groupQueries,
                )
            )
        }
    }
}
