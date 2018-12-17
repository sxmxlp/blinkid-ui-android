package com.microblink.documentscanflow.recognition.implementations

import com.microblink.documentscanflow.R
import com.microblink.documentscanflow.isNotEmpty
import com.microblink.documentscanflow.recognition.BaseTwoSideRecognition
import com.microblink.documentscanflow.recognition.ResultValidator
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.blinkid.malaysia.MalaysiaMyKadBackRecognizer
import com.microblink.entities.recognizers.blinkid.malaysia.MalaysiaMyKasFrontRecognizer

class MalaysiaKasRecognition: BaseTwoSideRecognition() {

    val frontRecognizer by lazy { MalaysiaMyKasFrontRecognizer() }
    val backRecognizer by lazy { MalaysiaMyKadBackRecognizer() }

    val frontResult by lazy { frontRecognizer.result }
    val backResult by lazy { backRecognizer.result }

    override fun createValidator() = ResultValidator()

    override fun extractFields() {
        if (frontResult.isNotEmpty()) {
            extractFrontSide()
        }
        if (backResult.isNotEmpty()) {
            extractBackSide()
        }
    }

    private fun extractFrontSide() {
        add(R.string.keyFullName, frontResult.fullName)
        add(R.string.keyNricNumber, frontResult.nric)
        add(R.string.keyDateOfBirth, frontResult.birthDate)
        add(R.string.keyAddress, frontResult.fullAddress)
        add(R.string.keySex, frontResult.sex)
        add(R.string.keyReligion, frontResult.religion)
    }

    private fun extractBackSide() {
        add(R.string.keyNricNumber, backResult.nric)
        add(R.string.keyExtendedNricNumber, backResult.extendedNric)
        add(R.string.keyDateOfBirth, backResult.dateOfBirth)
        add(R.string.keySex, backResult.sex)
    }

    override fun getResultTitle(): String? {
        if (frontResult.isNotEmpty()) {
            return frontResult.fullName
        } else if (backResult.isNotEmpty()) {
            return backResult.nric
        }
        return null
    }

    override fun getSingleSideRecognizers(): List<Recognizer<*, *>> = listOf(frontRecognizer, backRecognizer)

}