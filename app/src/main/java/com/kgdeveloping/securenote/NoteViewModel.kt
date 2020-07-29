package com.kgdeveloping.securenote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel (application: Application): AndroidViewModel (application) {
    private val repository: NoteRepository
    val notes:LiveData<List<Note>>

    init {
        val noteDao = AppDatabase.getAppDatabase(application, viewModelScope)?.noteDao()
        repository = NoteRepository(noteDao!!)
        notes = repository.notes
    }

    //Launch Coroutines to get/add/update/delete data
    fun insertNote(vararg notes:Note) = viewModelScope.launch(Dispatchers.IO){
        repository.insertNote(*notes)
    }

    fun updateNote(note:Note) = viewModelScope.launch(Dispatchers.IO){
        repository.updateNote(note)
    }

    fun deleteNote(note:Note) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteNote(note)
    }

    fun loadByTitle(noteTitle:String) = viewModelScope.launch(Dispatchers.IO){
        repository.loadByTitle(noteTitle)
    }

}