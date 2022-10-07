package ru.ertel.scannerqr.app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import ru.ertel.scannerqr.app.R

class DemoFragment : Fragment() {

    private lateinit var textDemo: TextView
    private lateinit var btn: Button
    private lateinit var message: String
    private lateinit var activateFragment: ActivateFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        message = arguments?.getString("demo").toString()
        return inflater.inflate(R.layout.fragment_demo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textDemo = view.findViewById(R.id.textDemo)
        btn = view.findViewById(R.id.buttonRepeatActive)
        btn.setOnClickListener {
            activateFragment = ActivateFragment()
            openFragment(activateFragment)
        }
    }

    private fun openFragment(fragment: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.placeDateFragments, fragment)
            ?.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = DemoFragment()
    }
}