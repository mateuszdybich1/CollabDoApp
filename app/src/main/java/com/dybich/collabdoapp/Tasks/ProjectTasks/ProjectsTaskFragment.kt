package com.dybich.collabdoapp.Tasks.ProjectTasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dybich.collabdoapp.Tasks.RefreshLayoutViewModel
import com.dybich.collabdoapp.Tasks.ProjectTasks.Done.ProjectDoneTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.PendingTasks.ProjectsPendingTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.StartedTasks.ProjectStartedTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.Undone.ProjectsUndoneTasksFragment
import com.dybich.collabdoapp.databinding.FragmentProjectTasksMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class ProjectsTaskFragment : Fragment() {



    private lateinit var binding : FragmentProjectTasksMainBinding

    private val sharedViewModel: RefreshLayoutViewModel by activityViewModels()

    private lateinit var viewPager:ViewPager2

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



    private fun setupViewPager() {

        viewPager.adapter = MyFragmentStateAdapter(requireActivity())

        val tabLayout: TabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Started"
                1 -> "Pending"
                2 -> "Done"
                else -> "Undone"
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        if(sharedViewModel.shouldRefresh.value == true){
            if(isAdded){
                sharedViewModel.shouldRefresh.value = false
                val activity = requireActivity()
                viewPager.adapter = MyFragmentStateAdapter(activity)
                setupViewPager()
            }
        }


    }
     class MyFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 4

         override fun getItemId(position: Int): Long {
             return position.toLong()
         }

         override fun containsItem(itemId: Long): Boolean {
             return itemId < itemCount
         }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ProjectStartedTasksFragment()
                1 -> ProjectsPendingTasksFragment()
                2 -> ProjectDoneTasksFragment()
                else -> ProjectsUndoneTasksFragment()
            }
        }
    }



}