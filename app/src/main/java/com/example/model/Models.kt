package com.example.model

enum class TaskCategory(val displayName: String, val description: String) {
    GUILD_QUEST("Nhiệm Vụ Bang", "Tự động làm chuỗi Hội Vụ, Vận Tiêu, Tuần Tra, Luyện Công"),
    FARMING("Trồng Trọt", "Tự canh tác, tưới tiêu, bón phân và thu hoạch dược liệu"),
    MINING("Đào Khoáng", "Khai thác khoáng thạch tại các bản đồ, né PK & quái vật"),
    PUNISH_EVIL("Nhiệm Vụ Trừng Ác", "Săn lùng ác nhân, dùng Trừng Ác Lệnh, nhặt bảo rương")
}

enum class BotStatus(val label: String) {
    IDLE("Đang Chờ"),
    RUNNING("Đang Chạy Auto"),
    PAUSED("Đang Tạm Dừng"),
    COMPLETED("Đã Hoàn Thành"),
    ERROR("Gặp Sự Cố")
}

enum class GuildQuestType(val title: String, val expGain: Int, val devGain: Int) {
    HOI_VU("Hội Vụ Bang Hội", 12000, 150),
    VAN_TIEU("Vận Tiêu Bang", 25000, 300),
    TUAN_TRA("Tuần Tra Lãnh Địa", 15000, 180),
    CUU_TRO("Cứu Trợ Bang", 10000, 120),
    LUYEN_CONG("Luyện Công Bang", 20000, 250)
}

enum class CropType(
    val cropName: String,
    val growthTimeSeconds: Int,
    val harvestYield: Int,
    val expPerHarvest: Int,
    val iconResName: String
) {
    LINH_CHI("Linh Chi Ngàn Năm", 60, 4, 800, "ic_herb"),
    NHAN_SAM("Nhân Sâm Cổ Thụ", 120, 3, 1500, "ic_ginseng"),
    TUYET_LIEN("Thiên Sơn Tuyết Liên", 180, 2, 2400, "ic_lotus"),
    HOANG_TINH("Hoàng Tinh Thảo", 45, 5, 500, "ic_herb"),
    CHU_QUA("Hỏa Diệm Chu Quả", 90, 3, 1100, "ic_fruit")
}

enum class MineMap(val mapName: String, val recommendedLevel: Int, val dangerLevel: String) {
    DON_HOANG("Đôn Hoàng (Mỏ Tân Thủ)", 20, "An Toàn - Hòa Bình"),
    VO_LUONG_SON("Vô Lượng Sơn", 30, "An Toàn - Hòa Bình"),
    NHI_HAI("Nhĩ Hải - Thảo Nguyên", 40, "An Toàn - Tự Nhiên"),
    THAI_SON("Thái Sơn Đỉnh", 50, "Trung Bình - Quái Tinh Anh"),
    THIEN_LONG_DONG("Thiên Long Động", 60, "Trung Bình - Quái Đông"),
    YEN_VUONG_CO_MO("Yến Vương Cổ Mộ", 70, "Nguy Hiểm - Cho Phép PK"),
    CON_LON_SON("Côn Lôn Sơn (Cao Cấp)", 75, "Nguy Hiểm - Cho Phép PK"),
    HAC_MOC_NHAI("Hắc Mộc Nhai", 80, "Cực Độ Nguy Hiểm - PK Tự Do"),
    KINH_HO("Kính Hồ (Mỏ Thần Bí)", 85, "Cực Độ Nguy Hiểm - PK Tranh Đoạt")
}

enum class TargetOre(val oreName: String, val rarity: String, val sellPrice: Int) {
    THIET_KHOANG("Thiết Khoáng", "Thường", 50),
    HUYEN_THIET("Huyền Thiết Thô", "Hiếm", 250),
    TU_TINH_THACH("Tử Tinh Thạch", "Cực Phẩm", 800),
    HOANG_KIM_KHOANG("Hoàng Kim Khoáng", "Thần Phẩm", 2000),
    THAN_BI_TINH_THACH("Thần Bí Tinh Thạch", "Vô Giá", 5000)
}

enum class EvilLevel(val levelName: String, val levelReq: Int, val rewardDesc: String) {
    CAP_30_40("Ác Nhân Cấp 30 - 40", 30, "Rương Đồng, 5 vạn Exp"),
    CAP_50_60("Ác Nhân Cấp 50 - 60", 50, "Rương Bạc, 15 vạn Exp"),
    CAP_70_80("Ác Nhân Cấp 70 - 80", 70, "Rương Vàng, 35 vạn Exp"),
    CAP_90_100("Ác Nhân Tinh Anh Cấp 90+", 90, "Rương Bạch Kim, Bí Tịch Môn Phái"),
    DAI_AC_NHAN("Đại Ác Nhân Boss", 80, "Trang Bị Hoàng Kim, Thần Binh Toái Phiến")
}

enum class SectType(val sectName: String, val element: String, val specialty: String) {
    THIEN_LONG("Thiên Long", "Tứ Thuộc Tính (Băng/Hỏa/Huyền/Độc)", "Nội Ngoại kiêm tu - Lục Mạch Thần Kiếm & Chỉ Điểm Giang Sơn"),
    CAI_BANG("Cái Bang", "Độc Công - Ngoại Công", "Né tránh siêu cao - Giáng Long Thập Bát Chưởng & Đả Cẩu Bổng"),
    VO_DANG("Võ Đang", "Huyền Công - Nội Công", "Khống chế áp đảo - Thái Cực Kiếm & Thiên Ngoại Phi Tiên"),
    TIEU_DAO("Tiêu Dao", "Hỏa / Độc - Nội Công", "Bẫy Bát Trận Đồ - Khê Sơn Hành Lữ x2 Sát Thương"),
    THIEU_LAM("Thiếu Lâm", "Huyền Công - Ngoại Công", "Huyết lượng vô địch - Hộ thể Kim Chung Tráo & Dịch Cân Kinh"),
    THIEN_SON("Thiên Sơn", "Băng Công - Ngoại Công", "Thích khách tàng hình - Bạo kích Lan Hoa Huyệt & Di Hoa Tiếp Mộc"),
    TINH_TUC("Tinh Túc", "Độc Công - Nội Công", "Độc công tầm xa - Hóa Công Đại Pháp & U Minh Thần Chưởng"),
    MINH_GIAO("Minh Giáo", "Hỏa Công - Ngoại Công", "Cuồng bạo sát thương - Nộ Hỏa Liên Trảm & Quỳ Hoa Thần Lực"),
    NGA_MI("Nga Mi", "Băng / Huyền - Nội Công", "Trị liệu hồi sinh - Thanh Tâm Phổ Thiện Chú & Cửu Âm Thần Trảo")
}
