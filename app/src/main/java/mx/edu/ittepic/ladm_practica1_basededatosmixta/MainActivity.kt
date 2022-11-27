package mx.edu.ittepic.ladm_practica1_basededatosmixta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Obtener la fecha y hora actual con simpledate format en español : EEEE, dd/MMMM/yyyy hh:mm:ss: miércoles 26/mayo/2021 11:01
         * DateTimeFormatter fecha6 = DateTimeFormatter.ofPattern("EEEE dd/MMMM/yyyy hh:mm");
        System.out.println("EEEE, dd/MMMM/yyyy hh:mm:ss: " + fecha6.format(LocalDateTime.now()));


         */

    }
}