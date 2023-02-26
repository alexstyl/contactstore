package com.alexstyl.contactstore.test

import com.alexstyl.contactstore.ExperimentalContactStoreApi
import com.alexstyl.contactstore.GroupMembership
import com.alexstyl.contactstore.GroupsPredicate
import com.alexstyl.contactstore.ImmutableContactGroup
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@OptIn(ExperimentalContactStoreApi::class)
internal class FetchGroupTestStoreTest {

    @Test
    fun returnsAllGroups(): Unit = runBlocking {
        val store = TestContactStore(
            contactGroupsSnapshot = listOf(
                StoredContactGroup(
                    groupId = 0,
                    title = "GroupA",
                    note = "Note"
                ),
                StoredContactGroup(
                    groupId = 1,
                    title = "GroupB",
                )
            )
        )
        val actual = store.fetchContactGroups().blockingGet()
        assertThat(actual).containsExactly(
            ImmutableContactGroup(
                groupId = 0,
                title = "GroupA",
                contactCount = 0,
                note = "Note",
            ),
            ImmutableContactGroup(
                groupId = 1,
                title = "GroupB",
                contactCount = 0,
                note = null,
            )
        )
    }

    @Test
    fun doesNotReturnDeleted(): Unit = runBlocking {
        val store = TestContactStore(
            contactGroupsSnapshot = listOf(
                StoredContactGroup(
                    groupId = 0,
                    title = "GroupA",
                    note = "Note"
                ),
                StoredContactGroup(
                    groupId = 1,
                    title = "GroupB",
                    isDeleted = true
                )
            )
        )
        val actual = store.fetchContactGroups().blockingGet()
        assertThat(actual).containsExactly(
            ImmutableContactGroup(
                groupId = 0,
                title = "GroupA",
                contactCount = 0,
                note = "Note",
            )
        )
    }

    @Test
    fun countsContactsInGroup(): Unit = runBlocking {
        val store = TestContactStore(
            contactGroupsSnapshot = listOf(
                StoredContactGroup(
                    groupId = 0,
                    title = "GroupA",
                    note = "Note"
                )
            ),
            contactsSnapshot = listOf(
                StoredContact(
                    contactId = 0,
                    groups = listOf(GroupMembership(0))
                ),
                StoredContact(
                    contactId = 1,
                    groups = listOf(GroupMembership(0))
                )
            )
        )
        val actual = store.fetchContactGroups().blockingGet()
        assertThat(actual).containsExactly(
            ImmutableContactGroup(
                groupId = 0,
                title = "GroupA",
                contactCount = 2,
                note = "Note",
            ),
        )
    }

    @Test
    fun fetchesAccordingToIdLookupPredicate(): Unit = runBlocking {
        val store = TestContactStore(
            contactGroupsSnapshot = listOf(
                StoredContactGroup(
                    groupId = 0,
                    title = "GroupA",
                    note = "Note"
                ),
                StoredContactGroup(
                    groupId = 1,
                    title = "GroupB",
                )
            )
        )
        val actual = store.fetchContactGroups(
            predicate = GroupsPredicate.GroupLookup(
                inGroupIds = listOf(0)
            )
        ).blockingGet()
        assertThat(actual).containsExactly(
            ImmutableContactGroup(
                groupId = 0,
                title = "GroupA",
                contactCount = 0,
                note = "Note",
            ),
        )
    }

    @Test
    fun fetchesAccordingToDeletedPredicate(): Unit = runBlocking {
        val store = TestContactStore(
            contactGroupsSnapshot = listOf(
                StoredContactGroup(
                    groupId = 0,
                    title = "GroupA",
                    note = "Note"
                ),
                StoredContactGroup(
                    groupId = 1,
                    title = "GroupB",
                    isDeleted = true
                )
            )
        )
        val actual = store.fetchContactGroups(
            predicate = GroupsPredicate.GroupLookup(
                includeDeleted = true
            )
        ).blockingGet()
        assertThat(actual).containsExactly(
            ImmutableContactGroup(
                groupId = 0,
                title = "GroupA",
                contactCount = 0,
                note = "Note",
            ),
            ImmutableContactGroup(
                groupId = 1,
                title = "GroupB",
                contactCount = 0,
                note = null,
            )
        )
    }

}
