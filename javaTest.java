

public class BallTest {
    @Test
    public void MyFirstTest() {
        Ball b = new Ball(params);

        b.setValue1(); // Changes value one
        b.setValue2(); // Changes value two


        b.collision(); // Meant change value 1 and value 2 to 0

        assertEquals(b.value1, 0);
        assertEquals(b.value2, 0);

    }
}