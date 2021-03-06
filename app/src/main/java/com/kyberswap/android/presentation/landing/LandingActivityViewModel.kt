package com.kyberswap.android.presentation.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyberswap.android.domain.usecase.wallet.CreateWalletUseCase
import com.kyberswap.android.domain.usecase.wallet.GetMnemonicUseCase
import com.kyberswap.android.presentation.common.Event
import com.kyberswap.android.util.ErrorHandler
import io.reactivex.functions.Consumer
import javax.inject.Inject

class LandingActivityViewModel @Inject constructor(
    private val createWalletUseCase: CreateWalletUseCase,
    private val getMnemonicUseCase: GetMnemonicUseCase,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _getMnemonicCallback = MutableLiveData<Event<CreateWalletState>>()
    val createWalletCallback: LiveData<Event<CreateWalletState>>
        get() = _getMnemonicCallback

    fun createWallet(walletName: String = "Untitled") {
        _getMnemonicCallback.postValue(Event(CreateWalletState.Loading))
        createWalletUseCase.execute(
            Consumer {
                _getMnemonicCallback.value =
                    Event(CreateWalletState.Success(it.first, it.second))
            },
            Consumer {
                it.printStackTrace()
                _getMnemonicCallback.value =
                    Event(CreateWalletState.ShowError(errorHandler.getError(it)))
            },
            CreateWalletUseCase.Param(walletName)
        )
    }

    override fun onCleared() {
        createWalletUseCase.dispose()
        super.onCleared()
    }
}