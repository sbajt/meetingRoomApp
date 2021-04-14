package com.scorealarm.meeting.rooms.fragments

import android.util.Log
import androidx.fragment.app.Fragment
import com.scorealarm.meeting.rooms.R
import com.scorealarm.meeting.rooms.activities.MainActivity
import com.scorealarm.meeting.rooms.models.Meeting
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_meeting_room_description.*
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

class MeetingRoomDetailsFragment :
    Fragment(R.layout.fragment_meeting_room_description) {

    private val compositeDisposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()
        initViews()
        observeMeetingRoomSubject()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }

    private fun initViews() {
        setTimeText(DateTime.now())
        runClock()
    }

    private fun setTimeText(dateTime: DateTime) {
        timeView?.text = dateTime.toString("HH:mm")
    }

    private fun runClock() {
        compositeDisposable.add(
            Observable.interval(1, TimeUnit.MINUTES, Schedulers.newThread())
                .map { DateTime.now() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::setTimeText) { Log.e(TAG, it.toString()) }
        )
    }

    private fun observeMeetingRoomSubject() {
        compositeDisposable.add(
            (activity as MainActivity).meetingRoomSubject
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setupDescriptionViews(it.meetingList) })
                { Log.e(TAG, it.toString()) }
        )
    }

    private fun setupDescriptionViews(meetings: List<Meeting>?) {
        val currentMeeting =
            meetings?.find { it.startDateTime.isBeforeNow && it.endDateTime.isAfterNow }
        if (currentMeeting == null) {
            currentMeetingNameView?.run {
                text = "No meeting in progress"
                textSize = 16f
            }
            currentMeetingTimeView?.text = ""
            currentMeetingOrganizerView?.text = ""
        } else {
            currentMeetingTimeView?.text =
                currentMeeting.startDateTime.toString("HH:mm") + " - " + currentMeeting.endDateTime.toString(
                    "HH:mm"
                )
            currentMeetingNameView?.text = "${currentMeeting.title}"
            currentMeetingOrganizerView?.text = "${currentMeeting.organizer}"
        }
    }

    companion object {

        private val TAG = MeetingRoomDetailsFragment::class.java.canonicalName

        fun getInstance() = MeetingRoomDetailsFragment()

    }


}