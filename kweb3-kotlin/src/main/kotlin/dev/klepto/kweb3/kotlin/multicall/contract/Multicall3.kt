package dev.klepto.kweb3.kotlin.multicall.contract

import dev.klepto.kweb3.core.contract.annotation.View
import dev.klepto.kweb3.core.contract.type.EthStructContainer
import dev.klepto.kweb3.core.ethereum.type.primitive.EthAddress
import dev.klepto.kweb3.core.ethereum.type.primitive.EthArray
import dev.klepto.kweb3.core.ethereum.type.primitive.EthArray.array
import dev.klepto.kweb3.core.ethereum.type.primitive.EthBool
import dev.klepto.kweb3.core.ethereum.type.primitive.EthBool.bool
import dev.klepto.kweb3.core.ethereum.type.primitive.EthBytes
import dev.klepto.kweb3.kotlin.multicall.MulticallContract

/**
 * Implementation of [Multicall3](https://github.com/mds1/multicall) smart
 * contract executor.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@JvmDefaultWithoutCompatibility
interface Multicall3 : MulticallContract {

    override suspend fun execute(allowFailure: Boolean, calls: List<MulticallContract.Call>): List<EthBytes?> {
        val encodedCalls = array(calls.map { Call(it.address, bool(allowFailure), it.data) })
        val response = aggregate3(encodedCalls)
        return response.map {
            if (it.success.check()) it.returnData else null
        }
    }

    /**
     * Aggregates all calls into a single request and returns an array
     * containing [results][Result] of each call.
     *
     * @param calls an ethereum array containing calls for aggregation
     * @return an ethereum array containing result of each call
     */
    @View
    suspend fun aggregate3(calls: EthArray<Call>): EthArray<Result>

    /**
     * Contains a single smart contract call request. If `allowFailure` is set
     * to `false`, failure of this call will cause entire aggregation request
     * to fail.
     *
     * @param target the target contract address
     * @param allowFailure marks that aggregation request is allowed to
     *     continue upon failure of this call
     * @param callData the call-data contained in ethereum bytes
     */
    data class Call(val target: EthAddress, val allowFailure: EthBool, val callData: EthBytes) : EthStructContainer

    /**
     * Contains result of a single smart contract call.
     *
     * @param success true if call was successful
     * @param returnData ethereum bytes containing return data
     */
    data class Result(val success: EthBool, val returnData: EthBytes) : EthStructContainer

}