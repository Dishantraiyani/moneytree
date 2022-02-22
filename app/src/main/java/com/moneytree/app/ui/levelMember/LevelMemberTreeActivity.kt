package com.moneytree.app.ui.levelMember

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityLevelMemberTreeBinding
import com.moneytree.app.databinding.ActivityMemberTreeBinding

class LevelMemberTreeActivity : NSActivity() {
    private lateinit var memberTreeBinding: ActivityLevelMemberTreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memberTreeBinding = ActivityLevelMemberTreeBinding.inflate(layoutInflater)
        setContentView(memberTreeBinding.root)
        loadInitialFragment(intent.extras!!)
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle) {
        replaceCurrentFragment(LevelMemberTreeFragment.newInstance(bundle), false, memberTreeBinding.levelMemberTreeContainer.id)
    }
}