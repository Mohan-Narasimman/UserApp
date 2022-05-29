package com.interview.pagination.ui.list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.interview.pagination.R
import com.interview.pagination.adapters.UsersAdapter
import com.interview.pagination.adapters.UsersLoadingStateAdapter
import com.interview.pagination.databinding.FragmentListBinding
import com.interview.pagination.model.Results
import com.interview.pagination.model.Status
import com.interview.pagination.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()
    private lateinit var binding: FragmentListBinding
    private val adapter =
        UsersAdapter { results: Results -> navigateToDetailPage(results) }

    private lateinit var layoutManager: GridLayoutManager

    private lateinit var latitude: String
    private lateinit var longitude: String

    private fun navigateToDetailPage(results: Results) {
        hideKeyboard()
        val bundle = bundleOf(getString(R.string.userObject) to results)
        view?.findNavController()?.navigate(
            R.id.action_mainFragment_to_detailFragment,
            bundle
        )
    }

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        latitude = arguments?.get("latitude") as String
        longitude = arguments?.get("longitude") as String
        apiCall()
    }

    override fun onResume() {
        super.onResume()
        closeSearch()
    }

    private fun pagination() {
        setUpAdapter()
        startSearchJob("")
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
        searchDataObserver()
    }

    private fun startSearchJob(name: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchPlayers(name)
                .collectLatest {
                    adapter.submitData(it)
                }
            binding.recyclerView.scrollToPosition(0)
        }
    }

    private fun localSearchJob(name: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchPlayers(name)
                .collect {
                    adapter.submitData(it)
                }
            binding.recyclerView.scrollToPosition(0)
        }
    }

    private fun searchDataObserver() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (binding.searchBar.length() > 0) {
                    binding.closeSearch.visibility = View.VISIBLE
                } else {
                    binding.closeSearch.visibility = View.GONE
                    binding.recyclerView.scrollToPosition(0)
                    hideKeyboard()
                }
                localSearchJob(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.closeSearch.setOnClickListener {
            hideKeyboard()
            closeSearch()
            adapter.notifyDataSetChanged()
        }
    }

    private fun closeSearch() {
        binding.searchBar.setText("")
        binding.searchBar.isFocusable = false
        binding.searchBar.isFocusableInTouchMode = true
        binding.recyclerView.scrollToPosition(0)
    }

    private fun setUpAdapter() {
        layoutManager =
            GridLayoutManager(
                requireActivity(),
                2
            )

        binding.recyclerView.layoutManager =
            layoutManager

        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = UsersLoadingStateAdapter { retry() }
        )

        binding.recyclerView.invalidateItemDecorations()
        binding.recyclerView.scrollToPosition(0)

        adapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                if (adapter.snapshot().isEmpty()) {
                    binding.progress.isVisible = true
                }
                binding.noResultFound.isVisible = false

            } else {
                binding.progress.isVisible = false
                binding.swipeRefreshLayout.isRefreshing = false

                val error = when {
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error

                    else -> null
                }
                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        binding.noResultFound.isVisible = true
                        binding.noResultFound.text = it.error.localizedMessage
                    }
                }
            }
        }
    }

    private fun retry() {
        adapter.retry()
    }

    private fun apiCall() {
        if (isInternetAvailable(requireActivity())) {
            showLoadingScreen(requireActivity())
            viewModel.getWeatherDataLatLong(latitude, longitude)
            viewModel.res.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data.let { res ->
                            binding.toolbar.tvPlace.text =
                                res?.name
                            binding.toolbar.tvDescription.text =
                                res?.main?.temp?.let { it1 ->
                                    String.format(
                                        "%.2f",
                                        convertKelvinToCelsius(it1)
                                    ).toDouble()
                                        .toString() + 0x00B0.toChar() + " " + res.weather[0].description
                                }
                            binding.toolbar.ivCloud.loadUrl(
                                Constants.OPEN_WEATHER_IMAGE + res?.weather?.get(
                                    0
                                )?.icon + Constants.PNG
                            )
                            pagination()
                            dismissLoadingScreen()
                            binding.llSearch.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.progress.visibility = View.GONE
                        }
                    }
                    Status.LOADING -> {
                        binding.progress.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        dismissLoadingScreen()
                        binding.progress.visibility = View.GONE
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.no_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}