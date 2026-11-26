package com.example.android_finalexam.data.local.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseSeeder {

    public static void seedDefaultData(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.SongEntry.TABLE_NAME, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                    return;
                }
            } finally {
                cursor.close();
            }
        }

        db.beginTransaction();
        try {
            // ===== ORIGINAL 6 BUNDLED ARTISTS (with actual audio) =====
            long artistColdplay = insertArtist(db, "Coldplay", "viva_lavi_da", "Ban nhạc rock Anh thành lập năm 1997 tại Luân Đôn.");
            long artistKis = insertArtist(db, "Kis", "muoitam", "Nghệ sĩ Indie Việt Nam với phong cách sâu lắng.");
            long artistMoshi = insertArtist(db, "Moshi", "dead_feelings", "Nhà sản xuất âm nhạc Lofi và giai điệu đêm muộn.");
            long artist7up = insertArtist(db, "7uppercuts, Cam", "wave_alpha", "Ban nhạc Punk Rock & Alternative sôi động.");
            long artistSonTung = insertArtist(db, "Sơn Tùng M-TP", "anh1", "Ca sĩ, nhạc sĩ hàng đầu V-Pop.");
            long artistMagnus = insertArtist(db, "Magnus Carlsson", "anh1", "Nghệ sĩ Pop Thụy Điển với giai điệu tươi sáng.");

            // ===== ORIGINAL ALBUMS =====
            long albumViva = insertAlbum(db, "Viva la Vida or Death and All His Friends", (int) artistColdplay, "viva_lavi_da", "2008");
            long albumMuoitam = insertAlbum(db, "Độc hành", (int) artistKis, "muoitam", "2023");
            long albumLofi = insertAlbum(db, "Lofi Night", (int) artistMoshi, "dead_feelings", "2022");
            long albumWave = insertAlbum(db, "Sóng Ngầm", (int) artist7up, "wave_alpha", "2021");

            // ===== 6 BUNDLED SONGS (with actual raw audio files) =====
            long song1 = insertSong(db, "Viva La Vida", (int) artistColdplay, (int) albumViva, 242000, "viva_lavi_da__coldplay", "viva_lavi_da");
            long song2 = insertSong(db, "Mười Tám", (int) artistKis, (int) albumMuoitam, 215000, "muoitam_kis", "muoitam");
            long song3 = insertSong(db, "Dead Feelings", (int) artistMoshi, (int) albumLofi, 185000, "dead_feelings__moshi", "dead_feelings");
            long song4 = insertSong(db, "Wave Alpha", (int) artist7up, (int) albumWave, 192000, "wave_alpha__7uppercuts_cam", "wave_alpha");
            long song5 = insertSong(db, "Đừng Làm Trái Tim Anh Đau", (int) artistSonTung, null, 315000, "dunglamtraitimanhdau", "anh1");
            long song6 = insertSong(db, "I Need Your Love", (int) artistMagnus, null, 178000, "ineedyourlove_magnus_carlsson", "anh1");

            // ===== ZINGMP3 TOP 100 ARTISTS (catalog data) =====
            long a1 = insertArtist(db, "Machiot, Low G", "playinglogo", "");
            long a2 = insertArtist(db, "SWAN", "playinglogo", "");
            long a3 = insertArtist(db, "Carly Rae Jepsen", "playinglogo", "");
            long a4 = insertArtist(db, "Ngân Ngân", "playinglogo", "");
            long a5 = insertArtist(db, "Phát Huy T4, Cầm", "playinglogo", "");
            long a6 = insertArtist(db, "Tóc Tiên", "playinglogo", "");
            long a7 = insertArtist(db, "K.O, Toki Thành Thỏ", "playinglogo", "");
            long a8 = insertArtist(db, "Liu Grace", "playinglogo", "");
            long a9 = insertArtist(db, "Saabirose, Yung Ni99", "playinglogo", "");
            long a10 = insertArtist(db, "The Masked Singer, RABIBUUM", "playinglogo", "");
            long a11 = insertArtist(db, "The Masked Singer, MAD LONG", "playinglogo", "");
            long a12 = insertArtist(db, "The Masked Singer, KHỈ XÙ MÌ", "playinglogo", "");
            long a13 = insertArtist(db, "The Masked Singer, MIMIBU", "playinglogo", "");
            long a14 = insertArtist(db, "The Masked Singer, MIMIBU, LyHan", "playinglogo", "");
            long a15 = insertArtist(db, "The Masked Singer, RABIBUUM, Ngọc Sơn", "playinglogo", "");
            long a16 = insertArtist(db, "Thịnh Suy", "playinglogo", "");
            long a17 = insertArtist(db, "Bon Nghiêm", "playinglogo", "");
            long a18 = insertArtist(db, "Mlee, Bảo Thy", "playinglogo", "");
            long a19 = insertArtist(db, "Khải Đăng", "playinglogo", "");
            long a20 = insertArtist(db, "Thiều Bảo Trâm, Hoàng Dũng", "playinglogo", "");
            long a21 = insertArtist(db, "Pháo, Đàm Vĩnh Hưng", "playinglogo", "");
            long a22 = insertArtist(db, "CL", "playinglogo", "");
            long a23 = insertArtist(db, "ERIK", "playinglogo", "");
            long a24 = insertArtist(db, "NAYEON", "playinglogo", "");
            long a25 = insertArtist(db, "BIGBANG", "playinglogo", "");
            long a26 = insertArtist(db, "ENHYPEN", "playinglogo", "");
            long a27 = insertArtist(db, "Sugababes", "playinglogo", "");
            long a28 = insertArtist(db, "Rtee, Resonance", "playinglogo", "");
            long a29 = insertArtist(db, "Đặng Tử Kỳ / G.E.M.", "playinglogo", "");
            long a30 = insertArtist(db, "The Kid LAROI", "playinglogo", "");
            long a31 = insertArtist(db, "Giveon", "playinglogo", "");
            long a32 = insertArtist(db, "keshi", "playinglogo", "");
            long a33 = insertArtist(db, "Gracie Abrams", "playinglogo", "");
            long a34 = insertArtist(db, "Orange", "playinglogo", "");
            long a35 = insertArtist(db, "Quốc Thiên", "playinglogo", "");
            long a36 = insertArtist(db, "Hồ Ngọc Hà, Suboi", "playinglogo", "");
            long a37 = insertArtist(db, "Hồ Ngọc Hà, Ái Phương, GiGi Hương Giang", "playinglogo", "");
            long a38 = insertArtist(db, "SpaceSpeakers, Rhymastic, SOOBIN, Cường Seven, Kiên Ứng", "playinglogo", "");
            long a39 = insertArtist(db, "SpaceSpeakers, Binz, Touliver", "playinglogo", "");
            long a40 = insertArtist(db, "Truant Fu, Try92, Nhuộm Collective", "playinglogo", "");
            long a41 = insertArtist(db, "VSTRA, Tyronee", "playinglogo", "");
            long a42 = insertArtist(db, "Khổng Tú Quỳnh, Seidolem V, Tuấn Võ", "playinglogo", "");
            long a43 = insertArtist(db, "Phương Mỹ Chi, DTAP", "playinglogo", "");
            long a44 = insertArtist(db, "Hoàng Thùy Linh", "playinglogo", "");
            long a45 = insertArtist(db, "NEW:V", "playinglogo", "");
            long a46 = insertArtist(db, "Pháp Kiều", "playinglogo", "");
            long a47 = insertArtist(db, "Hồ Ngọc Hà", "playinglogo", "");
            long a48 = insertArtist(db, "Thảo Trang", "playinglogo", "");
            long a49 = insertArtist(db, "Kacey Musgraves", "playinglogo", "");
            long a50 = insertArtist(db, "TINH HÀ \\SAY HI\\\"", "playinglogo", "");
            long a51 = insertArtist(db, "The Sheep", "playinglogo", "");
            long a52 = insertArtist(db, "Anh Quân", "playinglogo", "");
            long a53 = insertArtist(db, "Thành Đạt", "playinglogo", "");
            long a54 = insertArtist(db, "Trúc Nhân, Wren Evans", "playinglogo", "");
            long a55 = insertArtist(db, "Trúc Nhân, Rhymastic", "playinglogo", "");
            long a56 = insertArtist(db, "Trúc Nhân, MIN", "playinglogo", "");
            long a57 = insertArtist(db, "The Masked Singer, KOALAOKE, BÁO BERRY, CAO BỒI LANG, CẬU TƯ LÀNH", "playinglogo", "");
            long a58 = insertArtist(db, "The Masked Singer, BÁO BERRY", "playinglogo", "");
            long a59 = insertArtist(db, "The Masked Singer, CAO BỒI LANG", "playinglogo", "");
            long a60 = insertArtist(db, "The Masked Singer, KOALAOKE", "playinglogo", "");
            long a61 = insertArtist(db, "The Masked Singer, KOALAOKE, buitruonglinh", "playinglogo", "");
            long a62 = insertArtist(db, "The Masked Singer, CAO BỒI LANG, Anh Quân", "playinglogo", "");
            long a63 = insertArtist(db, "ATEEZ", "playinglogo", "");
            long a64 = insertArtist(db, "Armin Van Buuren, Mary Jensen", "playinglogo", "");
            long a65 = insertArtist(db, "Becky Hill", "playinglogo", "");
            long a66 = insertArtist(db, "Delta Goodrem", "playinglogo", "");
            long a67 = insertArtist(db, "CONGB, Trid Minh", "playinglogo", "");
            long a68 = insertArtist(db, "Lauv", "playinglogo", "");
            long a69 = insertArtist(db, "Mỹ Mỹ", "playinglogo", "");
            long a70 = insertArtist(db, "Nguyễn Hùng, PiaLinh, Mounter", "playinglogo", "");
            long a71 = insertArtist(db, "Hanzi, 502 Ocean, Nhuộm Collective", "playinglogo", "");
            long a72 = insertArtist(db, "Hồ Ngọc Hà, Hà An Huy", "playinglogo", "");
            long a73 = insertArtist(db, "Fishy, EREN", "playinglogo", "");
            long a74 = insertArtist(db, "AEONUS", "playinglogo", "");
            long a75 = insertArtist(db, "Nguyên Hà, Dani D", "playinglogo", "");
            long a76 = insertArtist(db, "WEAN", "playinglogo", "");
            long a77 = insertArtist(db, "prettyXIX, Yanbi, APJ", "playinglogo", "");
            long a78 = insertArtist(db, "LNGSHOT", "playinglogo", "");
            long a79 = insertArtist(db, "Rita Ora", "playinglogo", "");
            long a80 = insertArtist(db, "CHAERYEONG", "playinglogo", "");
            long a81 = insertArtist(db, "A2O, A2O SOUL", "playinglogo", "");
            long a82 = insertArtist(db, "NCT Dream", "playinglogo", "");
            long a83 = insertArtist(db, "Thịnh Suy, Lazii", "playinglogo", "");
            long a84 = insertArtist(db, "Lê Gia Bảo", "playinglogo", "");
            long a85 = insertArtist(db, "Hành Or, Rtee, Freaky, Hổ", "playinglogo", "");
            long a86 = insertArtist(db, "Khoai Lang Thang", "playinglogo", "");
            long a87 = insertArtist(db, "Hiền Hồ", "playinglogo", "");
            long a88 = insertArtist(db, "The Masked Singer, BÒ SỮA ACHU", "playinglogo", "");
            long a89 = insertArtist(db, "The Masked Singer, HEO MÁ HỒNG", "playinglogo", "");
            long a90 = insertArtist(db, "The Masked Singer, LION VUI TƯƠI", "playinglogo", "");
            long a91 = insertArtist(db, "The Masked Singer, BÒ SỮA ACHU, CONGB", "playinglogo", "");
            long a92 = insertArtist(db, "Trungg I.U", "playinglogo", "");
            long a93 = insertArtist(db, "James Arthur", "playinglogo", "");
            long a94 = insertArtist(db, "Vũ Thịnh, Cô Dâu", "playinglogo", "");

            // ===== ZINGMP3 TOP 100 NEWEST SONGS (catalog, metadata-only) =====
            insertCatalogSong(db, "Bế Em Qua Thềm", (int) a1, "playinglogo", "https://zingmp3.vn/bai-hat/Be-Em-Qua-Them-Machiot-Low-G/o4wH20kRmgvF.html");
            insertCatalogSong(db, "Thiếu Nàng", (int) a2, "playinglogo", "https://zingmp3.vn/bai-hat/Thieu-Nang-SWAN/uTDEVIw0bnI.html");
            insertCatalogSong(db, "Run Away With Me", (int) a3, "playinglogo", "https://zingmp3.vn/bai-hat/Run-Away-With-Me-Carly-Rae-Jepsen/onooFmpHA4o.html");
            insertCatalogSong(db, "Thuyền Biển Và Em", (int) a4, "playinglogo", "https://zingmp3.vn/bai-hat/Thuyen-Bien-Va-Em-Ngan-Ngan/VsMgxHAos3UE.html");
            insertCatalogSong(db, "Số 1 Thế Giới (Lofi Version)", (int) a5, "playinglogo", "https://zingmp3.vn/bai-hat/So-1-The-Gioi-Lofi-Version-Phat-Huy-T4-Cam/iBRvfkFd62iQ.html");
            insertCatalogSong(db, "EM TIÊN (Grand Final version)", (int) a6, "playinglogo", "https://zingmp3.vn/bai-hat/EM-TIEN-Grand-Final-version-Toc-Tien/3jMe0QzE4sc.html");
            insertCatalogSong(db, "KHÔNG MUỐN THỂ HIỆN", (int) a7, "playinglogo", "https://zingmp3.vn/bai-hat/KHONG-MUON-THE-HIEN-K-O-Toki-Thanh-Tho/3YMg3lCq3qBk.html");
            insertCatalogSong(db, "em đã yêu anh quá nhiều", (int) a8, "playinglogo", "https://zingmp3.vn/bai-hat/em-da-yeu-anh-qua-nhieu-Liu-Grace/5PPsnZRuVDTM.html");
            insertCatalogSong(db, "xinh nhat", (int) a9, "playinglogo", "https://zingmp3.vn/bai-hat/xinh-nhat-Saabirose-Yung-Ni99/uV0hIdIJSLve.html");
            insertCatalogSong(db, "E LÀ KHÔNG THỂ", (int) a10, "playinglogo", "https://zingmp3.vn/bai-hat/E-LA-KHONG-THE-The-Masked-Singer-RABIBUUM/inZoe2Mub2J7.html");
            insertCatalogSong(db, "MÌNH YÊU ĐẾN ĐÂY THÔI", (int) a11, "playinglogo", "https://zingmp3.vn/bai-hat/MINH-YEU-DEN-DAY-THOI-The-Masked-Singer-MAD-LONG/XdblRmSgfKZU.html");
            insertCatalogSong(db, "SAU HÌNH BÓNG KHÁC", (int) a12, "playinglogo", "https://zingmp3.vn/bai-hat/SAU-HINH-BONG-KHAC-The-Masked-Singer-KHI-XU-MI/7lRmia8zURVF.html");
            insertCatalogSong(db, "ĐẮNG DƯỢC", (int) a13, "playinglogo", "https://zingmp3.vn/bai-hat/DANG-DUOC-The-Masked-Singer-MIMIBU/Bs2DyjGHchxq.html");
            insertCatalogSong(db, "RƠI TỰ DO", (int) a14, "playinglogo", "https://zingmp3.vn/bai-hat/ROI-TU-DO-The-Masked-Singer-MIMIBU-LyHan/zsRu36NCQ93K.html");
            insertCatalogSong(db, "VẦNG TRĂNG CÔ ĐƠN", (int) a15, "playinglogo", "https://zingmp3.vn/bai-hat/VANG-TRANG-CO-DON-The-Masked-Singer-RABIBUUM-Ngoc-Son/eoDDLkc1NCTZ.html");
            insertCatalogSong(db, "I’m no robot", (int) a16, "playinglogo", "https://zingmp3.vn/bai-hat/I-m-no-robot-Thinh-Suy/5CzoS0i8sVEa.html");
            insertCatalogSong(db, "Gặp Anh Giữa Luân Hồi", (int) a17, "playinglogo", "https://zingmp3.vn/bai-hat/Gap-Anh-Giua-Luan-Hoi-Bon-Nghiem/rf9AUu5uvjXH.html");
            insertCatalogSong(db, "Triệu chứng của xinh đẹp", (int) a18, "playinglogo", "https://zingmp3.vn/bai-hat/Trieu-chung-cua-xinh-dep-Mlee-Bao-Thy/bnvrDmKKtkfY.html");
            insertCatalogSong(db, "Say Đắm Trong Tình Yêu Này", (int) a19, "playinglogo", "https://zingmp3.vn/bai-hat/Say-Dam-Trong-Tinh-Yeu-Nay-Khai-Dang/d75rLBmRgc8U.html");
            insertCatalogSong(db, "tạnh mưa (duet)", (int) a20, "playinglogo", "https://zingmp3.vn/bai-hat/tanh-mua-duet-Thieu-Bao-Tram-Hoang-Dung/dor0qgz7nLpb.html");
            insertCatalogSong(db, "Phố Không Buồn", (int) a21, "playinglogo", "https://zingmp3.vn/bai-hat/Pho-Khong-Buon-Phao-Dam-Vinh-Hung/EHEPgXYCVkZM.html");
            insertCatalogSong(db, "ICONIC", (int) a22, "playinglogo", "https://zingmp3.vn/bai-hat/ICONIC-CL/MdiGXodEoeHB.html");
            insertCatalogSong(db, "Hạ màn", (int) a23, "playinglogo", "https://zingmp3.vn/bai-hat/Ha-man-ERIK/Cw2tagJPQVA1.html");
            insertCatalogSong(db, "BABY ANGEL", (int) a24, "playinglogo", "https://zingmp3.vn/bai-hat/BABY-ANGEL-NAYEON/UtqQhW4eUeBY.html");
            insertCatalogSong(db, "GiiiRIP", (int) a25, "playinglogo", "https://zingmp3.vn/bai-hat/GiiiRIP-BIGBANG/tK1ocjtUGxzW.html");
            insertCatalogSong(db, "I Want You", (int) a26, "playinglogo", "https://zingmp3.vn/bai-hat/I-Want-You-ENHYPEN/OWD620BxOJxe.html");
            insertCatalogSong(db, "Love Language", (int) a27, "playinglogo", "https://zingmp3.vn/bai-hat/Love-Language-Sugababes/XnTJjmK72FUD.html");
            insertCatalogSong(db, "NGỰA CON", (int) a28, "playinglogo", "https://zingmp3.vn/bai-hat/NGUA-CON-Rtee-Resonance/ZiI40Vgjff9y.html");
            insertCatalogSong(db, "Play On", (int) a29, "playinglogo", "https://zingmp3.vn/bai-hat/Play-On-Dang-Tu-Ky-G-E-M/YBf0rqLRuZ2.html");
            insertCatalogSong(db, "A PLACE TO GO", (int) a30, "playinglogo", "https://zingmp3.vn/bai-hat/A-PLACE-TO-GO-The-Kid-LAROI/s90jL7MEmk64.html");
            insertCatalogSong(db, "Fast Asleep", (int) a31, "playinglogo", "https://zingmp3.vn/bai-hat/Fast-Asleep-Giveon/Ol8Sf0IjhuE.html");
            insertCatalogSong(db, "MISS ME (Orchestral)", (int) a32, "playinglogo", "https://zingmp3.vn/bai-hat/MISS-ME-Orchestral-keshi/Cn92AWTBR5Bw.html");
            insertCatalogSong(db, "Lost in It", (int) a33, "playinglogo", "https://zingmp3.vn/bai-hat/Lost-in-It-Gracie-Abrams/rsh9EIGyit28.html");
            insertCatalogSong(db, "CÔ GÁI ANH YÊU", (int) a34, "playinglogo", "https://zingmp3.vn/bai-hat/CO-GAI-ANH-YEU-Orange/7frg8wSxix3D.html");
            insertCatalogSong(db, "Khóc Thầm", (int) a35, "playinglogo", "https://zingmp3.vn/bai-hat/Khoc-Tham-Quoc-Thien/eryZxHhuysVb.html");
            insertCatalogSong(db, "Thân Mật", (int) a36, "playinglogo", "https://zingmp3.vn/bai-hat/Than-Mat-Ho-Ngoc-Ha-Suboi/JYNCQbMBUTzB.html");
            insertCatalogSong(db, "Manifest", (int) a37, "playinglogo", "https://zingmp3.vn/bai-hat/Manifest-Ho-Ngoc-Ha-Ai-Phuong-GiGi-Huong-Giang/OieiWXJtKwrM.html");
            insertCatalogSong(db, "Here We Go (feat. Cường…)", (int) a38, "playinglogo", "https://zingmp3.vn/bai-hat/Here-We-Go-feat-Cuong-Seven-Kien-Ung-Kimmese-Touliver-SpaceSpeakers-Rhymastic-SOOBIN-Cuong-Seven-Kien-Ung/waba21zf9ADH.html");
            insertCatalogSong(db, "Love Ya (feat. Touliver)", (int) a39, "playinglogo", "https://zingmp3.vn/bai-hat/Love-Ya-feat-Touliver-SpaceSpeakers-Binz-Touliver/UcVOt2L7xZ2r.html");
            insertCatalogSong(db, "Một vòng Trái Đất", (int) a40, "playinglogo", "https://zingmp3.vn/bai-hat/Mot-vong-Trai-Dat-Truant-Fu-Try92-Nhuom-Collective/YrRscxFctlfv.html");
            insertCatalogSong(db, "Hơn Cả Anh", (int) a41, "playinglogo", "https://zingmp3.vn/bai-hat/Hon-Ca-Anh-VSTRA-Tyronee/TOJXUyotSOJc.html");
            insertCatalogSong(db, "Hàng Dễ Vỡ", (int) a42, "playinglogo", "https://zingmp3.vn/bai-hat/Hang-De-Vo-Khong-Tu-Quynh-Seidolem-V-Tuan-Vo/svyHi7cN4sSX.html");
            insertCatalogSong(db, "2 năm 1 không (2510)", (int) a23, "playinglogo", "https://zingmp3.vn/bai-hat/2-nam-1-khong-2510-ERIK/osOBYrZnCdZ2.html");
            insertCatalogSong(db, "RẠNG NGỜI VĂN HÓA VIỆT NAM", (int) a43, "playinglogo", "https://zingmp3.vn/bai-hat/RANG-NGOI-VAN-HOA-VIET-NAM-Phuong-My-Chi-DTAP/3uJtdLWCE6Qe.html");
            insertCatalogSong(db, "Vô Lượng Kiếp", (int) a44, "playinglogo", "https://zingmp3.vn/bai-hat/Vo-Luong-Kiep-Hoang-Thuy-Linh/2jQOEfQr2NAj.html");
            insertCatalogSong(db, "LÚ", (int) a45, "playinglogo", "https://zingmp3.vn/bai-hat/LU-NEW-V/XissTBgsurv.html");
            insertCatalogSong(db, "Thứ 7 Màu Hồng", (int) a46, "playinglogo", "https://zingmp3.vn/bai-hat/Thu-7-Mau-Hong-Phap-Kieu/5DJws5QRTzWA.html");
            insertCatalogSong(db, "Vài Câu Hỏi Lạ Kỳ", (int) a47, "playinglogo", "https://zingmp3.vn/bai-hat/Vai-Cau-Hoi-La-Ky-Ho-Ngoc-Ha/iwqQSWKIyI29.html");
            insertCatalogSong(db, "Em Kể Anh Nghe", (int) a35, "playinglogo", "https://zingmp3.vn/bai-hat/Em-Ke-Anh-Nghe-Quoc-Thien/z7MRJZQ5Nrs3.html");
            insertCatalogSong(db, "HỐI HẬN", (int) a48, "playinglogo", "https://zingmp3.vn/bai-hat/HOI-HAN-Thao-Trang/zs0YQ0UjRxYJ.html");
            insertCatalogSong(db, "The Vacation Song", (int) a49, "playinglogo", "https://zingmp3.vn/bai-hat/The-Vacation-Song-Kacey-Musgraves/LZ8tbOJ916yg.html");
            insertCatalogSong(db, "24EVER", (int) a50, "playinglogo", "Pháp Kiều");
            insertCatalogSong(db, "Yêu Một Người", (int) a51, "playinglogo", "https://zingmp3.vn/bai-hat/Yeu-Mot-Nguoi-The-Sheep/kOjAKxgVl6xl.html");
            insertCatalogSong(db, "Mang Cơn Đau Em Đi", (int) a52, "playinglogo", "https://zingmp3.vn/bai-hat/Mang-Con-Dau-Em-Di-Anh-Quan/SNF1Y2Bun4w.html");
            insertCatalogSong(db, "Khâu Lại Hạnh Phúc", (int) a53, "playinglogo", "https://zingmp3.vn/bai-hat/Khau-Lai-Hanh-Phuc-Thanh-Dat/rF40IElEd58G.html");
            insertCatalogSong(db, "Vì Anh Quá Đẹp Trai", (int) a54, "playinglogo", "https://zingmp3.vn/bai-hat/Vi-Anh-Qua-Dep-Trai-Truc-Nhan-Wren-Evans/b1mZ8G0p9YRu.html");
            insertCatalogSong(db, "Về Đi Em", (int) a55, "playinglogo", "https://zingmp3.vn/bai-hat/Ve-Di-Em-Truc-Nhan-Rhymastic/nzzfecF06ctv.html");
            insertCatalogSong(db, "Chuyện Tình Tứ Cứ Từ Từ Tính", (int) a56, "playinglogo", "https://zingmp3.vn/bai-hat/Chuyen-Tinh-Tu-Cu-Tu-Tu-Tinh-Truc-Nhan-MIN/yLVqRgtWWS5s.html");
            insertCatalogSong(db, "MASHUP: HÔN LỄ CỦA EM…", (int) a57, "playinglogo", "https://zingmp3.vn/bai-hat/MASHUP-HON-LE-CUA-EM-NGAY-EM-DEP-NHAT-The-Masked-Singer-KOALAOKE-BAO-BERRY-CAO-BOI-LANG-CAU-TU-LANH/IKe9FmqC8C5w.html");
            insertCatalogSong(db, "EM SAY RỒI", (int) a58, "playinglogo", "https://zingmp3.vn/bai-hat/EM-SAY-ROI-The-Masked-Singer-BAO-BERRY/RUKwrL0EuITJ.html");
            insertCatalogSong(db, "MASHUP: CÒN ANH EM…", (int) a59, "playinglogo", "https://zingmp3.vn/bai-hat/MASHUP-CON-ANH-EM-BO-CHO-AI-SAO-CUNG-DUOC-The-Masked-Singer-CAO-BOI-LANG/YrupCU7RPa9s.html");
            insertCatalogSong(db, "LÊNH ĐÊNH", (int) a60, "playinglogo", "https://zingmp3.vn/bai-hat/LENH-DENH-The-Masked-Singer-KOALAOKE/ETklXNRJ0tAL.html");
            insertCatalogSong(db, "DẪU CÓ LỖI LẦM", (int) a61, "playinglogo", "https://zingmp3.vn/bai-hat/DAU-CO-LOI-LAM-The-Masked-Singer-KOALAOKE-buitruonglinh/YdNi379nABFo.html");
            insertCatalogSong(db, "MANG CƠN ĐAU EM ĐI", (int) a62, "playinglogo", "https://zingmp3.vn/bai-hat/MANG-CON-DAU-EM-DI-The-Masked-Singer-CAO-BOI-LANG-Anh-Quan/92AyDzp7xIgm.html");
            insertCatalogSong(db, "UP", (int) a63, "playinglogo", "https://zingmp3.vn/bai-hat/UP-ATEEZ/SHYTCfsl7vRp.html");
            insertCatalogSong(db, "Go All Night", (int) a64, "playinglogo", "https://zingmp3.vn/bai-hat/Go-All-Night-Armin-Van-Buuren-Mary-Jensen/3BayVyExTNH.html");
            insertCatalogSong(db, "tie me d^wn", (int) a65, "playinglogo", "https://zingmp3.vn/bai-hat/tie-me-d-wn-Becky-Hill/UAaYeCe2KJo3.html");
            insertCatalogSong(db, "Telescope", (int) a66, "playinglogo", "https://zingmp3.vn/bai-hat/Telescope-Delta-Goodrem/tLvMjMLRHNKS.html");
            insertCatalogSong(db, "tạm thương (uni.verse ver)", (int) a67, "playinglogo", "https://zingmp3.vn/bai-hat/tam-thuong-uni-verse-ver-CONGB-Trid-Minh/qCqrHgdeaSjB.html");
            insertCatalogSong(db, "Guts", (int) a68, "playinglogo", "https://zingmp3.vn/bai-hat/Guts-Lauv/eRTKMAuKjVKu.html");
            insertCatalogSong(db, "Mỹ Miều", (int) a69, "playinglogo", "https://zingmp3.vn/bai-hat/My-Mieu-My-My/dN46mY9yGpr7.html");
            insertCatalogSong(db, "Nếu Em Không Là (From 'Bò Sữa Bay')", (int) a70, "playinglogo", "https://zingmp3.vn/bai-hat/Neu-Em-Khong-La-From-Bo-Sua-Bay-Nguyen-Hung-PiaLinh-Mounter/w7RCANGkl4TD.html");
            insertCatalogSong(db, "Nếu (Ta có chờ nhau)", (int) a71, "playinglogo", "https://zingmp3.vn/bai-hat/Neu-Ta-co-cho-nhau-Hanzi-502-Ocean-Nhuom-Collective/qCoKCzwUAYoJ.html");
            insertCatalogSong(db, "Lời Ong Bướm", (int) a72, "playinglogo", "https://zingmp3.vn/bai-hat/Loi-Ong-Buom-Ho-Ngoc-Ha-Ha-An-Huy/TJ3fSlcYG0SR.html");
            insertCatalogSong(db, "Mình Yêu Nhau Từ Bao Giờ", (int) a35, "playinglogo", "https://zingmp3.vn/bai-hat/Minh-Yeu-Nhau-Tu-Bao-Gio-Quoc-Thien/aopB0lezV5eb.html");
            insertCatalogSong(db, "Người Tình Ánh Trăng", (int) a73, "playinglogo", "https://zingmp3.vn/bai-hat/Nguoi-Tinh-Anh-Trang-Fishy-EREN/YBkghk8ZJfD7.html");
            insertCatalogSong(db, "Tình Yêu Rewind", (int) a74, "playinglogo", "https://zingmp3.vn/bai-hat/Tinh-Yeu-Rewind-AEONUS/TpF1tS1qLGlU.html");
            insertCatalogSong(db, "Ngày Con Còn Mẹ…", (int) a75, "playinglogo", "https://zingmp3.vn/bai-hat/Ngay-Con-Con-Me-Original-Song-from-Ngay-Con-Con-Me-Nguyen-Ha-Dani-D/dloDftgOFAGw.html");
            insertCatalogSong(db, "Số Không", (int) a76, "playinglogo", "https://zingmp3.vn/bai-hat/So-Khong-WEAN/UyVQwzYGLF9z.html");
            insertCatalogSong(db, "Like That", (int) a77, "playinglogo", "https://zingmp3.vn/bai-hat/Like-That-prettyXIX-Yanbi-APJ/u709MTlzD3Yo.html");
            insertCatalogSong(db, "Binhtinh.exe", (int) a16, "playinglogo", "https://zingmp3.vn/bai-hat/Binhtinh-exe-Thinh-Suy/n1UDZ2jsGnef.html");
            insertCatalogSong(db, "Bananas", (int) a78, "playinglogo", "https://zingmp3.vn/bai-hat/Bananas-LNGSHOT/oGooEV4Cm9ed.html");
            insertCatalogSong(db, "The Current (From The…)", (int) a79, "playinglogo", "https://zingmp3.vn/bai-hat/The-Current-From-The-ViQueens-Soundtrack-Rita-Ora/zrxMPi0fjFVz.html");
            insertCatalogSong(db, "Lovesick", (int) a80, "playinglogo", "https://zingmp3.vn/bai-hat/Lovesick-CHAERYEONG/2j29j9rpCYdm.html");
            insertCatalogSong(db, "Dancing in the Dark", (int) a81, "playinglogo", "https://zingmp3.vn/bai-hat/Dancing-in-the-Dark-A2O-A2O-SOUL/YVAguN3CySEx.html");
            insertCatalogSong(db, "Boogie Woogie", (int) a82, "playinglogo", "https://zingmp3.vn/bai-hat/Boogie-Woogie-NCT-Dream/u7LULLQh77A2.html");
            insertCatalogSong(db, "Như Trẻ Con", (int) a83, "playinglogo", "https://zingmp3.vn/bai-hat/Nhu-Tre-Con-Thinh-Suy-Lazii/SZzYhtpZ4utR.html");
            insertCatalogSong(db, "Mừng Ngày Em Cưới", (int) a84, "playinglogo", "https://zingmp3.vn/bai-hat/Mung-Ngay-Em-Cuoi-Le-Gia-Bao/AH1tFKnklvZa.html");
            insertCatalogSong(db, "LOOK NÀY LOOK KIA", (int) a85, "playinglogo", "https://zingmp3.vn/bai-hat/LOOK-NAY-LOOK-KIA-Hanh-Or-Rtee-Freaky-Ho/QFhPW6nK05Ze.html");
            insertCatalogSong(db, "Chạy Về Ánh Dương", (int) a86, "playinglogo", "https://zingmp3.vn/bai-hat/Chay-Ve-Anh-Duong-Khoai-Lang-Thang/KXjONkn9FLfU.html");
            insertCatalogSong(db, "Nỗi Buồn Em Yêu Nhất…", (int) a87, "playinglogo", "https://zingmp3.vn/bai-hat/Noi-Buon-Em-Yeu-Nhat-From-Chi-Chi-Em-Em-3-Original-Motion-Picture-Soundtrack-Hien-Ho/MBx14GFbSLyT.html");
            insertCatalogSong(db, "HẠT MƯA VƯƠNG VẤN", (int) a12, "playinglogo", "https://zingmp3.vn/bai-hat/HAT-MUA-VUONG-VAN-The-Masked-Singer-KHI-XU-MI/dPVeqX5fpOv.html");
            insertCatalogSong(db, "CÒN THƯƠNG NHƯNG…", (int) a88, "playinglogo", "https://zingmp3.vn/bai-hat/CON-THUONG-NHUNG-KHONG-CON-YEU-The-Masked-Singer-BO-SUA-ACHU/Da7oAdAcl8XU.html");
            insertCatalogSong(db, "QUYỀN YẾU ĐUỐI", (int) a59, "playinglogo", "https://zingmp3.vn/bai-hat/QUYEN-YEU-DUOI-The-Masked-Singer-CAO-BOI-LANG/djMraMvcKqW.html");
            insertCatalogSong(db, "THEO EM VỀ NHÀ", (int) a89, "playinglogo", "https://zingmp3.vn/bai-hat/THEO-EM-VE-NHA-The-Masked-Singer-HEO-MA-HONG/T3N7kwcJqPzT.html");
            insertCatalogSong(db, "EM ƠI VỀ THÔI", (int) a90, "playinglogo", "https://zingmp3.vn/bai-hat/EM-OI-VE-THOI-The-Masked-Singer-LION-VUI-TUOI/Iget2q1D3MNM.html");
            insertCatalogSong(db, "TẠM THƯƠNG", (int) a91, "playinglogo", "https://zingmp3.vn/bai-hat/TAM-THUONG-The-Masked-Singer-BO-SUA-ACHU-CONGB/10SxOXRpyp2G.html");
            insertCatalogSong(db, "Tình Ý", (int) a92, "playinglogo", "https://zingmp3.vn/bai-hat/Tinh-Y-Trungg-I-U/Cuketj1tAg9.html");
            insertCatalogSong(db, "Waste My Tears", (int) a93, "playinglogo", "https://zingmp3.vn/bai-hat/Waste-My-Tears-James-Arthur/CnJ3oBFvE3Ex.html");
            insertCatalogSong(db, "Mình Cùng Nhau Yêu Đương Đi", (int) a94, "playinglogo", "https://zingmp3.vn/bai-hat/Minh-Cung-Nhau-Yeu-Duong-Di-Vu-Thinh-Co-Dau/yEPOvYgqK33j.html");

            // ===== DEFAULT PLAYLISTS =====
            long playlist1 = insertPlaylist(db, "Giai điệu tuyển chọn", "Tuyển tập những bản nhạc chất lượng cao nghe mỗi ngày.", "playinglogo");
            long playlist2 = insertPlaylist(db, "Đêm muộn & Thư giãn", "Không gian âm thanh thư giãn, lắng đọng cho tâm hồn.", "dead_feelings");

            addSongToPlaylist(db, (int) playlist1, (int) song1, 0);
            addSongToPlaylist(db, (int) playlist1, (int) song2, 1);
            addSongToPlaylist(db, (int) playlist1, (int) song5, 2);

            addSongToPlaylist(db, (int) playlist2, (int) song2, 0);
            addSongToPlaylist(db, (int) playlist2, (int) song3, 1);
            addSongToPlaylist(db, (int) playlist2, (int) song6, 2);

            likeSong(db, (int) song1);
            likeSong(db, (int) song2);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static long insertArtist(SQLiteDatabase db, String name, String avatarRes, String bio) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.ArtistEntry.COLUMN_NAME, name);
        cv.put(DatabaseContract.ArtistEntry.COLUMN_AVATAR_RES, avatarRes);
        cv.put(DatabaseContract.ArtistEntry.COLUMN_BIO, bio);
        return db.insert(DatabaseContract.ArtistEntry.TABLE_NAME, null, cv);
    }

    private static long insertAlbum(SQLiteDatabase db, String title, int artistId, String coverRes, String releaseDate) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.AlbumEntry.COLUMN_TITLE, title);
        cv.put(DatabaseContract.AlbumEntry.COLUMN_ARTIST_ID, artistId);
        cv.put(DatabaseContract.AlbumEntry.COLUMN_COVER_RES, coverRes);
        cv.put(DatabaseContract.AlbumEntry.COLUMN_RELEASE_DATE, releaseDate);
        return db.insert(DatabaseContract.AlbumEntry.TABLE_NAME, null, cv);
    }

    private static long insertSong(SQLiteDatabase db, String title, int artistId, Integer albumId, int durationMs, String audioRes, String coverRes) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.SongEntry.COLUMN_TITLE, title);
        cv.put(DatabaseContract.SongEntry.COLUMN_ARTIST_ID, artistId);
        if (albumId != null) {
            cv.put(DatabaseContract.SongEntry.COLUMN_ALBUM_ID, albumId);
        }
        cv.put(DatabaseContract.SongEntry.COLUMN_DURATION_MS, durationMs);
        cv.put(DatabaseContract.SongEntry.COLUMN_AUDIO_RESOURCE, audioRes);
        cv.put(DatabaseContract.SongEntry.COLUMN_IMAGE_RESOURCE, coverRes);
        cv.put(DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE, "ASSET");
        return db.insert(DatabaseContract.SongEntry.TABLE_NAME, null, cv);
    }

    /**
     * Insert a catalog song (metadata-only from ZingMP3 chart, no bundled audio file).
     * These songs display in the UI but cannot be played until their audio is imported.
     */
    private static void insertCatalogSong(SQLiteDatabase db, String title, int artistId, String coverRes, String sourceUrl) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.SongEntry.COLUMN_TITLE, title);
        cv.put(DatabaseContract.SongEntry.COLUMN_ARTIST_ID, artistId);
        cv.put(DatabaseContract.SongEntry.COLUMN_DURATION_MS, 0);
        cv.put(DatabaseContract.SongEntry.COLUMN_IMAGE_RESOURCE, coverRes);
        cv.put(DatabaseContract.SongEntry.COLUMN_SOURCE_TYPE, "CATALOG");
        cv.put(DatabaseContract.SongEntry.COLUMN_SOURCE_HOST, sourceUrl);
        db.insert(DatabaseContract.SongEntry.TABLE_NAME, null, cv);
    }

    private static long insertPlaylist(SQLiteDatabase db, String name, String description, String coverRes) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_NAME, name);
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_DESCRIPTION, description);
        cv.put(DatabaseContract.PlaylistEntry.COLUMN_COVER_RES, coverRes);
        return db.insert(DatabaseContract.PlaylistEntry.TABLE_NAME, null, cv);
    }

    private static void addSongToPlaylist(SQLiteDatabase db, int playlistId, int songId, int position) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_PLAYLIST_ID, playlistId);
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_SONG_ID, songId);
        cv.put(DatabaseContract.PlaylistSongEntry.COLUMN_POSITION, position);
        db.insert(DatabaseContract.PlaylistSongEntry.TABLE_NAME, null, cv);
    }

    private static void likeSong(SQLiteDatabase db, int songId) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.LikedSongEntry.COLUMN_SONG_ID, songId);
        db.insertWithOnConflict(DatabaseContract.LikedSongEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }
}
