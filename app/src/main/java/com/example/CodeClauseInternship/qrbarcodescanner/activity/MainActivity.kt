package com.example.CodeClauseInternship.qrbarcodescanner.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.CodeClauseInternship.qrbarcodescanner.R
import com.example.CodeClauseInternship.qrbarcodescanner.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkRunTimePermission()
        }

        binding.llBarCodeScanner.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA,), 11)
            }  else {
                startScanner()
            }

        }

        binding.llQrCodeScanner.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA,), 11)
            }  else {
                val intent = Intent(this, QrCodeScannerActivity::class.java)
                startActivity(intent)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES)
        requestPermissions(permissionArrays, 11111)
    }


    private fun startScanner() {
        IntentIntegrator(this).apply {
            setOrientationLocked(true)
            initiateScan()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data)?.let { result ->
            result.contents?.let { barcode ->
                val intent = Intent(this, ScreenerDetailsActivity::class.java)
                intent.putExtra("key","Barcode")
                intent.putExtra("name", barcode)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.exit_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        val btnNo = dialog.findViewById<TextView>(R.id.ext_btn_no)
        val btnYes = dialog.findViewById<TextView>(R.id.ext_btn_yes)

        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        btnYes.setOnClickListener {
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }
}