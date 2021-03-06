package com.kyberswap.android.presentation.main.swap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyberswap.android.domain.model.Swap
import com.kyberswap.android.domain.model.Wallet
import com.kyberswap.android.domain.usecase.swap.EstimateGasUseCase
import com.kyberswap.android.domain.usecase.swap.GetSwapDataUseCase
import com.kyberswap.android.domain.usecase.swap.SwapTokenUseCase
import com.kyberswap.android.presentation.common.ADDITIONAL_SWAP_GAS_LIMIT
import com.kyberswap.android.presentation.common.Event
import com.kyberswap.android.presentation.common.calculateDefaultGasLimit
import com.kyberswap.android.presentation.common.specialGasLimitDefault
import io.reactivex.functions.Consumer
import javax.inject.Inject

class SwapConfirmViewModel @Inject constructor(
    private val getSwapData: GetSwapDataUseCase,
    private val estimateGasUseCase: EstimateGasUseCase,
    private val swapTokenUseCase: SwapTokenUseCase
) : ViewModel() {

    private val _getSwapCallback = MutableLiveData<Event<GetSwapState>>()
    val getSwapDataCallback: LiveData<Event<GetSwapState>>
        get() = _getSwapCallback

    private val _getGetGasLimitCallback = MutableLiveData<Event<GetGasLimitState>>()
    val getGetGasLimitCallback: LiveData<Event<GetGasLimitState>>
        get() = _getGetGasLimitCallback


    private val _swapTokenTransactionCallback =
        MutableLiveData<Event<SwapTokenTransactionState>>()
    val swapTokenTransactionCallback: LiveData<Event<SwapTokenTransactionState>>
        get() = _swapTokenTransactionCallback


    fun getSwapData(wallet: Wallet) {
        getSwapData.execute(
            Consumer {
                _getSwapCallback.value = Event(GetSwapState.Success(it))
            },
            Consumer {
                it.printStackTrace()
                _getSwapCallback.value = Event(GetSwapState.ShowError(it.localizedMessage))
            },
            GetSwapDataUseCase.Param(wallet)
        )
    }

    fun swap(wallet: Wallet?, swap: Swap?) {
        swap?.let {
            _swapTokenTransactionCallback.postValue(Event(SwapTokenTransactionState.Loading))
            swapTokenUseCase.execute(
                Consumer {
                    _swapTokenTransactionCallback.value =
                        Event(SwapTokenTransactionState.Success(it))
                },
                Consumer {
                    it.printStackTrace()
                    _swapTokenTransactionCallback.value =
                        Event(SwapTokenTransactionState.ShowError(it.localizedMessage))
                },
                SwapTokenUseCase.Param(wallet!!, swap)

            )
        }
    }

    fun getGasLimit(wallet: Wallet?, swap: Swap?) {
        if (wallet == null || swap == null) return
        estimateGasUseCase.dispose()
        estimateGasUseCase.execute(
            Consumer {
                if (it.error == null) {

                    val gasLimit = calculateDefaultGasLimit(swap.tokenSource, swap.tokenDest)
                        .min(it.amountUsed.multiply(120.toBigInteger()).divide(100.toBigInteger()) + ADDITIONAL_SWAP_GAS_LIMIT.toBigInteger())

                    val specialGasLimit = specialGasLimitDefault(swap.tokenSource, swap.tokenDest)

                    _getGetGasLimitCallback.value = Event(
                        GetGasLimitState.Success(
                            if (specialGasLimit != null) {
                                specialGasLimit.max(gasLimit)
                            } else {
                                gasLimit
                            }
                        )
                    )
                }

            },
            Consumer {
                it.printStackTrace()
            },
            EstimateGasUseCase.Param(
                wallet,
                swap.tokenSource,
                swap.tokenDest,
                swap.sourceAmount,
                swap.minConversionRate
            )
        )
    }

}