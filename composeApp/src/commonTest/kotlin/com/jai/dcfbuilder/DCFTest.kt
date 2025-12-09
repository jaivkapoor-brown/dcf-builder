package com.jai.dcfbuilder

import com.jai.dcfbuilder.engine.*
import kotlin.test.Test
import kotlin.test.assertTrue

class DCFTest {

    @Test
    fun testBasicDCF() {
        val inputs = DCFInputs(
            ebitdaHistory = listOf(16247.0, 17128.0, 18379.0),
            growthRates = listOf(0.075, 0.07, 0.06, 0.05, 0.04),
            terminalGrowthRate = 0.02,
            wacc = 0.135,
            taxRate = 0.17,
            taxDepreciation = 3700.0,
            workingCapitalChange = -100.0,
            netDebt = -18642.0,
            sharesOutstanding = 34200.0,
            currentPrice = 2.71
        )

        val result = DCFCalculator.calculate(inputs)

        println("=== DCF Results ===")
        println("Projected EBITDA: ${result.projectedEBITDA.map { "%.0f".format(it) }}")
        println("Unlevered FCF: ${result.unleveredFCF.map { "%.0f".format(it) }}")
        println("Enterprise Value: ${"%.0f".format(result.enterpriseValue)}")
        println("Equity Value: ${"%.0f".format(result.equityValue)}")
        println("Per Share: ${"%.2f".format(result.equityValuePerShare)}")
        println("Implied Upside: ${"%.1f".format(result.impliedUpside * 100)}%")

        assertTrue(result.equityValuePerShare > 0, "Share price should be positive")
    }

    @Test
    fun testMonteCarlo() {
        val inputs = MonteCarloInputs(
            ebitdaHistory = listOf(16247.0, 17128.0, 18379.0),
            growthRateRange = Range(0.03, 0.08),
            terminalGrowthRange = Range(0.015, 0.025),
            waccRange = Range(0.11, 0.15),
            taxRate = 0.17,
            taxDepreciation = 3700.0,
            workingCapitalChange = -100.0,
            netDebt = -18642.0,
            sharesOutstanding = 34200.0,
            currentPrice = 2.71
        )

        val result = MonteCarloSimulator.simulate(inputs, simulations = 1000)

        println("=== Monte Carlo Results (1000 sims) ===")
        println("Mean: ${"%.2f".format(result.mean)}")
        println("Median: ${"%.2f".format(result.median)}")
        println("10th percentile: ${"%.2f".format(result.percentile10)}")
        println("90th percentile: ${"%.2f".format(result.percentile90)}")
        println("P(above current): ${"%.1f".format(result.probabilityAboveCurrentPrice * 100)}%")

        assertTrue(result.mean > 0, "Mean should be positive")
    }
}