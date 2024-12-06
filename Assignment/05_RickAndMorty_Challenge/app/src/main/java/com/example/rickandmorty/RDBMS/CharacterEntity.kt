package com.example.rickandmorty.RDBMS

import androidx.room.Entity
import androidx.room.PrimaryKey

//
@Entity(tableName = "characters_table")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val species: String,
    val gender: String,
    val origin: String,
    val image: String
)
