package com.jai.dcfbuilder.engine

import kotlin.random.Random

/**
 * Monte Carlo simulation engine
 */
object MonteCarloSimulator {

    /**
     * Run n simulations with randomized inputs within specified ranges
     */
    fun simulate(
        inputs: MonteCarloInputs,
        simulations: Int = 10000,
        random: Random = Random.Default
    ): MonteCarloResult {

        val results = mutableListOf<Double>()

        repeat(simulations) {
            // Sample random values from ranges
            val wacc = sampleUniform(inputs.waccRange, random)
            val terminalGrowth = sampleUniform(inputs.terminalGrowthRange, random)

            // Generate growth rates for each forecast year
            val growthRates = List(inputs.forecastYears) {
                sampleUniform(inputs.growthRateRange, random)
            }

            // Build DCF inputs
            val dcfInputs = DCFInputs(
                ebitdaHistory = inputs.ebitdaHistory,
                growthRates = growthRates,
                terminalGrowthRate = terminalGrowth,
                wacc = wacc,
                taxRate = inputs.taxRate,
                taxDepreciation = inputs.taxDepreciation,
                workingCapitalChange = inputs.workingCapitalChange,
                netDebt = inputs.netDebt,
                sharesOutstanding = inputs.sharesOutstanding,
                currentPrice = inputs.currentPrice
            )

            // Run DCF
            val result = DCFCalculator.calculate(dcfInputs)
            results.add(result.equityValuePerShare)
        }

        // Sort for percentile calculations
        val sorted = results.sorted()

        return MonteCarloResult(
            simulations = simulations,
            results = sorted,
            mean = results.average(),
            median = sorted[sorted.size / 2],
            percentile10 = sorted[(sorted.size * 0.10).toInt()],
            percentile25 = sorted[(sorted.size * 0.25).toInt()],
            percentile75 = sorted[(sorted.size * 0.75).toInt()],
            percentile90 = sorted[(sorted.size * 0.90).toInt()],
            probabilityAboveCurrentPrice = results.count { it > inputs.currentPrice }.toDouble() / simulations
        )
    }

    /**
     * Sample from uniform distribution between min and max
     */
    private fun sampleUniform(range: Range, random: Random): Double {
        return range.min + random.nextDouble() * (range.max - range.min)
    }

    /**
     * Create histogram buckets for visualization
     */
    fun createHistogram(results: List<Double>, buckets: Int = 50): List<Pair<Double, Int>> {
        val min = results.min()
        val max = results.max()
        val bucketSize = (max - min) / buckets

        return (0 until buckets).map { i ->
            val bucketMin = min + i * bucketSize
            val bucketMax = bucketMin + bucketSize
            val count = results.count { it >= bucketMin && it < bucketMax }
            bucketMin to count
        }
    }
}