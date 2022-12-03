package com.moneytree.app.ui.youtube.detail

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentYoutubeViewBinding
import com.moneytree.app.repository.NSRoyaltyRepository.getRoyaltyListData
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController


class NSYoutubeViewFragment : NSFragment(), YouTubePlayerListener {
	private val youtubeViewModel: NSYoutubeViewModel by lazy {
		ViewModelProvider(this)[NSYoutubeViewModel::class.java]
	}
	private var _binding: FragmentYoutubeViewBinding? = null
	private val youtubeBinding get() = _binding!!
	private var youtubeAdapter: NSYoutubeListDetailRecycleAdapter? = null

	companion object {
		fun newInstance(bundle: Bundle?) = NSYoutubeViewFragment().apply {
			arguments = bundle
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			with(youtubeViewModel) {
				youtubeDetail = it.getString(NSConstants.YOUTUBE_DETAIL)
				youtubeFullResponse = it.getString(NSConstants.YOUTUBE_FULL_RESPONSE)
				getYoutubeResponse()
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentYoutubeViewBinding.inflate(inflater, container, false)
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
					tvHeaderBack.text = youtubeSelectedItem?.snippet?.title
					ivBack.visibility = View.VISIBLE
				}
				tvYoutubeVideoTitle.text = youtubeSelectedItem?.snippet?.title
				lifecycle.addObserver(videoFullScreenPlayer)
				//val customPlayerUi: View = videoFullScreenPlayer.inflateCustomPlayerUi(R.layout.custom_player_ui_youtube)
				val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
					override fun onReady(youTubePlayer: YouTubePlayer) {
						val defaultPlayerUiController =
							CustomPlayerUiController(videoFullScreenPlayer, youTubePlayer)
						defaultPlayerUiController.showYouTubeButton(false)
						defaultPlayerUiController.showMenuButton(false)
						defaultPlayerUiController.showBufferingProgress(false)
						defaultPlayerUiController.showFullscreenButton(true)
						defaultPlayerUiController.showUi(true)
						videoFullScreenPlayer.setCustomPlayerUi(defaultPlayerUiController.rootView)

						/*val defaultPlayerUiController = CustomPlayerUiController(videoFullScreenPlayer, youTubePlayer, customPlayerUi)
						defaultPlayerUiController.showUi(false)*/
						//videoFullScreenPlayer.setCustomPlayerUi(customPlayerUiController.rootView)
						youTubePlayer.loadVideo(youtubeSelectedItem!!.id!!.videoId!!, 0f)
					}
				}

				//videoFullScreenPlayer.addYouTubePlayerListener(this@NSYoutubeViewFragment)
				videoFullScreenPlayer.addFullScreenListener(object : YouTubePlayerFullScreenListener {
					override fun onYouTubePlayerEnterFullScreen() {
						requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
								or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
								or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
						requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
						rlToolbar.visibility = View.GONE
						clVideoList.gone()
						tvYoutubeVideoTitle.gone()
						viewLine.gone()
					}

					override fun onYouTubePlayerExitFullScreen() {
						requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
						requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
						rlToolbar.visibility = View.VISIBLE
						clVideoList.visible()
						tvYoutubeVideoTitle.visible()
						viewLine.gone()
					}

				})


				val options: IFramePlayerOptions = IFramePlayerOptions.Builder().controls(0).build()
				videoFullScreenPlayer.initialize(listener, options)
				 setYoutubeAdapter()
				 observeViewModel()
			}
		}
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		// Checks the orientation of the screen
		with(youtubeBinding) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				videoFullScreenPlayer.enterFullScreen();
			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				videoFullScreenPlayer.exitFullScreen();
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

	override fun onDestroy() {
		super.onDestroy()
		youtubeBinding.videoFullScreenPlayer.release()
	}

	/**
	 * To add data of royal in list
	 */
	private fun setYoutubeAdapter() {
		with(youtubeBinding) {
			with(youtubeViewModel) {
				rvYoutubeList.layoutManager = LinearLayoutManager(activity)
				youtubeAdapter =
					NSYoutubeListDetailRecycleAdapter(activity, youtubeSelectedItem!!.id!!.videoId!!, object : NSPageChangeCallback {
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
							finish()
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
						getString(com.moneytree.app.R.string.no_network_available),
						getString(com.moneytree.app.R.string.network_unreachable)
					)
				}

				validationErrorId.observe(viewLifecycleOwner) { errorId ->
					showAlertDialog(getString(errorId))
				}
			}
		}
	}

	override fun onApiChange(youTubePlayer: YouTubePlayer) {
		TODO("Not yet implemented")
	}

	override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
		TODO("Not yet implemented")
	}

	override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
		TODO("Not yet implemented")
	}

	override fun onPlaybackQualityChange(
		youTubePlayer: YouTubePlayer,
		playbackQuality: PlayerConstants.PlaybackQuality
	) {
		TODO("Not yet implemented")
	}

	override fun onPlaybackRateChange(
		youTubePlayer: YouTubePlayer,
		playbackRate: PlayerConstants.PlaybackRate
	) {
		TODO("Not yet implemented")
	}

	override fun onReady(youTubePlayer: YouTubePlayer) {
		TODO("Not yet implemented")
	}

	override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
		TODO("Not yet implemented")
	}

	override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
		TODO("Not yet implemented")
	}

	override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {
		TODO("Not yet implemented")
	}

	override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {
		TODO("Not yet implemented")
	}
}
