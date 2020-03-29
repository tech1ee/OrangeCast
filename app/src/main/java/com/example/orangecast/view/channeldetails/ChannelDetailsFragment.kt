package com.example.orangecast.view.channeldetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.orangecast.App
import com.example.orangecast.R
import com.example.orangecast.view.BaseFragment

class ChannelDetailsFragment: BaseFragment() {

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_channel_details, container, false)
    }

    override fun initView() {

    }

}