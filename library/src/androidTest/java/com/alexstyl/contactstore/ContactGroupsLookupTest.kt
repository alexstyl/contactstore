package com.alexstyl.contactstore

import com.alexstyl.contactstore.GroupsPredicate.GroupLookup
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

internal class ContactGroupsLookupTest : ContactStoreTestBase() {

    private lateinit var groupA: ContactGroup
    private lateinit var groupB: ContactGroup

    override fun before(): Unit = runBlocking {
        super.before()
        groupA = buildStoreContactGroup {
            title = "GroupA"
            note = "Hi"
        }
        groupB = buildStoreContactGroup { title = "GroupB" }
        store.execute {
            insert {
                groupMembership(groupA.groupId)
            }
        }
    }

    @Test
    fun fetchContactGroup(): Unit = runBlocking {
        val actual = store.fetchContactGroups().blockingGet()

        val expected = listOf(
            ImmutableContactGroup(
                groupId = groupA.groupId,
                title = groupA.title,
                contactCount = 1,
                note = "Hi",
            ),
            ImmutableContactGroup(
                groupId = groupB.groupId,
                title = groupB.title,
                contactCount = 0,
                note = "",
            )
        )

        assertThat(actual, equalTo(expected))
    }

    @Test
    fun lookupContactGroup(): Unit = runBlocking {
        val actual = store.fetchContactGroups(
            predicate = GroupLookup(
                listOf(groupA.groupId)
            )
        ).blockingGet()

        val expected = listOf(
            ImmutableContactGroup(
                groupId = groupA.groupId,
                title = groupA.title,
                contactCount = 1,
                note = "Hi",
            )
        )

        assertThat(actual, equalTo(expected))
    }
}
