package xyj.pers.accountkeeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import xyj.pers.accountkeeper.databinding.ActivityLoginBinding
import xyj.pers.accountkeeper.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container,  LoginFragment()).commit()

    }
}