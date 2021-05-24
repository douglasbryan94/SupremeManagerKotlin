package local.bryansapps.suprememanager

import java.text.SimpleDateFormat
import java.util.*

data class InventoryCount(val countLocation: LocationsEnum, val countData: Map<ProductsEnum, Int>) {
    val date: String = SimpleDateFormat("yyy-MM-dd", Locale.CANADA).format(Date())
    val productCount: Map<ProductsEnum, Int> = countData
}