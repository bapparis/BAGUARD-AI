package com.bapparis.baguardai.data.remote

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.bapparis.baguardai.data.model.InvestigationEvidence
import com.bapparis.baguardai.data.model.InvestigationReport
import com.bapparis.baguardai.data.model.InvestigationRequest
import com.bapparis.baguardai.data.model.InvestigationResult
import com.bapparis.baguardai.data.model.InvestigationUsage
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

class ApiException(val statusCode: Int, message: String) : IOException(message)

object BaguardApiClient {

    private const val BASE_URL = "https://fallingsurf-00640.app.onebit.ng/"
    private const val INVESTIGATION_ENDPOINT = "api/v1/investigations"
    private const val USAGE_ENDPOINT = "api/v1/usage"

    private const val PREFS_NAME = "baguard_ai_device"
    private const val DEVICE_ID_KEY = "installation_id"

    private val mainHandler = Handler(Looper.getMainLooper())

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .callTimeout(110, TimeUnit.SECONDS)
        .build()

    private fun getDeviceId(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val existingId = prefs.getString(DEVICE_ID_KEY, null)

        if (!existingId.isNullOrBlank()) {
            return existingId
        }

        val newId = UUID.randomUUID().toString()

        prefs.edit()
            .putString(DEVICE_ID_KEY, newId)
            .apply()

        return newId
    }

    fun investigate(
        context: Context,
        content: String,
        callback: (Result<InvestigationResult>) -> Unit
    ) {
        val input = content.trim()

        if (input.length !in 3..10000) {
            callback(
                Result.failure(
                    IllegalArgumentException(
                        "Content must contain between 3 and 10,000 characters."
                    )
                )
            )
            return
        }

        val requestData = InvestigationRequest(
            inputType = classifyInput(input),
            content = input
        )

        val json = JSONObject().apply {
            put("inputType", requestData.inputType)
            put("content", requestData.content)
            put("language", requestData.language)
        }

        val body = json.toString().toRequestBody(
            "application/json; charset=utf-8".toMediaType()
        )

        val request = Request.Builder()
            .url(BASE_URL + INVESTIGATION_ENDPOINT)
            .header("X-Device-ID", getDeviceId(context))
            .post(body)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BAGUARD_NETWORK", "Investigation request failed", e)
                mainHandler.post {
                    callback(
                        Result.failure(
                            IOException(
                                "We couldn't connect to our investigation service. Please try again.",
                                e
                            )
                        )
                    )
                }
            }
override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        val responseText = it.body?.string()
                            ?: throw IOException("The server returned an empty response.")

                        val responseJson = JSONObject(responseText)
if (!it.isSuccessful) {
                            val message = responseJson.optString(
                                "message",
                                when (it.code) {
                                    409 -> "Another investigation is already in progress."
                                    429 -> "Your daily investigation limit has been reached."
                                    502 -> "The AI investigation service is temporarily unavailable."
                                    else -> "Investigation failed. Please try again."
                                }
                            )

                            throw ApiException(it.code, message)
                        }

                        if (!responseJson.optBoolean("success", false)) {
                            throw IOException(
                                responseJson.optString(
                                    "message",
                                    "The investigation was unsuccessful."
                                )
                            )
                        }

                        val reportJson = responseJson.getJSONObject("investigation")
                        val evidenceJson = reportJson.getJSONArray("evidence")

                        val evidence = buildList {
                            for (index in 0 until evidenceJson.length()) {
                                val item = evidenceJson.getJSONObject(index)

                                add(
                                    InvestigationEvidence(
                                        observation = item.getString("observation"),
                                        significance = item.getString("significance")
                                    )
                                )
                            }
                        }

                        val report = InvestigationReport(
                            summary = reportJson.getString("summary"),
                            riskLevel = reportJson.getString("riskLevel"),
                            confidence = reportJson.getString("confidence"),
                            evidence = evidence,
                            alternativeExplanations = parseStringArray(
                                reportJson.getJSONArray("alternativeExplanations")
                            ),
                            recommendedActions = parseStringArray(
                                reportJson.getJSONArray("recommendedActions")
                            ),
                            limitations = parseStringArray(
                                reportJson.getJSONArray("limitations")
                            )
                        )

                        val usageJson = responseJson.getJSONObject("usage")
                        val usage = parseUsage(usageJson)

                        val result = InvestigationResult(
                            report = report,
                            usage = usage
                        )

                        mainHandler.post {
                            callback(Result.success(result))
                        }

                    } catch (e: Exception) {
                        mainHandler.post {
                            callback(Result.failure(e))
                        }
                    }
                }
            }
        })
    }

    fun getUsage(
        context: Context,
        callback: (Result<InvestigationUsage>) -> Unit
    ) {
        val request = Request.Builder()
            .url(BASE_URL + USAGE_ENDPOINT)
            .header("X-Device-ID", getDeviceId(context))
            .get()
            .build()

        httpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BAGUARD_NETWORK", "Usage request failed", e)
                mainHandler.post {
                    callback(
                        Result.failure(
                            IOException(
                                "We couldn't retrieve your investigation allowance. Please try again.",
                                e
                            )
                        )
                    )
                }
            }


            override fun onResponse(call: Call, response: Response) {
                response.use {
                    try {
                        val responseText = it.body?.string()
                            ?: throw IOException("The server returned an empty response.")

                        val responseJson = JSONObject(responseText)

                        if (!it.isSuccessful ||
                            !responseJson.optBoolean("success", false)
                        ) {
                            throw IOException(
                                responseJson.optString(
                                    "message",
                                    "Unable to retrieve usage information."
                                )
                            )
                        }

                        val usageJson = responseJson.getJSONObject("usage")
                        val usage = parseUsage(usageJson)

                        mainHandler.post {
                            callback(Result.success(usage))
                        }

                    } catch (e: Exception) {
                        mainHandler.post {
                            callback(Result.failure(e))
                        }
                    }
                }
            }
        })
    }

    private fun parseUsage(json: JSONObject): InvestigationUsage {
        return InvestigationUsage(
            dailyLimit = json.getInt("dailyLimit"),
            used = json.getInt("used"),
            remaining = json.getInt("remaining"),
            resetAt = json.getString("resetAt")
        )
    }

    private fun classifyInput(content: String): String {
        val standaloneUrl = Regex(
            """(?i)^(https?://|www\.)[^\s]+$"""
        )

        val bareDomain = Regex(
            """(?i)^[a-z0-9-]+(?:\.[a-z0-9-]+)*\.[a-z]{2,}(?:/[^\s]*)?$"""
        )

        return when {
            standaloneUrl.matches(content) -> "link"
            bareDomain.matches(content) -> "link"
            else -> "message"
        }
    }

    private fun parseStringArray(array: org.json.JSONArray): List<String> {
        return buildList {
            for (index in 0 until array.length()) {
                add(array.getString(index))
            }
        }
    }
}









