package local.bryansapps.suprememanager

enum class ProductsEnum(val productID: Int, val productName: String) {
    Cream(1, "Cream"),
    Milk(2, "Milk"),
    Soap(3, "Soap"),
    Cups(4, "Cups"),
    StirSticks(5, "Stir Sticks"),
    Coffee(6, "Coffee"),
    HotChocolate(7, "Hot Chocolate"),
    Equal(8, "Equal"),
    Sugar(9, "Sugar"),
    GreenTea(10, "Green Tea"),
    OrangePekoe(11, "Orange Pekoe"),
    ConeCupCase(12, "Cone Cup Case"),
    CoffeeFilters(-1, "Coffee Filters")
}