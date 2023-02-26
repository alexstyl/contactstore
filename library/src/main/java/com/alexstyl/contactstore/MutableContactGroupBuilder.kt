package com.alexstyl.contactstore

public class MutableContactGroupBuilder(
    public var title: String = "",
    public var note: String? = null,
    public var account: InternetAccount? = null
)