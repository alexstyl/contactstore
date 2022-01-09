package com.alexstyl.contactstore

public enum class DisplayNameStyle {
    /**
     * The standard text shown as the contact's display name, based on the best available information for the contact (for example, it might be the email address if the name
     * is not available).
     */
    Primary,

    /**
     * An alternative representation of the display name, such as "family name first" instead of "given name first" for Western names.
     * If an alternative is not available, the values should be the same as [Primary].
     */
    Alternative
}
