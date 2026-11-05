package com.bapparis.baguardai.data.model

data class InvestigationRequest(
    val inputType: String,
    val content: String,
    val language: String = "English"
)

data class InvestigationEvidence(
    val observation: String,
    val significance: String
)

data class InvestigationReport(
    val summary: String,
    val riskLevel: String,
    val confidence: String,
    val evidence: List<InvestigationEvidence>,
    val alternativeExplanations: List<String>,
    val recommendedActions: List<String>,
    val limitations: List<String>
)

data class InvestigationUsage(
    val dailyLimit: Int,
    val used: Int,
    val remaining: Int,
    val resetAt: String
)

data class InvestigationResult(
    val report: InvestigationReport,
    val usage: InvestigationUsage
)