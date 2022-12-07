package maulik.barcodescanner

interface OnScannerResponse {
    fun onScan(isSuccess: Boolean, value: String)
}