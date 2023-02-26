package com.alexstyl.contactstore

import com.alexstyl.contactstore.test.samePropertiesAs
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

internal class EditGroupTest : ContactStoreTestBase() {
    @Test
    fun createNewGroup(): Unit = runBlocking {
        val account = InternetAccount("test@test.com", "test.com")
        store.execute {
            insertGroup(intoAccount = account) {
                note = "Note"
                title = "GroupTitle"
            }
        }
        val actual = store.fetchContactGroups().blockingGet().first()

        val expected = ImmutableContactGroup(
            groupId = 12312,
            title = "GroupTitle",
            contactCount = 0,
            note = "Note",
            account = account
        )

        assertThat(actual, samePropertiesAs(expected))
    }

    @Test
    fun updateNewGroup(): Unit = runBlocking {
        val group = buildStoreContactGroup {
            note = "Note"
            title = "GroupTitle"
        }
        val updatedCopy = group.mutableCopy {
            note = "Updated Note"
            title = "Updated Title"
        }
        store.execute {
            updateGroup(updatedCopy)
        }
        val actual = store.fetchContactGroups().blockingGet().first()

        val expected = ImmutableContactGroup(
            groupId = -1,
            title = "Updated Title",
            contactCount = 0,
            note = "Updated Note",
        )

        assertThat(actual, samePropertiesAs(expected))
    }

    @Test
    fun deleteGroup(): Unit = runBlocking {
        val group = buildStoreContactGroup()
        store.execute {
            deleteGroup(group.groupId)
        }
        val actual = store.fetchContactGroups().blockingGet()

        assertThat(actual.isEmpty(), equalTo(true))
    }
}
