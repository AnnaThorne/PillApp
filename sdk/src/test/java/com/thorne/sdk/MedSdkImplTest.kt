package com.thorne.sdk

import android.content.Context
import com.thorne.sdk.meds.Medication
import com.thorne.sdk.meds.MedicationImpl
import com.thorne.sdk.storage.MedicationStorageManagerImpl
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`



class MedSdkImplTest {

    private lateinit var medSdkImpl: MedSdkImpl
    private lateinit var mockMedication: Medication
    private lateinit var mockStorageManager: MedicationStorageManagerImpl
    private lateinit var mockContext: Context


@Before
fun setUp() {
    mockMedication = mockk<MedicationImpl>()
    mockStorageManager = mock(MedicationStorageManagerImpl::class.java)
    mockContext = mock(Context::class.java)
    medSdkImpl = MedSdkImpl.getInstance() // pass mockStorageManager to the constructor
    medSdkImpl.initialize(mockContext)
}

    @Test
    fun testIsInitialized() {
        assertTrue(medSdkImpl.isInitialized())
    }

    @Test
    fun testAddMedication() {
        medSdkImpl.addMedication(mockMedication)
        verify(mockStorageManager).saveToStorage(any(), any())
    }

    @Test
    fun testRemoveMedication() {
        medSdkImpl.removeMedication(mockMedication)
        verify(mockStorageManager).saveToStorage(any(), any())
    }

    @Test
    fun testRemoveMedicationById() {
        `when`(mockMedication.getId()).thenReturn("testId")
        medSdkImpl.addMedication(mockMedication)
        medSdkImpl.removeMedication("testId")
        verify(mockStorageManager, times(2)).saveToStorage(any(), any())
    }

    @Test
    fun testGetMedicationById() {
        `when`(mockMedication.getId()).thenReturn("testId")
        medSdkImpl.addMedication(mockMedication)
        assertEquals(mockMedication, medSdkImpl.getMedicationById("testId"))
    }

    @Test
    fun testGetMedicationList() {
        medSdkImpl.addMedication(mockMedication)
        assertEquals(1, medSdkImpl.getMedicationList().size)
    }

    @Test
    fun testGetSdkVersion() {
        assertEquals("1.0.0", medSdkImpl.getSdkVersion())
    }
}