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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.ui.track.TrackAdapter
import com.example.playlistmaker.presentation.search.TracksState
import com.example.playlistmaker.ui.main.MainActivity
import com.example.playlistmaker.util.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {
    private var adapter: TrackAdapter? = null

    private val viewModel by viewModel<TracksSearchViewModel>()

    private lateinit var binding: FragmentSearchBinding

    private val trackHistoryInteractor: TracksHistoryInteractor by inject()
    private var textWatcher: TextWatcher? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(R.id.action_search_fragment_to_track_fragment,
                Bundle().apply {
                    putString("track", track.toString()) // Simplified
                }
            )
        }

        adapter = TrackAdapter(trackHistoryInteractor) { track ->
            (activity as MainActivity).animateBottomNavigationView()
            onTrackClickDebounce(track)
        }
        binding.trackListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.trackListRecycler.adapter = adapter

        binding.placeholderView.visibility = View.GONE
        binding.clearIcon.isVisible = false

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
        binding.trackListRecycler.adapter = null
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
            applyVisibility(
                placeholderVisible = false,
                recyclerVisible = true,
                progressBarVisible = true,
                historyTitleVisible = false,
                clearHistoryVisible = false
            )
            viewModel.searchRequest(viewModel.lastSearchText.toString())
        }

        viewModel.showHistory()

        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.clearIcon.setOnClickListener {
            binding.searchText.setText(EMPTY_SEARCH_TEXT)
            viewModel.showHistory()
            inputMethodManager?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
        }

        binding.clearHistoryButton.setOnClickListener {
            trackHistoryInteractor.saveTracksHistory(ArrayList())
            adapter?.items = emptyList()
            adapter?.notifyDataSetChanged()
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

        Log.e("SearchActivity", "trackList = $tracks")
        adapter?.items = tracks
        adapter?.notifyDataSetChanged()

        applyVisibility(
            placeholderVisible = false,
            recyclerVisible = true,
            progressBarVisible = false,
            historyTitleVisible = false,
            clearHistoryVisible = false
        )
    }

    fun showError(
        messageId: Int,
        iconId: Int,
        refreshButtonVisible: Boolean
    ) {
        Log.e("SearchActivity", "trackList loading error")
        adapter?.items = emptyList()
        adapter?.notifyDataSetChanged()
        binding.placeholderMessageText.text = getString(messageId)
        binding.placeholderIcon.setBackgroundResource(iconId)

        applyVisibility(
            placeholderVisible = true,
            recyclerVisible = false,
            progressBarVisible = false,
            historyTitleVisible = false,
            clearHistoryVisible = false,
            refreshButtonVisible = refreshButtonVisible
        )
    }

    fun showLoading() {
        applyVisibility(
            placeholderVisible = false,
            recyclerVisible = false,
            progressBarVisible = true,
            historyTitleVisible = false,
            clearHistoryVisible = false
        )

    }

    fun showHistory(tracks: List<Track>) {
        Log.e("TracksSearchController", tracks.toString())
        if (tracks.isNotEmpty()){
            applyVisibility(
                placeholderVisible = false,
                recyclerVisible = true,
                progressBarVisible = false,
                historyTitleVisible = true,
                clearHistoryVisible = true
            )
            Log.e("SearchActivity", "trackhistory = $tracks")
            adapter?.items = tracks
            adapter?.notifyDataSetChanged()
        } else {
            applyVisibility(
                placeholderVisible = false,
                recyclerVisible = true,
                progressBarVisible = false,
                historyTitleVisible = false,
                clearHistoryVisible = false
            )
        }
    }

    private fun applyVisibility(
        placeholderVisible: Boolean,
        recyclerVisible: Boolean,
        progressBarVisible: Boolean,
        historyTitleVisible: Boolean,
        clearHistoryVisible: Boolean,
        refreshButtonVisible: Boolean = false
    ) {
        binding.placeholderView.isVisible = placeholderVisible
        binding.trackListRecycler.isVisible = recyclerVisible
        binding.searchProgressBar.isVisible = progressBarVisible
        binding.searchHistoryTitle.isVisible = historyTitleVisible
        binding.clearHistoryButton.isVisible = clearHistoryVisible
        binding.refreshButton.isVisible = refreshButtonVisible
    }


    fun showUnknownError() {
        binding.placeholderView.visibility = View.GONE
    }

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Error -> showError(
                messageId = R.string.network_error_text,
                iconId = R.drawable.internet_error,
                refreshButtonVisible = true
            )

            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showError(
                messageId = R.string.empty_song_list_error_text,
                iconId = R.drawable.empty_results_error,
                refreshButtonVisible = false
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
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}