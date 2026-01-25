package com.example.myfinalproject.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinalproject.R
import com.example.myfinalproject.adapter.LeaderboardAdapter
import com.example.myfinalproject.data.LeaderboardItem
import com.google.firebase.database.*

class LeaderboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderboardAdapter
    private lateinit var dbRef: DatabaseReference
    private val leaderboardList = mutableListOf<LeaderboardItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        recyclerView = view.findViewById(R.id.recyclerLeaderboard)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LeaderboardAdapter()
        recyclerView.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().reference.child("leaderboard")

        loadLeaderboard()

        return view
    }

    private fun loadLeaderboard() {
        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.d("LEADERBOARD", "Leaderboard kosong")
                    adapter.setData(emptyList())
                    return
                }

                leaderboardList.clear()

                for (child in snapshot.children) {
                    val item = child.getValue(LeaderboardItem::class.java)
                    if (item != null) {
                        leaderboardList.add(item)
                    }
                }

                val sortedList = leaderboardList
                    .sortedByDescending { it.score }
                    .mapIndexed { index, item ->
                        val title = when (index + 1) {
                            1 -> "Pangeran Sentana"
                            2, 3 -> "Bupati Anom"
                            4, 5 -> "Lurah"
                            else -> "Abdi Dalem Magang"
                        }
                        item.copy(title = title)
                    }

                Log.d("LEADERBOARD", "Total data: ${sortedList.size}")
                adapter.setData(sortedList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LEADERBOARD", "Load gagal: ${error.message}")
            }
        })
    }
}
