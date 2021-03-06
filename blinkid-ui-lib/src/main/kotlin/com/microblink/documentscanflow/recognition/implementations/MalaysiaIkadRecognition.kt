package com.microblink.documentscanflow.recognition.implementations

import com.microblink.documentscanflow.R
import com.microblink.documentscanflow.isEmpty
import com.microblink.documentscanflow.recognition.BaseRecognition
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.blinkid.malaysia.IkadRecognizer

class MalaysiaIkadRecognition : BaseRecognition() {

    val recognizer by lazy { IkadRecognizer() }

    override fun getSingleSideRecognizers(): List<Recognizer<*, *>> {
        return listOf(recognizer)
    }

    override fun extractData(): String? {
        val result = recognizer.result
        if (result.isEmpty()) {
            return null
        }

        add(R.string.keyFullName, result.name)
        add(R.string.keyAddress, result.address)
        add(R.string.keyDateOfBirth, result.dateOfBirth)
        add(R.string.keyEmployer, result.employer)
        addDateOfExpiry(result.expiryDate)
        add(R.string.keyNationality, result.nationality)
        add(R.string.keyPassportNumber, result.passportNumber)
        add(R.string.keySector, result.sector)
        add(R.string.keySex, result.sex)
        
        return result.name
    }

}