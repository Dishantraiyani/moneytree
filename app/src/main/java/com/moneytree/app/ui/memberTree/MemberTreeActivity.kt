package com.moneytree.app.ui.memberTree

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityMemberTreeBinding

class MemberTreeActivity : NSActivity() {
    private lateinit var memberTreeBinding: ActivityMemberTreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memberTreeBinding = ActivityMemberTreeBinding.inflate(layoutInflater)
        setContentView(memberTreeBinding.root)
        loadInitialFragment(intent.extras!!)
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle) {
        replaceCurrentFragment(MemberTreeFragment.newInstance(bundle), false, memberTreeBinding.memberTreeContainer.id)
    }
}