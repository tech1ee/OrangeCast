package com.example.orangecast.view.splash

import android.graphics.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.orangecast.App
import com.example.orangecast.BaseFragment
import com.example.orangecast.R
import com.example.orangecast.view.discover.DiscoverViewModel
import io.reactivex.Completable
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
        disposable.add(
            Completable.timer(2, TimeUnit.SECONDS).subscribe { viewModel.discover() }
        )
    }

    private fun animateLogo() {
        val matrix = Matrix()
        logo_background?.scaleType = ImageView.ScaleType.MATRIX
        matrix.postRotate(360f)
        logo_background?.imageMatrix = matrix
    }

    override fun showData(data: Any?) {

    }
}