package ru.ertel.scannerqr.app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.ertel.scannerqr.app.R

class PirateFragment : Fragment() {

    private lateinit var textPirate: TextView
    private lateinit var licenseInfo: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        licenseInfo = arguments?.getString("pirate").toString()
        return inflater.inflate(R.layout.fragment_pirate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textPirate = view.findViewById(R.id.textPirate)
        textPirate.text = licenseInfo
    }

    companion object {
        @JvmStatic
        fun newInstance() = PirateFragment()
    }
}