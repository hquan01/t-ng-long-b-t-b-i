package com.example.data.database

import androidx.room.TypeConverter
import com.example.model.CropType
import com.example.model.EvilLevel
import com.example.model.GuildQuestType
import com.example.model.MineMap
import com.example.model.SectType
import com.example.model.TargetOre

class Converters {
    @TypeConverter
    fun fromCropType(value: CropType?): String? = value?.name

    @TypeConverter
    fun toCropType(value: String?): CropType? = value?.let { enumValueOf<CropType>(it) }

    @TypeConverter
    fun fromGuildQuestType(value: GuildQuestType?): String? = value?.name

    @TypeConverter
    fun toGuildQuestType(value: String?): GuildQuestType? = value?.let { enumValueOf<GuildQuestType>(it) }

    @TypeConverter
    fun fromMineMap(value: MineMap?): String? = value?.name

    @TypeConverter
    fun toMineMap(value: String?): MineMap? = value?.let { enumValueOf<MineMap>(it) }

    @TypeConverter
    fun fromTargetOre(value: TargetOre?): String? = value?.name

    @TypeConverter
    fun toTargetOre(value: String?): TargetOre? = value?.let { enumValueOf<TargetOre>(it) }

    @TypeConverter
    fun fromEvilLevel(value: EvilLevel?): String? = value?.name

    @TypeConverter
    fun toEvilLevel(value: String?): EvilLevel? = value?.let { enumValueOf<EvilLevel>(it) }

    @TypeConverter
    fun fromSectType(value: SectType?): String? = value?.name

    @TypeConverter
    fun toSectType(value: String?): SectType? = value?.let { enumValueOf<SectType>(it) }
}
