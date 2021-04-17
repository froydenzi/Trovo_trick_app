package info.froydenzi.trovotrick.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import info.froydenzi.trovotrick.R

class DeveloperFragment : Fragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewDev = inflater.inflate(R.layout.fragment_developer, container, false)
        val prefsName = "registration"
        val settings = context?.getSharedPreferences(prefsName, 0)
        val prefsText: TextView = viewDev.findViewById(R.id.prefs_text)

        prefsText.text = ""

        val allPrefs: Map<*, *> = settings!!.all
        val set = allPrefs.keys
        for (s in set) {
            prefsText.text = prefsText.text.toString() + s + " - " + allPrefs[s]!!.javaClass.simpleName + " -> " + allPrefs[s].toString() + "\n";
        }

        return viewDev
    }

}