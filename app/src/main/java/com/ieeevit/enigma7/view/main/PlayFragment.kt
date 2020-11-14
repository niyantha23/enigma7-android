package com.ieeevit.enigma7.view.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ieeevit.enigma7.R
import com.ieeevit.enigma7.utils.PrefManager
import com.ieeevit.enigma7.viewModel.PlayViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_play.view.*
import kotlinx.android.synthetic.main.hint_dialog_layout.view.*
import kotlinx.android.synthetic.main.questions_layout.view.*
import kotlinx.android.synthetic.main.view_hint_dialog.view.*

class PlayFragment : Fragment() {
    private lateinit var sharedPreference: PrefManager
    private lateinit var authCode: String
    private lateinit var builder: AlertDialog.Builder
    private lateinit var customInflater: LayoutInflater
    private lateinit var hint: String
    private val viewModel: PlayViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        ViewModelProvider(this, PlayViewModel.Factory(activity.application))
            .get(PlayViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View = inflater.inflate(R.layout.fragment_play, container, false)

        sharedPreference = PrefManager(this.requireActivity())
        if (sharedPreference.getHintString() != null) {
            root.get_hint_btn.visibility = GONE
            root.view_hint_btn.visibility = VISIBLE
        }
        init()
        viewModel.hint.observe(viewLifecycleOwner, {
            if (it == "") {
                Toast.makeText(activity, "Hint retrieval failed", Toast.LENGTH_SHORT).show()
            } else if (it != null) {
                hint = "Hint: $it"
                sharedPreference.setHint(hint)
                showAlertDialog(R.layout.view_hint_dialog)
                root.get_hint_btn.visibility = GONE
                root.view_hint_btn.visibility = VISIBLE
            }
        })
        viewModel.answerResponse.observe(viewLifecycleOwner, {
            if (it != null) {
                when {
                    it.closeAnswer == true -> {
                        showAlertDialog(R.layout.close_response_dialog)
                    }
                    it.answer == false -> {
                        showAlertDialog(R.layout.wrong_response_dialog)
                    }
                    it.answer == true -> {
                        showAlertDialog(R.layout.correct_response_dialog)
                        root.get_hint_btn.visibility = VISIBLE
                        root.view_hint_btn.visibility = GONE
                        sharedPreference.setHint(null)
                        root.answerBox.setText("")
                        viewModel.refreshQuestionsFromRepository("Token $authCode")
                        // TODO: 11-11-2020 get the keyboard down
                        // TODO: 30-10-2020 try to update profile(db)
                    }
                }
            }
        })
        root.submit_btn.setOnClickListener {
            val answer = root.answerBox.text.toString()
            viewModel.checkAnswer("Token $authCode", answer)
        }
        root.get_hint_btn.setOnClickListener {
            showAlertDialog(R.layout.hint_dialog_layout)
        }
        root.view_hint_btn.setOnClickListener {
            showAlertDialog(R.layout.view_hint_dialog)
        }

        root.main_screen.visibility = INVISIBLE
        root.question_loading_progress.visibility = VISIBLE

        viewModel.refreshQuestionsFromRepository("Token $authCode")
        viewModel.questionResponse.observe(viewLifecycleOwner, {
            if(it!=null){
                root.question.text = it.text
                root.question_id.text = "Q${it.id}."
                Picasso.get().load(it.img_url).into(root.question_image)
                root.main_screen.visibility = VISIBLE
                root.question_loading_progress.visibility = GONE
            }

        })

        return root
    }

    private fun init() {
        authCode = sharedPreference.getAuthCode()!!
        builder = AlertDialog.Builder(activity)
        customInflater = requireActivity().layoutInflater
    }

    private fun showAlertDialog(layoutId: Int) {
        val customLayout: View = customInflater.inflate(layoutId, null)
        builder.setView(customLayout)
        val alert = builder.create()
        when (customLayout.id) {
            R.id.hintDialog -> {
                customLayout.cancel.setOnClickListener {
                    alert.dismiss()
                }
                customLayout.accept.setOnClickListener {
                    viewModel.getHint("Token $authCode")
                    alert.dismiss()
                }
            }
            R.id.viewHintDialog -> {
                customLayout.hintView.text = sharedPreference.getHintString()
            }

        }
        alert.show()
    }

}