package com.oleohialli.farminventory.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.oleohialli.farminventory.Constants
import com.oleohialli.farminventory.R
import com.oleohialli.farminventory.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            loginButton.setOnClickListener {
//                if (emailField.text.toString().trim().isBlank()) {
//                    emailInputLayout.error = "This field is cannot be empty"
//                } else
//                if (passwordField.text.toString().trim().isBlank()){
//                    passwordInputLayout.error = "This field cannot be empty"
//                } else
//                if (emailField.text.toString().trim() != Constants.LOGIN_EMAIL
//                    && passwordField.text.toString().trim() != Constants.LOGIN_PASSWORD) {
//                    Snackbar.make(requireView(), "Incorrect username or password", Snackbar.LENGTH_LONG).show()
//                } else {
                    val action = LoginFragmentDirections.actionLoginFragment2ToDashboardFragment2()
                    findNavController().navigate(action)
                //}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}