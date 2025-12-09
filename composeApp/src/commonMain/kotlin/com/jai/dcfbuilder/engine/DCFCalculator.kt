package com.jai.dcfbuilder.engine

import kotlin.math.pow

/**
 * Core DCF calculation engine — pure Kotlin, runs on any platform
 */
object DCFCalculator {

    /**
     * Run a single DCF valuation with fixed inputs
     */
    fun calculate(inputs: DCFInputs): DCFResult {
        val baseEBITDA = inputs.ebitdaHistory.last()
        val forecastYears = inputs.growthRates.size

        // Project EBITDA
        val projectedEBITDA = mutableListOf<Double>()
        var currentEBITDA = baseEBITDA
        for (growthRate in inputs.growthRates) {
            currentEBITDA *= (1 + growthRate)
            projectedEBITDA.add(currentEBITDA)
        }

        // Calculate unlevered FCF for each year
        val unleveredFCF = projectedEBITDA.map { ebitda ->
            val operatingProfit = ebitda - inputs.taxDepreciation
            val taxes = operatingProfit * inputs.taxRate
            ebitda - taxes - inputs.taxDepreciation + inputs.workingCapitalChange
        }

        // Terminal value (Gordon Growth)
        val terminalFCF = unleveredFCF.last() * (1 + inputs.terminalGrowthRate)
        val terminalValue = terminalFCF / (inputs.wacc - inputs.terminalGrowthRate)

        // Discount cash flows
        val pvDiscrete = unleveredFCF.mapIndexed { index, fcf ->
            fcf / (1 + inputs.wacc).pow(index + 1)
        }.sum()

        val pvTerminal = terminalValue / (1 + inputs.wacc).pow(forecastYears)

        // Enterprise value to equity value
        val enterpriseValue = pvDiscrete + pvTerminal
        val equityValue = enterpriseValue - inputs.netDebt
        val equityValuePerShare = equityValue / inputs.sharesOutstanding
        val impliedUpside = (equityValuePerShare / inputs.currentPrice) - 1

        return DCFResult(
            projectedEBITDA = projectedEBITDA,
            unleveredFCF = unleveredFCF,
            terminalValue = terminalValue,
            pvDiscrete = pvDiscrete,
            pvTerminal = pvTerminal,
            enterpriseValue = enterpriseValue,
            equityValue = equityValue,
            equityValuePerShare = equityValuePerShare,
            impliedUpside = impliedUpside
        )
    }
}