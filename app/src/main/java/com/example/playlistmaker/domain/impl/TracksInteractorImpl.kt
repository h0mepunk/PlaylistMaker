package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor {

    // other option with executor if threads are not prefered
   // private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {

//        executor.execute {
//            consumer.consume(repository.searchTracks(text))
//        }

        val t = Thread {
            consumer.consume(repository.searchTracks(text))
        }
        t.start()
    }
}