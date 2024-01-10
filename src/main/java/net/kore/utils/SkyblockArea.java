package net.kore.utils;

public enum SkyblockArea {
    DUNGEON("Catacombs"),
    PRIVATE_ISLAND("Private Island"),
    DUNGEON_HUB("Dungeon Hub"),
    GOLD_MINE("Gold Mine"),
    DEEP_CAVERNS("Deep Caverns"),
    DWARVEN_MINES("Dwarven Mines"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    SPIDERS_DEN("Spider's Den"),
    CRIMSON_ISLE("Crimson Isle"),
    END("The End"),
    PARK("The Park"),
    FARMING_ISLANDS("The Farming Islands"),
    KUUDRA("Instanced"),
    HUB("Hub"),
    GARDEN("Garden"),
    RIFT("The Rift");

    public String areaName;

    public String getAreaName()
    {
        return areaName;
    }

    SkyblockArea(String name)
    {
        this.areaName = name;
    }

    public static SkyblockArea getArea(String name)
    {
        for (SkyblockArea area : SkyblockArea.values())
        {
            if (area.getAreaName() == name)
                return area;
        }

        return null;
    }
}

