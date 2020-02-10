package com.example.orangecast.view.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.navigation.fragment.findNavController
import com.example.orangecast.App
import com.example.orangecast.view.BaseFragment
import com.example.orangecast.R
import com.example.orangecast.view.discover.DiscoverViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_logo.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: DiscoverViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        disableProgress()
        return LayoutInflater.from(context).inflate(R.layout.fragment_splash, container, false)
    }

    override fun inject() {
        App.appComponent(context)?.inject(this)
    }

    override fun initView() {
        animateLogo()
        viewModel.getDiscoverLiveData().subscribeToEvent()
        disposable.add(
            Completable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.discover() }
        )
    }

    private fun animateLogo() {
        val rotate = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.repeatCount = Animation.INFINITE
        rotate.duration = 2000
        rotate.interpolator = LinearInterpolator()
        logo_background?.startAnimation(rotate)
    }

    override fun showData(data: Any?) {
        findNavController().navigate(R.id.action_open_home__screen)
    }
}