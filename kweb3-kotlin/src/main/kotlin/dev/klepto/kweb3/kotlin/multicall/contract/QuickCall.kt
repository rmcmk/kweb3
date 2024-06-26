package dev.klepto.kweb3.kotlin.multicall.contract

import dev.klepto.kweb3.core.contract.annotation.View
import dev.klepto.kweb3.core.contract.type.EthTupleContainer
import dev.klepto.kweb3.core.ethereum.type.primitive.EthAddress
import dev.klepto.kweb3.core.ethereum.type.primitive.EthArray
import dev.klepto.kweb3.core.ethereum.type.primitive.EthArray.array
import dev.klepto.kweb3.core.ethereum.type.primitive.EthBytes
import dev.klepto.kweb3.core.ethereum.type.primitive.EthUint
import dev.klepto.kweb3.kotlin.multicall.MulticallContract

/**
 * Implementation of *QuickCall* smart contract executor.
 *
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
@JvmDefaultWithoutCompatibility
interface QuickCall : MulticallContract {

    override suspend fun execute(allowFailure: Boolean, calls: List<MulticallContract.Call>): List<EthBytes?> {
        // TODO: Implement proper endpoint resolution.
        val gasLimit = client.endpoint.settings.gasLimit / calls.size
        val addrs = array(calls.map { it.address })
        val datas = array(calls.map { it.data })
        val result = execute(gasLimit, gasLimit, addrs, datas)
        return result.results.values()
    }

    /**
     * Aggregates all calls into a single request and returns an array
     * containing return data bytes of each call.
     *
     * @param gasLimit the maximum amount of gas to be used for each call
     * @param sizeLimit the maximum amount of bytes to be used for each call
     * @param addrs the array of smart contract addresses to be called
     * @param datas the array of data bytes to be called with smart contracts
     */
    @View
    suspend fun execute(
        gasLimit: EthUint,
        sizeLimit: EthUint,
        addrs: EthArray<EthAddress>,
        datas: EthArray<EthBytes>
    ): Result

    /**
     * Contains result tuple of a *multicall* request.
     *
     * @param blockNumber the block number of the request
     * @param statuses the array of statuses of each call
     * @param results the array of return data bytes from each call
     */
    data class Result(
        val blockNumber: EthUint,
        val statuses: EthArray<EthUint>,
        val results: EthArray<EthBytes>
    ) : EthTupleContainer

}