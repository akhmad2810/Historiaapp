package com.example.myfinalproject.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myfinalproject.R

class QuizQuestionsFragment : Fragment() {

    private val questions = listOf(
        Question("Pada gambar, apa nama bangunan bersejarah tersebut?", listOf("Tugu Yogyakarta", "Benteng Vredeburg", "Kraton Yogyakarta", "Benteng Kulon"), 1, R.drawable.benteng_vredeburg),
        Question("Pada masa kolonial Belanda, awalnya didirikan dengan nama Hollands Chinese School(HCS), Setelah Indonesia merdeka, bangunan ini diserahkan kepada BOPKRI. Apa nama bangunan bersejarah ini?", listOf("SMA Bopkri I Yogyakarta", "SDN Ungaran I", "SMP Bopkri I Yogyakarta", "SD Negeri Pakualaman I"), 2),
        Question("Pada gambar, bangunan ini sempat menjadi tempat tinggal sementara Dr. Prawoto Mangkusasmito(tokoh partai masyumi) saat ibukota negara berada di Yogyakarta. Apa nama bangunan bersejarah tersebut?", listOf("Indraloka Homestay", "Museum Biologi", "Lembaga Pemasyarakatan Wirogunan", "Hotel Limaran"), 0, R.drawable.indralokahomestay),
        Question("Pada titik koordinat ini, merupakan bangunan bersejarah. Bangunan ini didirikan sejak masa pemerintahan Belanda pada tahun 1887. Sejak berdirinya hingga saat ini, bangunan ini berfungsi sebagai stasiun utama. Apa nama bangunan bersejarah ini?", listOf("Stasiun Lempuyangan", "Perpustakaan Kota Yogyakarta", "Museum Sonobudoyo", "Stasiun Kereta Api Tugu Yogyakarta"), 3, R.drawable.koordinat_stasiuntugu),
        Question("Bangunan ini sudah berdiri sejak tahun 1824-an. Pada 6 Januari 1946, bangunan ini digunakan sebagai istana negara Yogyakarta saat ibukota negara pindah ke Yogyakarta. Apa nama bangunan bersejarah ini?", listOf("Kompleks Gedung Agung Yogyakarta", "Keraton Yogyakarta", "Benteng Vredeburg", "SMP Bopkri II Yogyakarta"), 0),
        Question("Bangunan ini berdiri pada tanggal 29 Agustus 1943. Pada saat ibukota negara pindah ke Kota Yogyakarta, bangunan ini digunakan sebagai tempat ibadah para menteri serta tokoh penting muslim yang berada di Yogyakarta. Apa nama bangunan bersejarah ini?", listOf("Masjid Gedhe Keraton", "Masjid Margoyuwono", "Kelenteng/Vuhara Buddha Prabha Gondomanan", "Masjid Agung Pura Pakualaman"), 1),
        Question("Pada gambar, merupakan titik koordinat bangunan bersejarah. Bangunan ini sempat difungsikan sebagai perpustakaan UGM pada tahun 1951 dan beralih fungsi menjadi PT Good Year pada tahun 1975. Apa nama bangunan bersejarah tersebut?", listOf("Indraloka Homestay", "Tugu Yogyakarta", "Benteng Vredeburg", "Hotel Limaran"), 3, R.drawable.koordinat_limaran),
        Question("Bangunan ini sempat digunakan untuk pertemuan Jenderal Sudirman pada masa perang kemerdekaan. Di ruang aula gedung ini, pernah dilakukan pelantikan Jenderal Soedirman sebagai Panglima Besar Tentara Nasional Indonesia. Saat ini, menjadi sekolah penting yang berada di Kota Yogyakarta. Apa nama bangunan bersejarah ini?", listOf("SMPN 8 Yogyakarta", "SMP Bopkri 1 Yogyakarta", "SMPN 2 Yogyakarta", "SD Negeri Ngupasan"), 0),
        Question("Bangunan ini didirikan oleh pihak UGM. Bangunan ini berdiri sejak tahun 1890 atau masa Kolonial. Dulunya, bangunan ini digunakan sebagai perumahan opsir-opsir Belanda untuk mengawasi aktivitas Kraton Pakualaman. Namun, pada tanggal 20 Desember 1969, bangunan ini dialihkan sebagai museum. Apa nama bangunan bersejarah ini?", listOf("Keraton Yogyakarta", "Museum Biologi", "Susteran Sang Timur", "Museum Sonobudoyo"), 1),
        Question("Pada gambar merupakan bangunan bersejarah. Bangunan ini didirikan oleh Sri Sultan Hamengkubuwono I dengan K.Wiryokusumo sebagai arsiteknya pada tahun 1775. Bangunan ini memiliki ciri-ciri atap tumpeng tiga dengan mustaka, berbentuk bujur sangkar, dan memiliki serambi. Apa nama bangunan bersejarah ini?", listOf("Masjid Margoyuwono", "Masjid Gedhe Kraton Kauman", "Gedung Societet Militer", "Stasiun Tugu Yogyakarta"), 1, R.drawable.masjidgedhekraton),
        Question("Bagaimana asal mula kawasan cagar budaya kraton ada?", listOf("Melalui perjanjian Giyanti pada tahun 1755 antara pihak Pangeran Mangkubumi dengan VOC", "Berupa peninggalan berkas permukiman masyarakat golongan Eropa Belanda", "Timbul karena konflik politik antara Pangeran Natakusuma dengan Pemerintah Kolonial Inggris", "Berawal dari pembukaan hutan Mentaok pada abad 16 yang diberikan oleh SUltan Hadiwijauya kepada Ki Pemanahan"), 0),
        Question("Apa periodisasi dari Taman Wijaya Brata?", listOf("Masa Kesultanan Matraman Baru","Masa Kolonial","Masa Pasca Kemerdekaan","Masa Kontemporer"), 2),
    ).shuffled().take(10)

    private var index = 0
    private var score = 0

    private lateinit var tvQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnNext: Button
    private lateinit var imgQuestion: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_quiz_questions_fragment, container, false)

        tvQuestion = view.findViewById(R.id.tvQuestion)
        radioGroup = view.findViewById(R.id.radioGroup)
        btnNext = view.findViewById(R.id.btnNext)
        imgQuestion = view.findViewById(R.id.imgQuestion)


        loadQuestion()

        btnNext.setOnClickListener {
            val selected = radioGroup.indexOfChild(view.findViewById(radioGroup.checkedRadioButtonId))

            if (selected == -1) {
                Toast.makeText(requireContext(), "Pilih jawaban!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selected == questions[index].correctIndex) score += 10

            index++
            if (index < questions.size) loadQuestion()
            else finishQuiz()
        }

        return view
    }

    //Melakukan pemuatan pertanyaan
    private fun loadQuestion() {
        val q = questions[index]
        tvQuestion.text = q.question

        // melakukan pengecekan jika ada gambar
        if (q.ImageResId != null) {
            imgQuestion.setImageResource(q.ImageResId)
            imgQuestion.visibility = View.VISIBLE
        } else {
            imgQuestion.visibility = View.GONE
        }
        radioGroup.removeAllViews()
        q.options.forEach {
            val rb = RadioButton(requireContext())
            rb.text = it
            radioGroup.addView(rb)
        }
    }

    private fun finishQuiz() {
        val prefs = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        prefs.edit().putInt("LAST_SCORE", score).apply()

        parentFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, QuizResultFragment.newInstance(score))
            .commit()
    }
}

data class Question(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val ImageResId: Int? = null
)
