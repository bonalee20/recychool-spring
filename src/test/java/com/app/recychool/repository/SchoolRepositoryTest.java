package com.app.recychool.repository;

import com.app.recychool.domain.entity.School;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@Transactional
@Commit
@Slf4j
class SchoolRepositoryTest {
    @Autowired
    private SchoolRepository schoolRepository;

    @PersistenceContext
    private EntityManager em;
    @Test
    public void findAlltest(){
        List<School> schools = schoolRepository.findAll();
        schools.forEach(s -> System.out.println(s.getSchoolName()));
    }

    @Test
    void findRandomSchoolWithParkLimitTest() {
        String sql =
                "SELECT * FROM (" +
                        "  SELECT s.* FROM TBL_SCHOOL s " +
                        "  WHERE s.SCHOOL_PARK_COUNT > 0 " +
                        "  ORDER BY DBMS_RANDOM.VALUE" +
                        ") WHERE ROWNUM <= 4";

        List<School> lists = em.createNativeQuery(sql, School.class).getResultList();

        log.info("schools: {}", lists);
    }

//    @Test
//    public void schoolRepositoryTestsave100() {
//        //예성 --------------------------------------------------------------------------------------------------
//        School school1 = new School();
//        school1.setSchoolCity("광진구");
//        school1.setSchoolName("서울화양초등학교");
//        school1.setSchoolAddress("서울특별시 광진구 군자로 9");
//
//        school1.setSchoolPhone("02-2286-3704");
//        school1.setSchoolImagePath("C:\\school");
//        school1.setSchoolImageName("광진구_서울화양초등학교.jpg");
//        schoolRepository.save(school1);
//
//        School school2 = new School();
//        school2.setSchoolCity("성동구");
//        school2.setSchoolName("덕수고등학교(행당분교)");
//        school2.setSchoolAddress("서울특별시 성동구 왕십리로 199");
//
//        school2.setSchoolPhone("02-2286-3704");
//        school2.setSchoolImagePath("C:\\school");
//        school2.setSchoolImageName("성동구_덕수고등학교(행당분교).jpg");
//        schoolRepository.save(school2);
//
//        School school3 = new School();
//        school3.setSchoolCity("성동구");
//        school3.setSchoolName("성수공업고등학교");
//        school3.setSchoolAddress("서울특별시 성동구 뚝섬로 365");
//;
//        school3.setSchoolPhone("02-2286-3704");
//        school3.setSchoolImagePath("C:\\school");
//        school3.setSchoolImageName("성동구_성수공업고등학교.jpg");
//        schoolRepository.save(school3);
//
//        School school4 = new School();
//        school4.setSchoolCity("금천구");
//        school4.setSchoolName("서울흥일초등학교");
//        school4.setSchoolAddress("서울특별시 영등포구 문래로 121");
//
//        school4.setSchoolPhone("02-2165-2122");
//        school4.setSchoolImagePath("C:\\school");
//        school4.setSchoolImageName("금천구_서울흥일초등학교.jpg");
//        schoolRepository.save(school4);
//
//        School school5 = new School();
//        school5.setSchoolCity("강서구");
//        school5.setSchoolName("서울염강초등학교");
//        school5.setSchoolAddress("서울특별시 강서구 허준로 221-22");
//
//        school5.setSchoolPhone("02-2600-0922");
//        school5.setSchoolImagePath("C:\\school");
//        school5.setSchoolImageName("강서구_서울염강초등학교.jpg");
//        schoolRepository.save(school5);
//
//        School school6 = new School();
//        school6.setSchoolCity("강서구");
//        school6.setSchoolName("공진중학교");
//        school6.setSchoolAddress("서울특별시 강서구 양천로55길 56");
//
//        school6.setSchoolPhone("02-2600-0922");
//        school6.setSchoolImagePath("C:\\school");
//        school6.setSchoolImageName("강서구_공진중학교.jpg");
//        schoolRepository.save(school6);
//
//        School school7 = new School();
//        school7.setSchoolCity("성남시");
//        school7.setSchoolName("구.영성여중");
//        school7.setSchoolAddress("");
//
//        school7.setSchoolPhone("031-780-2618");
//        school7.setSchoolImagePath("C:\\school");
//        school7.setSchoolImageName("성남시_구.영성여중.jpg");
//        schoolRepository.save(school7);
//
//        School school8 = new School();
//        school8.setSchoolCity("부천시");
//        school8.setSchoolName("부천덕산초대장분교장");
//        school8.setSchoolAddress("경기도 부천시 대장로92");
//
//        school8.setSchoolPhone("032-620-0282");
//        school8.setSchoolImagePath("C:\\school");
//        school8.setSchoolImageName("부천시_부천덕산초대장분교장.jpeg");
//        schoolRepository.save(school8);
//
//        School school9 = new School();
//        school9.setSchoolCity("부천시");
//        school9.setSchoolName("구.복사초");
//        school9.setSchoolAddress("경기도 부천시 소사구 소사로 96");
//
//        school9.setSchoolPhone("032-620-0282");
//        school9.setSchoolImagePath("C:\\school");
//        school9.setSchoolImageName("부천시_구.복사초.jpg");
//        schoolRepository.save(school9);
//
//        School school10 = new School();
//        school10.setSchoolCity("안산시");
//        school10.setSchoolName("구.화정초");
//        school10.setSchoolAddress("경기도 안산시 단원구 꽃우물길97");
//
//        school10.setSchoolPhone("031-412-4653");
//        school10.setSchoolImagePath("C:\\school");
//        school10.setSchoolImageName("안산시_구.화정초.jpg");
//        schoolRepository.save(school10);
//
//        School school11 = new School();
//        school11.setSchoolCity("안산시");
//        school11.setSchoolName("대동초선감분교장");
//        school11.setSchoolAddress("경기도 안산시 단원구 대부황금로603");
//
//        school11.setSchoolPhone("031-412-4653");
//        school11.setSchoolImagePath("C:\\school");
//        school11.setSchoolImageName("안산시_대동초선감분교장.jpg");
//        schoolRepository.save(school11);
//
//        School school12 = new School();
//        school12.setSchoolCity("평택시");
//        school12.setSchoolName("서탄초금각분교장");
//        school12.setSchoolAddress("경기도 평택시 서탄면 용소금각로438-14");
//
//        school12.setSchoolPhone("031-650-1274");
//        school12.setSchoolImagePath("C:\\school");
//        school12.setSchoolImageName("평택시_서탄초금각분교장.jpg");
//        schoolRepository.save(school12);
//
//        School school13 = new School();
//        school13.setSchoolCity("평택시");
//        school13.setSchoolName("내기초 신영분교장");
//        school13.setSchoolAddress("경기도 평택시 포승읍 신영새싹길 86-29");
//
//        school13.setSchoolPhone("031-650-1274");
//        school13.setSchoolImagePath("C:\\school");
//        school13.setSchoolImageName("평택시_내기초 신영분교장.jpg");
//        schoolRepository.save(school13);
//
//        School school14 = new School();
//        school14.setSchoolCity("평택시");
//        school14.setSchoolName("구.평택중");
//        school14.setSchoolAddress("경기도 평택시 평택2로 101");
//        school14.setSchoolLand(17949);
//        school14.setSchoolArea(9051);
//        school14.setSchoolPhone("031-650-1274");
//        school14.setSchoolImagePath("C:\\school");
//        school14.setSchoolImageName("평택시_구.평택중.jpg");
//        schoolRepository.save(school14);
//
//        School school15 = new School();
//        school15.setSchoolCity("여주시");
//        school15.setSchoolName("점동초당현분교장");
//        school15.setSchoolAddress("경기도 여주시 점동면 성주로946");
//        school15.setSchoolLand(9532);
//        school15.setSchoolArea(784);
//        school15.setSchoolPhone("031-880-2318");
//        school15.setSchoolImagePath("C:\\school");
//        school15.setSchoolImageName("여주시_점동초당현분교장.jpg");
//        schoolRepository.save(school15);
////준서 --------------------------------------------------------------------------------------------------
//        School school16 = new School();
//        school16.setSchoolCity("여주시");
//        school16.setSchoolName("강천초강남분교장");
//        school16.setSchoolAddress("경기도 여주시 강천면 강천로324-20");
//        school16.setSchoolLand(12717);
//        school16.setSchoolArea(929);
//        school16.setSchoolPhone("031-880-2318");
//        school16.setSchoolImagePath("C:\\school");
//        school16.setSchoolImageName("여주시_강천초강남분교장.jpg");
//        schoolRepository.save(school16);
//
//        School school17 = new School();
//        school17.setSchoolCity("여주시");
//        school17.setSchoolName("대신초옥촌분교장");
//        school17.setSchoolAddress("경기도 여주시 대신면 대신1로298");
//        school17.setSchoolLand(8879);
//        school17.setSchoolArea(476);
//        school17.setSchoolPhone("031-880-2318");
//        school17.setSchoolImagePath("C:\\school");
//        school17.setSchoolImageName("여주시_대신초옥촌분교장.jpg");
//        schoolRepository.save(school17);
//
//        School school18 = new School();
//        school18.setSchoolCity("여주시");
//        school18.setSchoolName("강천초걸은분교장");
//        school18.setSchoolAddress("경기도 여주시 강천면 마감로117-14");
//        school18.setSchoolLand(12256);
//        school18.setSchoolArea(950);
//        school18.setSchoolPhone("031-880-2318");
//        school18.setSchoolImagePath("C:\\school");
//        school18.setSchoolImageName("여주시_강천초걸은분교장.jpg");
//        schoolRepository.save(school18);
//
//        School school19 = new School();
//        school19.setSchoolCity("여주시");
//        school19.setSchoolName("점동초뇌곡분교장");
//        school19.setSchoolAddress("경기도 여주시 점동면 성주로584");
//        school19.setSchoolLand(10115);
//        school19.setSchoolArea(1110);
//        school19.setSchoolPhone("031-880-2318");
//        school19.setSchoolImagePath("C:\\school");
//        school19.setSchoolImageName("여주시_점동초뇌곡분교장.jpg");
//        schoolRepository.save(school19);
//
//        School school20 = new School();
//        school20.setSchoolCity("여주시");
//        school20.setSchoolName("북내초주암분교장");
//        school20.setSchoolAddress("경기도 여주시 북내면 주암길14");
//        school20.setSchoolLand(12338);
//        school20.setSchoolArea(1124);
//        school20.setSchoolPhone("031-880-2318");
//        school20.setSchoolImagePath("C:\\school");
//        school20.setSchoolImageName("여주시_북내초주암분교장.jpg");
//        schoolRepository.save(school20);
//
//        School school21 = new School();
//        school21.setSchoolCity("여주시");
//        school21.setSchoolName("점동초안평분교장");
//        school21.setSchoolAddress("경기도 여주시 점동면 안평로296");
//        school21.setSchoolLand(7290);
//        school21.setSchoolArea(712);
//        school21.setSchoolPhone("031-880-2318");
//        school21.setSchoolImagePath("C:\\school");
//        school21.setSchoolImageName("여주시_점동초안평분교장.jpeg");
//        schoolRepository.save(school21);
//
//        School school22 = new School();
//        school22.setSchoolCity("화성시");
//        school22.setSchoolName("비봉초유포분교장");
//        school22.setSchoolAddress("경기도 화성시 비봉면 남이로당곡말길34-11");
//        school22.setSchoolLand(4469);
//        school22.setSchoolArea(267);
//        school22.setSchoolPhone("031-371-0752");
//        school22.setSchoolImagePath("C:\\school");
//        school22.setSchoolImageName("화성시_비봉초유포분교장.jpeg");
//        schoolRepository.save(school22);
//
//        School school23 = new School();
//        school23.setSchoolCity("화성시");
//        school23.setSchoolName("정문초");
//        school23.setSchoolAddress("경기도 화성시 정남면 계향북길104");
//        school23.setSchoolLand(11232);
//        school23.setSchoolArea(1193);
//        school23.setSchoolPhone("031-371-0752");
//        school23.setSchoolImagePath("C:\\school");
//        school23.setSchoolImageName("화성시_정문초.jpg");
//        schoolRepository.save(school23);
//
//        School school24 = new School();
//        school24.setSchoolCity("화성시");
//        school24.setSchoolName("마산초어도분교장");
//        school24.setSchoolAddress("경기도 화성시 송산면 어섬길259번길64");
//        school24.setSchoolLand(3534);
//        school24.setSchoolArea(0);
//        school24.setSchoolPhone("031-371-0752");
//        school24.setSchoolImagePath("C:\\school");
//        school24.setSchoolImageName("화성시_마산초어도분교장.jpg");
//        schoolRepository.save(school24);
//
//        School school25 = new School();
//        school25.setSchoolCity("화성시");
//        school25.setSchoolName("창문초");
//        school25.setSchoolAddress("경기도 화성시 남양읍 남양로1405번길9");
//        school25.setSchoolLand(13721);
//        school25.setSchoolArea(1432);
//        school25.setSchoolPhone("031-371-0752");
//        school25.setSchoolImagePath("C:\\school");
//        school25.setSchoolImageName("화성시_창문초.jpg");
//        schoolRepository.save(school25);
//
//        School school26 = new School();
//        school26.setSchoolCity("화성시");
//        school26.setSchoolName("구.상신초");
//        school26.setSchoolAddress("경기도 화성시 향남읍 상신초교길52");
//        school26.setSchoolLand(14229);
//        school26.setSchoolArea(3087);
//        school26.setSchoolPhone("031-371-0752");
//        school26.setSchoolImagePath("C:\\school");
//        school26.setSchoolImageName("화성시_구.상신초.jpg");
//        schoolRepository.save(school26);
//
//        School school27 = new School();
//        school27.setSchoolCity("화성시");
//        school27.setSchoolName("서신초제부분교장");
//        school27.setSchoolAddress("경기도 화성시 서신면 제부말길74");
//        school27.setSchoolLand(14670);
//        school27.setSchoolArea(2765);
//        school27.setSchoolPhone("031-371-0752");
//        school27.setSchoolImagePath("C:\\school");
//        school27.setSchoolImageName("화성시_서신초제부분교장.jpg");
//        schoolRepository.save(school27);
//
//        School school28 = new School();
//        school28.setSchoolCity("광주시");
//        school28.setSchoolName("만선초봉현분교장");
//        school28.setSchoolAddress("");
//        school28.setSchoolLand(5999);
//        school28.setSchoolArea(0);
//        school28.setSchoolPhone("031-760-4057");
//        school28.setSchoolImagePath("C:\\school");
//        school28.setSchoolImageName("광주시_만선초봉현분교장.jpg");
//        schoolRepository.save(school28);
//
//        School school29 = new School();
//        school29.setSchoolCity("광주시");
//        school29.setSchoolName("분원초검천분교장");
//        school29.setSchoolAddress("경기도 광주시 남종면 산수로2600");
//        school29.setSchoolLand(7307);
//        school29.setSchoolArea(775);
//        school29.setSchoolPhone("031-760-4057");
//        school29.setSchoolImagePath("C:\\school");
//        school29.setSchoolImageName("광주시_분원초검천분교장.jpeg");
//        schoolRepository.save(school29);
//
//        School school30 = new School();
//        school30.setSchoolCity("광주시");
//        school30.setSchoolName("도수초삼성분교장");
//        school30.setSchoolAddress("경기도 광주시 남종면 태허정로437-45");
//        school30.setSchoolLand(3094);
//        school30.setSchoolArea(291);
//        school30.setSchoolPhone("031-760-4057");
//        school30.setSchoolImagePath("C:\\school");
//        school30.setSchoolImageName("광주시_도수초삼성분교장.jpg");
//        schoolRepository.save(school30);
//
//        School school31 = new School();
//        school31.setSchoolCity("광주시");
//        school31.setSchoolName("탄벌초목현분교장");
//        school31.setSchoolAddress("");
//        school31.setSchoolLand(6625);
//        school31.setSchoolArea(0);
//        school31.setSchoolPhone("031-760-4057");
//        school31.setSchoolImagePath("C:\\school");
//        school31.setSchoolImageName("광주시_탄벌초목현분교장.jpg");
//        schoolRepository.save(school31);
//
//        School school32 = new School();
//        school32.setSchoolCity("양평군");
//        school32.setSchoolName("금왕초");
//        school32.setSchoolAddress("경기도 양평군 양동면 양동로1112");
//        school32.setSchoolLand(10319);
//        school32.setSchoolArea(1088);
//        school32.setSchoolPhone("031-770-5683");
//        school32.setSchoolImagePath("C:\\school");
//        school32.setSchoolImageName("양평군_금왕초.jpg");
//        schoolRepository.save(school32);
//
//        School school33 = new School();
//        school33.setSchoolCity("양평군");
//        school33.setSchoolName("강남초");
//        school33.setSchoolAddress("경기도 양평군 강하면 동오3길21");
//        school33.setSchoolLand(11613);
//        school33.setSchoolArea(756);
//        school33.setSchoolPhone("031-770-5683");
//        school33.setSchoolImagePath("C:\\school");
//        school33.setSchoolImageName("양평군_강남초.jpg");
//        schoolRepository.save(school33);
//
//        School school34 = new School();
//        school34.setSchoolCity("양평군");
//        school34.setSchoolName("청운초신론분교장");
//        school34.setSchoolAddress("경기도 양평군 청운면 신론로346");
//        school34.setSchoolLand(4999);
//        school34.setSchoolArea(1412);
//        school34.setSchoolPhone("031-770-5683");
//        school34.setSchoolImagePath("C:\\school");
//        school34.setSchoolImageName("양평군_청운초신론분교장.png");
//        schoolRepository.save(school34);
////승훈 --------------------------------------------------------------------------------------------------
//        School school35 = new School();
//        school35.setSchoolCity("양평군");
//        school35.setSchoolName("부안초명성분교장");
//        school35.setSchoolAddress("경기도 양평군 단월면 분지울2길6-4");
//        school35.setSchoolLand(5040);
//        school35.setSchoolArea(390);
//        school35.setSchoolPhone("031-770-5683");
//        school35.setSchoolImagePath("C:\\school");
//        school35.setSchoolImageName("양평군_부안초명성분교장.jpg");
//        schoolRepository.save(school35);
//
//        School school36 = new School();
//        school36.setSchoolCity("양평군");
//        school36.setSchoolName("석장초");
//        school36.setSchoolAddress("경기도 양평군 개군면 석장길60-6");
//        school36.setSchoolLand(7452);
//        school36.setSchoolArea(1009);
//        school36.setSchoolPhone("031-770-5683");
//        school36.setSchoolImagePath("C:\\school");
//        school36.setSchoolImageName("양평군_석장초.jpg");
//        schoolRepository.save(school36);
//
//        School school37 = new School();
//        school37.setSchoolCity("양평군");
//        school37.setSchoolName("양평단월초산음분교장");
//        school37.setSchoolAddress("경기도 양평군 단월면 봉미산샘골길1");
//        school37.setSchoolLand(7175);
//        school37.setSchoolArea(723);
//        school37.setSchoolPhone("031-770-5683");
//        school37.setSchoolImagePath("C:\\school");
//        school37.setSchoolImageName("양평군_양평단월초산음분교장.jpg");
//        schoolRepository.save(school37);
//
//        School school38 = new School();
//        school38.setSchoolCity("양평군");
//        school38.setSchoolName("양평부안초");
//        school38.setSchoolAddress("경기도 양평군 단월면 단월로302-17");
//        school38.setSchoolLand(9709);
//        school38.setSchoolArea(1110);
//        school38.setSchoolPhone("031-770-5683");
//        school38.setSchoolImagePath("C:\\school");
//        school38.setSchoolImageName("양평군_양평부안초.jpg");
//        schoolRepository.save(school38);
//
//        School school39 = new School();
//        school39.setSchoolCity("양평군");
//        school39.setSchoolName("양동초매월분교장");
//        school39.setSchoolAddress("경기도 양평군 양동면 월은길31");
//        school39.setSchoolLand(8894);
//        school39.setSchoolArea(923);
//        school39.setSchoolPhone("031-770-5683");
//        school39.setSchoolImagePath("C:\\school");
//        school39.setSchoolImageName("양평군_양동초매월분교장.jpg");
//        schoolRepository.save(school39);
//
//        School school40 = new School();
//        school40.setSchoolCity("양평군");
//        school40.setSchoolName("계정초");
//        school40.setSchoolAddress("경기도 양평군 양동면 구창말길2");
//        school40.setSchoolLand(8598);
//        school40.setSchoolArea(1171);
//        school40.setSchoolPhone("031-770-5683");
//        school40.setSchoolImagePath("C:\\school");
//        school40.setSchoolImageName("양평군_계정초.jpg");
//        schoolRepository.save(school40);
//
//        School school41 = new School();
//        school41.setSchoolCity("양평군");
//        school41.setSchoolName("양동초단석분교장");
//        school41.setSchoolAddress("경기도 양평군 양동면 활거리길16-19");
//        school41.setSchoolLand(12566);
//        school41.setSchoolArea(1160);
//        school41.setSchoolPhone("031-770-5683");
//        school41.setSchoolImagePath("C:\\school");
//        school41.setSchoolImageName("양평군_양동초단석분교장.jpg");
//        schoolRepository.save(school41);
//
//        School school42 = new School();
//        school42.setSchoolCity("양평군");
//        school42.setSchoolName("지평초일신분교장");
//        school42.setSchoolAddress("경기도 양평군 지평면 구둔영화체험길17");
//        school42.setSchoolLand(7327);
//        school42.setSchoolArea(1180);
//        school42.setSchoolPhone("031-770-5683");
//        school42.setSchoolImagePath("C:\\school");
//        school42.setSchoolImageName("양평군_지평초일신분교장.jpg");
//        schoolRepository.save(school42);
//
//        School school43 = new School();
//        school43.setSchoolCity("양평군");
//        school43.setSchoolName("양동초삼산분교장");
//        school43.setSchoolAddress("경기도 양평군 양동면 도소리길44");
//        school43.setSchoolLand(8709);
//        school43.setSchoolArea(855);
//        school43.setSchoolPhone("031-770-5683");
//        school43.setSchoolImagePath("C:\\school");
//        school43.setSchoolImageName("양평군_양동초삼산분교장.jpg");
//        schoolRepository.save(school43);
//
//        School school44 = new School();
//        school44.setSchoolCity("양평군");
//        school44.setSchoolName("청운초갈운분교장");
//        school44.setSchoolAddress("경기도 양평군 청운면 경강로4867");
//        school44.setSchoolLand(8206);
//        school44.setSchoolArea(799);
//        school44.setSchoolPhone("031-770-5683");
//        school44.setSchoolImagePath("C:\\school");
//        school44.setSchoolImageName("양평군_청운초갈운분교장.jpg");
//        schoolRepository.save(school44);
//
//        School school45 = new School();
//        school45.setSchoolCity("이천시");
//        school45.setSchoolName("율면초월포분교장");
//        school45.setSchoolAddress("경기도 이천시 율면 임오산로362번길74-10");
//        school45.setSchoolLand(9953);
//        school45.setSchoolArea(403);
//        school45.setSchoolPhone("031-639-5673");
//        school45.setSchoolImagePath("C:\\school");
//        school45.setSchoolImageName("이천시_율면초월포분교장.jpg");
//        schoolRepository.save(school45);
//
//        School school46 = new School();
//        school46.setSchoolCity("이천시");
//        school46.setSchoolName("부발초백록분교장");
//        school46.setSchoolAddress("경기도 이천시 부발읍 두무재로79번길21-22");
//        school46.setSchoolLand(14527);
//        school46.setSchoolArea(1756);
//        school46.setSchoolPhone("031-639-5673");
//        school46.setSchoolImagePath("C:\\school");
//        school46.setSchoolImageName("이천시_부발초백록분교장.jpg");
//        schoolRepository.save(school46);
//
//        School school47 = new School();
//        school47.setSchoolCity("이천시");
//        school47.setSchoolName("진가초모가분교장");
//        school47.setSchoolAddress("경기도 이천시 모가면 사실로1035");
//        school47.setSchoolLand(19448);
//        school47.setSchoolArea(1102);
//        school47.setSchoolPhone("031-639-5673");
//        school47.setSchoolImagePath("C:\\school");
//        school47.setSchoolImageName("이천시_진가초모가분교장.jpg");
//        schoolRepository.save(school47);
//
//        School school48 = new School();
//        school48.setSchoolCity("용인시");
//        school48.setSchoolName("남사초서촌분교장");
//        school48.setSchoolAddress("경기도 용인시 처인구 남사읍 통삼로282-19");
//        school48.setSchoolLand(10169);
//        school48.setSchoolArea(0);
//        school48.setSchoolPhone("031-8020-9357");
//        school48.setSchoolImagePath("C:\\school");
//        school48.setSchoolImageName("용인시_남사초서촌분교장.jpg");
//        schoolRepository.save(school48);
//
//        School school49 = new School();
//        school49.setSchoolCity("용인시");
//        school49.setSchoolName("어정초동진원분교장");
//        school49.setSchoolAddress("경기도 용인시 기흥구 언동로 125번길9-4");
//        school49.setSchoolLand(2387);
//        school49.setSchoolArea(0);
//        school49.setSchoolPhone("031-8020-9357");
//        school49.setSchoolImagePath("C:\\school");
//        school49.setSchoolImageName("용인시_어정초동진원분교장.jpg");
//        schoolRepository.save(school49);
//
//        School school50 = new School();
//        school50.setSchoolCity("용인시");
//        school50.setSchoolName("남곡초 남곡분교장");
//        school50.setSchoolAddress("경기도 용인시 처인구 남사읍 남산로 7");
//        school50.setSchoolLand(17634);
//        school50.setSchoolArea(2068);
//        school50.setSchoolPhone("031-8020-9357");
//        school50.setSchoolImagePath("C:\\school");
//        school50.setSchoolImageName("용인시_남곡초 남곡분교장.jpg");
//        schoolRepository.save(school50);
//
//        School school51 = new School();
//        school51.setSchoolCity("용인시");
//        school51.setSchoolName("기흥중");
//        school51.setSchoolAddress("경기도 용인시 기흥구 신갈로138번길23");
//        school51.setSchoolLand(12972);
//        school51.setSchoolArea(6495);
//        school51.setSchoolPhone("031-8020-9357");
//        school51.setSchoolImagePath("C:\\school");
//        school51.setSchoolImageName("용인시_기흥중.jpg");
//        schoolRepository.save(school51);
////승찬 --------------------------------------------------------------------------------------------------
//        School school52 = new School();
//        school52.setSchoolCity("안성시");
//        school52.setSchoolName("고삼초고동분교장");
//        school52.setSchoolAddress("경기도 안성시 고삼면 사동길32");
//        school52.setSchoolLand(10635);
//        school52.setSchoolArea(0);
//        school52.setSchoolPhone("031-678-5271");
//        school52.setSchoolImagePath("C:\\school");
//        school52.setSchoolImageName("안성시_고삼초고동분교장.jpg");
//        schoolRepository.save(school52);
//
//        School school53 = new School();
//        school53.setSchoolCity("안성시");
//        school53.setSchoolName("양성초방축분교장");
//        school53.setSchoolAddress("경기도 안성시 양성면 한내로499");
//        school53.setSchoolLand(5636);
//        school53.setSchoolArea(721);
//        school53.setSchoolPhone("031-678-5271");
//        school53.setSchoolImagePath("C:\\school");
//        school53.setSchoolImageName("안성시_양성초방축분교장.jpg");
//        schoolRepository.save(school53);
//
//        School school54 = new School();
//        school54.setSchoolCity("안성시");
//        school54.setSchoolName("장암초금산분교장");
//        school54.setSchoolAddress("경기도 안성시 일죽면 일생로275");
//        school54.setSchoolLand(9371);
//        school54.setSchoolArea(696);
//        school54.setSchoolPhone("031-678-5271");
//        school54.setSchoolImagePath("C:\\school");
//        school54.setSchoolImageName("안성시_장암초금산분교장.jpg");
//        schoolRepository.save(school54);
//
//        School school55 = new School();
//        school55.setSchoolCity("안성시");
//        school55.setSchoolName("공제초");
//        school55.setSchoolAddress("경기도 안성시 공도읍 웅교3길27");
//        school55.setSchoolLand(18591);
//        school55.setSchoolArea(1111);
//        school55.setSchoolPhone("031-678-5271");
//        school55.setSchoolImagePath("C:\\school");
//        school55.setSchoolImageName("안성시_공제초.jpg");
//        schoolRepository.save(school55);
//
//        School school56 = new School();
//        school56.setSchoolCity("안성시");
//        school56.setSchoolName("장암초");
//        school56.setSchoolAddress("경기도 안성시 일죽면 장암초교길8");
//        school56.setSchoolLand(9129);
//        school56.setSchoolArea(983);
//        school56.setSchoolPhone("031-678-5271");
//        school56.setSchoolImagePath("C:\\school");
//        school56.setSchoolImageName("안성시_장암초.jpg");
//        schoolRepository.save(school56);
//
//        School school57 = new School();
//        school57.setSchoolCity("안성시");
//        school57.setSchoolName("원곡초성은분교장");
//        school57.setSchoolAddress("경기도 안성시 원곡면 통심길92");
//        school57.setSchoolLand(8739);
//        school57.setSchoolArea(640);
//        school57.setSchoolPhone("031-678-5271");
//        school57.setSchoolImagePath("C:\\school");
//        school57.setSchoolImageName("안성시_원곡초성은분교장.jpg");
//        schoolRepository.save(school57);
//
//        School school58 = new School();
//        school58.setSchoolCity("안성시");
//        school58.setSchoolName("구.백성초");
//        school58.setSchoolAddress("경기도 안성시 백성2길59");
//        school58.setSchoolLand(24820);
//        school58.setSchoolArea(8469);
//        school58.setSchoolPhone("031-678-5271");
//        school58.setSchoolImagePath("C:\\school");
//        school58.setSchoolImageName("안성시_구.백성초.jpg");
//        schoolRepository.save(school58);
//
//        School school59 = new School();
//        school59.setSchoolCity("안성시");
//        school59.setSchoolName("서삼초");
//        school59.setSchoolAddress("경기도 안성시 보개면 보개원삼로1001");
//        school59.setSchoolLand(11225);
//        school59.setSchoolArea(1911);
//        school59.setSchoolPhone("031-678-5271");
//        school59.setSchoolImagePath("C:\\school");
//        school59.setSchoolImageName("안성시_서삼초.jpg");
//        schoolRepository.save(school59);
//
//        School school60 = new School();
//        school60.setSchoolCity("안성시");
//        school60.setSchoolName("방초초");
//        school60.setSchoolAddress("경기도 안성시 일죽면 사실로 86");
//        school60.setSchoolLand(11319);
//        school60.setSchoolArea(1744);
//        school60.setSchoolPhone("031-678-5271");
//        school60.setSchoolImagePath("C:\\school");
//        school60.setSchoolImageName("안성시_방초초.jpg");
//        schoolRepository.save(school60);
//
//        School school61 = new School();
//        school61.setSchoolCity("동두천시");
//        school61.setSchoolName("동두천초걸산분교장");
//        school61.setSchoolAddress("");
//        school61.setSchoolLand(2175);
//        school61.setSchoolArea(0);
//        school61.setSchoolPhone("031-860-4372");
//        school61.setSchoolImagePath("C:\\school");
//        school61.setSchoolImageName("동두천시_동두천초걸산분교장.jpg");
//        schoolRepository.save(school61);
//
//        School school62 = new School();
//        school62.setSchoolCity("양주시");
//        school62.setSchoolName("가납초현암분교장");
//        school62.setSchoolAddress("");
//        school62.setSchoolLand(6809);
//        school62.setSchoolArea(0);
//        school62.setSchoolPhone("031-870-4041");
//        school62.setSchoolImagePath("C:\\school");
//        school62.setSchoolImageName("양주시_가납초현암분교장.jpg");
//        schoolRepository.save(school62);
//
//        School school63 = new School();
//        school63.setSchoolCity("양주시");
//        school63.setSchoolName("유양초천성분교장");
//        school63.setSchoolAddress("경기도 양주시 부흥로 1185번길15");
//        school63.setSchoolLand(2030);
//        school63.setSchoolArea(0);
//        school63.setSchoolPhone("031-870-4041");
//        school63.setSchoolImagePath("C:\\school");
//        school63.setSchoolImageName("양주시_유양초천성분교장.jpg");
//        schoolRepository.save(school63);
//
//        School school64 = new School();
//        school64.setSchoolCity("고양시");
//        school64.setSchoolName("백마초장항분교장");
//        school64.setSchoolAddress("경기도 고양시 일산동구 장대길 64-27");
//        school64.setSchoolLand(4355);
//        school64.setSchoolArea(497);
//        school64.setSchoolPhone("031-900-2973");
//        school64.setSchoolImagePath("C:\\school");
//        school64.setSchoolImageName("고양시_백마초장항분교장.jpg");
//        schoolRepository.save(school64);
//
//        School school65 = new School();
//        school65.setSchoolCity("고양시");
//        school65.setSchoolName("구.고양중");
//        school65.setSchoolAddress("경기도 고양시 덕양구 삼송로173");
//        school65.setSchoolLand(19545);
//        school65.setSchoolArea(5035);
//        school65.setSchoolPhone("031-900-2973");
//        school65.setSchoolImagePath("C:\\school");
//        school65.setSchoolImageName("고양시_구.고양중.jpg");
//        schoolRepository.save(school65);
//
//        School school66 = new School();
//        school66.setSchoolCity("고양시");
//        school66.setSchoolName("구.삼송초");
//        school66.setSchoolAddress("");
//        school66.setSchoolLand(4679);
//        school66.setSchoolArea(0);
//        school66.setSchoolPhone("031-900-2973");
//        school66.setSchoolImagePath("C:\\school");
//        school66.setSchoolImageName("고양시_구.삼송초.jpg");
//        schoolRepository.save(school66);
//
//        School school67 = new School();
//        school67.setSchoolCity("남양주시");
//        school67.setSchoolName("남양주송촌초시우분교장");
//        school67.setSchoolAddress("경기도 남양주시 조안면 고래산로413-77");
//        school67.setSchoolLand(3738);
//        school67.setSchoolArea(146);
//        school67.setSchoolPhone("031-550-6253");
//        school67.setSchoolImagePath("C:\\school");
//        school67.setSchoolImageName("남양주시_남양주송촌초시우분교장.jpg");
//        schoolRepository.save(school67);
//
//        School school68 = new School();
//        school68.setSchoolCity("남양주시");
//        school68.setSchoolName("마석초녹촌분교장");
//        school68.setSchoolAddress("경기도 남양주시 화도읍 가구단지6길63-12");
//        school68.setSchoolLand(7392);
//        school68.setSchoolArea(431);
//        school68.setSchoolPhone("031-550-6253");
//        school68.setSchoolImagePath("C:\\school");
//        school68.setSchoolImageName("남양주시_마석초녹촌분교장.jpg");
//        schoolRepository.save(school68);
////승원 --------------------------------------------------------------------------------------------------
//        School school69 = new School();
//        school69.setSchoolCity("파주시");
//        school69.setSchoolName("용운초");
//        school69.setSchoolAddress("경기도 파주시 적성면 윗배우니길68");
//        school69.setSchoolLand(13171);
//        school69.setSchoolArea(1343);
//        school69.setSchoolPhone("031-940-7334");
//        school69.setSchoolImagePath("C:\\school");
//        school69.setSchoolImageName("파주시_용운초.jpg");
//        schoolRepository.save(school69);
//
//        School school70 = new School();
//        school70.setSchoolCity("파주시");
//        school70.setSchoolName("금곡초");
//        school70.setSchoolAddress("경기도 파주시 법원읍 술이홀로1315");
//        school70.setSchoolLand(9414);
//        school70.setSchoolArea(1017);
//        school70.setSchoolPhone("031-940-7334");
//        school70.setSchoolImagePath("C:\\school");
//        school70.setSchoolImageName("파주시_금곡초.jpg");
//        schoolRepository.save(school70);
//
//        School school71 = new School();
//        school71.setSchoolCity("파주시");
//        school71.setSchoolName("법원초");
//        school71.setSchoolAddress("경기도 파주시 법원읍 술이홀로970-9");
//        school71.setSchoolLand(13253);
//        school71.setSchoolArea(2326);
//        school71.setSchoolPhone("031-940-7334");
//        school71.setSchoolImagePath("C:\\school");
//        school71.setSchoolImageName("파주시_법원초.png");
//        schoolRepository.save(school71);
//
//        School school72 = new School();
//        school72.setSchoolCity("파주시");
//        school72.setSchoolName("적성초");
//        school72.setSchoolAddress("경기도 파주시 적성면 달빛길54");
//        school72.setSchoolLand(14716);
//        school72.setSchoolArea(1443);
//        school72.setSchoolPhone("031-940-7334");
//        school72.setSchoolImagePath("C:\\school");
//        school72.setSchoolImageName("파주시_적성초.jpg");
//        schoolRepository.save(school72);
//
//        School school73 = new School();
//        school73.setSchoolCity("파주시");
//        school73.setSchoolName("구.교하중");
//        school73.setSchoolAddress("경기도 파주시 교하로1290번길38");
//        school73.setSchoolLand(20297);
//        school73.setSchoolArea(2231);
//        school73.setSchoolPhone("031-940-7334");
//        school73.setSchoolImagePath("C:\\school");
//        school73.setSchoolImageName("파주시_구.교하중.png");
//        schoolRepository.save(school73);
//
//        School school74 = new School();
//        school74.setSchoolCity("파주시");
//        school74.setSchoolName("신산초영장분교장");
//        school74.setSchoolAddress("경기도 파주시 광탄면 쇠장이길21");
//        school74.setSchoolLand(7473);
//        school74.setSchoolArea(449);
//        school74.setSchoolPhone("031-940-7334");
//        school74.setSchoolImagePath("C:\\school");
//        school74.setSchoolImageName("파주시_신산초영장분교장.jpg");
//        schoolRepository.save(school74);
//
//        School school75 = new School();
//        school75.setSchoolCity("연천군");
//        school75.setSchoolName("연천왕산초마전분교장(A)");
//        school75.setSchoolAddress("경기도 연천군 미산면 마동로59");
//        school75.setSchoolLand(6033);
//        school75.setSchoolArea(517);
//        school75.setSchoolPhone("031-839-0222");
//        school75.setSchoolImagePath("C:\\school");
//        school75.setSchoolImageName("연천군_연천왕산초마전분교장(A).jpg");
//        schoolRepository.save(school75);
//
//        School school76 = new School();
//        school76.setSchoolCity("연천군");
//        school76.setSchoolName("연천왕산초동중분교장");
//        school76.setSchoolAddress("경기도 연천군 왕징면 왕산로816");
//        school76.setSchoolLand(7479);
//        school76.setSchoolArea(0);
//        school76.setSchoolPhone("031-839-0222");
//        school76.setSchoolImagePath("C:\\school");
//        school76.setSchoolImageName("연천군_연천왕산초동중분교장.jpg");
//        schoolRepository.save(school76);
//
//        School school77 = new School();
//        school77.setSchoolCity("연천군");
//        school77.setSchoolName("연천초고문분교장");
//        school77.setSchoolAddress("경기도 연천군 연천읍 현문로459");
//        school77.setSchoolLand(8937);
//        school77.setSchoolArea(807);
//        school77.setSchoolPhone("031-839-0222");
//        school77.setSchoolImagePath("C:\\school");
//        school77.setSchoolImageName("연천군_연천초고문분교장.jpg");
//        schoolRepository.save(school77);
//
//        School school78 = new School();
//        school78.setSchoolCity("연천군");
//        school78.setSchoolName("전곡초양원분교장");
//        school78.setSchoolAddress("경기도 연천군 전곡읍 양원로46번길20");
//        school78.setSchoolLand(11283);
//        school78.setSchoolArea(551);
//        school78.setSchoolPhone("031-839-0222");
//        school78.setSchoolImagePath("C:\\school");
//        school78.setSchoolImageName("연천군_전곡초양원분교장.jpg");
//        schoolRepository.save(school78);
//
//        School school79 = new School();
//        school79.setSchoolCity("연천군");
//        school79.setSchoolName("군남초북삼분교장");
//        school79.setSchoolAddress("경기도 연천군 왕징면 북삼로98");
//        school79.setSchoolLand(4863);
//        school79.setSchoolArea(259);
//        school79.setSchoolPhone("031-839-0222");
//        school79.setSchoolImagePath("C:\\school");
//        school79.setSchoolImageName("연천군_군남초북삼분교장.jpg");
//        schoolRepository.save(school79);
//
//        School school80 = new School();
//        school80.setSchoolCity("연천군");
//        school80.setSchoolName("옥계초");
//        school80.setSchoolAddress("경기도 연천군 군남면 옥계안길24-43");
//        school80.setSchoolLand(8114);
//        school80.setSchoolArea(1246);
//        school80.setSchoolPhone("031-839-0222");
//        school80.setSchoolImagePath("C:\\school");
//        school80.setSchoolImageName("연천군_옥계초.jpg");
//        schoolRepository.save(school80);
//
//        School school81 = new School();
//        school81.setSchoolCity("연천군");
//        school81.setSchoolName("백학초고랑포분교장");
//        school81.setSchoolAddress("경기도 연천군 장남면 원당로87번길34");
//        school81.setSchoolLand(12979);
//        school81.setSchoolArea(1031);
//        school81.setSchoolPhone("031-839-0222");
//        school81.setSchoolImagePath("C:\\school");
//        school81.setSchoolImageName("연천군_백학초고랑포분교장.jpg");
//        schoolRepository.save(school81);
//
//        School school82 = new School();
//        school82.setSchoolCity("연천군");
//        school82.setSchoolName("연천왕산초마전분교장(B)");
//        school82.setSchoolAddress("경기도 연천군 미산면 유노로164");
//        school82.setSchoolLand(12878);
//        school82.setSchoolArea(500);
//        school82.setSchoolPhone("031-839-0222");
//        school82.setSchoolImagePath("C:\\school");
//        school82.setSchoolImageName("연천군_연천왕산초마전분교장(B).jpg");
//        schoolRepository.save(school82);
//
//        School school83 = new School();
//        school83.setSchoolCity("연천군");
//        school83.setSchoolName("구.군남초");
//        school83.setSchoolAddress("경기도 연천군 군남면 군남로373");
//        school83.setSchoolLand(12920);
//        school83.setSchoolArea(2150);
//        school83.setSchoolPhone("031-839-0222");
//        school83.setSchoolImagePath("C:\\school");
//        school83.setSchoolImageName("연천군_구.군남초.png");
//        schoolRepository.save(school83);
//
//        School school84 = new School();
//        school84.setSchoolCity("연천군");
//        school84.setSchoolName("전곡초적동분교장");
//        school84.setSchoolAddress("경기도 연천군 전곡읍 첫마을길17");
//        school84.setSchoolLand(8869);
//        school84.setSchoolArea(745);
//        school84.setSchoolPhone("031-839-0222");
//        school84.setSchoolImagePath("C:\\school");
//        school84.setSchoolImageName("연천군_전곡초적동분교장.png");
//        schoolRepository.save(school84);
//
//        School school85 = new School();
//        school85.setSchoolCity("연천군");
//        school85.setSchoolName("구.대광중");
//        school85.setSchoolAddress("경기도 연천군 신서면 구시울길23");
//        school85.setSchoolLand(33545);
//        school85.setSchoolArea(2939);
//        school85.setSchoolPhone("031-839-0222");
//        school85.setSchoolImagePath("C:\\school");
//        school85.setSchoolImageName("연천군_구.대광중.png");
//        schoolRepository.save(school85);
//
////영서 --------------------------------------------------------------------------------------------------
//        School school86 = new School();
//        school86.setSchoolCity("포천시");
//        school86.setSchoolName("삼정초금동분교장");
//        school86.setSchoolAddress("경기도 포천시 신북면 탑신로1148");
//        school86.setSchoolLand(7538);
//        school86.setSchoolArea(0);
//        school86.setSchoolPhone("031-539-0033");
//        school86.setSchoolImagePath("C:\\school");
//        school86.setSchoolImageName("포천시_삼정초금동분교장.png");
//        schoolRepository.save(school86);
//
//        School school87 = new School();
//        school87.setSchoolCity("포천시");
//        school87.setSchoolName("관인초사정분교장");
//        school87.setSchoolAddress("경기도 포천시 관인면 북원로371-115");
//        school87.setSchoolLand(9787);
//        school87.setSchoolArea(650);
//        school87.setSchoolPhone("031-539-0033");
//        school87.setSchoolImagePath("C:\\school");
//        school87.setSchoolImageName("포천시_관인초사정분교장.png");
//        schoolRepository.save(school87);
//
//        School school88 = new School();
//        school88.setSchoolCity("포천시");
//        school88.setSchoolName("보장초");
//        school88.setSchoolAddress("경기도 포천시 창수면 창동로115");
//        school88.setSchoolLand(10152);
//        school88.setSchoolArea(1751);
//        school88.setSchoolPhone("031-539-0033");
//        school88.setSchoolImagePath("C:\\school");
//        school88.setSchoolImageName("포천시_보장초.png");
//        schoolRepository.save(school88);
//
//        School school89 = new School();
//        school89.setSchoolCity("포천시");
//        school89.setSchoolName("영북초보광분교장");
//        school89.setSchoolAddress("경기도 포천시 영북면 호국로3552번길21-15");
//        school89.setSchoolLand(12834);
//        school89.setSchoolArea(353);
//        school89.setSchoolPhone("031-539-0033");
//        school89.setSchoolImagePath("C:\\school");
//        school89.setSchoolImageName("포천시_영북초보광분교장.png");
//        schoolRepository.save(school89);
//
//        School school90 = new School();
//        school90.setSchoolCity("포천시");
//        school90.setSchoolName("냉정초");
//        school90.setSchoolAddress("경기도 포천시 관인면 찬우물길201-47");
//        school90.setSchoolLand(10762);
//        school90.setSchoolArea(3007);
//        school90.setSchoolPhone("031-539-0033");
//        school90.setSchoolImagePath("C:\\school");
//        school90.setSchoolImageName("포천시_냉정초.png");
//        schoolRepository.save(school90);
//
//        School school91 = new School();
//        school91.setSchoolCity("포천시");
//        school91.setSchoolName("영중초");
//        school91.setSchoolAddress("경기도 포천시 영중면 호국로3054");
//        school91.setSchoolLand(16513);
//        school91.setSchoolArea(3247);
//        school91.setSchoolPhone("031-539-0033");
//        school91.setSchoolImagePath("C:\\school");
//        school91.setSchoolImageName("포천시_영중초.png");
//        schoolRepository.save(school91);
//
//        School school92 = new School();
//        school92.setSchoolCity("포천시");
//        school92.setSchoolName("영평초");
//        school92.setSchoolAddress("경기도 포천시 영중면 전영로1429번길5");
//        school92.setSchoolLand(49937);
//        school92.setSchoolArea(2663);
//        school92.setSchoolPhone("031-539-0033");
//        school92.setSchoolImagePath("C:\\school");
//        school92.setSchoolImageName("포천시_영평초.png");
//        schoolRepository.save(school92);
//
//        School school93 = new School();
//        school93.setSchoolCity("포천시");
//        school93.setSchoolName("금주초");
//        school93.setSchoolAddress("경기도 포천시 영중면 궁들길50");
//        school93.setSchoolLand(15570);
//        school93.setSchoolArea(2209);
//        school93.setSchoolPhone("031-539-0033");
//        school93.setSchoolImagePath("C:\\school");
//        school93.setSchoolImageName("포천시_금주초.png");
//        schoolRepository.save(school93);
//
//        School school94 = new School();
//        school94.setSchoolCity("가평군");
//        school94.setSchoolName("목동초광악분교장");
//        school94.setSchoolAddress("경기도 가평군 북면 노씨터길12");
//        school94.setSchoolLand(7362);
//        school94.setSchoolArea(520);
//        school94.setSchoolPhone("031-580-5132");
//        school94.setSchoolImagePath("C:\\school");
//        school94.setSchoolImageName("가평군_목동초광악분교장.png");
//        schoolRepository.save(school94);
//
//        School school95 = new School();
//        school95.setSchoolCity("가평군");
//        school95.setSchoolName("상색초두밀분교장");
//        school95.setSchoolAddress("경기도 가평군 가평읍 태봉두밀로388");
//        school95.setSchoolLand(4103);
//        school95.setSchoolArea(1014);
//        school95.setSchoolPhone("031-580-5132");
//        school95.setSchoolImagePath("C:\\school");
//        school95.setSchoolImageName("가평군_상색초두밀분교장.png");
//        schoolRepository.save(school95);
//
//        School school96 = new School();
//        school96.setSchoolCity("가평군");
//        school96.setSchoolName("청평초양진분교장");
//        school96.setSchoolAddress("경기도 가평군 청평면 호반로882");
//        school96.setSchoolLand(2165);
//        school96.setSchoolArea(639);
//        school96.setSchoolPhone("031-580-5132");
//        school96.setSchoolImagePath("C:\\school");
//        school96.setSchoolImageName("가평군_청평초양진분교장.png");
//        schoolRepository.save(school96);
//
//        School school97 = new School();
//        school97.setSchoolCity("가평군");
//        school97.setSchoolName("목동초화악분교장");
//        school97.setSchoolAddress("경기도 가평군 북면 화악지암길43-26");
//        school97.setSchoolLand(7558);
//        school97.setSchoolArea(1275);
//        school97.setSchoolPhone("031-580-5132");
//        school97.setSchoolImagePath("C:\\school");
//        school97.setSchoolImageName("가평군_목동초화악분교장.png");
//        schoolRepository.save(school97);
//
//        School school98 = new School();
//        school98.setSchoolCity("가평군");
//        school98.setSchoolName("목동초백둔분교장");
//        school98.setSchoolAddress("");
//        school98.setSchoolLand(14552);
//        school98.setSchoolArea(0);
//        school98.setSchoolPhone("031-580-5132");
//        school98.setSchoolImagePath("C:\\school");
//        school98.setSchoolImageName("가평군_목동초백둔분교장.png");
//        schoolRepository.save(school98);
//
//        School school99 = new School();
//        school99.setSchoolCity("가평군");
//        school99.setSchoolName("조종초운악분교장");
//        school99.setSchoolAddress("경기도 가평군 조종면 명지산로83");
//        school99.setSchoolLand(9193);
//        school99.setSchoolArea(900);
//        school99.setSchoolPhone("031-580-5132");
//        school99.setSchoolImagePath("C:\\school");
//        school99.setSchoolImageName("가평군_조종초운악분교장.png");
//        schoolRepository.save(school99);
//
//        School school101 = new School();
//        school101.setSchoolCity("가평군");
//        school101.setSchoolName("미원초엄소분교장");
//        school101.setSchoolAddress("경기도 가평군 설악면 묵안로181");
//        school101.setSchoolLand(1930);
//        school101.setSchoolArea(211);
//        school101.setSchoolPhone("031-580-5132");
//        school101.setSchoolImagePath("C:\\school");
//        school101.setSchoolImageName("가평군_미원초엄소분교장.png");
//        schoolRepository.save(school101);
//
//        School school102 = new School();
//        school102.setSchoolCity("가평군");
//        school102.setSchoolName("구.목동초");
//        school102.setSchoolAddress("경기도 가평군 북면 가화로1059");
//        school102.setSchoolLand(11943);
//        school102.setSchoolArea(1734);
//        school102.setSchoolPhone("031-580-5132");
//        school102.setSchoolImagePath("C:\\school");
//        school102.setSchoolImageName("가평군_구.목동초.png");
//        schoolRepository.save(school102);
//
//        School school103 = new School();
//        school103.setSchoolCity("가평군");
//        school103.setSchoolName("청평초회곡분교장");
//        school103.setSchoolAddress("경기도 가평군 설악면 유명로2285");
//        school103.setSchoolLand(5391);
//        school103.setSchoolArea(1230);
//        school103.setSchoolPhone("031-580-5132");
//        school103.setSchoolImagePath("C:\\school");
//        school103.setSchoolImageName("가평군_청평초회곡분교장.png");
//        schoolRepository.save(school103);
//
//        School school104 = new School();
//        school104.setSchoolCity("가평군");
//        school104.setSchoolName("목동초도대분교장");
//        school104.setSchoolAddress("경기도 가평군 북면 대촌들길61");
//        school104.setSchoolLand(7187);
//        school104.setSchoolArea(732);
//        school104.setSchoolPhone("031-580-5132");
//        school104.setSchoolImagePath("C:\\school");
//        school104.setSchoolImageName("가평군_목동초도대분교장.png");
//        schoolRepository.save(school104);
//
//    }
}