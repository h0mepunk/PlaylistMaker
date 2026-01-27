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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
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

    private val currentTrackInteractor: CurrentTrackInteractor by inject()

    private val trackHistoryInteractor: TracksHistoryInteractor by inject()
    private var textWatcher: TextWatcher? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.trackListRecycler.adapter = null
        textWatcher?.let { binding.searchText.removeTextChangedListener(it) }
    }

    override fun onResume() {
        super.onResume()
        if (binding.searchText.text.isNotEmpty()) {
           viewModel.searchRequest(binding.searchText.text.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(R.id.action_search_fragment_to_track_fragment,
                Bundle().apply {
                    putString("track", track.toString())
                }
            )
        }

        adapter = TrackAdapter(currentTrackInteractor,trackHistoryInteractor) { track ->
            (activity as MainActivity).animateBottomNavigationView(View.GONE)
            onTrackClickDebounce(track)
        }
        binding.trackListRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.trackListRecycler.adapter = adapter

        binding.placeholderView.visibility = View.GONE
        binding.clearIcon.visibility = View.GONE

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

        binding.refreshButton.setOnClickListener {
            applyVisibility(
                placeholderVisible = View.GONE,
                recyclerVisible = View.GONE,
                progressBarVisible = View.VISIBLE,
                historyTitleVisible = View.GONE,
                clearHistoryVisible = View.GONE
            )
            viewModel.searchRequest(viewModel.lastSearchText.toString())
        }

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
                } else {
                    binding.clearIcon.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
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

        Log.i("SearchActivity", "trackList = $tracks")
        adapter?.items = tracks
        adapter?.notifyDataSetChanged()

        applyVisibility(
            placeholderVisible = View.GONE,
            recyclerVisible = View.VISIBLE,
            progressBarVisible = View.GONE,
            historyTitleVisible = View.GONE,
            clearHistoryVisible = View.GONE
        )
    }

    fun showError(
        messageId: Int,
        iconId: Int,
        refreshButtonVisible: Int
    ) {
        Log.i("SearchActivity", "trackList loading error")
        adapter?.items = emptyList()
        adapter?.notifyDataSetChanged()
        binding.placeholderMessageText.text = getString(messageId)
        binding.placeholderIcon.setBackgroundResource(iconId)

        applyVisibility(
            placeholderVisible = View.VISIBLE,
            recyclerVisible = View.GONE,
            progressBarVisible = View.GONE,
            historyTitleVisible = View.GONE,
            clearHistoryVisible = View.GONE,
            refreshButtonVisible = refreshButtonVisible
        )
    }

    fun showLoading() {
        applyVisibility(
            placeholderVisible = View.GONE,
            recyclerVisible = View.GONE,
            progressBarVisible = View.VISIBLE,
            historyTitleVisible = View.GONE,
            clearHistoryVisible = View.GONE
        )

    }

    fun showHistory(tracks: List<Track>) {
        Log.i("SearchActivity", tracks.toString())
        if (tracks.isNotEmpty()){
            applyVisibility(
                placeholderVisible = View.GONE,
                recyclerVisible = View.VISIBLE,
                progressBarVisible = View.GONE,
                historyTitleVisible = View.VISIBLE,
                clearHistoryVisible = View.VISIBLE
            )
            Log.i("SearchActivity", "track history = $tracks")
            adapter?.items = tracks
            adapter?.notifyDataSetChanged()
        } else {
            Log.i("SearchActivity", "track history empty")
            applyVisibility(
                placeholderVisible = View.GONE,
                recyclerVisible = View.GONE,
                progressBarVisible = View.GONE,
                historyTitleVisible = View.GONE,
                clearHistoryVisible = View.GONE
            )
        }
    }

    private fun applyVisibility(
        placeholderVisible: Int,
        recyclerVisible: Int,
        progressBarVisible: Int,
        historyTitleVisible: Int,
        clearHistoryVisible: Int,
        refreshButtonVisible: Int = View.GONE
    ) {
        binding.placeholderView.visibility = placeholderVisible
        binding.trackListRecycler.visibility = recyclerVisible
        binding.searchProgressBar.visibility = progressBarVisible
        binding.searchHistoryTitle.visibility = historyTitleVisible
        binding.clearHistoryButton.visibility = clearHistoryVisible
        binding.refreshButton.visibility = refreshButtonVisible
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
                refreshButtonVisible = View.VISIBLE
            )

            is TracksState.Content -> showContent(state.tracks)
            is TracksState.Empty -> showError(
                messageId = R.string.empty_song_list_error_text,
                iconId = R.drawable.empty_results_error,
                refreshButtonVisible = View.GONE
            )

            is TracksState.UnknownErrorState -> showUnknownError()
            is TracksState.History -> showHistory(state.tracks)
            is TracksState.Initial -> showHistory(emptyList())
        }
    }

    fun showToast(additionalMessage: String?) {
        Log.i("SearchActivity", "showToast: $additionalMessage")
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