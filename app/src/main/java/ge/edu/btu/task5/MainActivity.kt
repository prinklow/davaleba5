package ge.edu.btu.task5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import ge.edu.btu.task5.data.AppDatabase
import ge.edu.btu.task5.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var runDistance: EditText
    private lateinit var swimDistance: EditText
    private lateinit var calories: EditText
    private lateinit var statistic: TextView
    private lateinit var register: Button
    private lateinit var dtbase: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        runDistance = findViewById(R.id.runRange)
        swimDistance = findViewById(R.id.swimRange)
        calories = findViewById(R.id.calorie)
        statistic = findViewById(R.id.stats)
        register = findViewById(R.id.save)

        dtbase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_db"
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            setData()
        }

        register.setOnClickListener {
            if (validateInputs()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    dtbase.userDao().insert(
                        User(
                            runRange = runDistance.toDouble(),
                            swimRange = swimDistance.toDouble(),
                            calorie = calories.toDouble()
                        )
                    )
                    clearInputs()
                    setData()
                }
            }
        }
    }

    private suspend fun setData() {
        val userDao = dtbase.userDao()
        val totalDistance = userDao.totalDistance()

        val avgRun = userDao.avgOfRunRange().toString()
        val avgSwim = userDao.avgOfSwimRange().toString()
        val avgCalorie = userDao.avgOfCalories().toString()

        withContext(Dispatchers.Main) {
            statistic.text = getString(
                R.string.average_stats,
                avgRun,
                avgSwim,
                avgCalorie,
                totalDistance.toString()
            )
        }
    }


    private fun EditText.toDouble(): Double {
        return this.text.toString().toDouble()
    }

    private fun validateInputs(): Boolean {
        return runDistance.text.toString().isEmpty() || swimDistance.text.toString()
            .isEmpty() || calories.text.toString().isEmpty()
    }

    private fun clearInputs() {
        runDistance.setText("")
        swimDistance.setText("")
        calories.setText("")
    }
}
