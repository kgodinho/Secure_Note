package com.kgdeveloping.securenote

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_note_update.*
import kotlinx.android.synthetic.main.activity_note_update.noteUpdateToolbar
import kotlinx.android.synthetic.main.password_prompt.*

class NoteUpdate : AppCompatActivity() {

    private var id:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_update)

        id = intent.getIntExtra("ID", 0)
        etNoteUpdateTitle.setText(intent.getStringExtra("title"))
        etNoteUpdateContent.setText(intent.getStringExtra("content"))
        cbNoteUpdate.isChecked = intent.getBooleanExtra("encrypted", false)

        //Code for top bar
        noteUpdateToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px)
        noteUpdateToolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        noteUpdateToolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.saveNote -> {
                    Log.d("NoteUpdateBar", "Save Note clicked")
                    if (etNoteUpdateTitle.text.isEmpty()){
                        Toast.makeText(this, R.string.no_title, Toast.LENGTH_LONG).show()
                    }else{
                        val encrypted = cbNoteUpdate.isChecked //If true then encryption selected
                        if (encrypted){
                            Log.d("UpdateNote", "Encryption checked")
                            getPass(encrypted)
                        }else{
                            returnNote(encrypted)
                        }
                    }
                    true
                }
                else -> false
            }
        }

    }

    private fun getPass(encrypted: Boolean){
        //Inflate AlertDialog with password prompt
        val alertDialogBuilder = AlertDialog.Builder(this).setView(R.layout.password_prompt)
        val alertDialog = alertDialogBuilder.show()

        //Set on click listeners for buttons
        alertDialog.buPassAccept.setOnClickListener {
            val pass = alertDialog.etPassword.text.toString()
            alertDialog.dismiss()
            if (pass.isEmpty()){
                Toast.makeText(this, R.string.encrypt_pass_empty, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            returnNote(encrypted, pass)
        }

        alertDialog.buPassCancel.setOnClickListener {
            alertDialog.dismiss()
//            Toast.makeText(this, R.string.pass_prompt_cancel, Toast.LENGTH_LONG).show()
        }
    }

    private fun returnNote(encrypted:Boolean, password:String = "no pass"){
        //Get data from fields and return

        intent = Intent()
        intent.putExtra("ID", id)
        intent.putExtra("encrypted", encrypted)
        if (encrypted) intent.putExtra("password", password)
        intent.putExtra("title", etNoteUpdateTitle.text.toString())
        if (etNoteUpdateContent.text.isEmpty()){
            intent.putExtra("content", "")
        }else{
            intent.putExtra("content", etNoteUpdateContent.text.toString())
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}