package com.udbvirtual.eventpulse.management

import EventsAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.udbvirtual.eventpulse.R
import com.udbvirtual.eventpulse.auth.LoginActivity
import com.udbvirtual.eventpulse.model.Event

class UpcomingEventsActivity : AppCompatActivity() {

    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerViewUpcomingEvents: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val events = mutableListOf<Event>()
    private lateinit var fabAddEvent: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upcoming_events)

        // Configurar RecyclerView
        recyclerViewUpcomingEvents = findViewById(R.id.recyclerViewUpcomingEvents)
        recyclerViewUpcomingEvents.layoutManager = LinearLayoutManager(this)

        fabAddEvent = findViewById(R.id.fabAddEvent)
        // Configurar menú lateral
        navigationView = findViewById(R.id.navigation_view)

        // Acción para el botón flotante
        fabAddEvent.setOnClickListener {
            val intent = Intent(this, NewEventActivity::class.java)
            startActivity(intent)
        }

        setupNavigationMenu()
        loadEvents()

    }

    private fun setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_my_events -> {
                    startActivity(Intent(this, MyEventsActivity::class.java))
                    true
                }
                R.id.nav_my_account -> {
                    startActivity(Intent(this, MyAccountActivity::class.java))
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.activity_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Configurar el header del menú
        val headerView = navigationView.getHeaderView(0)
        val userPhotoImageView = headerView.findViewById<ImageView>(R.id.imageViewUserPhoto)
        val userNameTextView = headerView.findViewById<TextView>(R.id.textViewUserName)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.textViewUserEmail)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            userNameTextView.text = currentUser.displayName ?: "Usuario"
            userEmailTextView.text = currentUser.email ?: "Sin correo"
            val photoUrl = currentUser.photoUrl
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(userPhotoImageView)
            } else {
                userPhotoImageView.setImageResource(R.drawable.ic_user_placeholder)
            }
        }
    }

    private fun loadEvents() {
        db.collection("events")
            .orderBy("date") // Ordenar por fecha de evento
            .get()
            .addOnSuccessListener { result ->
                events.clear()
                for (document in result) {
                    val event = document.toObject(Event::class.java)
                    event.id = document.id
                    events.add(event)
                }
                recyclerViewUpcomingEvents.adapter = EventsAdapter(events) { event ->
                    val intent = Intent(this, ViewEventActivity::class.java)
                    intent.putExtra("eventId", event.id)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar eventos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
