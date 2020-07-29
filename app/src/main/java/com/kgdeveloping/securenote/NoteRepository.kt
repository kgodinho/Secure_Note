package com.kgdeveloping.securenote

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao:NoteDao) {
    val notes:LiveData<List<Note>> = noteDao.getAll()

    suspend fun insertNote(vararg notes:Note){
        noteDao.insertNote(*notes)
    }

    suspend fun updateNote(note:Note){
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note:Note){
        noteDao.deleteNote(note)
    }

    suspend fun loadByTitle(noteTitle: String):List<Note>{
        return noteDao.loadByTitle(noteTitle)
    }

}