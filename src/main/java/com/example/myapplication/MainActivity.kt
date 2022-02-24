package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.*
import android.os.Build
import android.provider.Settings
import android.util.Log


class MainActivity : AppCompatActivity() {
    var btn1: Button? = null
    var btn2: Button? = null
    var txtv1: TextView? = null
    var txt1: EditText? = null
    var location = ""
    val requestcode = 111
    var net_text_data = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtv1 = findViewById(R.id.textInputEditText)
        txt1 = findViewById(R.id.editTextTextMultiLine)
        btn1 = findViewById(R.id.button)
        btn1!!.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), requestcode)
        }
        btn2 = findViewById(R.id.button2)
        btn2!!.setOnClickListener{
            readcsv()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestcode && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d("load", selectedFile.toString())
            if (isExternalStorageDocument(selectedFile!!)){
                val docId = DocumentsContract.getDocumentId(selectedFile)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                val storageDefinition: String
                if ("primary".equals(type, ignoreCase = true)) {
                    location = Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else {
                    storageDefinition = if (Environment.isExternalStorageRemovable()) {
                        "EXTERNAL_STORAGE"
                    } else {
                        "SECONDARY_STORAGE"
                    }
                    location = System.getenv(storageDefinition)!! + "/" + split[1]
                }
            }
            txtv1!!.text = location
        }
    }

    fun readcsv() {
        if (location != "") {
            // If you have access to the external storage, do whatever you need
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                if(!Environment.isExternalStorageManager()){
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }

            val allline = File(location).readLines()
            var str4g1 = ""
            var str4g2 = ""
            var str4g3 = ""
            var str4g4 = ""

            var str3g1 = ""
            var str3g2 = ""
            var str3g3 = ""
            var str3g4 = ""

            var str2g1 = ""
            var str2g2 = ""
            var str2g3 = ""
            var str2g4 = ""

            var mcc = ""

            var mnc1 = ""
            var mnc2 = ""
            var mnc3 = ""
            var mnc4 = ""

            var net_type: String
            var lac: String
            var cid: String

            var last_lac = ""
            var last_cid = ""

            var strsim1= ""
            var strsim2= ""
            var strsim3= ""
            var strsim4= ""
            var strout = ""

            var duplicate = false
            var count = 0
            var alllinez = allline.subList(1,allline.lastIndex)
            for (perline in alllinez) {
                count += 1
                var simnum: Int = 0
                if (perline.isEmpty()) {
                    continue
                }
                if (isLetters(perline)) {
                    continue
                }

                    var splitted = perline.split(",").toTypedArray()
                    mcc = splitted[0]
                    net_type = splitted[9]
                    if (simnum == 0) {
                        if (mnc1.isEmpty()) {
                            println("SIM 1 EMPTYYYYYY")
                            mnc1 = splitted[1]
                            simnum = 1
                        }else if (mnc1.isNotEmpty() and (mnc1 == splitted[1])) {
                            simnum = 1
                        }else if (mnc2.isEmpty()) {
                            mnc2 = splitted[1]
                            simnum = 2
                        }else if (mnc2.isNotEmpty() and (mnc2 == splitted[1])) {
                            simnum = 2
                        }else if (mnc3.isEmpty()) {
                            mnc3 = splitted[1]
                            simnum = 3
                        }else if (mnc3.isNotEmpty() and (mnc3 == splitted[1])) {
                            simnum = 3
                        }else if (mnc4.isEmpty()) {
                            mnc4 = splitted[1]
                            simnum = 4
                        }else if (mnc4.isNotEmpty() and (mnc4 == splitted[1])) {
                            simnum = 4
                        }else{
                            continue
                        }
                    }

                    if ((last_lac == splitted[2]) and (last_cid == splitted[3])) {
                        duplicate = true
                    }

                    if (net_type == "1") {
                        var strbuff: String
                        if (simnum == 1) {
                            strbuff = str4g1
                        } else if (simnum == 2) {
                            strbuff = str4g2
                        } else if (simnum == 3) {
                            strbuff = str4g3
                        } else if (simnum == 4) {
                            strbuff = str4g4
                        } else {
                            continue
                        }

                        var ci = Integer.toHexString((splitted[3].toInt()))
                        cid = ci.toString().takeLast(2).toLong(radix = 16).toString() // int(ci[-2:],16)
                        lac = ci.toString().take(ci.toString().length - 2).toLong(radix = 16).toString() // lac=int(ci[:-2],16)
                        if (strbuff.indexOf(lac)>-1) {
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0,find1) // strbuff.takeLast(find1+lac.length).toString()//strbuff[:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length)
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            strtemp2 = strtemp2.toString() + "/" + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2
                        } else {
                            if (strbuff.length > 0) {
                                strbuff = strbuff + "\n   " + lac + "-" + cid + "\\"
                            } else {
                                strbuff = "4G:" + lac + "-" + cid + "\\"
                            }
                        }

                        if (simnum == 1) {
                            str4g1 = strbuff
                        } else if (simnum == 2) {
                            str4g2 = strbuff
                        } else if (simnum == 3) {
                            str4g3 = strbuff
                        } else if (simnum == 4) {
                            str4g4 = strbuff
                        } else {
                            continue
                        }

                        last_lac = splitted[2]
                        last_cid = splitted[3]
                        println("sim1 =" + str4g1)
                        println()
                        println("sim2 =" + str4g2)
                        println()
                        println("sim3 =" + str4g3)
                    } else if (net_type == "2") {
                        var strbuff: String?
                        if (simnum == 1) {
                            strbuff = str3g1
                        } else if (simnum == 2) {
                            strbuff = str3g2
                        } else if (simnum == 3) {
                            strbuff = str3g3
                        } else if (simnum == 4) {
                            strbuff = str3g4
                        } else {
                            continue
                        }

                        lac = splitted[2]
                        cid = (splitted[3].toInt() % 65536).toString()
                        if (lac in strbuff) {
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // strbuff[:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            strtemp2 = strtemp2.toString() + "/" + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2
                        } else {
                            if (strbuff.length > 0) {
                                strbuff = strbuff + "\n   " + lac + "-" + cid + "\\"
                            } else {
                                strbuff = strbuff + "3G:" + lac + "-" + cid + "\\"
                            }
                        }

                        if (simnum == 1) {
                            str3g1 = strbuff
                        } else if (simnum == 2) {
                            str3g2 = strbuff
                        } else if (simnum == 3) {
                            str3g3 = strbuff
                        } else if (simnum == 4) {
                            str3g4 = strbuff
                        } else {
                            continue
                        }

                        last_lac = splitted[2]
                        last_cid = splitted[3]
                    } else if (net_type == "3") {
                        var strbuff: String?
                        if (simnum == 1) {
                            strbuff = str2g1
                        } else if (simnum == 2) {
                            strbuff = str2g2
                        } else if (simnum == 3) {
                            strbuff = str2g3
                        } else if (simnum == 4) {
                            strbuff = str2g4
                        } else {
                            continue
                        }

                        lac = splitted[2]
                        cid = splitted[3]
                        if (lac in strbuff) {
                            var find1 = strbuff.indexOf(lac)
                            var strbackupf = strbuff.subSequence(0, find1) // [:find1]
                            var strtemp = strbuff.subSequence(find1, strbuff.length) // [find1:]
                            var find2 = strtemp.indexOf("\\")
                            var strtemp2 = strtemp.subSequence(0, find2) // [:find2]
                            // strbackupe = strtemp[find2:]
                            strtemp2 = strtemp2.toString() + "/" + cid + "\\"
                            strbuff = strbackupf.toString() + strtemp2 // + strbackupe
                        } else {
                            if (strbuff.length > 0) {
                                strbuff = strbuff + "\n   " + lac + "-" + cid + "\\"
                            } else {
                                strbuff = strbuff + "2G:" + lac + "-" + cid + "\\"
                            }
                        }

                        if (simnum == 1) {
                            str2g1 = strbuff
                        } else if (simnum == 2) {
                            str2g2 = strbuff
                        } else if (simnum == 3) {
                            str2g3 = strbuff
                        } else if (simnum == 4) {
                            str2g4 = strbuff
                        } else {
                            continue
                        }

                        last_lac = splitted[2]
                        last_cid = splitted[3]
                    }


            }

            if (mnc1.isNotEmpty()) {
                strsim1 = str4g1 + "\n" + str3g1 + "\n" + str2g1 + "\n"
                strsim1 = strsim1.replace("\\", "")
                if (mnc1 == "01") {
                    strout += "INDOSAT\n"
                } else if (mnc1 == "10") {
                    strout += "TELKOMSEL\n"
                } else if (mnc1 == "11") {
                    strout += "XL\n"
                } else if (mnc1 == "89") {
                    strout += "THREE\n"
                }
                strout = strout + strsim1
            }
            if (mnc2.length > 0) {
                strsim2 = str4g2 + "\n" + str3g2 + "\n" + str2g2 + "\n"
                strsim2 = strsim2.replace("\\", "")
                if (mnc2 == "01") {
                    strout += "INDOSAT\n"
                } else if (mnc2 == "10") {
                    strout += "TELKOMSEL\n"
                } else if (mnc2 == "11") {
                    strout += "XL\n"
                } else if (mnc2 == "89") {
                    strout += "THREE\n"
                }
                strout = strout + strsim2
            }
            if (mnc3.length > 0) {
                strsim3 = str4g3 + "\n" + str3g3 + "\n" + str2g3 + "\n"
                strsim3 = strsim3.replace("\\", "")
                if (mnc3 == "01") {
                    strout += "INDOSAT\n"
                } else if (mnc3 == "10") {
                    strout += "TELKOMSEL\n"
                } else if (mnc3 == "11") {
                    strout += "XL\n"
                } else if (mnc3 == "89") {
                    strout += "THREE\n"
                }
                strout = strout + strsim3
            }
            if (mnc4.length > 0) {
                strsim4 = str4g4 + "\n" + str3g4 + "\n" + str2g4 + "\n"
                strsim4 = strsim4.replace("\\", "")
                if (mnc4 == "01") {
                    strout += "INDOSAT\n"
                } else if (mnc4 == "10") {
                    strout += "TELKOMSEL\n"
                } else if (mnc4 == "11") {
                    strout += "XL\n"
                } else if (mnc4 == "89") {
                    strout += "THREE\n"
                }
                strout = strout + strsim4
            }
            if (duplicate) {
                strout = strout + "!!!Ada duplikasi data!!!"
            }
            strout = strout.trim()
            txt1?.setText(strout)
        }
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isLetters(string: String): Boolean {
        return string.matches("^[a-zA-Z]*$".toRegex())
    }

    fun String.onlyLetters() = all { it.isLetter() }
}