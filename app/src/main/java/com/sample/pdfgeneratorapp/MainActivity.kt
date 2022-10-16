package com.sample.pdfgeneratorapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.sample.pdfgeneratorapp.databinding.ActivityMainBinding
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    //Binding
    private lateinit var binding: ActivityMainBinding

    private val storageCode = 1001

    private val usersList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Adding users
        usersList.add(User("1", "Amine", 18))
        usersList.add(User("2", "Ahmed", 19))
        usersList.add(User("3", "Samir", 45))


        //Get permission and generate the PDF
        binding.btnGeneratePdf.setOnClickListener {
            //Check the permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, storageCode)
                } else {
                    //Generate the PDF
                    savePDF()
                }
            } else {
                //Generate the PDF
                savePDF()
            }
        }


    }

    private fun savePDF() {
        val mDoc = Document()

        //Generate unique file name
        val mFileName = SimpleDateFormat("yyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())

        //PDF save path
        val mFilePath =
            Environment.getExternalStorageDirectory().toString() + "/" + mFileName + ".pdf"

        //
        try {

            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))

            //Open
            mDoc.open()

            //PDF file content
            val data = binding.edtPdfData.text.toString().trim()
            mDoc.addAuthor("${R.string.app_name}")
            mDoc.add(
                Paragraph(
                    data,
                    FontFactory.getFont("arial", 25f, Font.BOLD, BaseColor.BLUE)
                )
            )
            mDoc.add(Paragraph("\n\n"))

            val usersTable = PdfPTable(3)
            usersTable.addCell("USER ID")
            usersTable.addCell("USER NAME")
            usersTable.addCell("USER AGE")

            usersList.forEach {
                usersTable.addCell(it.id)
                usersTable.addCell(it.name)
                usersTable.addCell(it.age.toString())
            }

            //Adding the table
            mDoc.add(usersTable)

            //Close
            mDoc.close()
            "$mFileName.pdf\n is create to \n$mFilePath".shortToast()

        } catch (ex: Exception) {
            ex.message.toString().shortSnackBar()
        }
    }

    //Request the permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            storageCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    savePDF()
                } else {
                    "Permission denied!".shortSnackBar()
                }
            }
        }
    }

    //Short SnackBar
    private fun String.shortSnackBar() {
        Snackbar.make(binding.root, this, Snackbar.LENGTH_SHORT).show()
    }

    //Short Toast
    private fun String.shortToast() {
        Toast.makeText(this@MainActivity, this, Toast.LENGTH_SHORT).show()
    }
}

data class User(
    val id: String,
    val name: String,
    val age: Int
)