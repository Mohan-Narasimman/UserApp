package com.interview.pagination.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.interview.pagination.R
import com.interview.pagination.databinding.FragmentDetailBinding
import com.interview.pagination.model.Results
import com.interview.pagination.model.Status
import com.interview.pagination.utils.*
import com.interview.pagination.utils.Constants.Companion.OPEN_WEATHER_IMAGE
import com.interview.pagination.utils.Constants.Companion.PNG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private lateinit var detailFragmentBinding: FragmentDetailBinding
    private lateinit var resultResponseElement: Results
    private lateinit var navController: NavController
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailFragmentBinding = FragmentDetailBinding.inflate(inflater, container, false)
        return detailFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        resultResponseElement = arguments?.get(getString(R.string.userObject)) as Results

        detailFragmentBinding.ivFlag.loadUrl(resultResponseElement.picture.medium)
        detailFragmentBinding.toolbar.title.text = getString(R.string.userDetail)
        detailFragmentBinding.toolbar.ivBack.visibility = View.VISIBLE
        detailFragmentBinding.toolbar.ivBack.setOnClickListener {
            navController.popBackStack()
        }
        detailFragmentBinding.tvUserName.text = getString(
            R.string.detail_item,
            resultResponseElement.name.title,
            resultResponseElement.name.first,
            resultResponseElement.name.last
        ).trim()
        detailFragmentBinding.tvEmailValue.text = (resultResponseElement.email).trim()
        detailFragmentBinding.tvStreetValue.text = getString(
            R.string.street_item,
            resultResponseElement.location.street.number,
            resultResponseElement.location.street.name
        ).trim()
        detailFragmentBinding.tvStatevalue.text = (resultResponseElement.location.state).trim()
        detailFragmentBinding.tvCityValue.text = (resultResponseElement.location.city).trim()
        detailFragmentBinding.tvCountryNameValue.text =
            (resultResponseElement.location.country).trim()
        apiCall()
    }

    private fun apiCall() {
        if (isInternetAvailable(requireActivity())) {
            showLoadingScreen(requireActivity())
            detailViewModel.getWeatherData(resultResponseElement.location.country)
            detailViewModel.res.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data.let { res ->
                            detailFragmentBinding.toolbar.tvDescription.text =
                                res?.main?.temp?.let { it1 ->
                                    String.format(
                                        "%.2f",
                                        convertKelvinToCelsius(it1)
                                    ).toDouble()
                                        .toString() + 0x00B0.toChar() + " " + res.weather[0].description
                                }
                            detailFragmentBinding.toolbar.tvPlace.text =
                                resultResponseElement.location.country
                            detailFragmentBinding.toolbar.ivCloud.loadUrl(
                                OPEN_WEATHER_IMAGE + res?.weather?.get(
                                    0
                                )?.icon + PNG
                            )
                            dismissLoadingScreen()
                            detailFragmentBinding.cardView.visibility = View.VISIBLE
                            detailFragmentBinding.cardViewFooter.visibility = View.VISIBLE
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        dismissLoadingScreen()
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