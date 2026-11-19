package com.kapav.ridemode

import com.kapav.ridemode.data.isCallablePhoneNumber
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CallHistoryPolicyTest {
    @Test fun onlyOrdinaryDisplayedNumbersOfferDirectCalling() {
        assertTrue(isCallablePhoneNumber("+91 98765 43210"))
        assertFalse(isCallablePhoneNumber("*21*123#"))
        assertFalse(isCallablePhoneNumber("Private number"))
        assertFalse(isCallablePhoneNumber(""))
    }
}
