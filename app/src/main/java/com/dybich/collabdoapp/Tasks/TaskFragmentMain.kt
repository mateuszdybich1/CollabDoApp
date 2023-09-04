package com.dybich.collabdoapp.Tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.UserViewModel
import com.dybich.collabdoapp.databinding.FragmentAddProjectBinding
import com.dybich.collabdoapp.databinding.FragmentTaskMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TaskFragmentMain : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader:Boolean?=false

    private val userViewModel: UserViewModel by activityViewModels()


    private lateinit var binding: FragmentTaskMainBinding
    private lateinit var backButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskMainBinding.inflate(inflater, container, false)

        backButton = binding.fragmentTaskMainBackButton

        backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }

}