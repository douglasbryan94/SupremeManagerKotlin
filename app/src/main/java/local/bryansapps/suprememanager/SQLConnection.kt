package local.bryansapps.suprememanager

import com.google.gson.Gson
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SQLConnection {
    companion object {
        private const val connectionString =
                #REMOVED FOR PUBLIC REPO#

        fun addToInventory(product: ProductsEnum, quantity: Int) {
            val altThread = Thread {
                try {
                    val conn = DriverManager.getConnection(connectionString)
                    val ps = conn.prepareStatement(
                            ("UPDATE INVENTORY SET Quantity = (SELECT Quantity FROM INVENTORY WHERE ProductID = " + product.productID +
                                    ") + (SELECT " + quantity + ") WHERE ProductID = " + product.productID)
                    )
                    // call executeUpdate to execute our sql update statement
                    ps.executeUpdate()
                    ps.close()
                    conn.close()
                } catch (ex: SQLException) {
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }
        }

        fun getAllInventoryValues(): Map<ProductsEnum, Int> {
            val tempInvMap = mutableMapOf<ProductsEnum, Int>()

            val altThread = Thread {
                try {
                    val conn = DriverManager.getConnection(connectionString)
                    val result = conn.createStatement()
                            .executeQuery("SELECT p.ProductName, i.Quantity FROM INVENTORY i, PRODUCTS p WHERE i.ProductID = p.ProductID ORDER BY i.ProductID ASC")
                    while (result.next()) {
                        tempInvMap[ProductsEnum.valueOf(result.getString("ProductName"))] = result.getInt("Quantity")
                    }
                    conn.close()
                } catch (ex: SQLException) {
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }

            return tempInvMap.toMap()
        }

        fun getMostRecentCountByLocation(locationID: Int, date: LocalDate): Map<ProductsEnum, Int> {
            val gson = Gson()
            var encodedData: String
            var inventoryCount: InventoryCount? = null
            val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val altThread = Thread {
                try {
                    val conn = DriverManager.getConnection(connectionString)
                    val result = conn.createStatement().executeQuery(
                            "SELECT CountData FROM INVENTORYCOUNTS WHERE InventoryCountDate = '$dateString' AND LocationID = $locationID"
                    )
                    while (result.next()) {
                        encodedData = result.getString("CountData")
                        inventoryCount = gson.fromJson(encodedData, InventoryCount::class.java)
                    }
                    conn.close()
                } catch (ex: SQLException) {
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }

            return inventoryCount!!.productCount
        }

        fun getMostRecentCountSubmissionForLocation(locationID: Int): String {
            var submissionDate = ""

            val altThread = Thread {
                try {
                    val conn = DriverManager.getConnection(connectionString)
                    val result = conn.createStatement().executeQuery(
                            "SELECT TOP 1 InventoryCountDate FROM INVENTORYCOUNTS WHERE LocationID = $locationID ORDER BY InventoryCountDate DESC"
                    )
                    while (result.next()) {
                        submissionDate = result.getString("InventoryCountDate")
                    }
                    conn.close()
                } catch (ex: SQLException) {
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }

            return submissionDate
        }

        fun removeFromInventory(sales: Map<ProductsEnum, Int>) {
            val altThread = Thread {
                if (sales.count() > 0) {
                    val conn = DriverManager.getConnection(connectionString)
                    var ps: PreparedStatement? = null

                    for ((k, v) in sales) {
                        ps = conn.prepareStatement(
                                ("UPDATE INVENTORY SET Quantity = (SELECT Quantity FROM INVENTORY WHERE ProductID = ${k.productID}) - (SELECT $v) WHERE ProductID = ${k.productID}")
                        )
                        // call executeUpdate to execute our sql update statement
                        ps.executeUpdate()
                    }
                    ps!!.close()
                    conn.close()
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }
        }

        fun getAllLevelValues(): Map<LocationsEnum, Map<ProductsEnum, Int>> {
            val levels = mutableMapOf<LocationsEnum, MutableMap<ProductsEnum, Int>>()

            val locationsArray = LocationsEnum.values()
            val productsArray = ProductsEnum.values()

            for (location in locationsArray) {
                levels[location] = mutableMapOf()
            }

            val altThread = Thread {
                try {
                    val conn = DriverManager.getConnection(connectionString)
                    val result = conn.createStatement()
                            .executeQuery("SELECT LocationID, ProductID, ProductLevel FROM PRODUCTLEVELS")
                    while (result.next()) {
                        var levelLocation: LocationsEnum? = null
                        var levelProduct: ProductsEnum? = null
                        val levelQuantity = result.getInt("ProductLevel")

                        for (location in locationsArray) {
                            if (location.locationCode == result.getInt("LocationID")) {
                                levelLocation = location
                                break
                            }
                        }

                        for (product in productsArray) {
                            if (product.productID == result.getInt("ProductID")) {
                                levelProduct = product
                                break
                            }
                        }

                        levels[levelLocation]!![levelProduct!!] = levelQuantity
                    }
                    conn.close()
                } catch (ex: SQLException) {
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }

            return levels.toMap()
        }

        fun getCountSubmissionsForToday(): MutableMap<LocationsEnum, Boolean> {
            val locationsArr = LocationsEnum.values()
            val data: LinkedList<Int> = LinkedList()
            val results: MutableMap<LocationsEnum, Boolean> = mutableMapOf()

            val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val altThread = Thread {
                try {
                    val conn = DriverManager.getConnection(connectionString)
                    val query =
                            "SELECT LocationID FROM INVENTORYCOUNTS WHERE InventoryCountDate = '$date'"
                    val result = conn.createStatement().executeQuery(query)
                    while (result.next()) {
                        data.add(result.getInt("LocationID"))
                    }
                    conn.close()
                } catch (ex: SQLException) {
                }
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }

            for (location in locationsArr) {
                results[location] = data.contains(location.locationCode)
            }

            return results
        }

        fun submitCountToDatabase(count: InventoryCount) {
            val altThread = Thread {
                val gson = Gson()

                val json: String = gson.toJson(count)
                val queryString =
                        "INSERT INTO INVENTORYCOUNTS VALUES ('${count.date}', ${count.countLocation.locationCode}, '$json')"
                val conn = DriverManager.getConnection(connectionString)
                conn.createStatement().execute(queryString)
                conn.close()
            }
            altThread.start()
            try {
                altThread.join()
            } catch (ex: InterruptedException) {
            }
        }
    }
}