package com.alexstyl.contactstore.test

import com.alexstyl.contactstore.ContactGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

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
                else -> true
            }
        }
    }
}