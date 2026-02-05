package com.example.myfinalproject.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myfinalproject.MainActivity
import com.example.myfinalproject.R

class DetailPlaceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarDetail)
        toolbar.title = arguments?.getString("nama") ?: "Detail Tempat"

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // View
        val tvName = view.findViewById<TextView>(R.id.tvPlaceName)
        val tvAddress = view.findViewById<TextView>(R.id.tvAdress)
        val tvCoordinate = view.findViewById<TextView>(R.id.tvCoordinate)
        val tvNomor = view.findViewById<TextView>(R.id.tvNomor)
        val tvPeriode = view.findViewById<TextView>(R.id.tvPeriode)
        val tvJenis = view.findViewById<TextView>(R.id.tvJenis)
        val tvDesc = view.findViewById<TextView>(R.id.tvPlaceDesc)
        val imgPlace = view.findViewById<ImageView>(R.id.imgPlace)

        // Ambil data dari bundle
        arguments?.let { bundle ->
            tvName.text = bundle.getString("nama")
            tvAddress.text = "Alamat: ${bundle.getString("address")}"
            tvCoordinate.text = "Koordinat: ${bundle.getString("coordinateStr")}"
            tvNomor.text = "Nomor: ${bundle.getString("nomor")}"
            tvPeriode.text = "Periode/Tahun: ${bundle.getString("periode")}"
            tvJenis.text = "Jenis: ${bundle.getString("jenis")}"
            tvDesc.text = bundle.getString("deskripsi")

            // Load gambar AMAN (hindari ANR)
            Glide.with(this)
                .load(bundle.getInt("fotoRes"))
                .into(imgPlace)
        }
    }

    override fun onResume() {
        super.onResume()
        // Menyembunyikan toolbar MainActivity
        (activity as? MainActivity)?.hideToolbar()
    }

    override fun onPause() {
        super.onPause()
        //Menampilkan kembali Toolbar Mainactivity
        (activity as? MainActivity)?.showToolbar()
    }
}
