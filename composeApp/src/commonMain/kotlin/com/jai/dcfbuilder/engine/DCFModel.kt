package com.jai.dcfbuilder.engine

/**
 * All inputs needed to run a DCF valuation
 */
data class DCFInputs(
    // Historical EBITDA (last 3 years, in thousands)
    val ebitdaHistory: List<Double>,

    // Projected growth rates (one per forecast year)
    val growthRates: List<Double>,

    // Terminal assumptions
    val terminalGrowthRate: Double,
    val wacc: Double,

    // Other assumptions
    val taxRate: Double,
    val taxDepreciation: Double,
    val workingCapitalChange: Double,

    // Equity bridge
    val netDebt: Double,
    val sharesOutstanding: Double,
    val currentPrice: Double
)

/**
 * Results from a single DCF run
 */
data class DCFResult(
    val projectedEBITDA: List<Double>,
    val unleveredFCF: List<Double>,
    val terminalValue: Double,
    val pvDiscrete: Double,
    val pvTerminal: Double,
    val enterpriseValue: Double,
    val equityValue: Double,
    val equityValuePerShare: Double,
    val impliedUpside: Double
)

/**
 * For Monte Carlo: define a range instead of a point estimate
 */
data class Range(
    val min: Double,
    val max: Double,
    val mostLikely: Double? = null  // Optional: for triangular distribution
)

/**
 * Monte Carlo inputs — ranges instead of fixed values
 */
data class MonteCarloInputs(
    val ebitdaHistory: List<Double>,
    val growthRateRange: Range,
    val terminalGrowthRange: Range,
    val waccRange: Range,
    val taxRate: Double,
    val taxDepreciation: Double,
    val workingCapitalChange: Double,
    val netDebt: Double,
    val sharesOutstanding: Double,
    val currentPrice: Double,
    val forecastYears: Int = 5
)

/**
 * Monte Carlo output — distribution of values
 */
data class MonteCarloResult(
    val simulations: Int,
    val results: List<Double>,  // All equity values per share
    val mean: Double,
    val median: Double,
    val percentile10: Double,
    val percentile25: Double,
    val percentile75: Double,
    val percentile90: Double,
    val probabilityAboveCurrentPrice: Double
)