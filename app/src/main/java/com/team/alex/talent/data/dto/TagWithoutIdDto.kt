package com.team.alex.talent.data.dto

import com.team.alex.talent.domain.model.Tag

// TagWithoutIdDto is used to create a new one.
data class TagWithoutIdDto(
    val color: String,
    val name: String
)

fun Tag.toDto(): TagWithoutIdDto{
    return TagWithoutIdDto(
        color = this.color,
        name = this.name
    )
}