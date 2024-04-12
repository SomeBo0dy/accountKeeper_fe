package pers.xyj.accountkeeper.ui.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pers.xyj.accountkeeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
    }
}