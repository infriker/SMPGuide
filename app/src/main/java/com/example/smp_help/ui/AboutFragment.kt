package com.example.smp_help.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.smp_help.R
import com.example.smp_help.databinding.FragmentAboutBinding
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    private var downloadId: Long = -1
    private var downloadReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val versionName = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName
        binding.versionText.text = getString(R.string.about_version, versionName)
        binding.githubLink.setOnClickListener {
            val url = getString(R.string.github_url)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        binding.checkUpdateButton.setOnClickListener {
            checkForUpdates()
        }
    }

    private fun checkForUpdates() {
        binding.checkUpdateButton.isEnabled = false
        binding.updateStatusText.text = getString(R.string.update_checking)
        binding.updateProgressBar.visibility = View.VISIBLE

        Thread {
            try {
                val url = URL("https://api.github.com/repos/infriker/SMPGuide/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    val tagName = json.getString("tag_name")
                    val latestVersion = tagName.removePrefix("v")

                    var apkUrl: String? = null
                    val assets = json.getJSONArray("assets")
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        if (asset.getString("name").endsWith(".apk")) {
                            apkUrl = asset.getString("browser_download_url")
                            break
                        }
                    }

                    val currentVersion = requireContext().packageManager
                        .getPackageInfo(requireContext().packageName, 0).versionName

                    activity?.runOnUiThread {
                        binding.updateProgressBar.visibility = View.GONE
                        if (isVersionNewer(latestVersion, currentVersion ?: "0.0.0")) {
                            if (apkUrl != null) {
                                binding.updateStatusText.text =
                                    getString(R.string.update_available, latestVersion)
                                startDownload(apkUrl, latestVersion)
                            } else {
                                binding.updateStatusText.text =
                                    getString(R.string.update_available, latestVersion)
                                binding.checkUpdateButton.isEnabled = true
                                Toast.makeText(context, R.string.update_no_apk, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            binding.updateStatusText.text = getString(R.string.update_up_to_date)
                            binding.checkUpdateButton.isEnabled = true
                        }
                    }
                } else {
                    showError()
                }
                connection.disconnect()
            } catch (e: Exception) {
                showError()
            }
        }.start()
    }

    private fun showError() {
        activity?.runOnUiThread {
            binding.updateProgressBar.visibility = View.GONE
            binding.updateStatusText.text = getString(R.string.update_error)
            binding.checkUpdateButton.isEnabled = true
        }
    }

    private fun isVersionNewer(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLen = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    private fun startDownload(apkUrl: String, version: String) {
        binding.updateStatusText.text = getString(R.string.update_downloading)
        binding.updateProgressBar.visibility = View.VISIBLE

        val fileName = "SMPGuide-$version.apk"
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle(getString(R.string.app_name))
            .setDescription(getString(R.string.update_downloading))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                requireContext(), Environment.DIRECTORY_DOWNLOADS, fileName
            )
            .setAllowedOverMetered(true)

        val dm = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = dm.enqueue(request)

        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    activity?.runOnUiThread {
                        binding.updateProgressBar.visibility = View.GONE
                        binding.updateStatusText.text = ""
                        binding.checkUpdateButton.isEnabled = true
                        installApk(fileName)
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            requireContext().registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
    }

    private fun installApk(fileName: String) {
        val file = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName
        )
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        downloadReceiver?.let {
            try { requireContext().unregisterReceiver(it) } catch (_: Exception) { }
        }
        _binding = null
    }
}
