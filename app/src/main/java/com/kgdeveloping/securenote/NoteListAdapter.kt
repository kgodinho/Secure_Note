package com.kgdeveloping.securenote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteListAdapter internal constructor(context: Context,
                                           val deleteListener: (Note) -> Unit,
                                           val noteClickListener: (Note) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //Store Item types
    companion object Types{
        const val ENCRYPTED = 1
        const val PLAIN = 2
    }

    private val inflator: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    inner class NotePlainViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ticket: LinearLayout = itemView.findViewById(R.id.noteTicket)
        val noteTitlePlain: TextView = itemView.findViewById(R.id.tvNoteTitlePlain)
        val noteContentPlain: TextView = itemView.findViewById(R.id.tvNoteContentPlain)
        val noteDeletePlain: ImageButton = itemView.findViewById(R.id.noteDeletePlain)
    }

    inner class NoteEncryptedViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ticket: LinearLayout = itemView.findViewById(R.id.noteTicketEncrypted)
        val noteTitleEncrypted: TextView = itemView.findViewById(R.id.tvNoteTitleEncrypted)
        val noteDeleteEncrypted: ImageButton = itemView.findViewById(R.id.noteDeleteEncrypted)
    }

    override fun getItemViewType(position: Int): Int {
        if (notes[position].isEncrypted){
            return ENCRYPTED
        }
        return PLAIN
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currNote = notes[position]
        when(holder){
            is NotePlainViewHolder -> {
                holder.noteTitlePlain.text = currNote.title
                holder.noteContentPlain.text = currNote.plainContent
                holder.ticket.setOnClickListener { noteClickListener(currNote) }
                holder.noteDeletePlain.setOnClickListener {deleteListener(currNote) }
            }
            is NoteEncryptedViewHolder -> {
                holder.noteTitleEncrypted.text = currNote.title
                holder.ticket.setOnClickListener { noteClickListener(currNote) }
                holder.noteDeleteEncrypted.setOnClickListener { deleteListener(currNote) }
            }
            else -> throw IllegalArgumentException("Invalid ViewHolder Type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            ENCRYPTED -> {
                val itemView = inflator.inflate(R.layout.note_encrypted_ticket, parent, false)
                NoteEncryptedViewHolder(itemView)
            }
            PLAIN -> {
                val itemView = inflator.inflate(R.layout.note_ticket, parent, false)
                NotePlainViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid Note Type")
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }
}