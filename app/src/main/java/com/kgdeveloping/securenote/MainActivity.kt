package com.kgdeveloping.securenote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.password_prompt.*

class MainActivity : AppCompatActivity() {

    companion object{
        const val NEWNOTE = 1
        const val EDITNOTE = 2
    }

    private lateinit var noteViewModel: NoteViewModel
    private val encrypt = Encrypt(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set Adapter and LinearLayout on Recycle View
        val adapter = NoteListAdapter(this,
            deleteListener = {note:Note -> noteDeleteClicked(note)},
            noteClickListener = {note:Note -> noteClicked(note)})
        mainRecycler.adapter = adapter
        mainRecycler.layoutManager = LinearLayoutManager(this)

        //Init view model and set it to observe the notes list and update adapter when it is updated.
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.notes.observe(this, Observer { notes ->
            notes.let { adapter.setNotes(it) }
        })

        //Code for top app bar
        mainTopBar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.barAddNote -> {
                    Log.d("MainAppBar", "Add Note Clicked")
                    //Open add note activity
                    intent = Intent(this, NoteAdd::class.java)
                    startActivityForResult(intent, NEWNOTE)
                    true
                }
                R.id.barAbout -> {
                    Log.d("MainAppBar", "About Clicked")
                    //open about activity
                    intent = Intent(this, about::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) return

        when (requestCode){
            NEWNOTE -> {
                Log.d("Activity Result", "New Note returned")
                if (data != null){
                    val encrypted = data.getBooleanExtra("encrypted", false)
                    if (encrypted){
                        val password = data.getStringExtra("password")
                        val title = data.getStringExtra("title")
                        val content = data.getStringExtra("content")
                        //get encrypted content and values
                        val map: HashMap<String, ByteArray>
                        try {
                            map = encrypt.encrypt(plaintext = content!!, pass = password!!)
                        }catch (ex:Exception){
                            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                            return
                        }
                        noteViewModel.insertNote(Note(title = title!!,encryptedContent = map["Cipher"], salt = map["Salt"], iv = map["IV"] , isEncrypted = encrypted))
                    }else{
                        val title = data.getStringExtra("title")
                        val content = data.getStringExtra("content")
                        noteViewModel.insertNote(Note(title = title!!, plainContent = content, isEncrypted = encrypted))
                    }
                }
            }
            EDITNOTE -> {
                Log.d("Activity Result", "Edited Note returned")
                if (data != null){
                    val id = data.getIntExtra("ID", 0) // notes are updated using their primary keys (their ID) so we can just make a new note using the same ID to update it
                    val encrypted = data.getBooleanExtra("encrypted", false)
                    if (encrypted){
                        val password = data.getStringExtra("password")
                        val title = data.getStringExtra("title")
                        val content = data.getStringExtra("content")
                        //get encrypted content and values
                        val map:HashMap<String, ByteArray>
                        try {
                            map = encrypt.encrypt(plaintext = content!!, pass = password!!)
                        }catch (ex:Exception){
                            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
                            return
                        }
                        //update note
                        val note = Note(id = id, title = title!!, plainContent = null, encryptedContent = map["Cipher"], salt = map["Salt"], iv = map["IV"], isEncrypted = encrypted)
                        noteViewModel.updateNote(note)
                    }else{
                        val title = data.getStringExtra("title")
                        val content = data.getStringExtra("content")
                        val note = Note(id = id, title = title!!, plainContent = content, isEncrypted = encrypted)
                        noteViewModel.updateNote(note)
                    }
                }
            }
        }
    }

    //When a note is clicked
    private fun noteClicked(note: Note){
        //open Note View activity
        Log.d("noteClicked", "ID: ${note.id}\tTitle: ${note.title}")
        if (note.isEncrypted){
            getPass(note)
        }else{
            intent = Intent(this, NoteUpdate::class.java)
            intent.putExtra("ID", note.id)
            intent.putExtra("title", note.title)
            intent.putExtra("content", note.plainContent)
            intent.putExtra("encrypted", note.isEncrypted)
            startActivityForResult(intent, EDITNOTE)
        }
    }

    private fun getPass(note:Note){
        //Inflate AlertDialog with password prompt
        val alertDialogBuilder = AlertDialog.Builder(this).setView(R.layout.password_prompt)
        val alertDialog = alertDialogBuilder.show()

        //Set on click listeners for buttons
        alertDialog.buPassAccept.setOnClickListener {
            val password = alertDialog.etPassword.text.toString()
            alertDialog.dismiss()
            //password given so create intent, decrypt with given pass, and start activity
            intent = Intent(this, NoteUpdate::class.java)
            intent.putExtra("ID", note.id)
            val map:HashMap<String, ByteArray> = HashMap()
            map["Salt"] = note.salt!!
            map["IV"] = note.iv!!
            map["Cipher"] = note.encryptedContent!!
            val content:String?
            try {
                content = encrypt.decrypt(map, password)
            }catch (ex:Exception){
                Toast.makeText(this, R.string.bad_decrypt, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            intent.putExtra("title", note.title)
            intent.putExtra("content", content)
            intent.putExtra("encrypted", note.isEncrypted)
            startActivityForResult(intent, EDITNOTE)
        }

        alertDialog.buPassCancel.setOnClickListener {
            alertDialog.dismiss()
//            Toast.makeText(this, R.string.pass_prompt_cancel, Toast.LENGTH_LONG).show()
        }
    }

    //When the delete Button on a note is clicked
    private fun noteDeleteClicked(note: Note){
        Log.d("deleteClicked", "ID: ${note.id}\tTitle: ${note.title}")
        noteViewModel.deleteNote(note)
    }

}