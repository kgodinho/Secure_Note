package com.kgdeveloping.securenote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
Class to interact with Room to abstract the SQLite Database
Storing Note objects that contain:
    1. It's ID
    2. It's Title
    3. It's plainContent (Plaintext if not encrypted)
    4. It's encryptedContent (The ByteArray if it is encrypted)
    5. Whether it is encrypted or not
    6. Its Salt (if encrypted)
    7. Its IV (if encrypted)
SHOULD have a title and isEncrypted SHOULD be set. The rest is dependant on whether it is encrypted or not.
 */

@Entity
data class Note (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "Title") var title:String,
    @ColumnInfo(name = "PlainContent") var plainContent:String? = null,
    @ColumnInfo(name = "EncryptedContent") var encryptedContent:ByteArray? = null,
    @ColumnInfo(name = "IsEncrypted") var isEncrypted:Boolean,
    @ColumnInfo(name = "Salt") var salt:ByteArray? = null,
    @ColumnInfo(name = "IV") var iv:ByteArray? = null
) {
    //Auto-Generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (id != other.id) return false
        if (title != other.title) return false
        if (plainContent != other.plainContent) return false
        if (isEncrypted != other.isEncrypted) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result!! + (title.hashCode())
        result = 31 * result + (plainContent?.hashCode() ?: 0)
        result = 31 * result + (isEncrypted.hashCode())
        return result
    }
}

@Dao
interface NoteDao{
    @Query("SELECT * FROM note")
    fun getAll(): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE Title LIKE :noteTitle")
    suspend fun loadByTitle(noteTitle: String): List<Note>

    @Update
    suspend fun updateNote(note: Note)

    @Insert
    suspend fun insertNote(vararg notes: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase(){
    abstract fun noteDao() : NoteDao

    private class NoteDatabaseCallback(private val scope: CoroutineScope):RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE.let { database ->
                scope.launch {
                    popDatabase(database!!.noteDao())
                }
            }
        }

        /* Don't really need this as we don't need to populate the database when the App starts.
            We are already just reading whatever notes are already saved. So the Observer in MainActivity is seeing that notes in
            the NoteViewModel are populated and fires an event which, in turn, updates the 'notes' in the adapter and notifies that they are changed.
            If we were using this we would do this in getAppDatabase:
                INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "NoteDB").addCallback(NoteDatabaseCallback(scope)).build()
         */
        suspend fun popDatabase(noteDao: NoteDao){
            //noteDao.getAll()
        }
    }

    //For singleton access
    companion object {
        @Volatile
        private var INSTANCE:AppDatabase? = null

        fun getAppDatabase(context:Context, scope: CoroutineScope): AppDatabase?{
            //Create if not created yet and return. Or just return if made already.
            if (INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "NoteDB").build()
                }
            }
            return INSTANCE
        }
    }
}