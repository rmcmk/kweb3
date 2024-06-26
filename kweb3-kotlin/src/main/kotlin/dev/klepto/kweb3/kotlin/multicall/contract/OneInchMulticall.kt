package dev.klepto.kweb3.kotlin.multicall.contract

import dev.klepto.kweb3.core.contract.annotation.View
import dev.klepto.kweb3.core.contract.type.EthStructContainer
import dev.klepto.kweb3.core.contract.type.EthTupleContainer
import dev.klepto.kweb3.core.ethereum.type.primitive.EthAddress
import dev.klepto.kweb3.core.ethereum.type.primitive.EthArray
import dev.klepto.kweb3.core.ethereum.type.primitive.EthArray.array
import dev.klepto.kweb3.core.ethereum.type.primitive.EthBytes
import dev.klepto.kweb3.core.ethereum.type.primitive.EthUint
import dev.klepto.kweb3.core.ethereum.type.primitive.EthUint.uint256
import dev.klepto.kweb3.kotlin.multicall.MulticallContract

/**
 * Implementation of [1inch Multicall](https://github.com/1inch/multicall)
 * smart contract executor.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@JvmDefaultWithoutCompatibility
interface OneInchMulticall : MulticallContract {

    override suspend fun execute(allowFailure: Boolean, calls: List<MulticallContract.Call>): List<EthBytes?> {
        val encodedCalls = array(calls.map { Call(it.address, it.data) })
        val response = multicallWithGasLimitation(encodedCalls)
        return response.results.values()
    }

    /**
     * Aggregates all [calls] into a single request and returns an array
     * containing return data bytes of each call. If remaining gas is ever
     * lower than the [gasBuffer], returns all the available results up to that
     * point and the index of the last successful call.
     *
     * @param calls the array of calls to be aggregated
     * @param gasBuffer the amount of gas to be left in the transaction before
     *     returning
     * @return the response tuple containing results and last successful call
     *     index
     */
    @View
    suspend fun multicallWithGasLimitation(calls: EthArray<Call>, gasBuffer: EthUint = uint256(3_000_000)): Response

    /**
     * Container for a single call request.
     *
     * @param to the address of the smart contract
     * @param data the encoded call data to be sent to the smart contract
     */
    data class Call(val to: EthAddress, val data: EthBytes) : EthStructContainer

    /**
     * Container for *multicall* request result.
     *
     * @param results the array of return data bytes from each call
     * @param lastSuccessIndex the index of the last successful call
     */
    data class Response(val results: EthArray<EthBytes>, val lastSuccessIndex: EthUint) : EthTupleContainer

}