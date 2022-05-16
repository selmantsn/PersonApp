package com.example.personapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personapp.R
import com.example.personapp.data.Person
import com.example.personapp.databinding.ActivityMainBinding
import com.example.personapp.observeOnce
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var adapter: PersonAdapter = PersonAdapter()

    private lateinit var binding: ActivityMainBinding

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var viewModel: MainViewModel

    private lateinit var dialog: AlertDialog
    private var allPersonList: MutableList<Person> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initViews()
        setRecyclerViewScrollListener()
        setObservers()

        viewModel.getPersons()
    }

    private fun setObservers() {
        viewModel.fetchResponse.observe(this) { response ->
            viewModel.mNext = response.next

            val allIds = allPersonList.map { it.id }.toSet()
            val idUniqueList = response.people.filter { !allIds.contains(it.id) }

            allPersonList.addAll(idUniqueList)
            adapter.addAll(idUniqueList as MutableList<Person>)
            setCounts(idUniqueList)
            binding.tvEmptyMessage.isVisible = allPersonList.isEmpty()
        }

        viewModel.fetchError.observe(this) { error ->
            binding.tvEmptyMessage.isVisible = allPersonList.isEmpty()
            Snackbar.make(
                binding.parent,
                error.errorDescription + getString(R.string.no_new_added),
                Snackbar.LENGTH_SHORT
            ).show()
            setCounts(null)
        }

        viewModel.loading.observe(this) { loading ->
            if (loading) dialog.show() else dialog.hide()
        }

        viewModel.isLastPage.observeOnce(this) { isLastPage ->
            if (isLastPage)
                Snackbar.make(
                    binding.parent,
                    getString(R.string.end_of_the_list),
                    Snackbar.LENGTH_LONG
                ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCounts(idUniqueList: List<Person>?) {
        val lastAdded = idUniqueList?.size ?: 0
        binding.tvTotalCount.text = getString(R.string.total_person) + ": " + allPersonList.size
        binding.tvLastAdded.text = getString(R.string.last_added_person) + ": " + lastAdded
    }

    private fun initViews() {
        binding.rvPersonList.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPersonList.layoutManager = linearLayoutManager
        binding.rvPersonList.adapter = adapter

        setProgressDialog()

        binding.swipeRefresh.setOnRefreshListener {
            binding.tvEmptyMessage.isVisible = false

            allPersonList.clear()
            adapter.clearList()

            // If a different list is wanted, need to generate again
            viewModel.getPersons(null)
            binding.swipeRefresh.isRefreshing = false
        }

    }

    private fun setRecyclerViewScrollListener() {
        binding.rvPersonList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.loadMore(linearLayoutManager, dy, allPersonList.size)
            }
        })
    }


    private fun setProgressDialog() {
        val builder = AlertDialog.Builder(this)
        val progressBar = ProgressBar(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        progressBar.layoutParams = lp
        builder.setView(progressBar)
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}