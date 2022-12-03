package com.moneytree.app.ui.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentYoutubeBinding
import com.moneytree.app.ui.offers.OfferDetailActivity
import com.moneytree.app.ui.youtube.detail.YoutubeViewActivity

class NSYoutubeFragment : NSFragment() {
    private val youtubeViewModel: NSYoutubeModel by lazy {
		ViewModelProvider(this)[NSYoutubeModel::class.java]
    }
    private var _binding: NsFragmentYoutubeBinding? = null
	private val youtubeBinding get() = _binding!!
	private var youtubeAdapter: NSYoutubeListRecycleAdapter? = null

	companion object {
        fun newInstance() = NSYoutubeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentYoutubeBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return youtubeBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(youtubeBinding) {
            with(youtubeViewModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.youtube)
                    ivBack.visibility = View.VISIBLE
                }
                setYoutubeAdapter()
				observeViewModel()
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(youtubeBinding) {
            with(youtubeViewModel) {
                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

					srlRefresh.setOnRefreshListener {
						pageIndex = ""
						getYoutubeVideos(pageIndex, false, isBottomProgress = false)
					}
                }
            }
        }
    }

	/**
	 * To add data of royal in list
	 */
	private fun setYoutubeAdapter() {
		with(youtubeBinding) {
			with(youtubeViewModel) {
				rvYoutubeList.layoutManager = LinearLayoutManager(activity)
				youtubeAdapter =
					NSYoutubeListRecycleAdapter(activity, object : NSPageChangeCallback {
						override fun onPageChange() {
							if (!youtubeResponse?.nextPageToken.isNullOrEmpty()) {
								val page: Int = youtubeList.size/ NSConstants.PAGINATION + 1
								pageIndex = youtubeResponse?.nextPageToken!!
								getYoutubeVideos(pageIndex, false, isBottomProgress = true)
							}
						}
					}, object : NSInfoSelectCallback {
						override fun onClick(position: Int) {
							val data = youtubeList[position]
							val bundle = Bundle()
							bundle.putString(NSConstants.YOUTUBE_DETAIL, Gson().toJson(data))
							bundle.putString(NSConstants.YOUTUBE_FULL_RESPONSE, Gson().toJson(youtubeResponse))
							switchActivity(YoutubeViewActivity::class.java, bundle)
							// EventBus.getDefault().post(NSFragmentChange(NSRoyaltyInfoFragment.newInstance(bundle)))
						}

					})
				rvYoutubeList.adapter = youtubeAdapter
				pageIndex = ""
				getYoutubeVideos(pageIndex, true, isBottomProgress = false)
			}
		}
	}

	private fun bottomProgress(isShowProgress: Boolean) {
		with(youtubeBinding) {
			cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
		}
	}

	/**
	 * Set Royalty data
	 *
	 * @param isRoyalty when data available it's true
	 */
	private fun setRoyaltyData(isRoyalty: Boolean) {
		with(youtubeViewModel) {
			royaltyDataManage(isRoyalty)
			if (isRoyalty) {
				youtubeAdapter!!.clearData()
				youtubeAdapter!!.updateData(youtubeList)
			}
		}
	}

	/**
	 * Royalty data manage
	 *
	 * @param isYoutubeVideoAvailable when Youtube available it's visible
	 */
	private fun royaltyDataManage(isYoutubeVideoAvailable: Boolean) {
		with(youtubeBinding) {
			rvYoutubeList.visibility = if (isYoutubeVideoAvailable) View.VISIBLE else View.GONE
			clRoyaltyNotFound.visibility = if (isYoutubeVideoAvailable) View.GONE else View.VISIBLE
		}
	}

	/**
	 * To observe the view model for data changes
	 */
	private fun observeViewModel() {
		with(youtubeViewModel) {
			with(youtubeBinding) {
				isProgressShowing.observe(
					viewLifecycleOwner
				) { shouldShowProgress ->
					updateProgress(shouldShowProgress)
				}

				isBottomProgressShowing.observe(
					viewLifecycleOwner
				) { isBottomProgressShowing ->
					bottomProgress(isBottomProgressShowing)
				}

				isYoutubeVideosAvailable.observe(
					viewLifecycleOwner
				) { isRoyalty ->
					srlRefresh.isRefreshing = false
					setRoyaltyData(isRoyalty)
				}

				failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
					srlRefresh.isRefreshing = false
					showAlertDialog(errorMessage)
				}

				apiErrors.observe(viewLifecycleOwner) { apiErrors ->
					srlRefresh.isRefreshing = false
					parseAndShowApiError(apiErrors)
				}

				noNetworkAlert.observe(viewLifecycleOwner) {
					srlRefresh.isRefreshing = false
					showNoNetworkAlertDialog(
						getString(R.string.no_network_available),
						getString(R.string.network_unreachable)
					)
				}

				validationErrorId.observe(viewLifecycleOwner) { errorId ->
					showAlertDialog(getString(errorId))
				}
			}
		}
	}
}
