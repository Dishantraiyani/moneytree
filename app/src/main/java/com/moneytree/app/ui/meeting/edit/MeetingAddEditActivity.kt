package com.moneytree.app.ui.meeting.edit

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class MeetingAddEditActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment(intent.extras)
    }

    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(MeetingAddEditFragment.newInstance(bundle), false, binding.commonContainer.id)
    }
}
