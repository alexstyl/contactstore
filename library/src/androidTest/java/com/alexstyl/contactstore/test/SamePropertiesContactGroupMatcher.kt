package com.alexstyl.contactstore.test

import com.alexstyl.contactstore.ContactGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

/**
 * Creates a matcher that matches when the examining object is logically equal to the passing ContactGroup, minus its id.
 *
 * As an example, two groups with the same title, contactCount and note will match, even if their groupId is not the same.
 *
 */
public fun samePropertiesAs(expected: ContactGroup): Matcher<in ContactGroup> {
    return SamePropertiesContactGroupMatcher(expected)
}

private class SamePropertiesContactGroupMatcher(
    private val expected: ContactGroup
) : TypeSafeDiagnosingMatcher<ContactGroup>() {
    override fun describeTo(description: Description) {
        description.appendText("ContactGroup with: ${contentsOf(expected)}")
    }

    private fun contentsOf(expected: ContactGroup): String {
        return buildString {
            with(expected) {
                append("Title = \"$title\"")
                append(" contactCount = $contactCount")
                append(" note = \"$note\"")
                append(" account = \"$account\"")
            }
        }
    }

    override fun matchesSafely(actual: ContactGroup, mismatchDescription: Description): Boolean {
        return with(actual) {
            when {
                title != expected.title -> {
                    mismatchDescription.appendText("title was \"$title\"")
                    false
                }
                note != expected.note -> {
                    mismatchDescription.appendText("note was \"$note\"")
                    false
                }
                contactCount != expected.contactCount -> {
                    mismatchDescription.appendText("contactCount was $contactCount")
                    false
                }
                account != expected.account -> {
                    mismatchDescription.appendText("account was $account")
                    false
                }
                else -> true
            }
        }
    }
}