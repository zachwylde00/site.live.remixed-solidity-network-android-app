package com.kyberswap.android.presentation.main.swap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyberswap.android.domain.model.Swap
import com.kyberswap.android.domain.usecase.swap.GetExpectedRateUseCase
import com.kyberswap.android.domain.usecase.swap.GetMarketRateUseCase
import com.kyberswap.android.domain.usecase.token.GetBalancePollingUseCase
import com.kyberswap.android.domain.usecase.wallet.GetSwapDataUseCase
import com.kyberswap.android.domain.usecase.wallet.GetWalletByAddressUseCase
import com.kyberswap.android.presentation.common.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

class SwapViewModel @Inject constructor(
    private val getBalancePollingUseCase: GetBalancePollingUseCase,
    private val getWalletByAddressUseCase: GetWalletByAddressUseCase,
    private val getExpectedRateUseCase: GetExpectedRateUseCase,
    private val getSwapData: GetSwapDataUseCase,
    private val getMarketRate: GetMarketRateUseCase
) : ViewModel() {

    private val _getSwapCallback = MutableLiveData<Event<GetSwapState>>()
    val getSwapDataCallback: LiveData<Event<GetSwapState>>
        get() = _getSwapCallback


    private val _getExpectedRateCallback = MutableLiveData<Event<GetExpectedRateState>>()
    val getExpectedRateCallback: LiveData<Event<GetExpectedRateState>>
        get() = _getExpectedRateCallback

    val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private val _getGetMarketRateStateCallback = MutableLiveData<Event<GetMarketRateState>>()
    val getGetMarketRateStateCallback: LiveData<Event<GetMarketRateState>>
        get() = _getGetMarketRateStateCallback

    fun getMarketRate(srcToken: String, destToken: String) {
        if (srcToken.isNotBlank() && destToken.isNotBlank()) {
            getMarketRate.execute(
                Consumer {
                    _getGetMarketRateStateCallback.value = Event(GetMarketRateState.Success(it))
        ,
                Consumer {
                    it.printStackTrace()
                    _getGetMarketRateStateCallback.value =
                        Event(GetMarketRateState.ShowError(it.localizedMessage))
        ,
                GetMarketRateUseCase.Param(srcToken, destToken)
            )

    }

    fun getSwapData(address: String) {
        getSwapData.execute(
            Consumer {
                _getSwapCallback.value = Event(GetSwapState.Success(it))
    ,
            Consumer {
                it.printStackTrace()
                _getSwapCallback.value = Event(GetSwapState.ShowError(it.localizedMessage))
    ,
            GetSwapDataUseCase.Param(address)
        )
    }

    fun getExpectedRate(
        walletAddress: String,
        swap: Swap,
        srcAmount: String
    ) {

        getExpectedRateUseCase.execute(
            Consumer {
                _getExpectedRateCallback.value = Event(GetExpectedRateState.Success(it))
    ,
            Consumer {
                it.printStackTrace()
                _getExpectedRateCallback.value =
                    Event(GetExpectedRateState.ShowError(it.localizedMessage))
    ,
            GetExpectedRateUseCase.Param(walletAddress, swap, srcAmount)
        )
    }

    override fun onCleared() {
        getBalancePollingUseCase.dispose()
        getWalletByAddressUseCase.dispose()
        compositeDisposable.dispose()
        super.onCleared()
    }

}