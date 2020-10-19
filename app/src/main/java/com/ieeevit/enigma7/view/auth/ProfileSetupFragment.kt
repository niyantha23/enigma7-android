package com.ieeevit.enigma7.view.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ieeevit.enigma7.R
import com.ieeevit.enigma7.databinding.FragmentProfileSetupBinding
import com.ieeevit.enigma7.utils.PrefManager
import com.ieeevit.enigma7.view.timer.CountdownActivity
import com.ieeevit.enigma7.viewModel.ProfileSetupViewModel


class ProfileSetupFragment : Fragment() {
    private lateinit var sharedPreference: PrefManager
    var usernameEntered = MutableLiveData<Int>()
    var string:String=""
    private val viewModel: ProfileSetupViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileSetupViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = PrefManager(this.requireActivity())
    }

    init {
        usernameEntered.value = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentProfileSetupBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_setup, container, false)
        binding.usernameEditText.afterTextChangedDelayed {
            Log.i("TAGGGGGGG", "STOPPEDDDD")
            binding.nextButton.visibility = View.VISIBLE
             string = it
        }

        binding.nextButton.setOnClickListener {
            //startActivity(Intent(activity, CountdownActivity::class.java))
            val authCode: String? = sharedPreference.getAuthCode()
            viewModel.editUsername("Token "+ authCode,string)
        }
        viewModel.usernameChanged.observe(viewLifecycleOwner, Observer {it->
            if(it==1){
                sharedPreference.setUserStatus(true)
                startActivity(Intent(activity, CountdownActivity::class.java))
            }
        })
        return binding.root
    }

    fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(1000, 1500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        afterTextChanged.invoke(editable.toString())
                    }
                }.start()
            }
        })
    }

}