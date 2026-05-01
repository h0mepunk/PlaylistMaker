package com.example.playlistmaker.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.Const.EMPTY_STRING
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.CurrentTrackInteractor
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.search.TracksSearchViewModel
import com.example.playlistmaker.presentation.search.TracksState
import com.example.playlistmaker.ui.common.PlaylistMakerTheme
import com.example.playlistmaker.ui.main.MainActivity
import com.example.playlistmaker.util.debounce
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private val viewModel by viewModel<TracksSearchViewModel>()

    private val currentTrackInteractor: CurrentTrackInteractor by inject()

    private val trackHistoryInteractor: TracksHistoryInteractor by inject()
    private val themeInteractor: ThemeInteractor by inject()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PlaylistMakerTheme(
                darkTheme = themeInteractor.getTheme()
            ) {
                SearchScaffold(
                    viewModel = viewModel,
                    onSearchTextChange = { onSearchTextChange(it) },
                    onItemClick = { onTrackClick(it) },
                    onClearHistoryClick = { onClearHistoryClick() },
                    onErrorButtonClick = { onRefreshClick() },
                )
            }
        }
    }

    fun onSearchTextChange(text: String) {
        viewModel.setText(text)
        if (text.isEmpty()) {
            showHistory(trackHistoryInteractor.getTracksHistory())
        } else {
            viewModel.lastSearchText = text
            viewModel.searchRequest(text)
        }
    }

    fun onTrackClick(track: Track) {
        (activity as MainActivity).animateBottomNavigationView(View.GONE)
        onTrackClickDebounce(track)
    }

    private fun addTrackToHistoryFromSearch(track: Track) {
        val trackHistory = trackHistoryInteractor.getTracksHistory()
        if (trackHistory.size == 10) {
            trackHistory.removeAt(9)
            trackHistory.add(0, track)
        }
        if (trackHistory.contains(track)) {
            trackHistory.remove(track)
            trackHistory.add(0, track)
        } else {
            trackHistory.add(0, track)
        }
        trackHistoryInteractor.saveTracksHistory(trackHistory)
    }

    fun onClearClick() {
        with(viewModel) {
            setText(EMPTY_SEARCH_TEXT)
            showHistory()
        }
    }

    fun onRefreshClick() {
        with(viewModel) {
            applyVisibility(
                placeholderVisible = false,
                recyclerVisible = false,
                progressBarVisible = true,
                historyTitleVisible = false,
                clearHistoryVisible =false
            )
            searchRequest(viewModel.lastSearchText.toString())
        }
    }

    fun onClearHistoryClick() {
        trackHistoryInteractor.saveTracksHistory(ArrayList())
        with(viewModel) {
            setTrackList(emptyList())
            setClearHistoryButtonVisibility(false)
            setHistoryTitleVisibility(false)
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
////        adapter = null
////        binding.trackListRecycler.adapter = null
////        textWatcher?.let { binding.searchText.removeTextChangedListener(it) }
//    }

    override fun onResume() {
        super.onResume()
        if (viewModel.text.value.isNotEmpty())
        {
           viewModel.searchRequest(viewModel.text.value)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            Log.i(LOG_TAG, "Track $track clicked")
            addTrackToHistoryFromSearch(track)
            currentTrackInteractor.saveCurrentTrack(track)
            findNavController().navigate(R.id.action_search_fragment_to_track_fragment,
                Bundle().apply {
                    putString("track", track.toString())
                }
            )
        }

        with(viewModel) {
            setErrorVisibility(false)
            setClearIconVisibility(false)

            observeState().observe(viewLifecycleOwner) { render(it) }
            observeShowToast().observe(viewLifecycleOwner) { showToast(it) }

            onRestoreInstanceState(savedInstanceState)?:EMPTY_STRING
        }

//        binding.refreshButton.setOnClickListener {
//            applyVisibility(
//                placeholderVisible = View.GONE,
//                recyclerVisible = View.GONE,
//                progressBarVisible = View.VISIBLE,
//                historyTitleVisible = View.GONE,
//                clearHistoryVisible = View.GONE
//            )
//            viewModel.searchRequest(viewModel.lastSearchText.toString())
//        }

//        binding.clearIcon.setOnClickListener {
//            binding.searchText.setText(EMPTY_SEARCH_TEXT)
//            viewModel.showHistory()
//            inputMethodManager?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
//        }

//        binding.clearHistoryButton.setOnClickListener {
//            trackHistoryInteractor.saveTracksHistory(ArrayList())
//            adapter?.items = emptyList()
//            adapter?.notifyDataSetChanged()
//            binding.searchHistoryTitle.visibility = View.GONE
//            binding.clearHistoryButton.visibility = View.GONE
//        }

//        binding.searchText.setOnFocusChangeListener() { _, hasFocus -> }
//        binding.searchText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (binding.searchText.text.isNotEmpty()) {
//                 //   inputMethodManager?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
//                    viewModel.lastSearchText = binding.searchText.text.toString()
//                    viewModel.searchRequest(binding.searchText.text.toString())
//                }
//            }
//            false
//        }

//        textWatcher =    object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                viewModel.searchDebounce(
//                    changedText = s?.toString() ?: ""
//                )
//                if (!s.isNullOrEmpty()) {
//                    binding.clearIcon.visibility = View.VISIBLE
//                } else {
//                    binding.clearIcon.visibility = View.GONE
//                }
//            }

//            override fun afterTextChanged(s: Editable?) {
//                if((binding.searchText.hasFocus()) && s.isNullOrEmpty()) {
//                    viewModel.showHistory()
//                }
//            }

 //       }

   //     binding.searchText.setText(viewModel.onRestoreInstanceState(savedInstanceState)?:EMPTY_STRING)
   //     textWatcher?.let { binding.searchText.addTextChangedListener(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
       viewModel.onSaveInstanceState(outState)
    }

    fun showContent(tracks: List<Track>) {

        Log.i(LOG_TAG, "trackList = $tracks")
        with(viewModel) {
            setTrackList(tracks)
            applyVisibility(
                placeholderVisible = false,
                recyclerVisible = true,
                progressBarVisible = false,
                historyTitleVisible = false,
                clearHistoryVisible = false
            )
        }
    }

    fun showError(
        messageId: Int,
        iconId: Int,
        refreshButtonVisible: Boolean
    ) {
        Log.i(LOG_TAG, "trackList loading error")

        with(viewModel) {
            setTrackList(emptyList())
            setErrorMessageText(getString(messageId))
            setErrorIconResource(iconId)
            applyVisibility(
                placeholderVisible = true,
                recyclerVisible = false,
                progressBarVisible = false,
                historyTitleVisible = false,
                clearHistoryVisible = false,
                refreshButtonVisible = refreshButtonVisible
            )
        }
    }

    fun showLoading() {
        viewModel.applyVisibility(
            placeholderVisible = false,
            recyclerVisible = false,
            progressBarVisible = true,
            historyTitleVisible = false,
            clearHistoryVisible = false
        )

    }

    fun showHistory(tracks: List<Track>) {
        Log.i(LOG_TAG, tracks.toString())
        if (tracks.isNotEmpty()){
            Log.i(LOG_TAG, "track history = $tracks")
            with(viewModel) {
                applyVisibility(
                    placeholderVisible = false,
                    recyclerVisible = true,
                    progressBarVisible = false,
                    historyTitleVisible = true,
                    clearHistoryVisible = true
                )
                setTrackList(tracks)
            }
        } else {
            Log.i(LOG_TAG, "track history empty")
            viewModel.applyVisibility(
                placeholderVisible = false,
                recyclerVisible = false,
                progressBarVisible = false,
                historyTitleVisible = false,
                clearHistoryVisible = false
            )
        }
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

            is TracksState.UnknownErrorState -> viewModel.setErrorVisibility(false)
            is TracksState.History -> showHistory(state.tracks)
            is TracksState.Initial -> showHistory(emptyList())
        }
    }

    fun showToast(additionalMessage: String?) {
        Log.i(LOG_TAG, "showToast: $additionalMessage")
        requireActivity().runOnUiThread {
            Toast.makeText(requireActivity(), additionalMessage?: "Empty message", Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        const val EMPTY_SEARCH_TEXT = ""
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val LOG_TAG = "SearchFragment"
    }
}