package com.kyberswap.android.presentation.main.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kyberswap.android.domain.model.Alert
import com.kyberswap.android.domain.model.UserInfo
import com.kyberswap.android.domain.usecase.alert.GetCampaignResultUseCase
import com.kyberswap.android.domain.usecase.alert.GetLeaderBoardAlertsUseCase
import com.kyberswap.android.domain.usecase.wallet.GetSelectedWalletUseCase
import com.kyberswap.android.presentation.common.Event
import com.kyberswap.android.presentation.main.SelectedWalletViewModel
import com.kyberswap.android.presentation.main.profile.alert.GetLeaderBoardState
import com.kyberswap.android.util.ErrorHandler
import io.reactivex.functions.Consumer
import javax.inject.Inject

class LeaderBoardViewModel @Inject constructor(
    private val getLeaderBoardAlertsUseCase: GetLeaderBoardAlertsUseCase,
    private val getCampaignResultUseCase: GetCampaignResultUseCase,
    getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val errorHandler: ErrorHandler
) : SelectedWalletViewModel(getSelectedWalletUseCase, errorHandler) {

    private val _getAlertsCallback = MutableLiveData<Event<GetLeaderBoardState>>()
    val getAlertsCallback: LiveData<Event<GetLeaderBoardState>>
        get() = _getAlertsCallback

    fun getLeaderBoard(userInfo: UserInfo) {
        _getAlertsCallback.postValue(Event(GetLeaderBoardState.Loading))
        getLeaderBoardAlertsUseCase.execute(
            Consumer { lb ->
                val meAlert = lb.data.find {
                    it.userId == userInfo.uid
                }

                val alerts = mutableListOf<Alert>()

                if (meAlert != null) {
                    alerts.add(meAlert.copy(userName = userInfo.name))
                }
                alerts.addAll(lb.data)

                _getAlertsCallback.value =
                    Event(
                        GetLeaderBoardState.Success(
                            alerts,
                            lb.campaignInfo,
                            lb.lastCampaignTitle
                        )
                    )
            },
            Consumer {
                it.printStackTrace()
                _getAlertsCallback.value =
                    Event(GetLeaderBoardState.ShowError(errorHandler.getError(it)))
            },
            null
        )
    }

    fun getCampaignResult(userInfo: UserInfo) {
        _getAlertsCallback.postValue(Event(GetLeaderBoardState.Loading))
        getCampaignResultUseCase.execute(
            Consumer { lb ->
                val meAlert = lb.data.find {
                    it.userId == userInfo.uid
                }
                val alerts = lb.data.toMutableList()
                if (meAlert != null) {
                    alerts[lb.data.indexOf(meAlert)] = meAlert.copy(userName = userInfo.name)
                }

                _getAlertsCallback.value =
                    Event(
                        GetLeaderBoardState.Success(
                            alerts,
                            lb.campaignInfo,
                            lb.lastCampaignTitle
                        )
                    )
            },
            Consumer {
                it.printStackTrace()
                _getAlertsCallback.value =
                    Event(GetLeaderBoardState.ShowError(errorHandler.getError(it)))
            },
            null
        )
    }

}