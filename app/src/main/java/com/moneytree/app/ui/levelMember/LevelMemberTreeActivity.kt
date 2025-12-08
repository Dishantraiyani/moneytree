package com.moneytree.app.ui.levelMember

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.levelMemberDetail.LevelMemberTreeDetailFragment

class LevelMemberTreeActivity : NSActivity() {
    private lateinit var memberTreeBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        memberTreeBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(memberTreeBinding.root)
        initView(memberTreeBinding.root)
        loadInitialFragment(intent.extras!!)
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle) {
        replaceCurrentFragment(LevelMemberTreeDetailFragment.newInstance(bundle), false, memberTreeBinding.commonContainer.id)
    }
}
