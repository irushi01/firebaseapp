package com.example.firebaseapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference
        // References to UI elements
        val studentIdEditText = findViewById<EditText>(R.id.studentIdEditText)
        val studentNameEditText = findViewById<EditText>(R.id.studentNameEditText)
        val studentMarksEditText = findViewById<EditText>(R.id.studentMarksEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        // Handle save button click
        saveButton.setOnClickListener {
            val id = studentIdEditText.text.toString()
            val name = studentNameEditText.text.toString()
            val marks = studentMarksEditText.text.toString().toIntOrNull()
            if (id.isNotEmpty() && name.isNotEmpty() && marks != null) {
                saveStudent(id, name, marks)
                Toast.makeText(this, "Student saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Handle search button click
        searchButton.setOnClickListener {
            val id = studentIdEditText.text.toString()
            if (id.isNotEmpty()) {
                searchStudent(id, resultTextView)
            } else {
                Toast.makeText(this, "Please enter a valid ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Function to save student to Firebase
    private fun saveStudent(id: String, name: String, marks: Int) {
        val student = Student(name, marks)
        database.child("students").child(id).setValue(student)
    }
    // Function to search student by ID in Firebase
    private fun searchStudent(id: String, resultTextView: TextView) {
        database.child("students").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val student = dataSnapshot.getValue(Student::class.java)
                    resultTextView.text = "ID: $id\nName: ${student?.name}\nMarks: ${student?.marks}"
                } else {
                    resultTextView.text = "Student not found"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                resultTextView.text = "Error retrieving data"
            }
        })
    }
}
// Data class for Student
data class Student(val name: String = "", val marks: Int = 0)
