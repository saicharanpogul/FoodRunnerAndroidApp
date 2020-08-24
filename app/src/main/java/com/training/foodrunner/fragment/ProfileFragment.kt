package com.training.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.training.foodrunner.R
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var txtUsername: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences =
            this.activity?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)!!

        txtUsername = view.findViewById(R.id.txtUserNameProfile)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumberProfile)
        txtEmail = view.findViewById(R.id.txtEmailProfile)
        txtAddress = view.findViewById(R.id.txtAddress)

        val username = sharedPreferences.getString("name", "User Name")
        val email = sharedPreferences.getString("email", "example@example.com")
        val mobileNumber = sharedPreferences.getString("mobileNumber", "9876543210")
        val address = sharedPreferences.getString("address", "Earth")

        txtUsername.text = username
        txtMobileNumber.text = mobileNumber
        txtEmail.text = email
        txtAddress.text = address

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}