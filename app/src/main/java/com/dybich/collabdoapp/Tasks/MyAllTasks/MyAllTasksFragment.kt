package com.dybich.collabdoapp.Tasks.MyAllTasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Tasks.MyAllTasks.DoneTasks.MyDoneTasksFragment
import com.dybich.collabdoapp.Tasks.MyAllTasks.UndoneTasks.MyUndoneTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.ProjectsTaskFragment
import com.dybich.collabdoapp.Tasks.RefreshLayoutViewModel
import com.dybich.collabdoapp.databinding.FragmentProjectTasksMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MyAllTasksFragment : Fragment() {

    private lateinit var binding : FragmentProjectTasksMainBinding
    private val sharedViewModel: RefreshLayoutViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectTasksMainBinding.inflate(inflater, container, false)


        viewPager= binding.projectTasksViewPager


        setupViewPager()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if(sharedViewModel.shouldRefresh.value == true){
            if(isAdded){
                sharedViewModel.shouldRefresh.value = false
                val activity = requireActivity()
                viewPager.adapter = ProjectsTaskFragment.MyFragmentStateAdapter(activity)
                setupViewPager()
            }
        }


    }

    private fun setupViewPager() {
        viewPager.adapter = MyFragmentStateAdapter(requireActivity())

        val tabLayout: TabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {

                0 -> "Done"
                else -> "Undone"
            }
        }.attach()
    }


    class MyFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {

                0 -> MyDoneTasksFragment()
                else -> MyUndoneTasksFragment()
            }
        }
    }
}