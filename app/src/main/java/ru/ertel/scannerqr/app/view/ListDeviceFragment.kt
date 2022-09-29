package ru.ertel.scannerqr.app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import ru.ertel.scannerqr.app.R

class ListDeviceFragment : Fragment() {

    private lateinit var listViewDevices: ListView
    private lateinit var device: String
    private lateinit var arrayDevice: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        device = arguments?.getString("device").toString()
        device = device.replace("[", "").replace("]", "")
        arrayDevice = device.split(", ").toTypedArray()
        return inflater.inflate(R.layout.fragment_list_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listViewDevices = view.findViewById(R.id.listDevices)
        val adapter = activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_list_item_activated_1,
                arrayDevice.toList()
            )
        }

        listViewDevices.adapter = adapter
    }

    companion object {

        @JvmStatic
        fun newInstance() = ListDeviceFragment()
    }
}