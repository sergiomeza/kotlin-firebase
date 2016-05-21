package com.sergiomeza.firebasetest.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.client.*
import com.sergiomeza.firebasetest.Utils
import com.sergiomeza.firebasetest.adapters.MainAdapter
import com.sergiomeza.firebasetest.model.Movies
import org.jetbrains.anko.find
import com.sergiomeza.firebasetest.R
import com.sergiomeza.firebasetest.R.menu.main_menu

import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {
    val mAdapter = MainAdapter()
    lateinit var mUrl : String
    lateinit var mAddDialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sergiomeza.firebasetest.R.layout.activity_main)

        Firebase.setAndroidContext(this)
        mUrl = Utils(this).mUrl + "movies/" //NODO FIREBASE

        mRecView.setHasFixedSize(true);
        val llm = LinearLayoutManager(this);
        llm.orientation = LinearLayoutManager.VERTICAL;
        mRecView.layoutManager = llm;

        mRecView.adapter = mAdapter

        //MUESTRA EL PROGRESS
        mSwipeMain.post {
            mSwipeMain.isRefreshing = true
        }

        fireBaseInit()

        //AL PRESIONAR SOBRE AGREGAR NUEVO
        mBtnAdd.setOnClickListener {
            mAddDialog = Utils(this)
                    .showCustom(R.layout.add_movie,
                            R.string.add_pelicula)
            mAddDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener {
                validateForm()
            }
        }

        //ESCONDER FAB AL HACER SCROLL EN EL RECYCLER
        mRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0)
                    mBtnAdd.hide();
                else if (dy < 0)
                    mBtnAdd.show();
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId){
        R.id.logout -> logOut()
        else -> super.onOptionsItemSelected(item)
    }

    //VALIDAR FORMULARIO
    fun validateForm(){
        val mView = mAddDialog.customView
        val mName = mView?.find<TextInputLayout>(R.id.mTxtName)
        val mGenre = mView?.find<TextInputLayout>(R.id.mTxtGenero)
        val mUrl = mView?.find<TextInputLayout>(R.id.mTxtUrl)
        val mDirector = mView?.find<TextInputLayout>(R.id.mTxtDirector)

        if(!mName?.editText?.text?.isEmpty()!! &&
                !mGenre?.editText?.text?.isEmpty()!! &&
                !mUrl?.editText?.text?.isEmpty()!! &&
                !mDirector?.editText?.text?.isEmpty()!!){
            val mMovie =
                    Movies(mName?.editText?.text.toString().trim(),
                            mGenre?.editText?.text.toString().trim(),
                            mUrl?.editText?.text.toString().trim(),
                            mDirector?.editText?.text.toString().trim()
                    )
            addToFire(mMovie)
            mAddDialog.dismiss()
        }
        else {
            toast("Llena todos los campos..")
        }
    }

    //INICIALIZAR FIREBASE CON LOS LISTENERS DE LOS EVENTOS DE LOS CHILDS
    fun fireBaseInit(){
        Firebase(mUrl).addChildEventListener(
                object : ChildEventListener {
                    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                        var mMovie = dataSnapshot.getValue(Movies::class.java)
                        mAdapter.removeItemByObject(mMovie)
                    }

                    override fun onCancelled(p0: FirebaseError?) {
                        //throw UnsupportedOperationException()
                    }

                    override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                        var mMovie = dataSnapshot.getValue(Movies::class.java)
                        mAdapter.update(mMovie)
                    }

                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                        //throw UnsupportedOperationException()
                    }

                    override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                        var mMovie = dataSnapshot.getValue(Movies::class.java)
                        mAdapter.add(mMovie)
                        mSwipeMain.isRefreshing = false
                        mSwipeMain.isEnabled = false
                    }
                })

        //CLICK PARA ELIMINAR
        mAdapter.onItemClickListener = {
            deleteMovie(it)
        }
    }

    //ELIMINAR
    fun deleteMovie(mPosition: Int){
        val mDialog =
                Utils(this).confirmDialog(R.string.are_you_sure,
                        mTitle = R.string.delete_title)
        mDialog.getActionButton(DialogAction.POSITIVE).onClick {
            val mMovie = mAdapter.getItemAt(mPosition)
            Firebase(mUrl)
                    .orderByChild("name")
                    .equalTo(mMovie.name)//SE PODRIA COMPARAR POR UNIQUE O ALGO MAS EXACTO
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                val firstChild = dataSnapshot.children.iterator().next();
                                firstChild.ref.removeValue();
                                mDialog.dismiss()
                            }
                        }

                        override fun onCancelled(p0: FirebaseError?) {
                            throw UnsupportedOperationException()
                        }
                    })
        }
    }

    //AGREGAR
    fun addToFire(mMovie: Movies){
        Firebase(mUrl)
            .push()
            .setValue(mMovie)
    }

    //CERRAR SESION
    fun logOut():Boolean{
        val mDialog =
                Utils(this).confirmDialog(R.string.are_you_sure, R.string.logout)
        mDialog.getActionButton(DialogAction.POSITIVE).onClick {
            Firebase(mUrl).unauth()
            startActivity(intentFor<LoginActivity>())
            finish()
            mDialog.dismiss()
        }
        return true
    }
}


