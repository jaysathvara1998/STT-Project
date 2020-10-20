package com.exapmle.sttproject

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exapmle.sttproject.databinding.RowUserBinding
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdminDashboardActivity : AppCompatActivity() {
    var userRef = FirebaseDatabase.getInstance().reference
    var userList: ArrayList<UserModel> = ArrayList()
    var adapter: LastAdapter? = null
    private var rvUser: RecyclerView? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        rvUser = findViewById(R.id.rvUser)
        rvUser!!.layoutManager = LinearLayoutManager(this)
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Loading....")
        dialog!!.setCancelable(false)
        dialog!!.show()

        adapter = LastAdapter(userList, BR.user).map<UserModel, RowUserBinding>(R.layout.row_user) {
            onBind {
                it.binding.card.setOnClickListener { view ->
                    val intent = Intent(this@AdminDashboardActivity, ResponseActivity::class.java)
                    intent.putExtra("key", userList[it.adapterPosition].id)
                    startActivity(intent)
                }
            }
        }.into(rvUser!!)
        rvUser!!.adapter = adapter

        userRef.child("User").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("Snapshot", dataSnapshot.children.toString())
                for (snapshot in dataSnapshot.children) {
                    val list = snapshot.key
                    userRef.child("User").child(list!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.value as Map<*, *>

                                val name = user["name"].toString()
                                val id = user["id"].toString()
                                val password = user["password"].toString()
                                val email = user["email"].toString()

                                val userModel = UserModel(name, email, password, id)
                                Log.e("Inner", userModel.name)
                                userList.add(userModel)
                                adapter!!.notifyDataSetChanged()
                            }
                        })
                    Log.e("Snapshot", list)
                }
                dialog!!.dismiss()
                Log.e("Admin", "${userList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AdminDashboardActivity,
                    "Error fetching data",
                    Toast.LENGTH_LONG
                ).show()
                error.toException().printStackTrace()
            }
        })
    }
}