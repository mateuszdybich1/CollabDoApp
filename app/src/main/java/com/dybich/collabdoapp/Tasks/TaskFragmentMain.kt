package com.dybich.collabdoapp.Tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dybich.collabdoapp.ProjectStatus
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.UserViewModel
import com.dybich.collabdoapp.databinding.FragmentTaskMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TaskFragmentMain : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader:Boolean?=false
    private var projectId : String?=null

    private val userViewModel: UserViewModel by activityViewModels()


    private lateinit var binding: FragmentTaskMainBinding
    private lateinit var backButton : FloatingActionButton
    private lateinit var addTaskButton : ExtendedFloatingActionButton
    private lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value
        isLeader = userViewModel.isLeader.value

        projectId = arguments?.getString("projectId")


        binding = FragmentTaskMainBinding.inflate(inflater, container, false)

        backButton = binding.fragmentLeaderTaskMainBackButton
        addTaskButton = binding.addTaskBTN
        bottomNavigation = binding.bottomNavigationView

        backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        if(!isLeader!!){
            addTaskButton.visibility = View.GONE
        }
        else{
            addTaskButton.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.addTaskFragment)
                binding.root.visibility = View.GONE
            }
        }


        val navHostFragment = childFragmentManager.findFragmentById(R.id.taskFragmentCV) as NavHostFragment
        val navController = navHostFragment.navController
        if (navHostFragment != null) {
            bottomNavigation.setupWithNavController(navController)
        }

        return binding.root
    }



}