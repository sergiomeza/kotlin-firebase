package com.sergiomeza.firebasetest.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.client.AuthData
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.sergiomeza.firebasetest.R
import com.sergiomeza.firebasetest.Utils
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {
    lateinit var mAddDialog : MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Firebase.setAndroidContext(this)

        //SI YA ESTA LOGGED
        val check = Firebase(Utils(this).mUrl)
        if(check.auth != null){
            startActivity(intentFor<MainActivity>())
            finish()
        }

        //INICIAR
        btnIniciar.setOnClickListener {
            if(!mTxtUser.text.isEmpty())
            {
                if(!mTxtPass.text.isEmpty()){
                    loginFireBase(mTxtUser?.text.toString(), mTxtPass.text.toString())
                }
            }
        }

        //REGISTRO
        btnRegistro.setOnClickListener {
            mAddDialog = Utils(this).showCustom(R.layout.register,
                    R.string.register)
            mAddDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener {
                if(validateForm()) {
                    val mDialog = Utils(this).showProgress()
                    val mView = mAddDialog.customView
                    val mUser = mView?.find<TextInputLayout>(R.id.mTxtUser)
                    val mPass = mView?.find<TextInputLayout>(R.id.mTxtPass)

                    val mFireReg = Firebase(Utils(this).mUrl)
                    mFireReg.createUser(mUser?.editText?.text?.toString()?.trim(),
                            mPass?.editText?.text?.toString()?.trim(),
                            object : Firebase.ResultHandler {
                                override fun onSuccess() {
                                    mDialog.dismiss()//CIERRA PROGRESS
                                    mAddDialog.dismiss() //CIERRA CUSTOM REG
                                    loginFireBase(mUser?.editText?.text?.toString()?.trim()!!,
                                            mPass?.editText?.text?.toString()?.trim()!!)
                                }

                                override fun onError(firebaseError: FirebaseError) {
                                    toast(firebaseError.message)
                                }
                            })
                }
            }
        }
    }

    //LOGIN EN FIREBASE
    fun loginFireBase(mEmail: String, mPass: String){
        val mDialog = Utils(this).showProgress()
        val mFireLogin = Firebase(Utils(this).mUrl)
        mFireLogin.authWithPassword(mEmail, mPass, object : Firebase.AuthResultHandler {
            override fun onAuthenticationError(firebaseError: FirebaseError) {
                mDialog.dismiss()
                toast(firebaseError.message)
            }

            override fun onAuthenticated(authData: AuthData) {
                if(!authData.uid.isEmpty()){
                    startActivity(intentFor<MainActivity>())
                    finish()
                }

                mDialog.dismiss()
            }
        })
    }

    //VALIDAR FORMULARIO BASIC
    fun validateForm(): Boolean {
        val mView = mAddDialog.customView
        val mUser = mView?.find<TextInputLayout>(R.id.mTxtUser)
        val mPass = mView?.find<TextInputLayout>(R.id.mTxtPass)

        if(!mUser?.editText?.text?.isEmpty()!!){
            if(!mPass?.editText?.text?.isEmpty()!!){
                return true
            }
            else {
                mPass?.setCustomError()
            }
        }
        else {
            mUser?.setCustomError()
        }

        return false
    }

    //EXTENSION PARA CUSTOM EROR
    fun TextInputLayout.setCustomError(){
        this.error = getString(R.string.error_form, this.hint)
        this.editText?.requestFocus()
    }
}
