package com.sample.pdfgeneratorapp

import android.content.pm.PackageManager
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.sample.pdfgeneratorapp.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
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
            usersTable.addCell(cellCenterHead("User id"))
            usersTable.addCell(cellCenterHead("User name"))
            usersTable.addCell(cellCenterHead("User age"))

            usersList.forEach {
                usersTable.addCell(cellCenter(it.id))
                usersTable.addCell(cellCenter(it.name))
                usersTable.addCell(cellCenter(it.age.toString()))
            }

            //Adding the table
            mDoc.add(usersTable)

            mDoc.add(Paragraph("\n\n"))

            //Adding image
            mDoc.add(imagePdf(R.drawable.logo, CompressFormat.PNG))

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


    private fun cellCenterHead(text: String): PdfPCell {
        val paragraph = Paragraph(text)
        val cell = PdfPCell(paragraph)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_CENTER
        cell.backgroundColor = BaseColor.PINK
        return cell
    }

    private fun cellCenter(text: String): PdfPCell {
        val paragraph = Paragraph(text)
        val cell = PdfPCell(paragraph)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_CENTER
        return cell
    }

    private fun imagePdf(
        imageSrc: Int, compressFormat: CompressFormat
    ): Image? {
        val bm = BitmapFactory.decodeResource(resources, imageSrc)
        val stream = ByteArrayOutputStream()
        bm.compress(compressFormat, 100, stream)
        var img: Image? = null
        val byteArray: ByteArray = stream.toByteArray()
        try {
            img = Image.getInstance(byteArray)
        } catch (ex: BadElementException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return img
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