package plug_and_play_service.DirectoryMonitor.Observer.Tests;

import plug_and_play_service.DirectoryMonitor.Observer.Publisher;
import plug_and_play_service.DirectoryMonitor.Observer.Subscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ObserverTest {

    StringBuilder sb1 = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();
    StringBuilder sb3 = new StringBuilder();

    Publisher<String> publisher;

    Subscriber<String> subscriber1;
    Subscriber<String> subscriber2;
    Subscriber<String> subscriber3;

    @BeforeEach
    void setUp() {

        publisher = new Publisher<>();

        Consumer<String> consumer1 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                sb1.append(s);
            }
        };
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("subscribe1 Stop subscribe");
            }
        };
        Consumer<String> consumer2 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                sb2.append(s);
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                System.out.println("subscribe2 Stop subscribe");
            }
        };

        Consumer<String> consumer3 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                sb3.append(s);
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                System.out.println("subscribe3 Stop subscribe");
            }
        };


        subscriber1 = new Subscriber<>(consumer1, runnable1);
        subscriber2 = new Subscriber<>(consumer2, runnable2);
        subscriber3 = new Subscriber<>(consumer3, runnable3);



    }

    @AfterEach
    void tearDown() {
        publisher = null;
        subscriber1 = null;
        subscriber2 = null;
        subscriber3 = null;
    }

    @Test
    void Test(){

        String s1 = "test1";
        String s2 = "test2";
        String s3 = "test3";
        String s4 = "test4";
        subscriber1.register(publisher);
        subscriber2.register(publisher);
        subscriber3.register(publisher);

        assertNull(subscriber1.getData());
        assertNull(subscriber2.getData());
        assertNull(subscriber3.getData());

        publisher.produce(s1);

        assertEquals(sb1.toString(), s1);
        assertEquals(sb2.toString(), s1);
        assertEquals(sb3.toString(), s1);

        publisher.produce(s1);

        assertEquals(sb1.toString(), s1 + s1);
        assertEquals(sb2.toString(), s1 + s1);
        assertEquals(sb3.toString(), s1 + s1);

        publisher.produce(s2);

        assertEquals(sb1.toString(), s1 + s1 + s2);
        assertEquals(sb2.toString(), s1 + s1 + s2);
        assertEquals(sb3.toString(), s1 + s1 + s2);


        publisher.produce(s3);

        assertEquals(sb1.toString(), s1 + s1 + s2 + s3);
        assertEquals(sb2.toString(), s1 + s1 + s2 + s3);
        assertEquals(sb3.toString(), s1 + s1 + s2 + s3);

        publisher.produce(s4);

        assertEquals(sb1.toString(), s1 + s1 + s2 + s3 + s4);
        assertEquals(sb2.toString(), s1 + s1 + s2 + s3 + s4);
        assertEquals(sb3.toString(), s1 + s1 + s2 + s3 + s4);

        publisher.close();

    }
}