package local.bryansapps.suprememanager

enum class LocationsEnum(val locationCode: Int, val prefsFileName: String) {
    CorpDown(1, "CorpDownInput"),
    CorpUp(2, "CorpUpInput"),
    CorpShip(3, "CorpShipInput"),
    CorpKpo(4, "CorpKpoInput"),
    CorpPlant(5, "CorpPlantInput"),
    Solutions(6, "SolutionsInput")
}