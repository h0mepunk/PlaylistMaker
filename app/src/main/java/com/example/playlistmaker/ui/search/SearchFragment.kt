package com.example.playlistmaker.ui.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.presentation.search.TracksState
import com.example.playlistmaker.ui.main.MainFragment
import com.example.playlistmaker.ui.track.TrackFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    private lateinit var adapter: TrackAdapter

    private val viewModel by viewModel<TracksSearchViewModel>()

    private lateinit var binding: FragmentSearchBinding

    private val trackHistoryInteractor: TracksHistoryInteractor by inject()
    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        adapter = TrackAdapter(trackHistoryInteractor) { track ->
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.root_container,
                    TrackFragment()
                )
                .addToBackStack(null)
                .commit()
        }
        binding.trackListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.trackListRecycler.adapter = adapter

        binding.placeholderView.visibility = View.GONE
        binding.clearIcon.isVisible = false

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.searchText.removeTextChangedListener(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        binding.refreshButton.setOnClickListener {
            binding.searchProgressBar.visibility = View.VISIBLE
            viewModel.searchRequest(viewModel.lastSearchText.toString())
        }


        viewModel.showHistory()

        binding.searchToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, MainFragment())
                .setReorderingAllowed(true)
                .commit()
        }

        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.clearIcon.setOnClickListener {
            binding.searchText.setText(EMPTY_SEARCH_TEXT)
            viewModel.showHistory()
            inputMethodManager?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
        }

        binding.clearHistoryButton.setOnClickListener {
            trackHistoryInteractor.saveTracksHistory(ArrayList())
            adapter.items = emptyList()
            adapter.notifyDataSetChanged()
            binding.searchHistoryTitle.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
        }

        binding.searchText.setOnFocusChangeListener() { _, hasFocus -> }
        binding.searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchText.text.isNotEmpty()) {
                    inputMethodManager?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
                    viewModel.lastSearchText = binding.searchText.text.toString()
                    viewModel.searchRequest(binding.searchText.text.toString())
                }
            }
            false
        }

        textWatcher =    object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                if (!s.isNullOrEmpty()) {
                    binding.clearIcon.visibility = View.VISIBLE
                }
                if (s.isNullOrEmpty()) {
                    binding.clearIcon.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    Log.e("TracksSearchController", "all callbacks removed")
                    viewModel.showHistory()
                }
                if((binding.searchText.hasFocus()) && s.isNullOrEmpty()) {
                    viewModel.showHistory()
                }
            }

        }

        binding.searchText.setText(viewModel.onRestoreInstanceState(savedInstanceState)?:"")
        textWatcher?.let { binding.searchText.addTextChangedListener(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
       viewModel.onSaveInstanceState(outState)
    }

    fun showContent(tracks: List<Track>) {
        binding.searchProgressBar.visibility = View.GONE
        binding.placeholderView .visibility = View.GONE

        Log.e("SearchActivity", "trackList = $tracks")
        adapter.items = tracks
        adapter.notifyDataSetChanged()

        binding.trackListRecycler.visibility = View.VISIBLE
        binding.searchHistoryTitle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
    }

    fun showError(messageId: Int, iconId: Int) {
        Log.e("SearchActivity", "trackList loading error")
        adapter.items = emptyList()
        adapter.notifyDataSetChanged()
        binding.searchProgressBar.visibility = View.GONE

        binding.searchHistoryTitle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE

        binding.placeholderMessageText.text = getString(messageId)
        binding.placeholderIcon.setBackgroundResource(iconId)
        binding.placeholderView.visibility = View.VISIBLE

        binding.refreshButton.visibility = View.VISIBLE
    }

    fun showLoading() {
        binding.searchProgressBar.visibility = View.VISIBLE
        binding.placeholderView.visibility = View.GONE
        binding.trackListRecycler.visibility = View.GONE
        binding.searchHistoryTitle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
    }

    fun showHistory(tracks: List<Track>) {
        Log.e("TracksSearchController", tracks.toString())
        if (tracks.isNotEmpty()){
            binding.searchHistoryTitle.visibility = View.VISIBLE
            binding.clearHistoryButton.visibility = View.VISIBLE
            binding.placeholderView.visibility = View.GONE
            Log.e("SearchActivity", "trackhistory = $tracks")
            adapter.items = tracks
            adapter.notifyDataSetChanged()
            binding.trackListRecycler.visibility = View.VISIBLE
        } else {
            binding.searchHistoryTitle.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
        }
    }

    fun showUnknownError() {
        binding.placeholderView.visibility = View.GONE
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Error -> showError(
                messageId = R.string.network_error_text,
                iconId = R.drawable.internet_error
            )

            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showError(
                messageId = R.string.empty_song_list_error_text,
                iconId = R.drawable.empty_results_error
            )

            is TracksState.UnknownErrorState -> showUnknownError()
            is TracksState.History -> showHistory(state.tracks)
        }
    }

    fun showToast(additionalMessage: String?) {
        Log.e("SearchActivity", "showToast: $additionalMessage")
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), additionalMessage?: "Empty message", Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        const val EMPTY_SEARCH_TEXT = ""
    }

}