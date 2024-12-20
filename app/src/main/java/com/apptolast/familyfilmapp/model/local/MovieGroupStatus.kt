package com.apptolast.familyfilmapp.model.local

data class MovieGroupStatus(
    val movieId: Int,
    val groups: List<GroupStatus>,
)

data class GroupStatus(
    val groupId: Int,
    val groupName: String,
    val status: MovieStatus,
)

enum class MovieStatus {
    NOT_IN_GROUP,
    WATCHED_BY_OTHER,
    WATCHED_BY_USER,
    TO_WATCH_BY_OTHER,
    TO_WATCH_BY_USER,
    ;

    companion object {
        fun fromString(value: String): MovieStatus {
            return when (value) {
                "NOT_IN_GROUP" -> NOT_IN_GROUP
                "WATCHED_BY_OTHER" -> WATCHED_BY_OTHER
                "WATCHED_BY_USER" -> WATCHED_BY_USER
                "TO_WATCH_BY_OTHER" -> TO_WATCH_BY_OTHER
                "TO_WATCH_BY_USER" -> TO_WATCH_BY_USER
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
