package vivid.money.elmslie.samples.android.compose.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vivid.money.elmslie.samples.android.compose.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(R.id.content, PagingFragment())
            .commit()
    }
}