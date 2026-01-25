package com.example.myfinalproject.Fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.myfinalproject.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.location.Location


data class PlaceData(
    val name: String,
    val position: LatLng,
    val description: String,
    val address: String,
    val coordinateStr: String,
    val nomor: String,
    val periode: String,
    val jenis: String,
    val imageRes: Int = R.drawable.default_building
)

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private val markerList = mutableListOf<com.google.android.gms.maps.model.Marker>()
    private var userLocation: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userMarker: Marker? = null
    private var routePolyline: Polyline? = null
    private lateinit var destination: LatLng
    private var hasArrived = false
    private var isNavigating = false




    private val places = listOf(

        PlaceData(
            	"Tugu Yogyakarta", LatLng(-7.782877816994324, 110.36706776150847),
            "Tugu Yogyakarta merupakan salah satu bangunan cagar budaya yang menjadi simbol dan penanda penting bagi Kota Yogyakarta. Monumen ini dibangun pada tahun 1755 atas prakarsa Pangeran Mangkubumi, yang kemudian bergelar Sri Sultan Hamengku Buwono I, sebagai bentuk rasa syukur atas berdirinya Kesultanan Yogyakarta setelah Perjanjian Giyanti. Tugu ini memiliki makna filosofis yang dalam, yaitu sebagai simbol persatuan antara raja dan rakyat dalam menghadapi masa depan bersama. Secara arsitektural, bangunan Tugu Yogyakarta terdiri dari tiga bagian utama: puncak, batang, dan umpak (dasar). Batangnya berbentuk silinder dengan lapisan kapur putih, sementara puncaknya berbentuk bulat dan runcing yang melambangkan kesatuan antara manusia dan Tuhan. Tingginya mencapai sekitar 15 meter dan terbuat dari batu bata berlapis kapur. Terletak di pertemuan empat jalan utama—Jalan Jenderal Sudirman, Jalan Pangeran Diponegoro, Jalan Pangeran Mangkubumi, dan Jalan A.M. Sangaji—Tugu ini menjadi pusat orientasi kota yang dikenal juga sebagai Tugu Pal Putih. Hingga kini, Tugu Yogyakarta tidak hanya menjadi penanda geografis dan simbol sejarah, tetapi juga ikon budaya yang merepresentasikan semangat kebersamaan, keagungan, dan identitas masyarakat Yogyakarta.",
            "Jalan Jenderal Sudirman, P.Diponegoro, Am Sangaji, Gowongan, Jetis, Kota Yogyakarta", "-7.782877816994324, 110.36706776150847", "2345", "Masa Kesultanan Mataraman baru/1755", "Bangunan bersejarah",
             R.drawable.tugu_jogja
            ),

        PlaceData(
            "Masjid Sulthonain dan Makam Nitikan", LatLng(-7.823937210168755, 110.3844562457499),
            "Masjid Sulthonain merupakan salah satu masjid kuno di Kota Yogyakarta yang dibangun pada tahun 1818, berdasarkan pahatan kuno di atas pintu utamanya. Masjid ini memiliki denah persegi panjang dengan atap bersusun dua dan empat tiang penyangga dari kayu jati. Bagian dalamnya masih mempertahankan kayu kusen dan reng asli, menampilkan keaslian arsitektur tradisional Jawa.\n" +
                    "Di kompleks masjid terdapat makam keluarga Keraton Mataram Islam, termasuk makam Raden Ronggo (putra Panembahan Senopati), Kanjeng Ratu Pakubuwono I, seorang syekh dari Arab, serta kerabat keraton lainnya. Masjid Sulthonain berdiri di atas lahan seluas ±2.869 m² dengan luas bangunan ±311,75 m². Awalnya dimiliki oleh Keraton Yogyakarta dan Surakarta, masjid ini kemudian dibangun kembali pada tahun 1889 hingga berbentuk seperti sekarang.",
            "Jalan Masjid Sulthonain, Nitikan, Sorosutan, Umbulharjo, Kota Yogyakarta",
            "-7.823937210168755, 110.3844562457499",
            "2345, belum update",
            "Masa Kesultanan Mataraman baru/1889",
            "Bangunan bersejarah",
           R.drawable.masjidsulthonain
        ),

        PlaceData(
            "Kelenteng/Vihara Buddha Prabha Gondomanan", LatLng(-7.801831423538868, 110.3695057742926),
            "Klenteng Buddha Prabha Gondomanan merupakan salah satu bangunan vihara buddha Prabha(klenteng) yang berada di Kota Yogyakarta. Klenteng ini memiliki luas bangunan sebesaar kurang lebih 274, 5 m2 dan luas lahan sebesar 563,5 m2. Klenteng ini dimiliki dan dikelola oleh Yayasan Bhakti Loka.   Bangunan ini menghadap ke barat dengan pintu masuk gerbang bagian atas berbentuk lengkung kurawa terbuat dari besi, lengkungan pintu ini dipergunakan sebagai tumpuan lampu penerangan.",
            "Jalan Brigjen Katamso No 3, Prawirodirjan, Gondomanan, Kota Yogyakarta",
            "-7.801831423538868, 110.3695057742926",
            "2345, belum update",
            "Masa Kesultanan Mataraman baru/1846",
            "Bangunan bersejarah",
             R.drawable.vihara_budha_prabha
        ),

        PlaceData(
            "Stasiun Kereta Api Tugu Yogyakarta", LatLng(-7.789296536031006, 110.36317211257544),
            "Stasiun Kereta Api Tugu Yogyakarta dibangun pada tahun 1887 pada masa pemerintahan kolonial Belanda. Stasiun ini terletak di Jalan Pangeran Mangkubumi, Kelurahan Sosromenduran, Kecamatan Gedongtengen, Kota Yogyakarta, Provinsi Daerah Istimewa Yogyakarta, dengan posisi astronomis 07°47'21\" LS – 110°21'52\" BT. Pembangunan stasiun ini merupakan bagian dari pengembangan jaringan kereta api yang diselenggarakan oleh pemerintah Belanda melalui perusahaan Staatsspoorwegen (SS). Sejak berdiri, Stasiun Tugu difungsikan sebagai stasiun utama di Kota Yogyakarta dan tetap berperan penting hingga sekarang.\n" +
                    "Bangunan Stasiun Kereta Api Tugu memiliki luas sekitar ±74.128 m² dengan luas lahan ±96.112 m². Arsitekturnya mencerminkan gaya kolonial dengan tampilan simetris dan atap pelana besar yang kokoh. Ruang-ruang utama stasiun terdiri atas ruang tunggu penumpang, ruang informasi, dan loket penjualan tiket yang terletak di sisi timur serta barat pintu masuk utama. Interior stasiun memiliki langit-langit tinggi untuk sirkulasi udara alami, menciptakan kenyamanan bagi para penumpang.\n" +
                    "Di sisi utara bangunan terdapat area tambahan yang kini digunakan sebagai kios-kios, mushola, dan kamar mandi. Bagian depan stasiun menampilkan fasad klasik dengan pilar-pilar besar dan jendela lebar bergaya Eropa yang khas. Hingga kini, Stasiun Tugu tidak hanya berfungsi sebagai pusat transportasi kereta api, tetapi juga menjadi salah satu ikon arsitektur bersejarah di Yogyakarta.\n",
            "Jalan Pangeran Mangkubumi, Sosromenduran, Sorosutan, Gedongtengen, Kota Yogyakarta",
            "7.789296536031006, 110.36317211257544",
            "2345, belum update",
            "Masa Kolonial/1887",
            "Bangunan bersejarah",
            R.drawable.stasiuntugu
        ),


              PlaceData(
            "Gereja Katholik Santo Yusup Bintaran", LatLng(-7.8029360665662795, 110.37282791523373),
            "Gereja Katolik Santo Yusup Bintaran merupakan salah satu bangunan bersejarah di Kota Yogyakarta yang memiliki nilai arsitektur tinggi dari masa kolonial. Gereja ini dirancang oleh arsitek J.H. Van Oijen dengan gaya khas arsitektur Eropa. Bangunan berbentuk persegi panjang ini menghadap ke utara dan dibangun dengan bahan utama beton, termasuk atap dan pondasinya. Aula gereja dahulu sering digunakan untuk rapat para pejabat negara, bahkan Presiden Soekarno dan Mgr. Soegijapranata pernah memanfaatkannya untuk pertemuan penting. Pada masa perjuangan kemerdekaan sekitar tahun 1947–1948, gereja ini juga digunakan sebagai tempat pengungsian warga sekitar. Salah satu ciri khas bangunannya adalah keberadaan menara lonceng di bagian depan dengan roda bercincin logam dan tujuh buah ventilasi bundar. Hingga kini, gereja yang berlokasi di Jalan Bintaran Kidul Nomor 5 ini masih aktif digunakan dan menjadi bagian dari Keuskupan Agung Yogyakarta.",
            "Jalan Bintaran Kidul No.5, Bintaran, Wirogunan, Mergangsan, Kota Yogyakarta",
            "-7.8029360665662795, 110.37282791523373",
            "2345",
            "Masa Kolonial/11 Oktober 1857",
            "Bangunan bersejarah",
                  R.drawable.gereja_santo_yusup
        ),


              PlaceData(
            "Pendopo Agung Tamansiswa", LatLng(-7.805365956582192, 110.37824550159577),
            "Pendopo Agung Tamansiswa merupakan salah satu bangunan bersejarah yang memiliki nilai penting dalam perkembangan pendidikan nasional di Indonesia. Bangunan ini didirikan pada 10 Juli 1938 dengan peletakan batu pertama oleh Nyi Hajar Dewantara. Pendopo ini menjadi bagian utama dari kompleks Tamansiswa di Jalan Tamansiswa No. 31–33, Yogyakarta. Pembangunan pendopo dilakukan secara bertahap dan diresmikan pada 16 November 1938. Sejak masa itu, pendopo digunakan untuk berbagai kegiatan penting, termasuk Kongres Tamansiswa, serta menjadi pusat pendidikan dan pengembangan nilai-nilai kebangsaan. Secara arsitektural, bangunan utama berbentuk persegi panjang yang menghadap ke barat, ditopang oleh 12 tiang penyangga dengan atap joglo tinggi khas tradisi Jawa. Di sekelilingnya terdapat bangunan tambahan seperti tratag dan ruang pendukung lain. Kompleks ini juga mencakup Sekolah Taman Dewasa, Taman Muda, dan Taman Indria, serta rumah tinggal Ki Hajar Dewantara yang kini dijadikan Museum Dewantara Kirti Griya.",
            "a",
            "-7.805365956582192, 110.37824550159577",
            "2345",
            "Masa Kesultanan Mataraman baru/1889",
            "Bangunan bersejarah",
             R.drawable.pendopo_tamansiswa
        ),

              PlaceData(
            "Gedung SMK II Yogyakarta", LatLng(-7.77705808579883, 110.36736560560931),
            "Gedung SMK Negeri 2 Yogyakarta atau dahulu dikenal sebagai Prinses Juliana School (PJS) merupakan bangunan bersejarah peninggalan masa kolonial Belanda yang didirikan pada tahun 1919. Sekolah ini menjadi lembaga pendidikan teknik tingkat pertama di Yogyakarta dan berperan penting dalam pengembangan pendidikan kejuruan di Indonesia. Gedung ini berbentuk huruf “U” dengan susunan ruang yang memanjang dan menghadap ke timur, beratap limasan dengan bahan penutup genteng. Pintu utamanya berbentuk model kupu tarung dan jendela kayu lebar bergaya kolonial. Bangunan terdiri atas ruang kelas, ruang kepala sekolah, laboratorium, ruang praktek, hingga aula dan perpustakaan. Dinding bangunan bercat krem dengan lis berwarna abu-abu, memperlihatkan gaya arsitektur kolonial yang sederhana namun kokoh. Gedung ini pernah mengalami beberapa renovasi pada tahun 1929, 1950, dan 1954, namun tetap mempertahankan bentuk aslinya. Hingga kini, bangunan tersebut masih digunakan sebagai sarana pendidikan di bawah pengelolaan SMK Negeri 2 Yogyakarta.",
            "Jalan A.M.Sangaji No 47, Jetis, Cokrodiningratan, Kota Yogyakarta",
            "-7.77705808579883, 110.36736560560931",
            "2345",
            "Kolonial/1919",
            "Bangunan bersejarah",
             R.drawable.smk2yogya
        ),

              PlaceData(
            "SMP Bopkri I Yogyakarta", LatLng(-7.793571206790184, 110.37220640007965),
            "SMP BOPKRI I Yogyakarta merupakan salah satu bangunan bersejarah peninggalan masa kolonial Belanda yang awalnya didirikan sebagai Hollands Chinese School (HCS), yaitu sekolah untuk anak-anak keturunan Tionghoa dengan bahasa pengantar Belanda. Setelah Indonesia merdeka, sekolah ini sempat ditutup, lalu pada 11 Juni 1950 dibuka kembali dan diserahkan kepada Yayasan BOPKRI. Gedung sekolah berdenah huruf “U” dan terdiri atas ruang kelas, ruang kepala sekolah, tata usaha, laboratorium, aula, dan kafetaria. Atapnya berbentuk limasan dengan genteng khas bangunan kolonial, serta memiliki teras depan yang tinggi dan dihiasi ventilasi melengkung untuk sirkulasi udara. Bangunan tambahan terletak di sisi selatan dari bangunan lama. Dengan ciri arsitektur kolonial dan peran pentingnya dalam sejarah pendidikan di Yogyakarta, SMP BOPKRI I menjadi salah satu simbol perkembangan pendidikan modern di Indonesia.",
            "Jalan Mas Soeharto No 48, Tegalpanggung, Danurejan,  Kota Yogyakarta",
            "-7.793571206790184, 110.37220640007965",
            "2345",
            "Kolonial",
            "Bangunan bersejarah",
             R.drawable.smpbopkri1
        ),


              PlaceData(
            "SMP Bopkri II Yogyakarta", LatLng(-7.801663184866722, 110.37227293401102),
            "SMP BOPKRI II Yogyakarta dibangun pada masa pemerintahan Belanda tahun 1913. Awalnya gedung ini digunakan sebagai sekolah Hollands Javaansche School (HJS), yaitu sekolah untuk anak-anak pribumi dengan bahasa Belanda sebagai pengantar. Bangunannya berdenah persegi panjang menghadap ke utara dan berlantai dua, terdiri atas ruang kelas, ruang kepala sekolah, dan tata usaha. Atapnya berbentuk limasan dengan genteng tua yang kini sebagian telah diganti. Terdapat sembilan ruang kelas dengan jendela besar dan ventilasi lebar yang dicat hijau untuk pencahayaan alami. Di sisi selatan terdapat bangunan tambahan yang digunakan untuk ruang kelas baru.",
            "Jalan Sultan Agung No 2, Bintaran, Wirogunan, Mergangsan, Kota Yogyakarta",
            "-7.801663184866722, 110.37227293401102",
            "2345",
            "Masa Kesultanan Mataraman baru/1889",
            "Bangunan bersejarah",
             R.drawable.smpbopkri2
        ),




              PlaceData(
            "SMA Bopkri I Yogyakarta", LatLng(-7.786801203294551, 110.37684714627378),
            "          Gedung SMA BOPKRI I Yogyakarta merupakan bangunan bersejarah yang berfungsi sebagai sekolah dan didirikan pada masa pemerintahan Belanda pada abad ke-19. Bangunan ini awalnya digunakan untuk Christeijk Mulo, dan pada masa pendudukan Jepang beralih fungsi menjadi tangsi militer. Pada masa revolusi, tepatnya 31 Oktober 1945, berdirilah Militer Akademi yang memanfaatkan gedung ini sebagai pusat pendidikan militer. Tahun 1957, akademi tersebut dipindahkan ke Magelang dan berubah nama menjadi Akademi Militer Nasional. Sejak saat itu, bangunan bekas akademi militer ini digunakan sebagai Sekolah Kristen SMA BOPKRI I Yogyakarta hingga sekarang.\n" +
                    "Secara arsitektural, gedung ini terdiri dari beberapa bagian, namun secara keseluruhan memiliki bentuk memanjang dengan ruang depan yang lebih tinggi dan berteras. Bangunan utama memanjang ke arah kanan dan kiri serta sedikit menyerong ke belakang. Gaya arsitektur Eropa tampak jelas pada bagian dinding, pintu, dan jendela meskipun tanpa banyak ornamen. Luas lahan bangunan ini sekitar ±8.160 m² dan dikelola oleh Yayasan BOPKRI. Lokasinya berada di Jalan Wardani No. 2, Kelurahan Kotabaru, Kecamatan Gondokusuman, Kota Yogyakarta, Provinsi Daerah Istimewa Yogyakarta, dengan batas-batas wilayah: sebelah utara Rumah Sakit Tentara, timur Universitas Duta Wacana, selatan Jalan Truno, dan barat Jalan Wardani.\n",
            "Jalan Wardani No 2, Kotabaru, Gondokusuman, Kota Yogyakarta",
            "-7.786801203294551, 110.37684714627378",
            "SK Gubernur DIY No. 239/KEP/2017",
            "Kolonial",
            "Bangunan bersejarah",
             R.drawable.smaborpkri1
        ),

              PlaceData(
            "SMPN 8 Yogyakarta", LatLng(-7.781969651855648, 110.37342878833324),
            "Gedung SMPN 8 Yogyakarta merupakan bangunan peninggalan masa pemerintahan Belanda yang berfungsi sebagai sekolah. Bangunan ini pernah digunakan untuk pertemuan Jenderal Soedirman pada masa perang mempertahankan kemerdekaan. Di ruang aula gedung ini pula pernah dilakukan pelantikan Jenderal Soedirman sebagai Panglima Besar Tentara Nasional Indonesia. Selain itu, bangunan ini juga pernah difungsikan sebagai Sekolah Guru Putri serta asramanya. Setelah itu, gedung tersebut ditetapkan menjadi SMP Negeri 7 Yogyakarta dan kini dikenal sebagai SMP Negeri 8 Yogyakarta.\n" +
                    "Secara arsitektural, bangunan ini berbentuk memanjang dengan orientasi menghadap ke utara. Di dalamnya terdapat ruang-ruang kelas dan kantor. Dinding ruang kelas dilengkapi dengan enam jendela kaca besar, sementara di atas jendela terdapat ventilasi udara model jendela kecil. Atap bangunan utama menggunakan model limasan susunan dua, sedangkan bagian lain memakai atap genteng. Pada sisi timur atap terdapat jendela kecil berbentuk dudukan di atas kemiringan sebagai hiasan dan ventilasi tambahan untuk sirkulasi udara di dalam ruangan.\n",
            "Jalan Prof Dr.Kahar Mujakir No 2, Terban, Sorosutan, Umbulharjo, Kota Yogyakarta",
            "-7.781969651855648, 110.37342878833324",
            "SK Menteri : PM.25/PW.007/MKP/2007",
            "Kolonial/Abad 19",
            "Bangunan bersejarah",
             R.drawable.smpn8yogya
        ),

              PlaceData(
            "SD Negeri Ngupasan", LatLng(-7.798950755649508, 110.36316971085063),
            "Gedung Sekolah Dasar Ngupasan I dan II Yogyakarta didirikan pada tahun 1920 pada masa pemerintahan Belanda. Sejak tahun 1950, bangunan ini digunakan sebagai Sekolah Dasar Ngupasan I dan II hingga sekarang. Gedung ini terletak di Jalan Reksoabaya No. 6, Kelurahan Ngupasan, Kecamatan Gondomanan, Kota Yogyakarta, Provinsi Daerah Istimewa Yogyakarta, dengan posisi astronomis 07°47'58\" LS – 110°21'53\" BT.\n" +
                    "Bangunan peninggalan Belanda ini berbentuk persegi panjang dengan orientasi menghadap ke selatan, terdiri atas beberapa ruang kelas dan ruang kantor. Atapnya berbentuk limasan dengan bahan genteng, memberikan kesan rendah karena teras depan dan atapnya tampak menyatu. Teras bagian depan memiliki garis geometris yang tegas serta dilengkapi ventilasi udara di bagian atas. Pintu dan jendela terbuat dari kayu di bagian bawah serta kaca di bagian atas, menciptakan pencahayaan alami yang baik di dalam ruangan.\n" +
                    "Di atas pintu terdapat ventilasi udara tambahan, sedangkan di bagian bawah jendela terdapat kisi-kisi kecil untuk sirkulasi udara. Jendela kaca berteralis memiliki ukuran tinggi dan lebar yang proporsional dengan bentuk bangunan kolonialnya. Semua ruang kelas memiliki bentuk dan model yang sama. Dinding bawah bangunan dilapisi keramik berwarna biru, sementara bagian tembok atas dibiarkan polos. Hingga kini, bangunan ini masih mempertahankan bentuk aslinya meskipun telah mengalami beberapa perubahan kecil untuk perawatan.\n",
            "Jalan Reksobayan No 6, Ngupasan, Gondomanan,  Kota Yogyakarta",
            "-7.798950755649508, 110.36316971085063",
            "SK Menteri : Permenbudpar Nomor PM25/PW.007",
            "Pasca Kemerdekaan",
            "Bangunan bersejarah",
             R.drawable.sdnegerigupasan
        ),

              PlaceData(
            "SD Ungaran I", LatLng(-7.787005782492925, 110.37114820340926),
            "                       Gedung SD Ungaran I Yogyakarta merupakan bangunan peninggalan masa pemerintahan Belanda yang memiliki nilai sejarah tinggi dalam bidang pendidikan di Yogyakarta. Awalnya, gedung ini digunakan untuk sekolah Hollandsch Inlandsche School (HIS), lalu pada masa perang Kotabaru tanggal 13 Oktober 1945 dialihfungsikan sebagai Militer Akademi (MA). Setelah masa perang berakhir, bangunan ini digunakan kembali sebagai Sekolah Rakyat (SR) latihan dan Sekolah Guru Putri (SGP). Pada tanggal 5 Juli 1949, sekolah tersebut resmi menjadi Sekolah Rakyat (SR) Ungaran, dan hingga kini dikenal dengan nama SD Ungaran I Yogyakarta. \n" +
                    "\n" +
                    "           Pintu dan jendela bangunan dibuat dari kayu jati dengan bentuk berdaun ganda dan bermotif krepyak khas arsitektur kolonial. Bangunan tambahan terletak di bagian depan membujur dari barat ke timur, menghadap ke selatan, dan menyatu dengan bangunan utama. Tambahan bangunan terdiri dari dua lantai; lantai bawah difungsikan sebagai ruang kelas, pos keamanan, serta area pintu gerbang utama, sementara lantai atas digunakan untuk aula dan ruang kegiatan lainnya.  Luas bangunan ini mencapai ±607 m² dengan luas lahan sekitar ±6.761 m². Saat ini, pengelolaan gedung berada di bawah Pemerintah Kota Yogyakarta. Adapun batas-batas wilayahnya meliputi: sebelah utara Jalan Pattimura, sebelah timur Klinik Bersalin, sebelah selatan permukiman penduduk, dan sebelah barat jalan lingkungan sekolah. Kini, sebagian ruang di bangunan ini juga digunakan untuk pos keamanan sekolah.\n",
            "Jalan Patimura, Kotabaru, Gondokusuman, Kota Yogyakarta",
            "-7.787005782492925, 110.37114820340926",
            "SK Menteri : Permenbudpar No PM.25/PW.007/MKP/2007",
            "Kolonial (Belanda/Cina)",
            "Bangunan bersejarah",
             R.drawable.sdungaran1
        ),

              PlaceData(
            "Rumah Sakit Mata Dr.Yap", LatLng(-7.780774265806887, 110.37500139809904),
            "Rumah Sakit Mata “Dr. YAP” merupakan salah satu bangunan bersejarah di Yogyakarta yang memiliki nilai penting dalam bidang kesehatan dan arsitektur kolonial. Rumah sakit ini didirikan pada tanggal 21 November 1922 oleh Sri Sultan Hamengku Buwono VIII. Berdasarkan prasasti marmer yang tertanam pada dinding barat bangunan utama, tercatat tulisan berbahasa Belanda yang menunjukkan peletakan batu pertama oleh Sultan pada tanggal tersebut. Terletak di Jalan Teuku Cik Ditiro Nomor 5, Kelurahan Terban, Kecamatan Gondokusuman, rumah sakit ini menjadi fasilitas pelayanan kesehatan mata tertua di Yogyakarta.\n" +
                    "Secara arsitektural, bangunan Rumah Sakit Mata Dr. YAP menampilkan gaya kolonial dengan penyesuaian terhadap iklim tropis. Bangunannya menghadap ke timur dengan kanopi di bagian depan sebagai pintu utama. Kompleks rumah sakit terdiri atas bangunan induk dan beberapa bangunan sayap di sisi utara dan selatan yang digunakan untuk ruang perawatan, VIP, serta fasilitas pendukung seperti mushola. Atap bangunan berbentuk limasan tinggi dengan kemiringan tajam untuk memperlancar sirkulasi udara dan mengurangi panas tropis. Jendela besar berdaun ganda dan ventilasi di bagian atas berfungsi menjaga kesejukan alami di dalam ruangan. Dengan luas bangunan sekitar 4.888 meter persegi di atas lahan seluas 22.690 meter persegi, Rumah Sakit Mata Dr. YAP tidak hanya berperan penting dalam sejarah pelayanan kesehatan, tetapi juga menjadi warisan arsitektur kolonial yang masih terjaga hingga kini. Bangunan ini berada di bawah pengelolaan Yayasan Rumah Sakit Mata “Dr. YAP” dan tetap aktif memberikan layanan medis kepada masyarakat Yogyakarta.\n",
            "Jalan Teuku Cik Di Tiro No 5, Terban, Gondokusuman, Kota Yogyakarta",
            "-7.780774265806887, 110.37500139809904",
            "SK Menteri : Per. Menbudpar. No. PM25/PW.00",
            "Kolonial (Belanda/Cina)/1923",
            "Bangunan bersejarah",
             R.drawable.rumahsakitmatadryap
        ),

              PlaceData(
            "PD Tarumartani (Pabrik Cerutu)", LatLng(-7.790560997957057, 110.38142862530378),
            "PD Tarumartani atau pabrik cerutu merupakan bangunan bersejarah yang dibangun pada tahun 1918. Pabrik ini sebelumnya bernamakan NV Negresco yang berlokasi di Bulu, Jalan Magelang, Yogyakarta. Pabrik ini dahulunya diambil alih oleh Jepang. Namun, pada tahun 1945, saat Jepang menyerah, pabrik diambil alih oleh pemerintah Republik Indonesia oleh Sri Sultan Hamengku Buwono IX dengan nama PD Tarumartani yang memiliki makna “daun yang menghidupi”.  Bangunan ini memiliki arsitektur yang unik seperti bentuk atap kampung setrawuran yang memiliki makna kapasitas daya tamping yang besar. Bangunan ini terdiri dari dua blok pada komplek yang dibangun secara bertahap. Bangunan A sebagai bangunan administrasi dan produksi yang didirikan pada tahun 1920 dan bangunan B digunakan sebagai produksi dan gudang yang didirikan pada tahun 1921. Bangunan ini memiliki luas bangunan sebesar 1 ha dan luas lahan 1,899 ha. ",
            "Jalan Bambang Suprapto, Baciro, Gondokusuman, Kota Yogyakarta",
            "-7.790560997957057, 110.38142862530378",
            "SK Menteri : PM.25/PW.007/MKP/2007",
            "Masa Kolonial/1918",
            "Bangunan bersejarah",
             R.drawable.pdtarumartanipabrikcerutu
        ),

              PlaceData(
            "Perpustakaan Kota Jogja", LatLng(-7.7840172307626565, 110.37448946745587),
            "Gedung Badan Perpustakaan Daerah terletak di Jalan Farida Muridan Noto No. 21, Kotabaru, Gondokusuman, Kota Yogyakarta. Bangunan ini didirikan pada tahun 1917 pada masa kolonial dengan nama Rijksblad van Sultanaat Djogjakarta. Setelah kemerdekaan Indonesia, gedung ini sempat digunakan oleh Kementerian Luar Negeri serta Kementerian Pendidikan, Pengajaran, dan Kebudayaan. Pada masa selanjutnya (1949–1950), bangunan ini berpindah fungsi menjadi kantor Jawatan Kebudayaan dan Kesenian, serta Kantor Bidang Muskala dan Permuseuman. Kini, gedung ini dimanfaatkan sebagai Kantor Badan Perpustakaan Daerah Unit Jogja Study Centre. Arsitektur bangunan ini merupakan perpaduan gaya Eropa dengan sentuhan tradisional Jawa, berlantai dua dan menghadap ke timur laut. Atapnya berbentuk limasan dengan kemiringan tajam, dilengkapi jendela besar serta ventilasi udara berpola geometris yang memperindah tampilan fasadnya.",
            "Jalan Farida Muridan Noto  No 21, Kotabaru, Gondokusuman, Kota Yogyakarta",
            "-7.7840172307626565, 110.37448946745587",
            "2345",
            "Masa Kesultanan Mataraman baru/1755",
            "Bangunan bersejarah",
             R.drawable.perpustakaankotayogyakarta
        ),

              PlaceData(
            "Taman Wijaya Brata", LatLng(-7.806701694031693, 110.38247983635274),
            "Makam Taman Wijaya Brata yang merupakan makam tokoh pahlawan Ki Hadjar Dewantara beserta makam istrinya yakni Nyi Hadjar Dewantara merupakan salah satu makam bersejararah yang berada di Kota Yogyakarta. Makam Taman Wijaya Brata dibangun atas prakarsa Ki Soedarminto yang mengenang jasa Ki Hadjar Dewantar dan Keluarga Perguruan Tamansiswa. Beliau dikenal sebagai Bapak Pendidikan Nasiondal dan juga sebagai pendiri Tamansiswa hingga sekarang pada setiap tanggal 2 Mei yang diperingati Hari Pendidikan Nasional. Makam Taman Wijaya Brata tidak hanya makam Ki Hadjar Dewantara dan makam Nyi Hadjar Dewantara, tetapi juga makam keluarga Tamansiswa beserta tokoh-tokoh nasional bangsa Indonesia. Kompleks Makam Taman Wijaya Brata memiliki ukuran 10 x 8 m, dengan tinggi batu nisan 1,3 m. Untuk menuju Makam Ki Hadjar Dewantara, melewati lima tangga dengan Selatan dua tangga, barat dan timur masing-masing satu buah dan sebelah utara satu buah. Kelima tangga tersebut memiliki makna sebagai lambing Pancasila dan Pancadarma. Pada anak batu nisan, terdapat lambing Tamansiswa, yakni Cakra Garuda sebelah utara dan Cakra Kembang di bagian Selatan. Pada bagian bawah, terdapat karangan bunag berbentuk kelir pewayangan yang menggambarkan pergelaran hidup kemasyarakatan yang merupakan cita-cita Ki Hadjar Dewantara yang bertuliskan Tut Wuri Handayani.  Pada dinding, dipasang relief berjumlah 22 buah yang menggambarkan hidup serta perjuangan Ki Hadjar Dewantara sejak kanak-kanak hingga wafat. Taman ini memiliki luas lahan 3600m2 dan luas bangunan sebesar 80m2. Saat ini, Taman Makam Wijaya Brata diurus oleh Yayasan Pendidikan Tamansiswa.",
            "Jl. Soga No.28, Tahunan, Kec. Umbulharjo, Kota Yogyakarta",
            "-7.806701694031693, 110.38247983635274",
            "SK Menteri : PM.25/PW.007/MKP/2007 ; SK Mendikbud 226/P/2019",
            "Pasca Kemerdekaan",
            "Situs bersejarah",
             R.drawable.tamanwijayabrata
        ),

              PlaceData(
            "PT Asuransi Jiwasraya", LatLng(-7.78657672693727, 110.37198664040078),
            "Di kawasan Kotabaru, Yogyakarta, berdiri sebuah bangunan tua yang sarat akan cerita sejarah, yakni Gedung PT. Asuransi Jiwasraya di Jalan Faridan M. Noto No. 9. Bangunan ini telah ada sejak masa pemerintahan Belanda dan menjadi saksi bisu berbagai peristiwa penting di kota ini. Pada masa awal kemerdekaan, tepatnya tanggal 6 Oktober 1945, gedung ini pernah digunakan sebagai tempat perundingan pelucutan senjata antara pihak Republik Indonesia dan tentara Jepang. Dalam pertemuan tersebut hadir tokoh-tokoh penting seperti Soedarsono dan Mayor Otsuka, menjadikan gedung ini bagian dari jejak perjuangan bangsa. Secara arsitektur, bangunan ini menampilkan gaya Indis khas kolonial, dengan bentuk persegi panjang dan dua lantai yang tampak megah. Dinding bagian bawahnya dilapisi plester berpola kepala molding, sedangkan teras depannya dihiasi pagar balustrade yang kokoh. Pintu dan jendela besar bergaya krepyak memberi kesan anggun, sementara atap limasan yang tinggi dengan menara kecil di puncaknya menambah keindahan tampilan bangunan. Hingga kini, gedung ini masih berdiri tegap di antara hiruk-pikuk Kota Yogyakarta, menyimpan kenangan akan masa lalu yang penuh sejarah dan semangat perjuangan.",
            "Jalan Faridan M.Noto No.9, Kotabaru, Gondokusuman, Kota Yogyakarta",
            "-7.78657672693727, 110.37198664040078",
            "SK Menteri : PM.89/PW.007/MKP/2011",
            "Masa Kolonial/1945",
            "Bangunan bersejarah",
             R.drawable.ptasuransijiwasraya
        ),

              PlaceData(
            "Kompleks Gedung Agung ", LatLng(-7.800157964983784, 110.36389057686792),
            "Kompleks Utama Gedung Agung merupakan bangunan bersejarah yang cukup vital yang berada di Kota Yogyakarta. Bangunan ini berdiri pada tahun 1824 an. Bangunan ini difungsikan sebagai gedung Karesidenan. Pada Juni 1867, gedung ini rusak akibat gempa bumi dan difungsikan kembali pada tahun 1869 an. Pada zaman pemerintahan Jepang, gedung Agung digunakan oleh kediaman Koochi Zimmukyoku Tyookan yakni penguasa militer tertinggi Jepang di Kota Yogyakarta. Gedung ini bersejarah karena pada tanggal 29 Oktober 1945, digunakan sebagai Komite Nasional Indonesia. Pada 6 Januari 1946, gedung ini digunakan sebagai istana negara karena pada masa tersebut, ibu kota Indonesia dipindahkan ke Yogyakarta. Saat ini, gedung tersebut dijadikan sebagai gedung kepresidenan. Gedung ini memiliki tujuh bangunan dengan gaya arsitektur indis yakni Gedung Utama dengan arsitektur Eropa, Wisma Negara, Wisam Indraprasta, Wisma Sawojajar, Wisma Bumiratawu, Wisam Saptapratata dan kantor.",
            "Jalan Ahmad Yani No 3, Ngupasan, Gondomanan, Kota Yogyakarta",
            "-7.800157964983784, 110.36389057686792",
            "027/BCB/TB/III/2010",
            "Masa Kesultanan Mataraman baru/1824",
            "Bangunan bersejarah",
             R.drawable.gedungagungyogya
        ),

              PlaceData(
            "Masjid Gedhe Kraton Kauman", LatLng(-7.803879664427267, 110.36237538336924),
            "Dibangun pada tahun 1775, Masjid Gede Kauman menjadi salah satu bangunan bersejarah yang berada di Ngupasan, Gondomana, Kota Yogyakarta. Bangunan ini didirikan oleh Sri Sultan Hamengkubuwono I dengan K. Wiryokusumo sebagai arsiteknya. Masjid Gede Kauman memiliki ciri-ciri yang mirip dengan masjid-masjid kuno lainnya di Jawa, yakni dengan beratap tumpeng tiga dengan mustaka, berdenah bujur sangkar, memiliki serambi, memiliki pawestren, serta memiliki kolam pada tiga sisi masjid. Selain itu, terdapat pula gapura depan berbentuk semar tinandhu. ",
            "Jalan Pekapalan, Ngupasan, Gondomanan, Kota Yogyakarta",
            "-7.803879664427267, 110.36237538336924",
            "032/BCB/TB/III/2010",
            "Masa Kesultanan Matraman Baru/1775",
            "Bangunan bersejarah",
             R.drawable.masjidgedhekraton
        ),

              PlaceData(
            "Dalem Pengulon", LatLng(-7.803220005266179, 110.36260651826247),
            "Dalem Pengulon merupakan bangunan bersejarah yang didirikan pada tahun 1775 bersamaan dengan pembangunan Masjid Gedhe Kraton Yogyakarta yakni pada masa Sri Sultan Hamengkubuwono I atas persetujuan dari Pejabat penghulu I yakni Kiai Fakih Ibrahim Diponingrat. Di samping tempat kediaman Kiai Penghulu, masjid ini memiliki fungsi sebagai pusat pelayanan kegiatan ritual seperti Grebeg Maulid Nabi serta perkawinan putra putri sultan. Pada kompleks bangunan Pengulon, dikelilingi oleh pagar tembok dengan satu pintu masuk di bagian sudut barat laut. Secara keseluruhan, bangunan ini memiliki pendopo, pringgitan serta rumah induk. Pada bagian pintu masuk utama bangunan pendopo, terletak di bagian Selatan dan menghadap ke Selatan serta pada masing-masing pintu di dinding samping.\n",
            "Kauman GM I/111, Ngupasan, Gondomanan, Kota Yogyakarta",
            "-7.803220005266179, 110.36260651826247",
            "SK Menteri : PM.89/PW.007/MKP/201",
            "Masa Kesultanan Matraman Baru /1945",
            "Bangunan bersejarah",
             R.drawable.dalempengulon
        ),

              PlaceData(
            "Masjid Agung Pura Pakualaman", LatLng(-7.801021015567826, 110.37525898281261),
            "Masjid Agung Puro Pakualaman merupakan salah satu bangunan bersejarah yang berada di Kota Yogyakarta yang didirikan pada tahun 1850 di masa Sri Paku Alam II(1829-1858). Berdirinya masjid ini, ditandai dengan penemuan batu tulis yang berada di dinding serambi masjid tersebut. Tulisan batu tersebut yakni dengan huruf Arab 2 buah dan huruf Jawa 2 buah. Masjid ini mengalami perbaikan sehingga prasasti huruf Jawa terletak di bagian utara dan Selatan Masjid. Sedangkan, untuk prasasti huruf Arab terletak di bagian utara dan Selatan pintu masuk. Masjid Agung Puro Pakualaman memiliki tiga bagian yang penting yakni serambi, bagian utama, dan teras. Pada bagian utama,terdapat Ma;surah yakni sebagai tempat salat raja yang berada di shaf paling depan di sebelah selatan bagian imam. Ma’surah ini terbuat dari kayu dengan ragam hias ceplok  bunga dan stilisasi huruf Arab atau juga disebut mirong, serta pada lantainya lebih tinggi daripada lantai bangunan induk. Bagian atap masjid berbentuk mahkota. Dulu, pada bagian depan dan kedua samping masjid terdapat blumbangan air. Blumbangan ini kemudian diganti dengan teras depan, sedangkan pada sisi selaran terdapat tempat wudhu dan sebelah utara dibangun rumah untuk pengurus masjid.",
            "Kauman, Gunung Ketur, Pakualaman, Kota Yogyakarta",
            "-7.801021015567826, 110.37525898281261",
            "105/BCB/TB/III/2010",
            "Masa Kesultanan Matraman Baru/1850",
            "Bangunan bersejarah",
             R.drawable.masjidpurapakualaman
        ),

              PlaceData(
            "Museum Sonobudoyo", LatLng(-7.802507521450961, 110.36390049223961),
            "Museum Sonobudoyo yang terletak di Jalan Trikora 6, Ngupasan, Gondomanan, Kota Yogyakarta, merupakan salah satu bangunan bersejarah penting yang berdiri sejak masa kolonial Belanda. Museum ini didirikan pada tahun 1934 dan diresmikan pada 6 November 1935 oleh Sultan Hamengku Buwana VIII, ditandai dengan sengkalan “Kayu Winayang Ing Brahmana Budhi.” Pada masa pendudukan Jepang, pengelolaan museum berada di bawah Bupati Parindra Poerwodipoetro, sedangkan setelah kemerdekaan museum beralih ke Bupati Urutoyodoyo Budaya Pratjja sebelum akhirnya diserahkan kepada Pemerintah Daerah Istimewa Yogyakarta. Pada tahun 1974, Museum Sonobudoyo resmi menjadi milik pemerintah pusat. Bangunan museum memiliki bentuk joglo yang megah, menghadap ke selatan dan dikelilingi ruang-ruang pamer yang memanjang di lantai pertama. Selain itu, terdapat bangunan tambahan di bagian barat yang dahulu digunakan untuk kegiatan perkantoran. Dengan luas lahan lebih dari 7.800 meter persegi, museum ini tidak hanya menjadi pusat pelestarian budaya, tetapi juga saksi perjalanan panjang sejarah Jawa dan Yogyakarta.",
            "Jalan Trikora No 6, Ngupasan, Gondomanan, Kota Yogyakarta",
            "-7.802507521450961, 110.36390049223961",
            "030/BCB/TB/III/2010",
            "Masa Kolonial Belanda/1934",
            "Bangunan bersejarah",
             R.drawable.museumsonobudoyo
        ),


              PlaceData(
            "Gedung Societet Militer", LatLng(-7.799635142392841, 110.36811317124719),
            "Gedung Societet Militer yang berada di Jalan Sriwedani, Ngupasan, Gondomanan, Yogyakarta, merupakan salah satu bangunan bersejarah yang mencerminkan dinamika kehidupan kota pada masa kolonial. Dibangun pada masa pemerintahan Hindia Belanda, gedung ini awalnya difungsikan sebagai tempat berkumpulnya kaum militer Belanda. Pada masa pendudukan Jepang tahun 1942, bangunan megah ini beralih fungsi menjadi pusat latihan pertanian dan kemudian digunakan sebagai kantor lembaga pertanian. Setelah kemerdekaan Indonesia, Gedung Societet Militer dipakai oleh institusi militer yang dikelola Departemen Pertahanan dan Keamanan hingga tahun 1977. Transformasi besar terjadi pada tahun 1992, ketika gedung ini direnovasi dan dihidupkan kembali sebagai ruang pertunjukan dan tempat kegiatan kebudayaan. Kini, gedung ini terdiri atas tiga ruang utama: ruang depan yang berfungsi sebagai lobby dan area berkumpul, ruang sayap selatan dan barat yang difungsikan untuk latihan seni maupun kegiatan masyarakat, serta ruang sayap utara yang digunakan sebagai ruang ganti. Keindahan arsitektur kolonial yang dipadukan dengan fungsi budaya modern menjadikan Gedung Societet Militer sebagai saksi perjalanan panjang sejarah kota Yogyakarta dan pusat aktivitas seni yang terus hidup hingga hari ini.",
            "Jalan Sriwedani, Ngupasan, Gondomanan, Kota Yogyakarta",
            "-7.799635142392841, 110.36811317124719",
            "039/BCB/TB/III/2010",
            "Masa Kolonial/1945",
            "Bangunan bersejarah",
             R.drawable.gedungsocietetmiliter
        ),


              PlaceData(
            "Hotel Limaran", LatLng(-7.80109116236944, 110.36917339419423),
            "Hotel Limaran merupakan salah satu bangunan bersejarah yang berada di Kota Yogyakarta. Mulanya, bangunan ini sebagai Howokai Societet. Pada tanggal 1 Maret 1951, bangunan ini difungsikan sebagai perpustakaan UGM dan beralih fungsi menjadi bangunan PT Good Year di tahun 1975.  Mulai tahun 1990an, bangunan ini difungsikan sebagai Hotel Limaran hingga saat ini. Bentuk bangunan ini memiliki ciri khas dengan gaya Indis  dengan ciri-ciri seperti pintu dan jendela dengan ukuran besar serta plafon yang tinggi. Kondisinya juga mengalami pengembangan pada beberapa bagian. Luas bangunan ini Adalah 875 m2.",
            "Jalan Mayor Suryotomo No.1, Ngupasan, Gondomanan, Kota Yogyakarta",
            "-7.80109116236944, 110.36917339419423",
            "040/BCB/TB/III/2010",
            "Masa Pasca Kemerdekaan/1951",
            "Bangunan bersejarah",
             R.drawable.hotellimaran
        ),


              PlaceData(
            "SD Negeri Pura Pakualaman I", LatLng(-7.798511787008055, 110.37573648731929),
            "Awal mulanya, SD Pakualaman berada di depan bangunan Pura Pakualaman, namun pada tahun 1944 hingga saat ini, SD Pakualaman dipindahkan ke bagian belakang Pura Pakualaman yang semula digunakan oleh para abdi dalem Pura Pakualaman. Bangunan bersejarah ini menghadap ke barat dengan bentuk bangunan yakni persegi panjang. Tembok bangunan sebelah timur menempel pada pagar Pura Pakualaman sebelah belakang. Bangunan ini memiliki beberapa ruang kelas, ruang kepala sekolah, ruang guru, dan kamar kecil atau wc. ",
            "Jalan Harjowinatan 15 B, Purwokinanti, Pakualaman, Kota Yogyakarta",
            "-7.798511787008055, 110.37573648731929",
            "041/BCB/TB/III/2010",
            "Masa Kolonial/1931",
            "Bangunan bersejarah",
             R.drawable.sdpurapakualaman
        ),


              PlaceData(
            "Lembaga Pemasyarakatan Wirogunan", LatLng(-7.803714980210225, 110.37823267277417),
            "Lembaga Pemasyarakatan Wirogunan  yang berada di Kota Yogyakarta merupakan bangunan bersejarah berupa penjara pusat yang dibangun pada masa pemerintah kolonial Belanda tahun 1917. Bangunan ini diperuntukkan sebagai barak-barak kerja bagi para tahanan. Para tahanan tersebut dikenakan hukuman kerja seperti penyamakan kulit, pembuatan Sepatu ataupun kerja paksa lainnya pada masa itu. Bentuk bangunan ini memiliki gaya Indis  dengan ciri-ciri seperti pintu dan jendela yang besar dan plafon yang tinggi. Selain itu, terdapat detail khas seperti tritisan relative kecil, balustrade dari teralis besi, daun pintu luar dari kayu berbentuk krepyak dan daun pintu dari kaca serta memiliki pilar-pilar. Pada bangunan, memiliki kantor, barak-barak dan sel tahanan. Bangunan ini memiliki luas 500 m2.",
            "Jalan Taman Siswa No 6, Wirogunan, Pakualaman, Kota Yogyakarta",
            "-7.803714980210225, 110.37823267277417",
            "042/BCB/TB/III/2010",
            "Masa Kolonial/1917",
            "Bangunan bersejarah",
             R.drawable.lembagapermasyarakatanwirogunan
        ),

              PlaceData(
            "Susteran Sang Timur", LatLng(-7.8018649272854015, 110.37704122018275),
            "Susteran Sang Timur merupakan bangunan bersejarah yang berada di Kota Yogyakarta. Bangunan ini merupakan tempat tinggal suster-suster Sang Timur dan Klinik Bersalin. Sebelumnya, bangunan ini merupakan rumah tinggal keluarga M.D.Mudai. Bangunan ini menghadap ke utara, berbentuk bangunan Indis dengan ciri-ciri seperti pintu da jendela berukuran besar dan plafon yang tinggi. Bangunan ini memiliku luas sebesar 777 m2.",
            "Jalan Sultan Agung No 50, Gunung Ketur, Pakualaman, Kota Yogyakarta",
            "-7.8018649272854015, 110.37704122018275",
            "043/BCB/TB/III/2010",
            "Masa Kolonial/1900",
            "Bangunan bersejarah",
             R.drawable.susteransangtimur
        ),

              PlaceData(
            "Museum Biologi", LatLng(-7.801742380920572, 110.3743799830766),
            "Museum Biologi merupakan bangunan bersejarah yang berada di Kota Yogyakarta yang dimiliki oleh Fakultas Biologi UGM. Bangunan ini berdiri pada tahun 1890 atau pada masa Kolonial. Awalnya, bangunan ini merupakan perumahan opsir-opsir Belanda yang dipakai untuk mengawasi aktivitas Kraton Pakualaman. Namun, pada tanggal 20 Desember 1969, bangunan ini dialihkan sebagai museum. Museum ini memiliki bangunan induk sebagai museum, bangunan sayap, dan bangunan belakang yang digunakan sebagai rumah tinggal. Museum ini menghadap ke utara. ",
            "Jalan Sultan Agung No 22, Wirogunan, Mergangsan, Kota Yogyakarta",
            "-7.801742380920572, 110.3743799830766",
            "043/BCB/TB/III/2010",
            "Masa Kolonial/1900",
            "Bangunan bersejarah",
             R.drawable.museumbiologi
        ),

              PlaceData(
            "Masjid Margoyuwono", LatLng(-7.811525977611365, 110.3647638253335),
            "Masjid Margoyuwono merupakan salah satu masjid kuno yang berada di Jalan Langenastran Lor No 9 Kota Yogyakarta. Masjid ini mulanya dibangun pada tanggal 28 Maulud 1874 AJ dan diresmikan pada tanggal 29 Agustus 1943. Pada masa kemerdekaan ketika Yogyakarta sebagai tempat ibukota negara sementara, Masjid Margoyuwono digunakan sebagai tempat ibadah para Menteri serta tokoh penting yang ada di Kota Yogyakarta. Pada tahun 1966, Masjid Margoyuwono mengalam renovasi bangunan dengan perbaikan tempat wudhu.  Saat ini, Masjid Margoyuwono digunakan sebagai tempat ibadah, masjid juga digunakan untuk acara keagamaan lainnya. Masjid Margoyuwono memiliki bentuk bujur sangkar dengan ruang utama masjid dan terbuka pada serambi yang dilengkapi dengan lengkung-lengkung atap bangunan yang berupa atap tumpeng, seperti rangka kayu jati dan lantainya dari tegel kembang.",
            "Jalan Langenastran, Panembahan, Kraton, Kota Yogyakarta",
            "-7.811525977611365, 110.3647638253335",
            "046/BCB/TB/III/2010",
            "Masa Kolonial/1900",
            "Bangunan bersejarah",
             R.drawable.masjidmargoyuwono
        ),

              PlaceData(
            "Asrama Mahasiswa Kalimantan Barat “Rahadi Osmani", LatLng(-7.802917376165238, 110.37424520601043),
            "Mulanya, Asrama Mahasiswa Kalimantan Barat “Rahadi Osmani”merupakan bangunan permukiman opsir atau pejabat dan pegawai pabrik gula yang berada di wilayah Kota Yogyakarta. Saat ini, bangunan tersebut menjadi tempat tinggal mahasiswa khususnya berasal dari Kalimantan Barat. Bangunan ini bergaya Indis dengan ciri-ciri seperti pintu dan jendela dengan ukuran yang besar dan plafon tinggi. Selain itu, terlihat detail menarik yang lain seperti terdapat tritisan yang keicl, balustrade dari teralis besi, daun pintu luar dari kayu berbentuk krepyak dan daun pintu dari kaca dan juga memiliki pilar-pilar. Bangunan ini memiliki luas 427 m2.",
            "Jalan Bintaran Tengah No 10, Wirogunan, Mergangsan, Kota Yogyakarta",
            "-7.802917376165238, 110.37424520601043",
            "043/BCB/TB/III/2010",
            "Masa Kolonial",
            "Bangunan bersejarah",
             R.drawable.asramamahasiswakalbar
        ),

              PlaceData(
            "Asrama Mahasiswa Putra Sulawesi Tengah", LatLng(-7.8028032411889034, 110.37384155322981),
            "Mulanya, Asrama Mahasiswa Sulawesi Tengah merupakan bangunan permukiman opsir atau pejabat dan pegawai pabrik gula yang berada di wilayah Kota Yogyakarta. Saat ini, bangunan tersebut menjadi tempat tinggal mahasiswa khususnya berasal dari Sulawesi Tengah. Bangunan ini bergaya Indis dengan ciri-ciri seperti pintu dan jendela dengan ukuran yang besar dan plafon tinggi. Selain itu, terlihat detail menarik yang lain seperti terdapat tritisan yang keicl, balustrade dari teralis besi, daun pintu luar dari kayu berbentuk krepyak dan daun pintu dari kaca dan juga memiliki pilar-pilar. Bangunan ini memiliki luas 345 m2.",
            "Jalan Bintaran Tengah No 7, Wirogunan, Mergangsan, Kota Yogyakarta",
            "-7.8028032411889034, 110.37384155322981",
            "048/BCB/TB/III/2010",
            "Masa Kesultanan Mataraman baru/1755",
            "Bangunan bersejarah",
             R.drawable.asramamahasiswaputrasulteng
        ),

              PlaceData(
            "Asrama Putri Bundo Kanduang", LatLng(-7.802587412482917, 110.37357183759752),
            "Bangunan ini merupakan bangunan tempat tinggal mahasiswa Minang dan bernamakan Asrama Bringin pada tahun 1953. Sejak 1 Januari 1955, asrama ini menjadi mahasiswa Minang. Asrama ini dikelola oleh pemerintah Provinsi Sumatera Barat. Bangunan ini memiliki gaya Indis dengan ciri-ciri  seperti pintu dan jendela dengan ukuran yang besar dan plafon tinggi. Selain itu, terlihat detail menarik yang lain seperti terdapat tritisan yang keicl, balustrade dari teralis besi, daun pintu luar dari kayu berbentuk krepyak dan daun pintu dari kaca dan juga memiliki pilar-pilar. Bangunan ini memiliki luas 350 m2.",
            "Jalan Bintaran Tengah No 7, Wirogunan, Mergangsan, Kota Yogyakarta",
            "-7.802587412482917, 110.37357183759752",
            "049/BCB/TB/III/2010",
            "Masa Kolonial/1900",
            "Bangunan bersejarah",
             R.drawable.asramaputribundokanduang
        ),

              PlaceData(
            "Kodim 0734", LatLng(-7.774589522171245, 110.36769103684951),
            "Bangunan Kodim 0734 merupakan bangunan bersejarah yang difungsikan sebagai markas komando distrik militer (kodim) 0734 Kota Yogyakarta, yang ditempati sebagai kantor Komandan Kodim, lobby, ruang kasdim, dan staff Dandim. Bangunan ini menghadap ke timur yang didirikan pada masa kolonial Hindia Belanda. Pada gaya bangunannya, memiliki khas bergaya Indis. Dahulu, bangunan ini difungsikan sebagai tangsi Belanda dan kantor Ajen Rem. Bangunan ini memiliki luas sebesar 1.241,77 m2.",
            "Jalan A.M.Sangaji No 48, Cokrodiningratan, Jetis, Kota Yogyakarta",
            "-7.774589522171245, 110.36769103684951",
            "051/BCB/TB/III/2010",
            "Masa Kolonial/1900",
            "Bangunan bersejarah",
             R.drawable.kodim0734
        ),

              PlaceData(
            "Indraloka Heritage Homestay", LatLng(-7.779592877839494, 110.375803246302),
            "Indraloka Homestay yang terletak di Jalan Cik Di Tiro No. 18, Terban, Kota Yogyakarta, merupakan bangunan bersejarah dari masa kolonial yang dibangun sekitar tahun 1930 oleh Van der Vin. Pada masa awal kemerdekaan, ketika ibu kota Republik Indonesia dipindahkan ke Yogyakarta, rumah ini sempat menjadi tempat tinggal Dr. Prawoto Mangkusasmito, seorang anggota DRP dan tokoh Partai Masyumi. Setelah ibu kota kembali ke Jakarta, rumah tersebut berada di bawah pengelolaan Jawatan Perumahan Yogyakarta. Pada tahun 1960, bangunan ini dimiliki oleh Moerdiyono Danosoesastro dan dinamai “Indraloka”. Rumah ini menghadap ke arah selatan dengan arsitektur bergaya kolonial yang menonjol, terutama pada bagian depan bangunan yang tinggi dan berkesan megah. Bangunan dua lantai ini memiliki teras luas berbentuk segi delapan yang kemudian dialihfungsikan menjadi ruangan tertutup. Beberapa bagian atap yang awalnya terbuat dari sirap diperbarui sekitar tahun 2005 demi mempertahankan keasliannya. Indraloka berdiri di atas lahan seluas 933 m² dengan luas bangunan mencapai 511 m², menjadikannya salah satu bangunan penting yang memadukan nilai sejarah dan karakter arsitektur kolonial yang khas.",
            "Jalan Cik Di Tiro No 18, Terban, Gondokusuman, Kota Yogyakarta",
            "-7.779592877839494, 110.375803246302",
            "052/BCB/TB/III/2010",
            "Masa Kolonial/1930",
            "Bangunan bersejarah",
             R.drawable.indralokahomestay
        ),

        //      PlaceData(
//            "a", LatLng(-7.789296536031006, 110.36317211257544),
//            " a",
//            "a",
//            "-7.782877816994324, 110.36706776150847",
//            "2345",
//            "Masa Kesultanan Mataraman baru/1755",
//            "Bangunan bersejarah",
//             R.drawable.
//        ),

        //      PlaceData(
//            "a", LatLng(-7.789296536031006, 110.36317211257544),
//            " a",
//            "a",
//            "-7.782877816994324, 110.36706776150847",
//            "2345",
//            "Masa Kesultanan Mataraman baru/1755",
//            "Bangunan bersejarah",
//             R.drawable.
//        ),

        //      PlaceData(
//            "a", LatLng(-7.789296536031006, 110.36317211257544),
//            " a",
//            "a",
//            "-7.782877816994324, 110.36706776150847",
//            "2345",
//            "Masa Kesultanan Mataraman baru/1755",
//            "Bangunan bersejarah",
//             R.drawable.
//        ),

        //      PlaceData(
//            "a", LatLng(-7.789296536031006, 110.36317211257544),
//            " a",
//            "a",
//            "-7.782877816994324, 110.36706776150847",
//            "2345",
//            "Masa Kesultanan Mataraman baru/1755",
//            "Bangunan bersejarah",
//             R.drawable.
//        ),

              PlaceData(
            "Kawasan Cagar Budaya Pakualaman", LatLng(-7.8010402971962725, 110.37625538100323),
            "Kawasan Cagar Budaya Pakualaman merupakan salah satu Kawasan bersejarah yang penting yang berada di Kota Yogyakarta. Kawasan ini merupakan wilayah geopolitik hasil perkembangan politik dalam Kasultanan Ngayogyakarta Hadiningrat pada periode pemerintahan Kolonial Inggris tahun 1811-1815 di Jawa. Awalnya, Kawasan ini dari pemerintahan Kadipaten Pakualaman. Kadipaten Pakualaman terbentuk karena adanya kontrak politik antara Pangeran Natakusuma (putra Sultan Hamengkubuwana I) dengan Pemerintah Kolonial Inggris pada 17 Maret 1813. Sementara setahun sebelumnya, tepatnya pada 29 Juni 1812, Pangeran Natakusuma diberi gelar oleh pemerintah Inggris saat itu sebagai pemimpin Pakualaman dengan gelar Kanjeng Gusti Pangeran Adipati Paku Alam I. Tata ruang dari Kawasan Cagar Budaya Pakualaman menggunakkan konsep tradisional Jawa dengan model pura sebagai pusat pemerintahan dilengkapi dengan tempat peribadatan (masjid), tempat perekonomian(pasar), ruang public(lapangan) ,kelengkapan permukiman bagi abdi dalem di Pakualaman serta tempat pemakaman di dekat pura ataupun di luar pura. Konsep tata ruang cukup khas menghadap ke selatan yang mencerminkan penghormatan pada Kasultanan Ngayogyakarta berstatus lebih tinggi. Kawasan ini memiliki pol aruang tradisional Jawa yang mencerminkan gaya arsitektur Tradisional Jawa pada bangunan-bangunan di Kawasan ini. Kawasan Cagar Budaya Pakualaman memiliki situs cagar budaya yaitu Situs Cagar Budaya Pakualaman Yogyakarta dan Situs Cagar Budaya Bintaran. Di Kawasan Cagar Budaya ini, terdapat 30 bangunan cagar budaya dan delapan banguna warisan budaya seperti Masjid Agung Puro Pakualaman, Museum Biologi UGM, dll. Kawasan ini memiliki luas sebesar 22,4 ha dan zona penyangga dengan luas 78,6 ha. ",
            "Wirogunan, Purwokinanti,dan  Gunungketur (kelurahan), Pakualaman dan Mergangsan (Kecamatan), Kota Yogyakarta",
            "-7.8010402971962725, 110.37625538100323",
            "129/KEP/2023",
            "Masa Kasultanan Matraman Baru/1811-1815",
            "Kawasan bersejarah",
             R.drawable.kawasanpakualaman
        ),

              PlaceData(
            "Kawasan Cagar Budaya Kota Baru", LatLng(-7.785762455756269, 110.37269970854292),
            "Kawasan Cagar Budaya Kota Baru merupakan berkas permukiman masyarakat golongan Eropa Belanda di Kota Yogyakarta pada masa pemerintahan Kolonial Hindia Belanda. Kawasan Kota Baru merupakan permukiman Eropa–Belanda yang mulai dibangun pada sekitar tahun 1920-an sebagai kawasan terencana dengan karakter tata ruang berbentuk radial konsentris. Perancangannya mengadopsi konsep Garden City yang diadaptasikan dengan kondisi lingkungan Hindia Belanda, sehingga menghasilkan kawasan yang tertata rapi dengan fungsi permukiman, pendidikan, kesehatan, keagamaan, dan berbagai fasilitas bagi penghuninya. Mataram Boulevard—kini Jalan Sudarto—menjadi sumbu utama kawasan, berperan sebagai poros terbuka yang membentang lurus ke arah utara sekaligus menjadi pembatas wilayah barat dan timur. Pada bagian barat, banyak nama jalan menggunakan nama sungai, sedangkan bagian timur memakai nama gunung, menunjukkan pola penamaan yang terstruktur. Kawasan ini juga dipenuhi bangunan sekolah, rumah sakit, asrama militer, serta kawasan hunian lengkap dengan sarana pendukungnya. Hingga kini, Kota Baru diakui sebagai kawasan penting dengan dua situs cagar budaya, yang penetapannya tertuang dalam Keputusan Gubernur DIY No. 63/KEP/2023 dan No. 62/KEP/2023. Bangunan cagar budaya yang terdapat pada Kawasan ini meliputi Stasiun Lempuyangan, SMPN 5 Yogyakarta, Rumah Sakit Bethesda, SMAN 3 Yogyakarta, dll. ",
            "Kotabaru, Gondokusuman(Kecamatan), Kota Yogyakarta",
            "-7.785762455756269, 110.37269970854292",
            "130/KEP/2023",
            "Masa Kolonial/1920",
            "Kawasan bersejarah",
             R.drawable.kawasankotabaru
        ),

              PlaceData(
            "Kawasan Cagar Budaya Kotagede", LatLng(-7.82482104235303, 110.3968746590714),
            "Kawasan Kotagede berawal dari pembukaan hutan Mentaok pada abad ke-16 yang diberikan oleh Sultan Hadiwijaya kepada Ki Pemanahan. Wilayah ini kemudian berkembang menjadi ibu kota pertama Kerajaan Mataram Islam di bawah kepemimpinan Panembahan Senopati, Panembahan Anyakrawati, dan Sultan Agung. Pada masa Panembahan Senopati, terjadi berbagai peperangan untuk memperluas kekuasaan dan menaklukkan daerah-daerah yang belum mengakui Mataram. Di masa inilah pembangunan dan penataan Kotagede sebagai pusat pemerintahan dilakukan. Kota ini berfungsi sebagai ibu kota antara tahun 1577–1618 hingga akhirnya dipindahkan ke Kerta oleh Sultan Agung.\n" +
                    "Sebagai pusat pemerintahan Mataram, Kotagede memiliki komponen bangunan penting yang tercatat dalam sumber-sumber seperti Babad Tanah Jawi, Babad Momana, dan Babad Ing Sangkala. Bangunan tersebut meliputi pintu gerbang pabean, benteng, masjid agung, dan struktur penting kerajaan lainnya. Hingga kini, sebagian besar komponen sejarah tersebut masih dapat ditemukan, termasuk dua situs utama: Masjid Gedhe Mataram Kotagede dan Kompleks Makam Raja-Raja Mataram.\n" +
                    "Secara administratif, Kawasan Cagar Budaya Kotagede memiliki luas sekitar 303 hektare, yang terdiri atas zona inti seluas ±111 ha dan zona penyangga seluas ±192 ha. Saat ini, kawasan ini tetap mempertahankan nilai sejarahnya dan dikelola dengan baik oleh pemerintah. Penataan kawasan juga tergolong baik sehingga fungsi budaya, sejarah, dan permukiman tradisional dapat tetap terjaga hingga sekarang.\n",
            "Kotagede, Kota Yogyakarta",
            "-7.82482104235303, 110.3968746590714",
            "131/KEP/2023",
            "Masa Kesultanan Mataraman baru/1600",
            "Kawasan bersejarah",
             R.drawable.kawasankotagede
        ),

              PlaceData(
            "Kawasan Cagar Budaya Kraton", LatLng(-7.810515696680299, 110.36355733575601),
            "Pendirian Kraton Yogyakarta berawal dari konflik antara Pangeran Mangkubumi dan Sunan Pakubuwono II terkait perjanjian dengan VOC. Ketidakpuasan Pangeran Mangkubumi terhadap Perjanjian Panaraga (1746) memunculkan peperangan panjang yang berakhir dengan Perjanjian Giyanti tahun 1755. Melalui perjanjian ini, ia dinobatkan sebagai raja Ngayogyakarta bergelar Sri Sultan Hamengkubuwono I. Sebagai pemimpin sekaligus arsitek, ia membangun Panggung Krapyak, Kraton, dan Tugu Pal Putih dalam satu garis lurus yang melambangkan perjalanan hidup manusia dari bayi hingga dewasa." +
                    "Secara administrasi, Kawasan Kraton berada di Kabupaten Bantul dan Kota Yogyakarta. Kawasan ini memiliki bangunan bersejarah seperti Alun-Alun Utara, Kraton, Masjid Gedhe, dan Benteng Vredeburg, serta permukiman etnis Jawa, Cina, Eropa, dan Arab. Dengan luas sekitar 1.534 hektare, wilayah ini berfungsi sebagai pusat pemerintahan, kebudayaan, dan perdagangan. Saat ini, tatanan Kawasan Kraton tergolong terjaga dengan baik karena mendapatkan perawatan dan pengelolaan yang cukup intensif dari pemerintah. Meski demikian, pembangunan baru yang tidak terkendali tetap berpotensi merusak karakter serta bangunan bersejarah di kawasan ini.",
            "Ngupasan, Ngampilan, Pringgokusuman, Sosromenduran, Gowongan, Bumijo, Cokrodiningratan, Panembahan, Kadipaten, Patehan, Suryodiningratan, Suryatmajan, Mantrijeron, Gedongkiwo, Notoprajan, Prawirodirjan, dan Desa Panggungharjo (Kalurahan/Desa), Ngampilan, Gedongtengen, Kraton, Mantrijeron, dan Sewon (Kapanewon), Kota Yogyakarta dan Kabupaten Bantul",
            "-7.810515696680299, 110.36355733575601",
            "75/KEP/2017",
            "Masa Kasultanan Matraman Baru/1755",
            "Kawasan bersejarah",
             R.drawable.kawasankraton
        ),

        //      PlaceData(
//            "a", LatLng(-7.789296536031006, 110.36317211257544),
//            " a",
//            "a",
//            "-7.782877816994324, 110.36706776150847",
//            "2345",
//            "Masa Kesultanan Mataraman baru/1755",
//            "Bangunan bersejarah",
//             R.drawable.
//        ),

        //      PlaceData(
//            "a", LatLng(-7.789296536031006, 110.36317211257544),
//            " a",
//            "a",
//            "-7.782877816994324, 110.36706776150847",
//            "2345",
//            "Masa Kesultanan Mataraman baru/1755",
//            "Bangunan bersejarah",
//             R.drawable.
//        ),




        PlaceData(
            "Benteng Vredeburg",
            LatLng(-7.80028, 110.36611),
            "Benteng Vredeburg merupakan bangunan bersejarah bergaya arsitektur Indische yang dibangun dari bata dan kayu. Berdiri di atas lahan seluas 45.574 m² dengan total bangunan 12.803,87 m², benteng ini masih kokoh dan terawat hingga kini. Pembangunan benteng ini dimulai pada tahun 1765–1790 atas perintah Gubernur W.H. van Ossenberch untuk kepentingan VOC di wilayah Kasultanan Ngayogyakarta. Awalnya bernama Benteng Rustenburg, bangunan ini berfungsi sebagai pusat administrasi dan militer VOC. Setelah VOC berakhir, penguasaan beralih kepada pemerintah Belanda (1799–1807) dan kemudian kepada Inggris (1807–1816) di bawah Jenderal Daendels dan Jenderal Thomas Stamford Raffles. Setelah berakhirnya Perang Diponegoro sekitar tahun 1830-an, namanya diubah menjadi Benteng Vredeburg, menandai reorganisasi tentara Hindia Belanda. Pada masa pendudukan Jepang (1942), benteng difungsikan sebagai markas dan kantor administrasi militer. Hingga saat ini, Benteng Vredeburg telah ditetapkan sebagai Bangunan Cagar Budaya sekaligus Museum Khusus Perjuangan Nasional, berdasarkan Surat Keputusan Menteri Pendidikan dan Kebudayaan Republik Indonesia Nomor 0475/O/1992 tanggal 23 November 1992.",
            "Jalan Jendral A. Yani No 6, Ngupasan, Gondomanan, Yogyakarta, Daerah Istimewa Yogyakarta",
            "-7.80028, 110.36611",
            "209495/A5.1/IIK/2013",
            "Masa Kesultanan Mataraman baru/1765",
            "Bangunan bersejarah",
            R.drawable.benteng_vredeburg
        )
    )







    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val spinner = view.findViewById<android.widget.Spinner>(R.id.spinnerMapType)
        val cbSitus = view.findViewById<CheckBox>(R.id.cbSitus)
        val cbKawasan = view.findViewById<CheckBox>(R.id.cbKawasan)
        val cbBangunan = view.findViewById<CheckBox>(R.id.cbBangunan)

        val listener = CompoundButton.OnCheckedChangeListener { _, _ ->
            applyFilter(
                situs = cbSitus.isChecked,
                kawasan = cbKawasan.isChecked,
                bangunan = cbBangunan.isChecked
            )
        }

        cbSitus.setOnCheckedChangeListener(listener)
        cbKawasan.setOnCheckedChangeListener(listener)
        cbBangunan.setOnCheckedChangeListener(listener)

        val mapTypes = listOf("Normal", "Satellite", "Terrain", "Hybrid")
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, mapTypes)
        spinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                googleMap?.mapType = when(position){
                    1 -> GoogleMap.MAP_TYPE_SATELLITE
                    2 -> GoogleMap.MAP_TYPE_TERRAIN
                    3 -> GoogleMap.MAP_TYPE_HYBRID
                    else -> GoogleMap.MAP_TYPE_NORMAL
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        enableMyLocation()
        fetchUserLocation()

        val jogjaCenter = LatLng(-7.8014300732224795, 110.36477562090462)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(jogjaCenter, 15f))

        places.forEach { place ->
            val hue = when(place.jenis) {
                "Kawasan bersejarah" -> BitmapDescriptorFactory.HUE_BLUE
                "Situs bersejarah" -> BitmapDescriptorFactory.HUE_GREEN
                else -> BitmapDescriptorFactory.HUE_RED
            }

            val marker = googleMap!!.addMarker(
                MarkerOptions()
                    .position(place.position)
                    .title(place.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue))
            )
            marker?.tag = place
            if (marker != null) markerList.add(marker)
        }

        googleMap?.setOnMarkerClickListener { marker ->
            val place = marker.tag as? PlaceData ?: return@setOnMarkerClickListener false
            showBottomSheet(place)
            true
        }
    }

    private fun applyFilter(situs: Boolean, kawasan: Boolean, bangunan: Boolean) {
        markerList.forEach { marker ->
            val place = marker.tag as PlaceData
            val jenis = place.jenis.lowercase()

            val visible = when {
                jenis.contains("situs") && situs -> true
                jenis.contains("kawasan") && kawasan -> true
                jenis.contains("bangunan") && bangunan -> true
                else -> false
            }

            marker.isVisible = visible
        }

    }


    private fun fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                userLocation = LatLng(it.latitude, it.longitude)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun startLocationUpdates(dest: LatLng) {
        destination = dest
        isNavigating = true
        hasArrived = false

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            5000L
        )
            .setMinUpdateIntervalMillis(3000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }



    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            if (!isNavigating) return
            if (!::destination.isInitialized) return

            val loc = result.lastLocation ?: return
            val userLatLng = LatLng(loc.latitude, loc.longitude)

            updateUserMarker(userLatLng)

            drawRoute(userLatLng, destination)

            checkDistance(userLatLng, destination)
        }
    }


    private fun updateUserMarker(position: LatLng) {
        if(userMarker == null) {
            userMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Posisi Anda")
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE
                        )
                    )
            )
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
        } else{
            userMarker?.position = position
            }
        }

    private fun drawRoute(origin: LatLng, destination: LatLng) {


        routePolyline?.remove()


        routePolyline = googleMap?.addPolyline(
            PolylineOptions()
                .add(origin, destination)
                .width(8f)
                .color(Color.MAGENTA)
        )
    }




    private fun checkDistance(origin: LatLng, destination: LatLng) {
        if (hasArrived) return

        val results = FloatArray(1)
        Location.distanceBetween(
            origin.latitude, origin.longitude,
            destination.latitude, destination.longitude,
            results
        )

        if (results[0] < 20) {
            hasArrived = true
            Toast.makeText(
                requireContext(),
                "Anda sudah sampai Tujuan!",
                Toast.LENGTH_LONG
            ).show()

            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun startManualNavigation(dest: LatLng){

        routePolyline?.remove()
        routePolyline = null

        startLocationUpdates(dest)
    }






    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        googleMap?.isMyLocationEnabled = true
    }

    private fun showBottomSheet(place: PlaceData) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottomsheet_place_info, null)

        val tvName = view.findViewById<TextView>(R.id.tvPlaceName)
        val tvCoord = view.findViewById<TextView>(R.id.tvCoordinate)
        val btnDetail = view.findViewById<Button>(R.id.btnDetail)
        val btnNavigate = view.findViewById<Button>(R.id.btnNavigate)

        tvName.text = place.name
        tvCoord.text = "Lat: ${place.position.latitude}, Lng: ${place.position.longitude}"


        btnDetail.setOnClickListener {
            val detail = DetailPlaceFragment()
            detail.arguments = Bundle().apply {
                putString("nama", place.name)
                putDouble("lat", place.position.latitude)
                putDouble("lng", place.position.longitude)
                putString("deskripsi", place.description)
                putInt("fotoRes", place.imageRes)
                putString("address", place.address)
                putString("coordinateStr", place.coordinateStr)
                putString("nomor", place.nomor)
                putString("periode", place.periode)
                putString("jenis", place.jenis)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, detail)
                .addToBackStack(null)
                .commit()

            dialog.dismiss()
        }

        //Melakukan navigasi ke arah tujuan
        btnNavigate.setOnClickListener {
            startManualNavigation(place.position)

            Toast.makeText(
                requireContext(),
                "Navigasi manual dimulai",
                Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }


}


