package com.redhub.controller.article

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.redhub.R
import com.redhub.controller.mainscreen.MainScreenActivity
import com.redhub.controller.mainscreen.SearchActivity
import com.redhub.controller.profile.ViewProfileActivity
import com.redhub.databinding.ActivityManageArticleBinding
import com.redhub.model.BriefArticleModel

class ManageArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageArticleBinding
    private val storage = FirebaseStorage.getInstance()
    private lateinit var articleListener: ValueEventListener
    private lateinit var myRef: DatabaseReference
    private val user = Firebase.auth.currentUser

    var dataAdmin: DatabaseReference
    init{
        dataAdmin = FirebaseDatabase.getInstance().reference
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
        myRef = database.getReference("article")
        addArticleEventListener(myRef)
        //Log.d("Han", "title:" + title + "later")

        binding.btnUploadArticle.setOnClickListener {
            myRef.removeEventListener(articleListener)
            val intent = Intent(this,PostArticleActivity::class.java)
            startActivity(intent)
        }
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigasjonen)
    }

    private fun addArticleRow(article: BriefArticleModel, articleID: String)
    {
        val inflater = LayoutInflater.from(this).inflate(R.layout.article_manage_row, null)
        binding.parentLinearLayoutArticle.addView(inflater, binding.parentLinearLayoutArticle.childCount)
        val tv_title= inflater.findViewById<TextView>(R.id.tv_title)
        tv_title.text = article.title
        val tv_rate= inflater.findViewById<TextView>(R.id.tv_rate)
        tv_rate.text = article.rates.toString()
        val iv_poster = inflater.findViewById<ImageView>(R.id.iv_poster)

        val poster = storage.getReferenceFromUrl(article.posterUri)
        poster.downloadUrl.addOnSuccessListener { uri ->
            //Toast.makeText(applicationContext, uri.toString(), Toast.LENGTH_LONG).show()
            Glide.with(this)
                .load(uri.toString())
                .override(378 , 297)
                .into(iv_poster)
        }.addOnFailureListener {
            // Handle any errors
        }
        val btn_delete = inflater.findViewById<Button>(R.id.btn_delete_article)
        btn_delete.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Do you want to delete this article?")
            builder.setPositiveButton("OK"){ _, _ ->
                val database = Firebase.database("https://redhub-a0b58-default-rtdb.firebaseio.com/")
                val myRef = database.getReference("article")
                myRef.child(articleID).removeValue()
            }
            builder.setNeutralButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        val btn_edit = inflater.findViewById<Button>(R.id.btn_edit_article)
        btn_edit.setOnClickListener {
            val intent = Intent(this, EditArticleActivity::class.java)
            intent.putExtra("articleID",articleID)
            myRef.removeEventListener(articleListener)
            startActivity(intent)
        }
    }
    private fun addArticleEventListener(articleReference: DatabaseReference) {
        // [START post_value_event_listener]
        articleListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.parentLinearLayoutArticle.removeAllViews()
                for (articleSnapshot in dataSnapshot.getChildren()) {
                    val title = articleSnapshot.child("title").getValue().toString()
                    val posterUri = articleSnapshot.child("posterUri").getValue().toString()
                    val rate = articleSnapshot.child("rate").getValue().toString().toFloat()
                    val briefarticle = BriefArticleModel(title, posterUri, rate)
                    val articleID = articleSnapshot.key
                    if (articleID != null) {
                        addArticleRow(briefarticle, articleID)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        articleReference.addValueEventListener(articleListener)
        // [END post_value_event_listener]
    }
    private val navigasjonen = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.ic_home -> {
                val intent = Intent(this@ManageArticleActivity, MainScreenActivity::class.java)
                myRef.removeEventListener(articleListener)
                startActivity(intent)

            }

            R.id.ic_profile -> {
                val intent = Intent(this@ManageArticleActivity, ViewProfileActivity::class.java)
                myRef.removeEventListener(articleListener)
                startActivity(intent)
            }
            R.id.ic_save -> {
                var adminValue: String
                var authNow: String

                dataAdmin.child("admin_account").addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var adminValue = snapshot.getValue().toString()
                        user?.let {
                            authNow = user.email.toString()
                            if (adminValue.compareTo(authNow) == 0) {

                            } else {
                                Toast.makeText(baseContext, "This feature is for admin only",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            R.id.ic_search -> {
                val intent = Intent(this@ManageArticleActivity, SearchActivity::class.java)
                myRef.removeEventListener(articleListener)
                startActivity(intent)
            }

        }
        true

    }
}