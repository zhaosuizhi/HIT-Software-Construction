package P3;

import org.junit.Test;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.lang.reflect.Field;
import java.security.KeyException;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.*;


public class FriendshipGraphTest {

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {
        return (T) obj;
    }

    private FriendshipGraph getGraph() {
        return new FriendshipGraph();
    }

    private Field getAccessibleField(String fieldName) throws NoSuchFieldException {
        Class<FriendshipGraph> graphClass = FriendshipGraph.class;
        Field field = graphClass.getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

    private HashMap<String, Set<String>> getHashMapGraph(FriendshipGraph graph) throws NoSuchFieldException, IllegalAccessException {
        String fieldName = "graph"; // 成员变量名
        Field field = getAccessibleField(fieldName);

        return cast(field.get(graph)); // 将其转换为HashMap类型后返回
    }

    @Test
    public void addVertexTest() throws NoSuchFieldException, IllegalAccessException {
        Person p1 = new Person("Wang Huahua");
        Person p2 = new Person("Li Hua");

        FriendshipGraph graph = getGraph();
        HashMap<String, Set<String>> hashMap = getHashMapGraph(graph); // 获取FriendshipGraph内部的hashMap

        // 测试是否能正常添加Person
        graph.addVertex(p1);
        graph.addVertex(p2);
        assertTrue(hashMap.containsKey(p1.getName()));
        assertTrue(hashMap.containsKey(p2.getName()));

        // 测试添加相同的Person时是否报错
        try {
            graph.addVertex(p1);
            throw new AssertionError();
        } catch (KeyAlreadyExistsException ignored) {
            // correct
        }

        // 测试添加相同名字的Person时是否报错
        try {
            graph.addVertex(new Person("Wang Huahua"));
            throw new AssertionError();
        } catch (KeyAlreadyExistsException ignored) {
            // correct
        }
    }

    @Test
    public void addEdgeTest() throws NoSuchFieldException, IllegalAccessException, KeyException {
        String p1Name = "Wang Huahua";
        String p2Name = "Li Hua";
        Person p1 = new Person(p1Name);
        Person p2 = new Person(p2Name);

        FriendshipGraph graph = getGraph();
        HashMap<String, Set<String>> hashMap = getHashMapGraph(graph); // 获取FriendshipGraph内部的hashMap

        graph.addVertex(p1);
        graph.addVertex(p2);

        // 测试是否能正常添加边
        assertTrue(graph.addEdge(p1, p2));
        assertTrue(hashMap.get(p1Name).contains(p2Name));
        assertTrue(graph.addEdge(p2, p1));
        assertTrue(hashMap.get(p2Name).contains(p1Name));

        // 测试添加相同边是否返回正确结果
        assertFalse(graph.addEdge(p1, p2));

        // 测试自指是否会报错
        try {
            graph.addEdge(p2, p2);
            throw new AssertionError();
        } catch (IllegalArgumentException ignored) {
            // correct
        }
    }

    @Test
    public void getDistanceTest() throws KeyException {
        Person p1 = new Person("Wang Huahua");
        Person p2 = new Person("Li Hua");
        Person p3 = new Person("Zhang san");
        Person p4 = new Person("Li si");

        FriendshipGraph graph = getGraph();
        graph.addVertex(p1);
        graph.addVertex(p2);
        graph.addVertex(p3);
        graph.addVertex(p4);

        graph.addEdge(p1, p2);
        graph.addEdge(p2, p1);
        graph.addEdge(p2, p3);
        graph.addEdge(p4, p1);

        assertEquals(1, graph.getDistance(p1, p2));
        assertEquals(3, graph.getDistance(p4, p3));
        assertEquals(-1, graph.getDistance(p2, p4));
    }
}
