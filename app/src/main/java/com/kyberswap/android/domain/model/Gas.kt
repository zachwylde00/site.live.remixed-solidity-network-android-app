package com.kyberswap.android.domain.model

import android.os.Parcelable
import com.kyberswap.android.data.api.gas.GasEntity
import com.kyberswap.android.util.ext.toBigDecimalOrDefaultZero
import com.kyberswap.android.util.ext.toDisplayNumber
import com.kyberswap.android.util.ext.toDoubleSafe
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class Gas(
    var fast: String = "",
    var standard: String = "",
    var low: String = "",
    var default: String = ""

) : Parcelable {
    constructor(entity: GasEntity) : this(
        entity.fast, entity.standard, entity.low, entity.default
    )

    val superFast: String
        get() = BigDecimal("20").max(fast.toBigDecimalOrDefaultZero() * 2.toBigDecimal())
            .min(100.toBigDecimal())
            .toDisplayNumber()

    fun toPromoGas(): Gas {
        return this.copy(
            fast = (this.fast.toDoubleSafe() + 2.0).toString(),
            standard = (this.standard.toDoubleSafe() + 2.0).toString(),
            low = (this.low.toDoubleSafe() + 2.0).toString(),
            default = (this.default.toDoubleSafe() + 2.0).toString()
        )
    }
}