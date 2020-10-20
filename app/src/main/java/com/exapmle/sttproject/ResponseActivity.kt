package com.exapmle.sttproject

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exapmle.sttproject.databinding.RowResponseBinding
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResponseActivity : AppCompatActivity() {

    var adapter: LastAdapter? = null
    var responseList: ArrayList<VoiceCommandModel> = ArrayList()
    var rvResponse: RecyclerView? = null
    var resRef = FirebaseDatabase.getInstance().reference
    var key: String = ""
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        val intent = intent
        key = intent.getStringExtra("key")!!
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Loading....")
        dialog!!.setCancelable(false)
        dialog!!.show()

        rvResponse = findViewById(R.id.rvResponse)
        rvResponse!!.layoutManager = LinearLayoutManager(this)
        adapter = LastAdapter(responseList, BR.response).map<VoiceCommandModel, RowResponseBinding>(R.layout.row_response) {
            onBind {

            }
        }.into(rvResponse!!)
        rvResponse!!.adapter = adapter

        resRef.child("VoiceResponse").child(key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("Snapshot", dataSnapshot.toString())
                for (snapshot in dataSnapshot.children) {
                    val list = snapshot.key
                    resRef.child("VoiceResponse").child(key).child(list!!).addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.e("Response", snapshot.toString())
                            val response = snapshot.value as Map<*, *>

                            val action = response["action"].toString()
                            val poleHeight = response["poleHeight"].toString()
                            val switchCondition = response["switchCondition"].toString()
                            val switchDetail = response["switchDetail"].toString()
                            val resposeId = response["responseId"].toString()

                            val voiceCommandModel = VoiceCommandModel(resposeId, switchDetail, switchCondition, action, poleHeight)
                            responseList.add(voiceCommandModel)
                            adapter!!.notifyDataSetChanged()
                        }
                    })
                }
                dialog!!.dismiss()
            }
        })
    }
}