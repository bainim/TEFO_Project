package com.talan.empreintecarbone;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoSpringbootAngularApplicationTests {
//    @Mock
//    private UserService userRepository;
//    @Mock
//    MoyenTransport metro;
//    @Mock
//    MoyenTransport rer;
//    @Mock
//    MoyenTransport tramway;
//    @Mock
//    MoyenTransport bus;
//    @Mock
//    MoyenTransport voiture;
//    @Mock
//    MoyenTransport transilien;
//

    @Before
    public void before() {
//        metro = new MoyenTransport((long) 1, "Metro", (float) 3.8);  // 19   38
//        rer = new MoyenTransport((long) 2, "RER", (float) 3.9);      // 19.5 39
//        tramway = new MoyenTransport((long) 3, "Tramway", (float) 3.1);  //15.5  31
//        bus = new MoyenTransport((long) 4, "Bus", (float) 94.4);     // 472  944
//        voiture = new MoyenTransport((long) 5, "Voiture", (float) 206);  // 1030 2060
//        transilien = new MoyenTransport((long) 6, "Transilien SNCF", (float) 6.4); // 32 64
    }

    @Test
    public void testCalculCo2() {
//        List<Trajet> trajets = new ArrayList<>();
//        trajets.add(
//                new Trajet()
//                        .depart("aaa")
//                        .arrive("bbb")
//                        .distance(5)
//                        .moyenTransport(metro));
//        trajets.add(
//                new Trajet()
//                        .depart("bbb")
//                        .arrive("ccc")
//                        .distance(10)
//                        .moyenTransport(rer));
//        Voyage voyage = new Voyage();
//        voyage.setDateVoyage(new Date());
//        voyage.setTrajets(trajets);
//        System.out.println(voyage.calculerCo2());
//        Assert.assertEquals(voyage.calculerCo2(), 58, 0.0f);
    }
}
