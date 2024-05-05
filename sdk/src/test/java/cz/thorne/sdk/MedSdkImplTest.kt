package cz.thorne.sdk

import android.content.Context
import android.test.mock.MockContext
import cz.thorne.sdk.meds.Medication
import cz.thorne.sdk.meds.MedicationImpl
import cz.thorne.sdk.storage.MedicationStorageManagerImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class MedSdkImplTest {

    private lateinit var medSdkImpl: MedSdkImpl
    private lateinit var mockMedication: MedicationImpl
    private lateinit var mockStorageManager: MedicationStorageManagerImpl
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        // Mock Medication
        mockMedication = mockk<MedicationImpl>()
        every { mockMedication.getId() } returns "testId"

        // Mock StorageManager
        mockStorageManager = mockk<MedicationStorageManagerImpl>()
        every { mockStorageManager.isEmpty(any()) } returns true
        every { mockStorageManager.loadFromStorage(any()) } returns ArrayList()
        every { mockStorageManager.saveToStorage(any(), any()) } returns Unit

        // Mock Context
        mockContext = mockk<MockContext>()
        every { mockContext.applicationContext } returns mockContext

        // Create MedSdkImpl instance
        medSdkImpl = spyk(MedSdkImpl.getInstance())
        medSdkImpl.setCustomStorageManager(mockStorageManager)
        medSdkImpl.initialize(mockContext)
    }

    @Test
    fun testIsInitialized() {
        assertTrue(medSdkImpl.isInitialized())
    }

    @Test
    fun testAddMedication() {
        // GIVEN
        // SDK is initialized
        assertTrue(medSdkImpl.isInitialized())

        // WHEN
        // new medication is added
        medSdkImpl.addMedication(mockMedication)

        // THEN
        // medication shall be saved to storage
        val argCapture = slot<ArrayList<Medication>>()
        verify(exactly = 1) { mockStorageManager.saveToStorage(any(),capture(argCapture) )}
        assertTrue(argCapture.captured.contains(mockMedication))
    }

    @Test
    fun testRemoveMedication() {
        medSdkImpl.removeMedication(mockMedication)
        verify(exactly = 1) { mockStorageManager.saveToStorage(any(), any()) }
    }

    @Test
    fun testRemoveMedicationById() {
        medSdkImpl.removeMedication("testId")
        verify(exactly = 1) { mockStorageManager.saveToStorage(any(), any()) }
    }

    @Test
    fun testGetMedicationById() {
        // get medication from in-memory buffer
        assertTrue(medSdkImpl.getMedicationById("testId") is Medication)
        verify(exactly = 0) { mockStorageManager.saveToStorage(any(), any()) }
    }

    @Test
    fun testGetMedicationList() {
        // get medication list from in-memory buffer
        assertEquals(ArrayList<Medication>()::class, medSdkImpl.getMedicationList()::class)
        verify(exactly = 0) { mockStorageManager.saveToStorage(any(), any()) }
    }

    @Test
    fun testGetSdkVersion() {
        assertEquals("1.0.2", medSdkImpl.getSdkVersion())
    }
}