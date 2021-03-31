package com.scorealarm.meeting.rooms.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.scorealarm.meeting.rooms.R
import com.scorealarm.meeting.rooms.activities.MainActivity
import com.scorealarm.meeting.rooms.list.ListItemActionListener
import com.scorealarm.meeting.rooms.list.MeetingRoomListAdapter
import com.scorealarm.meeting.rooms.models.MeetingRoom
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_meeting_room_list.*

class MeetingRoomListFragment : Fragment(R.layout.fragment_meeting_room_list),
    ListItemActionListener<MeetingRoom> {

    private val listAdapter = MeetingRoomListAdapter(this)
    private val compositeDisposable = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = listAdapter
    }

    override fun onStart() {
        super.onStart()
        compositeDisposable.add(
            (activity as MainActivity).fetchMeetingRoomList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listAdapter::update) { Log.d(TAG, it.toString()) }
        )

    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onClick(data: MeetingRoom) {
        (activity as? MainActivity)?.run {
            saveMeetingRoomIntoPreference(data)
            navigateToMeetingRoom(data)
            meetingRoomSubject.onNext(data)
        }
    }


    companion object {

        private val TAG = MeetingRoomListFragment::class.java.canonicalName

        fun getInstance() = MeetingRoomListFragment()

    }


}