package com.kgdeveloping.securenote

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_note_add.*
import kotlinx.android.synthetic.main.activity_note_update.noteUpdateToolbar
import kotlinx.android.synthetic.main.add_password_prompt.*
import kotlinx.android.synthetic.main.password_prompt.buPassAccept
import kotlinx.android.synthetic.main.password_prompt.buPassCancel
import kotlinx.android.synthetic.main.password_prompt.etPassword

class NoteAdd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_add)

        //Code for top bar
        noteUpdateToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px)
        noteUpdateToolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        noteUpdateToolbar.setOnMenuItemClickListener{ menuItem ->
            when(menuItem.itemId){
                R.id.saveNote -> {
                    Log.d("NoteAddBar", "Save Note clicked")
                    if (etNoteAddTitle.text.isEmpty()){
                        Toast.makeText(this, R.string.no_title, Toast.LENGTH_LONG).show()
                    }else{
                        val encrypted = cbNoteAdd.isChecked //If true then encryption selected
                        if (encrypted){
                            Log.d("addNote", "Encryption checked")
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
        val alertDialogBuilder = AlertDialog.Builder(this).setView(R.layout.add_password_prompt)
        val alertDialog = alertDialogBuilder.show()

        //Set on click listeners for buttons
        alertDialog.buPassAccept.setOnClickListener {
            val pass = alertDialog.etPassword.text.toString()
            var passGood = true
            if (pass.isEmpty()){
                Toast.makeText(this, R.string.encrypt_pass_empty, Toast.LENGTH_LONG).show()
                passGood = false
            }else{
                //check that passwords match
                val passConfirm = alertDialog.etPasswordConfirm.text.toString()
                if (passConfirm.isEmpty()){
                    Toast.makeText(this, R.string.pass_no_match, Toast.LENGTH_LONG).show()
                    passGood = false
                }else{
                    if (!pass.equals(passConfirm, ignoreCase = false)){
                        Toast.makeText(this, R.string.pass_no_match, Toast.LENGTH_LONG).show()
                        passGood = false
                    }
                }
            }

            if (passGood){
                alertDialog.dismiss()
                returnNote(encrypted, pass)
            }
        }

        alertDialog.buPassCancel.setOnClickListener {
            alertDialog.dismiss()
//            Toast.makeText(this, R.string.pass_prompt_cancel, Toast.LENGTH_LONG).show()
        }
    }

    private fun returnNote(encrypted:Boolean, password:String = "no pass"){
        //Get data from fields and return

        intent = Intent()
        intent.putExtra("encrypted", encrypted)
        if (encrypted) intent.putExtra("password", password)
        intent.putExtra("title", etNoteAddTitle.text.toString())
        if (etNoteAddContent.text.isEmpty()){ //make sure null is not passed
            intent.putExtra("content", "")
        }else{
            intent.putExtra("content", etNoteAddContent.text.toString())
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}