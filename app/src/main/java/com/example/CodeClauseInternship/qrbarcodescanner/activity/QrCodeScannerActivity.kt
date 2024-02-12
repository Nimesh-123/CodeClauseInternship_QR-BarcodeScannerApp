package com.example.CodeClauseInternship.qrbarcodescanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.example.CodeClauseInternship.qrbarcodescanner.R
import com.example.CodeClauseInternship.qrbarcodescanner.databinding.ActivityQrCodeScannerBinding
import com.google.zxing.Result

class QrCodeScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrCodeScannerBinding

    var mCodeScanner: CodeScanner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mCodeScanner = CodeScanner(this, binding.scannerView)

        mCodeScanner!!.decodeCallback = DecodeCallback { result: Result ->
            val intent = Intent(this, ScreenerDetailsActivity::class.java)
            intent.putExtra("key","QrCode")
            intent.putExtra("name", result.text.toString())
            startActivity(intent)
        }
        binding.scannerView.setOnClickListener { view: View? -> mCodeScanner!!.startPreview() }
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner!!.startPreview()
    }

    override fun onPause() {
        mCodeScanner!!.releaseResources()
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}